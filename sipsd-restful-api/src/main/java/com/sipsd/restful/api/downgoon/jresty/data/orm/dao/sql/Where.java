/**
 * 
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;

import com.sipsd.restful.api.mode.JDBC.Pagination;
import lombok.extern.apachecommons.CommonsLog;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author liwei
 *
 */
@CommonsLog
public class Where {

	public Where(Object objectBean) {
		super();
		ORMBuilderFacade ormBuilderFacade = new ORMBuilderFacade(objectBean);
//      ORFieldMapping fieldMapping = ormBuilderFacade.buildFieldMapping();
      ORValueMapping valueMapping = ormBuilderFacade.buildValueMapping();
      this.dbTableName = valueMapping.getDbTalbeName();
      this.dbKeys = valueMapping.getDbKeysKV();
      this.dbFields = valueMapping.getDbFieldKV();
	}

	private String dbTableName;
	private Map<String, Object> dbKeys;
    private Map<String, Object> dbFields;

	protected Where(String dbTableName, Map<String, Object> dbKeys, Map<String, Object> dbFields) {
		this.dbTableName = dbTableName;
		this.dbKeys = dbKeys;
		this.dbFields = dbFields;
	}

	protected String toSelectSQL(String selectFieldList) {
		StringBuffer sqlBeforeWhere = new StringBuffer("SELECT " + selectFieldList + " FROM ");
		sqlBeforeWhere.append(SQLWrapper.wrapTableName(dbTableName));
        String whereCause = toWhereSQL();
        if (whereCause.length() > 0) {
        	sqlBeforeWhere.append(" WHERE ").append(whereCause);
        } else {
        	sqlBeforeWhere.append(whereCause);
        }
        return sqlBeforeWhere.toString();
	}

//这是mysql的分页方式
    protected String toLimitAndOrderSelectSQL(String selectFieldList, Pagination pagination) {
        StringBuffer sqlBeforeWhere = new StringBuffer("SELECT " + selectFieldList + " FROM ");
        sqlBeforeWhere.append(SQLWrapper.wrapTableName(dbTableName));
        String whereCause = toWhereSQL();
        if (whereCause.length() > 0) {
            sqlBeforeWhere.append(" WHERE ").append(whereCause);
        } else {
            sqlBeforeWhere.append(whereCause);
        }
        sqlBeforeWhere.append(parseLimit(pagination));
        log.info(sqlBeforeWhere.toString());
        return sqlBeforeWhere.toString();
    }

    //这是sqlserver的分页方式
    protected String toLimitAndOrderSelectSQLBySqlServer(String selectFieldList, Pagination pagination) {
        int start = pagination.getPageSize() * (pagination.getCurrentPage()-1);
        int end = pagination.getPageSize() * pagination.getCurrentPage();
        StringBuffer sqlBeforeWhere = new StringBuffer("select "+selectFieldList+" from (select "+selectFieldList+"," +
                " ROW_NUMBER() OVER(Order by "+pagination.getFieldName()+" ) AS RowId from "+SQLWrapper.wrapTableName(dbTableName)+" ) as b");
        String whereCause = toWhereSQL();
        if (whereCause.length() > 0) {
            sqlBeforeWhere.append(" WHERE ").append(whereCause);
            sqlBeforeWhere.append(" AND ").append("RowId between "+start+" and "+end+"");

        } else {
            sqlBeforeWhere.append(" WHERE ").append("RowId between "+start+" and "+end+"");
        }
        log.info(sqlBeforeWhere.toString());
        return sqlBeforeWhere.toString();
    }

