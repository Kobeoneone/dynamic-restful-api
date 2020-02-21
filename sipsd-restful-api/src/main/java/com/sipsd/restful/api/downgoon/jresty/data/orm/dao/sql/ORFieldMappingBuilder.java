/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;


import com.sipsd.restful.api.downgoon.jresty.data.orm.annotation.ORMField;
import com.sipsd.restful.api.downgoon.jresty.data.orm.annotation.ORMTable;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util.PojoOperatorFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @title ORFieldMappingBuilder
 * @description 对POJO类提取ORFieldMapping映射关系
 * @author liwei39
 * @date 2014-7-12
 * @version 1.0
 */
class ORFieldMappingBuilder {

	/**
	 * POJO only for select, NO update, create
	 * */
	private boolean isOnlyForSelect = false;
	
    private Class<?> pojoClass;

    public ORFieldMappingBuilder(Class<?> pojoClass) {
        this(pojoClass, false);
    }

    public ORFieldMappingBuilder(Class<?> pojoClass, boolean isOnlyForSelect) {
        super();
        this.pojoClass = pojoClass;
        this.isOnlyForSelect = true;
    }
    
    /**
     * @return POJO到关系型的字段名称映射关系
     * */
    public ORFieldMapping buildFieldMapping() {
        Map<String, String> java2db = new LinkedHashMap<String, String>();
        Map<String, String> db2java = new LinkedHashMap<String, String>();
        Map<String, Boolean> dbKeys = new LinkedHashMap<String, Boolean>();
        Map<String, Class<?>> dbFieldJavaType = new LinkedHashMap<String, Class<?>>();

        StringBuffer autoIncrColum = new StringBuffer(); // 输出参数
        String dbTableName = dumpFieldMapping(java2db, db2java, dbKeys, dbFieldJavaType, autoIncrColum);
        ORFieldMapping fm = new ORFieldMapping(dbTableName, java2db, db2java, dbKeys, dbFieldJavaType, autoIncrColum.toString());
        return fm;
    }

    /**
     * 提取POJO的ORM映射关系
     * 
     * @param pojoClass JAVA POJO对象
     * @param db2java 必选输出参数：数据库字段名到JAVA属性名的映射关系
     * @param java2db 必选输出参数：JAVA属性名到数据库字段名的映射关系
     * @param dbKeys 可选输出参数：DB的主键字段列表 （输入NULL时，不输出主键列表）
     * @param dbFieldJavaType 可选输出参数：db字段对应的JAVA POJO的类型
     * @return DB表名称
     * */
    protected String dumpFieldMapping(Map<String, String> java2db, Map<String, String> db2java,
            Map<String, Boolean> dbKeys, Map<String, Class<?>> dbFieldJavaType, final StringBuffer autoIncrColum) {

        // 确定TableName
        String dbTableName = null;
        ORMTable tableAnnotation = (ORMTable) pojoClass.getAnnotation(ORMTable.class);

        if (tableAnnotation != null) {
            dbTableName = tableAnnotation.name();
        } else { // ORMTable Annotation 不是必须的
            dbTableName = pojoClass.getSimpleName().toLowerCase();
        }

        // 确定字段名和主键名
        Method[] methods = pojoClass.getDeclaredMethods(); // pojoClass.getMethods();

        for (Method method : methods) { // 注：不同机器上，不同JVM，反射出来的Method顺序或不一样
            int mod = method.getModifiers();
            if (!(Modifier.isPublic(mod) && !Modifier.isStatic(mod))) { // SKIP
                continue;
            }

            String objAttriName = isPojoGetter(method);
            if (objAttriName == null) { // SKIP
                continue;
            }

            ORMField fieldAnnotation = method.getAnnotation(ORMField.class);
            boolean isSkip = isSkipField(objAttriName, fieldAnnotation);
            if (isSkip) { // SKIP
                continue;
            }
            String dbFieldName = attri2ColumName(objAttriName, fieldAnnotation);

            java2db.put(objAttriName, dbFieldName);
            db2java.put(dbFieldName, objAttriName);

            
            boolean isKey = isKeyField(dbTableName, objAttriName, fieldAnnotation);
            if (dbKeys != null && isKey) { // 非主键无需保存，有些请求不需要返回主键列表
                dbKeys.put(dbFieldName, true);
            }

            // AUTO_INCREMENT 必须通过 Annotation 声明
            boolean isAutoIncr = (fieldAnnotation != null && fieldAnnotation.isAutoIncrement());
            if (isAutoIncr) {
            	if (! isKey) {
            		throw new IllegalArgumentException(String.format("key required for AUTO_INCREMENT setting: %s.%s", 
            				pojoClass.getSimpleName(), objAttriName));
            	}
            	
            	Class<?> objAttriType = PojoOperatorFactory.getPojoOperator().attriType(pojoClass, objAttriName);
            	if (! isDigitalType(objAttriType)) {
            		throw new IllegalArgumentException(String.format("digital type required for AUTO_INCREMENT setting: %s.%s", 
            				pojoClass.getSimpleName(), objAttriName));
            	}
            	if (autoIncrColum.length() > 0) {
            		throw new IllegalArgumentException(String.format("too many AUTO_INCREMENT setting: %s.%s and %s.%s", 
            				pojoClass.getSimpleName(), objAttriName, pojoClass.getSimpleName(), autoIncrColum));
            	}
            	autoIncrColum.append(dbFieldName); // 输出参数
            }
            
            if (dbFieldJavaType != null) {
                dbFieldJavaType.put(dbFieldName,
                        PojoOperatorFactory.getPojoOperator().attriType(pojoClass, objAttriName));
            }

        }

        if (! isOnlyForSelect) {
        	if (dbKeys != null && dbKeys.size() <= 0) {
                throw new IllegalArgumentException("No keys found in the object bean named: " + pojoClass.getName());
            }
            if (db2java.size() <= 0) {
                throw new IllegalArgumentException("No fields found for update in the object bean named: "
                        + pojoClass.getName());
            }
        }

        return dbTableName;
    }
    
