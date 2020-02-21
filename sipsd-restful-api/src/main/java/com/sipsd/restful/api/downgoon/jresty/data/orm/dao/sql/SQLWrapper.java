package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;


import com.sipsd.restful.api.downgoon.jresty.commons.utils.DateUtil;

import java.util.Date;

class SQLWrapper {

	 /**
     * @param dbFieldValue 表示POJO的字段-数值映射关系变换成数据表字段-数值的映射关系 目前只考虑简单类型，对于复合类型，按它的toString()方法序列化成SQL语句。 <br/>
     *            <p/>
     *            例如：如果POJO中有定义List<Integer>，序列化时默认成类似：[1,4,8]形式。对应的DB字段类型可能是varchar，而不能是其他。
     * */
    public static String wrapDbFieldValue(Object dbFieldValue) {
        if (dbFieldValue instanceof String) {
        	String escaped = SQLEscapeUtils.escapeSql((String) dbFieldValue);
            return "'" + escaped + "'"; // e.g. name='steven'
        } else if (dbFieldValue instanceof Date) {
            return "'" + DateUtil.format((Date) dbFieldValue) + "'"; // e.g. birthday='1980-04-05 12:30:08'
        } else if (dbFieldValue instanceof Boolean) {
            return ((Boolean) dbFieldValue ? "1" : "0"); // TRUE/FALSE 用1/0 表示
        } else {
            return dbFieldValue.toString(); // e.g. age=18
        }
        /*
         * 可以判断一个object对象是否是数组： dbFieldValue.getClass().isArray()
         * 但无法迭代每个元素，在不知道类型的情况下，除非再逐个试探。另外考虑到，关系型如果VALUE出现多值，本身不符合范式。
         * 即使DB层违背范式，也通常在JAVA直接用String表示，而很少DB层存储'1,2,3,4'，JAVA直接翻译成int [] {1,2,3,4};
         */
    }
    
    public static String wrapTableName(String dbTableName) {
        /* 表名请勿加单引号，因为DBProxy需要使用dbname.tablename的格式，如果加了引号，DBProxy则识别不出库名，于是会默认加一个FC_Word作为默认库 */
        return dbTableName; // return "`" + dbTableName + "` SET ";
    }
    
    
}
