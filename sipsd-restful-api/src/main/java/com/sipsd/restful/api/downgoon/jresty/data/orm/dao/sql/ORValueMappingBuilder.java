/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;


import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util.PojoOperatorFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @title ORValueMappingBuilder
 * @description TODO
 * @author liwei39
 * @date 2014-7-12
 * @version 1.0
 */
class ORValueMappingBuilder {

    private Object pojoBean;

    private ORFieldMapping fieldMapping;

    public ORValueMappingBuilder(Object pojoBean, ORFieldMapping fieldMapping) {
        super();
        this.pojoBean = pojoBean;
        this.fieldMapping = fieldMapping;
    }

    public ORValueMapping buildValueMapping() {
        Map<String, Object> dbKeysKV = new LinkedHashMap<String, Object>();
        Map<String, Object> dbFieldKV = new LinkedHashMap<String, Object>();
        String dbTableName = dumpColumValue(dbKeysKV, dbFieldKV);
        return new ORValueMapping(dbTableName, dbKeysKV, dbFieldKV);
    }

    /**
     * @param dbKeysKV 输出参数：DB主键字段的取值（注：Map.Key是DB列名，不是POJO属性名）
     * @param dbFieldKV 输出参数：DB普通字段的取值 （注：Map.Key是DB列名，不是POJO属性名）
     * @return DB的表名
     * */
    private String dumpColumValue(Map<String, Object> dbKeysKV, Map<String, Object> dbFieldKV) {
        /* 遍历字段名称：JAVA -> DB 映射关系 */
        Iterator<Entry<String, String>> fields = fieldMapping.getJava2Db().entrySet().iterator();
        while (fields.hasNext()) {
            Entry<String, String> field = fields.next();
            String javaAttriName = field.getKey();
            String dbColumName = field.getValue();
            Object javaAttriValue = PojoOperatorFactory.getPojoOperator().doGetter(pojoBean, javaAttriName);
            if (javaAttriName == null) { // SKIP NULL VALUE FIELD
                continue;
            }

            if (fieldMapping.isKey(dbColumName)) {
                dbKeysKV.put(dbColumName, javaAttriValue);
            } else {
                dbFieldKV.put(dbColumName, javaAttriValue);
            }
        }
        return fieldMapping.getDbTableName();
    }

}