    private boolean isDigitalType(Class<?> classType) {
    	return Long.class.equals(classType) || Integer.class.equals(classType) || Short.class.equals(classType);
    }

    /**
     * 判断对象的方法是否是POJO的Getter方法
     * 
     * @param method 对象的方法
     * @return 如果返回NULL，表示不是普通Getter；否则，返回Getter方法对应的属性名称
     * */
    private static String isPojoGetter(Method method) {
        String attriName = null;
        String methodName = method.getName();
        if (methodName.startsWith("get") && !methodName.equals("getClass")) {
            /* 属性名： getSomeField -> SomeField -> someField */
            attriName = methodName.substring("get".length());
        } else if (methodName.startsWith("is") && Boolean.class.equals(method.getReturnType())) {
            /* 属性名： isSomeField -> SomeField -> someField */
            attriName = methodName.substring("is".length());
        }
        if (attriName == null || attriName.length() < 1) {
            return null;
        }
        Class<?>[] params = method.getParameterTypes();
        if (params != null && params.length > 0) { // e.g. getXXX(String param) 不是普通的Getter/Setter
            return null;
        }
        if (attriName.length() > 1) { // e.g. getApplicationName() => applicationName
            return attriName.substring(0, 1).toLowerCase() + attriName.substring(1);
        } else {
            return attriName.toLowerCase(); // e.g. getP() => p
        }
    }

    private static boolean isSkipField(String objAttriName, ORMField fieldAnnotation) {
        return (fieldAnnotation != null && fieldAnnotation.isSkip());
    }

    private static boolean isKeyField(String dbTableName, String objAttriName, ORMField fieldAnnotation) {
        if (fieldAnnotation == null) {
            if (objAttriName.startsWith("id")) { // 联合主键：idUser+idMd5
                return true;
            }
            if (objAttriName.toLowerCase().equals(dbTableName + "id")) { // e.g. Material类的 materialId
                return true;
            }
            return false;

        } else {
            return fieldAnnotation.isKey();
        }
    }

    /** JAVA属性名 转换成 DB的列名 */
    private static String attri2ColumName(String objAttriName, ORMField fieldAnnotation) {
        if (fieldAnnotation == null) {
            return objAttriName.toLowerCase();
        } else {
            return fieldAnnotation.name();
        }
    }

}
