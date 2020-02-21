/**
 * Baidu.com Inc. Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;

import com.sipsd.restful.api.mode.JDBC.Pagination;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * @title SQLGenerator
 * @description 负责生成SQL语句，包括：SELECT，INSERT和UPDATE语句 （暂不支持DELETE，项目中没有需求）。
 * @author liwei39
 * @date 2014-7-9
 * @version 1.0
 */
@CommonsLog
public class SQLGenerator {
    private static final int SQL_TYPE_UPDATE = 1;
    private static final int SQL_TYPE_INSERT = 2;
    private static final int SQL_TYPE_SELECT = 3;
    private static final int SQL_TYPE_DELETE = 4;

//    protected static String wrapTableName(String dbTableName) {
//        /* 表名请勿加单引号，因为DBProxy需要使用dbname.tablename的格式，如果加了引号，DBProxy则识别不出库名，于是会默认加一个FC_Word作为默认库 */
//        return dbTableName; // return "`" + dbTableName + "` SET ";
//    }

//    public String genSQLUpdate(Object objectBean) {
//        return genSQL(objectBean, SQL_TYPE_UPDATE);
//    }

    /**
     * 生成UPDATE语句：以指定的WHERE条件字段对除WHERE条件的非空字段更新
     * 
     * @param objectBean 操作的POJO对象
     * @param whereCondiFields 指定Update的Where条件字段（字段间是与关系），而不是默认的 主键字段
     * @return 返回SQL语句
     * */
    public String genSQLUpdate(Object objectBean, String...whereCondiFields) {

        ORMBuilderFacade ormBuilderFacade = new ORMBuilderFacade(objectBean);
        ORFieldMapping fieldMapping = ormBuilderFacade.buildFieldMapping();
        ORValueMapping valueMapping = ormBuilderFacade.buildValueMapping();

        Map<String, Object> whereCondi = new LinkedHashMap<String, Object>();
        // WHERE条件字段变换成数据库域的名字
        String[] dbColumNames = new String[whereCondiFields.length];

        if(dbColumNames.length == 0 && valueMapping.getDbKeysKV().size() == 0)
        {
            log.error("该表没有主键且也没有传入需要根据的字段更新!");
            throw new IllegalArgumentException("该表没有主键且也没有传入需要根据的字段更新!");
        }

        for (int i = 0; i < whereCondiFields.length; i++) {
            if (fieldMapping.getDb2Java().containsKey(whereCondiFields[i])) {
                dbColumNames[i] = whereCondiFields[i];
            } else if (fieldMapping.getJava2Db().containsKey(whereCondiFields[i])) {
                dbColumNames[i] = fieldMapping.getJava2Db().get(whereCondiFields[i]);
            } else {
                throw new IllegalArgumentException("unrecognized field name: " + whereCondiFields[i]);
            }

            if (fieldMapping.isKey(dbColumNames[i])) {
                whereCondi.put(dbColumNames[i], valueMapping.getDbFieldKV().get(dbColumNames[i]));

            } else { // 普通字段
                whereCondi.put(dbColumNames[i], valueMapping.getDbFieldKV().get(dbColumNames[i]));
                valueMapping.getDbFieldKV().remove(dbColumNames[i]);
            }
            if (whereCondi.get(dbColumNames[i]) == null) {
                throw new IllegalArgumentException("NULL field value for: " + whereCondiFields[i]);
            }
        }


        //没有传入自定义要根据该字段更新的字段则使用自带主键
        if(dbColumNames.length == 0)
        {
            return genSQLUpdate(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(), valueMapping.getDbFieldKV());
        }

        //如果传入字段则根据传入的字段更新
        return genSQLUpdate(fieldMapping.getDbTableName(), whereCondi, valueMapping.getDbFieldKV());
    }

    /**
     * 生成INSERT语句：对象的所有非空字段都会被插入到数据库
     *
     * @param objectBean 操作的POJO对象
     * @return 返回INSERT语句
     * */
    public String genSQLInsert(Object objectBean) {
        return genSQL(objectBean, SQL_TYPE_INSERT);
    }

