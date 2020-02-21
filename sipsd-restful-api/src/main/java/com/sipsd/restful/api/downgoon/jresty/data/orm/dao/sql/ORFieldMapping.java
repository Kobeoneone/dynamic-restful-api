/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @title ORFieldMapping
 * @description POJO类的Object-Relation 字段名称映射关系 
 * @author liwei39
 * @date 2014-8-18
 * @version 1.0
 */
public class ORFieldMapping {
    /*
     * 术语定义：
     * 1. Attri 表示JAVA POJO 的属性名称
     * 2. Colum 表示 DB表的列
     * 3. Field 表示JAVA POJO的属性和DB的列 的统称
     * */

    /* JAVA属性名到数据库字段名的映射关系 */
    private Map<String, String> java2db;

    /* DB表名称 */
    private String dbTableName;

    /* 数据库字段名到JAVA属性名的映射关系 */
    private Map<String, String> db2java;

    /* DB的主键字段列表 （输入NULL时，不输出主键列表） */
    private Map<String, Boolean> dbKeys;

    /* db字段对应的JAVA POJO的类型 */
    private Map<String, Class<?>> dbFieldJavaType;
    
    /** 自增长字段  */
    private String autoIncrementColum;

    /**
     * @param   dbTableName  DB表名
     * @param   java2db JAVA属性名到数据库字段名的映射关系
     * @param   db2java 数据库字段名到JAVA属性名的映射关系
     * @param   dbKeys  DB的主键字段列表 （输入NULL时，不输出主键列表）
     * @param   dbFieldJavaType db字段对应的JAVA POJO的类型
     * @param	autoIncrementColum	自增长主键数据库字段名称，如果没有，填NULL
     * */
    ORFieldMapping(String dbTableName, Map<String, String> java2db, Map<String, String> db2java,
            Map<String, Boolean> dbKeys, Map<String, Class<?>> dbFieldJavaType, String autoIncrementColum) {
        super();
        this.dbTableName = dbTableName;
        this.java2db = java2db;
        this.db2java = db2java;
        this.dbKeys = dbKeys;
        this.dbFieldJavaType = dbFieldJavaType;
        if (autoIncrementColum == null || autoIncrementColum.length() == 0) {
        	this.autoIncrementColum = null;
        } else {
        	this.autoIncrementColum = autoIncrementColum;
        }
    }

    /** db字段对应的JAVA POJO的类型 */
    public Map<String, Class<?>> getDbFieldJavaType() {
        return dbFieldJavaType;
    }

    /** DB表名称 */
    public String getDbTableName() {
        return dbTableName;
    }

    /** JAVA属性名到数据库字段名的映射关系 */
    public Map<String, String> getJava2Db() {
        return java2db;
    }

    /** 数据库字段名到JAVA属性名的映射关系 */
    public Map<String, String> getDb2Java() {
        return db2java;
    }

    /**
     * @param   filedName   字段名是否是主键（输入可以是数据库列名，也可以是POJO属性名）
     * @return  返回是否是主键
     * */
    public boolean isKey(String filedName) {
        boolean isDbKey = isDBKey(filedName); // 数据库列名
        if (!isDbKey) {
            String dbColumName = java2db.get(filedName); // POJO属性名
            if (dbColumName != null) {
                return isDBKey(dbColumName);
            }
        }
        return isDbKey;
    }

    private boolean isDBKey(String dbColumName) {
        Boolean keyFlag = dbKeys.get(dbColumName);
        if (keyFlag != null && keyFlag) {
            return true;
        }
        return false;
    }
    
    /**
     * 是否存在 自增长 主键 
     * 备注： 自增长主键必须是单一主键，不能是联合主键；同时类型是整数类型：Short/Int/Long 等
     * */
    public boolean hasAutoIncrementKey() {
    	return autoIncrementColum != null;
    }
    
    /**
     * @return 自增长数据库字段名称，如果没有，则返回 NULL
     * */
    public String getAutoIncrementColum() {
    	return this.autoIncrementColum;
    }
    
    /***
     * @return 自增长POJO属性名，如果没有，则返回 NULL
     * */
    public String getAutoIncrementAttri() {
    	if (autoIncrementColum == null) {
    		return null;
    	}
    	return db2java.get(autoIncrementColum);
    }

    public void print(PrintWriter writer) {
    	writer.println(String.format("TableName: %s", dbTableName));
    	writer.println(String.format("DB-Column\t|Java-Attri\t|Java-Type\t|isKey\t|isAuto"));
    	writer.println("--------");
    	
    	Iterator<Entry<String, String>> es = db2java.entrySet().iterator(); 
    	while (es.hasNext()) {
    		Entry<String, String> e = es.next();
    		String dbColumn = e.getKey();
    		String javaAttri = e.getValue();
    		String javaType = dbFieldJavaType.get(dbColumn).getSimpleName();
    		Boolean isKey = dbKeys.containsKey(dbColumn);
    		boolean isAutoIncr = (isKey && autoIncrementColum != null && dbColumn.equals(autoIncrementColum) );
    		writer.println(String.format("%s\t|%s\t|%s\t|%s\t|%s", dbColumn, javaAttri, javaType, 
    				(isKey ? "*": ""), (isAutoIncr ? "*" : "") ));
    	}
    	writer.println();
    	writer.flush();
    }
}