    //这是oracle的分页方式
    protected String toLimitAndOrderSelectByOracle(String selectFieldList, Pagination pagination) {

        int start = pagination.getPageSize() * (pagination.getCurrentPage()-1);
        int end = pagination.getPageSize() * pagination.getCurrentPage();

        StringBuffer sqlBeforeWhere = new StringBuffer("select "+selectFieldList+" from(select rownum as rn," +
                " t.* from "+SQLWrapper.wrapTableName(dbTableName)+" t) temp");
        String whereCause = toWhereSQL();
        if (whereCause.length() > 0) {
            sqlBeforeWhere.append(" WHERE ").append(whereCause);
            sqlBeforeWhere.append(" AND ").append("temp.rn between "+start+" and "+end+"");
        } else {
            sqlBeforeWhere.append(" WHERE ").append("temp.rn between "+start+" and "+end+"");
        }
        log.info(sqlBeforeWhere.toString());
        return sqlBeforeWhere.toString();
    }

    protected String toLimitAndOrderSelectByPg(String selectFieldList, Pagination pagination) {

        int start = pagination.getPageSize() * (pagination.getCurrentPage()-1);
        int end = pagination.getPageSize() * pagination.getCurrentPage();

        StringBuffer sqlBeforeWhere = new StringBuffer("select " + selectFieldList + " from "+SQLWrapper.wrapTableName(dbTableName)+"");
        String whereCause = toWhereSQL();
        if (whereCause.length() > 0) {
            sqlBeforeWhere.append(" WHERE ").append(whereCause);
        }
        sqlBeforeWhere.append(" limit "+end+" offset "+start+"");
        log.info(sqlBeforeWhere.toString());
        return sqlBeforeWhere.toString();
    }



    private String parseLimit(Pagination pagination){

        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(" ");
        stringBuffer.append("limit");
        stringBuffer.append(" ");
        stringBuffer.append((pagination.getCurrentPage()-1)*pagination.getPageSize());
        stringBuffer.append(",");
        stringBuffer.append(pagination.getPageSize());
        return stringBuffer.toString();
    }

	public String toSelectSQL() {
		return toSelectSQL("*");
	}

    public String toLimitAndOrderSelectSQL(Pagination pagination) {
        switch (pagination.getDbType().toLowerCase()) {
            case "mysql":
                return toLimitAndOrderSelectSQL("*",pagination);


            case "sqlserver":
                return toLimitAndOrderSelectSQLBySqlServer("*",pagination);

            case "oracle":
                return toLimitAndOrderSelectByOracle("*",pagination);

            case "postgresql":
                return toLimitAndOrderSelectByPg("*",pagination);

            default:
                return toLimitAndOrderSelectSQL("*",pagination);
        }


    }

	public String toWhereSQL() {
		return toWhereSQL(null);
	}

	public String toWhereSQL(final String tableAlias) {

        StringBuffer sqlBeforeWhere = new StringBuffer();

        StringBuffer sqlKeysCondi = new StringBuffer();
        int keysAdded = genSQLEachField(tableAlias, dbKeys, " and ", sqlKeysCondi, "=", sqlKeysCondi);

        StringBuffer sqlFieldsCondi = new StringBuffer();
        int fieldsAdded = genSQLEachField(tableAlias, dbFields, " and ", sqlFieldsCondi, "=", sqlFieldsCondi);

        if (keysAdded > 0 && sqlKeysCondi.length() > 0) { // primaryKey = value
        	sqlBeforeWhere.append(sqlKeysCondi);
        }

        if (keysAdded > 0 && fieldsAdded > 0 ) {
            sqlBeforeWhere.append(" and ");
        }

        if (fieldsAdded > 0 && sqlFieldsCondi.length() > 0) { // somefield = value
        	sqlBeforeWhere.append(sqlFieldsCondi);
        }

        return sqlBeforeWhere.toString();
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
    private static int genSQLEachField(final String tableAlias, final Map<String, Object> dbFields, final String catFields,
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
            if (tableAlias != null) {
            	sqlFieldName.append(tableAlias).append(".");
            }
            //TODO pg中不予许你出现''
            sqlFieldName.append(e.getKey());
//            sqlFieldName.append("`").append(e.getKey()).append("`");
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
    
}