    /**
     * 生成SELECT语句：对象的非空字段为WHERE条件（字段间的关系是与的关系）
     *
     * @param objectBean 操作的POJO对象
     * @return 返回SELECT语句
     * */
    public String genSQLSelect(Object objectBean) {
        return genSQL(objectBean, SQL_TYPE_SELECT);
    }


    public String genLimitAndOrderSQLSelect(Object objectBean,Pagination pagination) {
        return genLimitAndOrderSQL(objectBean, SQL_TYPE_SELECT,pagination);
    }




    /**
     * 生成SELECT语句：对象的非空字段为WHERE条件（字段间的关系是与的关系），同时支持OrderBy，分页和投影子句
     *
     * @param objectBean 操作的POJO对象
     * @param hqls OrderBy，分页和投影等子句
     * @return 返回生成的SELECT语句
     * */
    public String genSQLSelect(Object objectBean, HQL...hqls) {

        ORMBuilderFacade ormBuilderFacade = new ORMBuilderFacade(objectBean);
        ORFieldMapping fieldMapping = ormBuilderFacade.buildFieldMapping();
        ORValueMapping valueMapping = ormBuilderFacade.buildValueMapping();

        SortedMap<Integer, HQL> hqlMap = new TreeMap<Integer, HQL>();
        for (HQL hql : hqls) {
            if (hqlMap.containsKey(hql.getIndex())) {
                throw new IllegalArgumentException("duplicated HQL type: " + hql.getClass().getSimpleName());
            }
            hqlMap.put(hql.getIndex(), hql);
        }

        StringBuffer sql = new StringBuffer();
        HQL selectHQL = hqlMap.get(HQL.INDEX_SELECT); // SELECT HQL需单独处理
        if (selectHQL != null) {
            sql.append(genSQLSelect(selectHQL.genSQL(fieldMapping.getJava2Db(), fieldMapping.getDb2Java()),
                    fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(), valueMapping.getDbFieldKV()));
        } else {
            sql.append(genSQLSelect("*", fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                    valueMapping.getDbFieldKV()));
        }

        Iterator<Entry<Integer, HQL>> hqlIter = hqlMap.entrySet().iterator();
        while (hqlIter.hasNext()) { // 其他HQL统一处理，必须有序
            HQL hql = hqlIter.next().getValue();
            if (hql.getIndex() > HQL.INDEX_SELECT) {
                sql.append(hql.genSQL(fieldMapping.getJava2Db(), fieldMapping.getDb2Java()));
            }
        }
        return sql.toString();
    }

    /**
     * 依据类型的不同，生成具体的SQL语句
     *
     * @param objectBean 待执行OR Mapping，生成SQL语句的POJO对象
     * @param sqlType SQL语句的类型
     * */
    protected String genSQL(Object objectBean, int sqlType) {

        ORMBuilderFacade ormBuilderFacade = new ORMBuilderFacade(objectBean);
        ORFieldMapping fieldMapping = ormBuilderFacade.buildFieldMapping();
        ORValueMapping valueMapping = ormBuilderFacade.buildValueMapping();

        String sql = null;
        switch (sqlType) {
            case SQL_TYPE_UPDATE:
                sql =
                        genSQLUpdate(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                                valueMapping.getDbFieldKV());
                break;
            case SQL_TYPE_INSERT:
                sql =
                        genSQLInsert(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                                valueMapping.getDbFieldKV());
                break;
            case SQL_TYPE_SELECT:
                sql =
                        genSQLSelect(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                                valueMapping.getDbFieldKV());
                break;
            case SQL_TYPE_DELETE:
            	sql =
            		genSQLDelete(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                            valueMapping.getDbFieldKV());
            	break;
            default: // Unknown Type
                throw new IllegalArgumentException("not supported action: " + sqlType);
        }
        return sql;
    }


