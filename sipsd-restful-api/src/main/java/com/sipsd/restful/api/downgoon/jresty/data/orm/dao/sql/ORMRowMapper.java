/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;


import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util.PojoOperatorFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @title ORMRowMapper
 * @description 把从关系型筛选出来的 ResultSet转换成JAVA的POJO
 * @author liwei39
 * @date 2014-7-12
 * @version 1.0
 */
public class ORMRowMapper<T> implements RowMapper<T>
{

    /**
     * 完备的映射： DB中有的字段一定要求POJO存在与之对应的。 默认为FALSE，以便DB增加字段，不影响POJO。
     * */
    private final boolean isCompleteMapping;

    private final Class<?> pojoClass;

    private ORFieldMapping fieldMapping;

    /**
     * 构造一个从数据库字段映射到JAVA POJO对象的映射关系
     * 
     * @param pojoClass 数据库字段要映射成JAVA POJO对象的类
     * */
    public ORMRowMapper(Class<?> pojoClass) {
        this(pojoClass, false);
    }
    
    public ORMRowMapper(Class<?> pojoClass, boolean isCompleteMapping) {
        this(pojoClass, isCompleteMapping, false);
    }

    /**
     * 构造一个从数据库字段映射到JAVA POJO对象的映射关系
     * 
     * @param pojoClass 数据库字段要映射成JAVA POJO对象的类
     * @param isCompleteMapping 是否是完全映射。<br/>
     *            <p/>
     *            如果是完全映射，则要求ResultSet中的每个字段都必须在JAVA POJO上能找到映射关系；否则，则允许在POJO上没有对应的字段。
     *            如果数据库表新增一个字段，JAVA代码不变，代码需要运行正常，则isCompleteMapping配置成FALSE。
     * */
    public ORMRowMapper(Class<?> pojoClass, boolean isCompleteMapping, boolean isOnlyForSelect) {
        super();
        this.pojoClass = pojoClass;
        this.isCompleteMapping = isCompleteMapping;
        ORFieldMappingBuilder fieldBuilder = new ORFieldMappingBuilder(pojoClass, isOnlyForSelect);
        this.fieldMapping = fieldBuilder.buildFieldMapping();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T newBean = (T) PojoOperatorFactory.getPojoOperator().newInstance(pojoClass);
        int columns = rs.getMetaData().getColumnCount(); // 列数量
        for (int cidx = 1; cidx <= columns; cidx++) {
            // ResultSet的列下标编号是从1开始的
        	// BUGFIX 2015/11/30 Mysql 表字段不总是纯小写，之前之所以全用纯小写，是因为POJO字段默认映射规则是变换成纯小写当mysql字段。
        	// TODO POJO字段与Mysql表字段默认映射关系应该支持几种常见的供用户选择，而非只有一种。
            String columName = rs.getMetaData().getColumnName(cidx); // toLowerCase() 
            String attriName = fieldMapping.getDb2Java().get(columName);
            if (attriName == null) {
            	columName = columName.toLowerCase();
            	attriName = fieldMapping.getDb2Java().get(columName);
            }
            if (attriName == null) {
                if (isCompleteMapping) {
                    throw new IllegalStateException("No POJO attribute mapping found for db tablecolumn  \'"
                            + columName + "\'");
                } else { // 默认非完备映射，以便DB增加字段，不要求POJO一定需要增加字段
                    continue;
                }
            }
            Class<?> attriType = fieldMapping.getDbFieldJavaType().get(columName);
            if (rs.getObject(cidx) != null) { // maybe NULL when LEFT JOIN on 2015/12/XX
            	Object columValue = convertFieldType(rs, cidx, attriType);
                Object attriValue = convertFieldValue(columValue);
                PojoOperatorFactory.getPojoOperator().doSetter(newBean, attriName, attriValue);
        	}
            
        }
        return newBean;
    }

    /**
     * 字段类型转换：DB数据类型 转换成 JAVA数据类型
     * */
    private Object convertFieldType(ResultSet rs, int columnIndex, Class<?> attriType) throws SQLException {
        if (Integer.class.equals(attriType)) {
            return rs.getInt(columnIndex);
        } else if (String.class.equals(attriType)) {
            return rs.getString(columnIndex);
        } else if (Long.class.equals(attriType)) {
            return rs.getLong(columnIndex);
        } else if (Double.class.equals(attriType)) {
            return rs.getDouble(columnIndex);
        } else if (Short.class.equals(attriType)) {
            return rs.getShort(columnIndex);
        } else if (Boolean.class.equals(attriType)) {
            return rs.getBoolean(columnIndex);
        } else if (Date.class.equals(attriType)) { // 2015/11/30 sql timestamp to java.util.Date
        	// java.sql.Date contains: yyyyMMdd
        	// java.sql.Time contains: HHmmss
        	// java.sql.Timestamp contains: yyyyMMdd HHmmss.SSS
        	return new Date(rs.getTimestamp(columnIndex).getTime());
        }
        else {
            return rs.getObject(columnIndex);
        }
    }

    /**
     * 字段数值转换：DB数据类型 转换成 JAVA数据类型
     * */
    protected Object convertFieldValue(Object dbColumValue) {
        if (dbColumValue instanceof java.sql.Date) {
            return new Date(((java.sql.Date) dbColumValue).getTime());
        }
        return dbColumValue;
    }

}
