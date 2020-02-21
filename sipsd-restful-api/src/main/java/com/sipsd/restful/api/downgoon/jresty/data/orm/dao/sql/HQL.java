/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;

import java.util.Map;

/**
 * @title HQL
 * @description 为SELECT操作支持分页；排序；投影操作。
 * @author liwei39
 * @date 2014-7-12
 * @version 1.0
 */
public abstract class HQL {

    /** 投影操作，只筛选出需要的字段 */
    public static final int INDEX_SELECT = 10;

    /** 排序操作，支持多个字段组合排序 */
    public static final int INDEX_ORDER = 20;

    /** 分页操作 */
    public static final int INDEX_LIMIT = 30;

    public abstract int getIndex();

    public abstract String genSQL(Map<String, String> java2db, Map<String, String> db2java);

    protected String fieldMapping2DB(Map<String, String> java2db, Map<String, String> db2java, String[] fields,
            String[] padding) {
        StringBuffer sql = new StringBuffer();
        int cnt = 0;
        for (String fieldName : fields) {
            String columName = null;
            if (db2java.containsKey(fieldName)) {
                columName = fieldName;
            } else {
                columName = java2db.get(fieldName);
            }
            if (columName == null) {
                throw new IllegalArgumentException("HQL field not found: " + fieldName);
            }
            if (cnt > 0) {
                sql.append(", ");
            }
            sql.append("`").append(columName).append("`");
            if (padding != null) {
                sql.append(padding[cnt]);
            }
            cnt++;
        }
        return sql.toString();
    }

    /**
     * 针对分页子句的HQL对象
     * 
     * @param offset 分页起始位置
     * @param length 分页页面长度
     * @return 返回分页子句的HQL对象
     * */
    public static HQL limit(int offset, int length) {
        return new Limit(offset, length);
    }

    /**
     * 针对OrderBy子句的HQL对象
     * 
     * @param fields SQL语句的OrderBy子句字段列表（默认升序）
     * @return 返回OrderBy子句的HQL对象
     * */
    public static HQL orderBy(String...fields) {
        return new Order(fields);
    }

    /**
     * 针对OrderBy子句的HQL对象
     * 
     * @param fields SQL语句的OrderBy子句字段列表
     * @param isDESC 每个排序字段是否是降序（跟fields列表依次对应）
     * @return 返回OrderBy子句的HQL对象
     * */
    public static HQL orderBy(String[] fields, boolean[] isDESC) {
        return new Order(fields, isDESC);
    }

    /**
     * 针对投影子句的HQL对象
     * 
     * @param fields SQL语句的投影子句字段列表
     * @return 返回投影子句子句的HQL对象
     * */
    public static HQL select(String...fields) {
        return new Select(fields);
    }

    public static class Select extends HQL
    {

        private String[] fields;

        private Select(String[] fields) {
            super();
            this.fields = fields;
        }

        @Override
        public int getIndex() {
            return INDEX_SELECT;
        }

        @Override
        public String genSQL(Map<String, String> java2db, Map<String, String> db2java) {
            return fieldMapping2DB(java2db, db2java, fields, null);
        }

    }

    public static class Order extends HQL
    {

        private String[] fields;

        private boolean[] isDESC;

        private Order(String[] fields, boolean[] isDESC) {
            this.fields = fields;
            this.isDESC = isDESC;
            if (fields.length != isDESC.length) {
                throw new IllegalArgumentException("orderby fields.length != isDESC.length");
            }
        }

        private Order(String...fields) {
            super();
            this.fields = fields;
        }

        @Override
        public int getIndex() {
            return INDEX_ORDER;
        }

        public String[] getFields() {
            return fields;
        }

        @Override
        public String genSQL(Map<String, String> java2db, Map<String, String> db2java) {
            StringBuilder sql = new StringBuilder(" ORDER BY ");
            String[] padding = null;
            if (isDESC != null) {
                padding = new String[isDESC.length];
                for (int i = 0; i < isDESC.length; i++) {
                    padding[i] = (isDESC[i] ? " DESC" : " ASC");
                }
            }
            sql.append(fieldMapping2DB(java2db, db2java, fields, padding));
            return sql.toString();
        }
    }

    public static class Limit extends HQL
    {
        private int offset;
        private int length;

        private Limit(int offset, int length) {
            super();
            this.offset = offset;
            this.length = length;
        }

        @Override
        public String genSQL(Map<String, String> java2db, Map<String, String> db2java) {
            return String.format(" LIMIT %d,%d", offset, length);
        }

        @Override
        public int getIndex() {
            return INDEX_LIMIT;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

    }
}