    protected String genLimitAndOrderSQL(Object objectBean, int sqlType,Pagination pagination) {

        ORMBuilderFacade ormBuilderFacade = new ORMBuilderFacade(objectBean);
        ORFieldMapping fieldMapping = ormBuilderFacade.buildFieldMapping();
        ORValueMapping valueMapping = ormBuilderFacade.buildValueMapping();

        String sql = null;
        switch (sqlType) {
            case SQL_TYPE_UPDATE:
                sql =
                        genSQLUpdate(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                                valueMapping.getDbFieldKV());
                break;
            case SQL_TYPE_INSERT:
                sql =
                        genSQLInsert(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                                valueMapping.getDbFieldKV());
                break;
            case SQL_TYPE_SELECT:
                sql =
                        genLimitAndOrderSQLSelect(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                                valueMapping.getDbFieldKV(),pagination);
                break;
            case SQL_TYPE_DELETE:
                sql =
                        genSQLDelete(fieldMapping.getDbTableName(), valueMapping.getDbKeysKV(),
                                valueMapping.getDbFieldKV());
                break;
            default: // Unknown Type
                throw new IllegalArgumentException("not supported action: " + sqlType);
        }
        return sql;
    }

    public static String genSQLUpdate(String dbTableName, Map<String, Object> dbKeyes, Map<String, Object> dbFields) {
        /* sample: update material set duration=15.0,width=120,height=90 where materialid=8; */
        StringBuffer sqlUpdate = new StringBuffer("UPDATE ");
        sqlUpdate.append(SQLWrapper.wrapTableName(dbTableName)).append(" SET ");
        int fieldAdded = genSQLEachField(dbFields, ",", sqlUpdate, "=", sqlUpdate); // 更新普通字段
        sqlUpdate.append(" where ");
        int keysAdded = genSQLEachField(dbKeyes, " and ", sqlUpdate, "=", sqlUpdate); // 以主键作为WHERE条件
        if (fieldAdded <= 0 || keysAdded <= 0) {
            throw new IllegalArgumentException("update statement can't be generated: " + sqlUpdate.toString());
        }
        return sqlUpdate.toString();
    }

    public static String genSQLInsert(String dbTableName, Map<String, Object> dbKeys, Map<String, Object> dbFields) {
        StringBuffer sqlInsertKeys = new StringBuffer("INSERT INTO "); // Insert部分
        sqlInsertKeys.append(SQLWrapper.wrapTableName(dbTableName)).append("( ");
        StringBuffer sqlValueKeys = new StringBuffer("VALUES ("); // Value部分
        // 字段组装
        int keysAdded = genSQLEachField(dbKeys, ",", sqlInsertKeys, null, sqlValueKeys);

        StringBuffer sqlInsertFields = new StringBuffer();
        StringBuffer sqlValueFields = new StringBuffer();
        int fieldsAdded = genSQLEachField(dbFields, ",", sqlInsertFields, null, sqlValueFields);

        if (keysAdded > 0 && fieldsAdded > 0) {
            sqlInsertKeys.append(",");
            sqlValueKeys.append(",");
        }

        sqlInsertFields.append(") "); // Insert部分结束
        sqlValueFields.append(")"); // Value部分结束

        return sqlInsertKeys.toString() + sqlInsertFields.toString() + sqlValueKeys.toString()
                + sqlValueFields.toString();
    }

    public static String genSQLSelect(String dbTableName, Map<String, Object> dbKeys, Map<String, Object> dbFields) {
//        return genSQLSelect("*", dbTableName, dbKeys, dbFields);
    	return new Where(dbTableName, dbKeys, dbFields).toSelectSQL();
    }

    public static String genLimitAndOrderSQLSelect(String dbTableName, Map<String, Object> dbKeys, Map<String, Object> dbFields, Pagination pagination) {
        String keyName = "";
        //在sqlserver中如果有主键则按照主键做rownumber，如果没有则取任一字段做rownumber
        if(dbKeys.size() !=0)
        {
            keyName = dbKeys.keySet().iterator().next();
        }
        String fieldName =  StringUtils.isEmpty(keyName) ? dbFields.keySet().iterator().next(): dbKeys.keySet().iterator().next();
        pagination.setFieldName(fieldName);

        return new Where(dbTableName, dbKeys, dbFields).toLimitAndOrderSelectSQL(pagination);
    }

    private static String genSQLSelect(String selectFieldList, String dbTableName, Map<String, Object> dbKeys,
    		Map<String, Object> dbFields) {
    	return new Where(dbTableName, dbKeys, dbFields).toSelectSQL(selectFieldList);
    }

    public String genSQLDelete(Object objectBean) {
        return genSQL(objectBean, SQL_TYPE_DELETE);
    }

    protected static String genSQLDelete(String dbTableName, Map<String, Object> dbKeys, Map<String, Object> dbFields) {
    	StringBuffer sqlDelete = new StringBuffer("DELETE FROM ");
    	sqlDelete.append(SQLWrapper.wrapTableName(dbTableName));
    	String dynamicWhere = new Where(dbTableName, dbKeys, dbFields).toWhereSQL();
    	if (dynamicWhere != null && dynamicWhere.length() > 0) {
    		sqlDelete.append(" WHERE ").append(dynamicWhere);
    	}
    	return sqlDelete.toString();
    }

    /**
     * 字段迭代
     *
     * @param dbFields 数据库字段列表
     * @param catFields 字段间的链接符
     * @param sqlFieldName 字段名部分的SQL
     * @param catNameValue 字段内的链接符：INSERT语句时，为NULL；Update语句时为"="；
     * @param sqlFieldValue 字段值部分的SQL：INSERT语句时，不等于sqlFieldName；Update时，两者相等。
     * @return 返回有效Field的个数
     * */
    private static int genSQLEachField(final Map<String, Object> dbFields, final String catFields,
            StringBuffer sqlFieldName, String catNameValue, StringBuffer sqlFieldValue) {
        int fieldCntAppended = 0;
        Iterator<Entry<String, Object>> kvs = dbFields.entrySet().iterator();
        boolean isDivNeed = false;
        while (kvs.hasNext()) {
            Entry<String, Object> e = kvs.next();
            if (e.getValue() == null) {
                continue;
            }
            if (isDivNeed) {
                sqlFieldName.append(catFields);
                /* 不是同一个对象时，则两部分都追加。e.g. INSERT语句 */
                if (sqlFieldName != sqlFieldValue) {
                    sqlFieldValue.append(catFields);
                }
            }
//            sqlFieldName.append("`").append(e.getKey()).append("`");
            sqlFieldName.append(e.getKey());
            if (catNameValue != null) {
                /* FieldName与FieldValue之间是否需要插入链接符：UPDATE的SET和WHERE字句都需要；但INSERT语句不需要 */
                sqlFieldName.append(catNameValue);
            }
            sqlFieldValue.append(SQLWrapper.wrapDbFieldValue(e.getValue()));
            fieldCntAppended++;
            isDivNeed = true;
        }
        return fieldCntAppended;
    }

//    /**
//     * @param dbFieldValue 表示POJO的字段-数值映射关系变换成数据表字段-数值的映射关系 目前只考虑简单类型，对于复合类型，按它的toString()方法序列化成SQL语句。 <br/>
//     *            <p/>
//     *            例如：如果POJO中有定义List<Integer>，序列化时默认成类似：[1,4,8]形式。对应的DB字段类型可能是varchar，而不能是其他。
//     * */
//    private static String wrapDbFieldValue(Object dbFieldValue) {
//        if (dbFieldValue instanceof String) {
//            return "'" + (String) dbFieldValue + "'"; // e.g. name='steven'
//        } else if (dbFieldValue instanceof Date) {
//            return "'" + DateUtil.format((Date) dbFieldValue) + "'";// e.g. birthday='1980-04-05 12:30:08'
//        } else if (dbFieldValue instanceof Boolean) {
//            return ((Boolean) dbFieldValue ? "1" : "0"); // TRUE/FALSE 用1/0 表示
//        } else {
//            return dbFieldValue.toString(); // e.g. age=18
//        }
//        /*
//         * 可以判断一个object对象是否是数组： dbFieldValue.getClass().isArray()
//         * 但无法迭代每个元素，在不知道类型的情况下，除非再逐个试探。另外考虑到，关系型如果VALUE出现多值，本身不符合范式。
//         * 即使DB层违背范式，也通常在JAVA直接用String表示，而很少DB层存储'1,2,3,4'，JAVA直接翻译成int [] {1,2,3,4};
//         */
//    }
}
