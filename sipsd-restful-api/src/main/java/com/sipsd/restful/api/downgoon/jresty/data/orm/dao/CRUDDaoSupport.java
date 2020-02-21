/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao;


import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql.*;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util.PojoOperatorFactory;
import com.sipsd.restful.api.mode.JDBC.Pagination;
import com.sipsd.restful.api.mode.JDBC.PaginationResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @title CRUDDaoSupport
 * @description 提供基本的CRUD操作<br/>
 *              <p/>
 *              当在关系型中建好一张新表后，只需要创建与之对应的POJO，然后直接继承本类，则不用写一行代码就能具备CRUD操作。
 * @author liwei39
 * @date 2014-7-11
 * @version 1.0
 */

public class CRUDDaoSupport<T> extends JdbcDaoSupport implements CRUDDao<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CRUDDaoSupport.class);
    private SQLGenerator sqlGenerator = new SQLGenerator();

    @Override
    public int saveObject(final T t) {
        final String sqlInsert = sqlGenerator.genSQLInsert(t);
        LOGGER.debug("SQL: {}", sqlInsert);
        
        ORFieldMapping fieldMapping = new ORMBuilderFacade(t, false).buildFieldMapping(); // TODO Cached
        
        String autoIncrAttri = fieldMapping.getAutoIncrementAttri();
        if (autoIncrAttri == null || PojoOperatorFactory.getPojoOperator().doGetter(t, autoIncrAttri) != null) {
        	// 有自增长字段 并且 没有设置数值，才需要返回自增长数值；否则，不用。
        	return getJdbcTemplate().update(sqlInsert);
        }
        
        // return auto_increment id
        // REFER: http://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-last-insert-id.html
        final AtomicInteger rowsAffect = new AtomicInteger(); // output argument 
        Long autoIncrValue = getJdbcTemplate().execute(new StatementCallback<Long>() {
        	@Override
			public Long doInStatement(Statement stmt) throws SQLException, DataAccessException
            {
        		int rows = stmt.executeUpdate(sqlInsert, Statement.RETURN_GENERATED_KEYS);
        		rowsAffect.set(rows);
				ResultSet rs = stmt.getGeneratedKeys(); // ResultSet ColumnName is SCOPE_IDENTITY()
				if (rs.next()) { // hasNext
					return rs.getLong(1); // auto_increment value
				} else {
					throw new IllegalArgumentException(String.format("AUTO_INCREMENT not supported in Table %s", 
							t.getClass().getSimpleName()));
				}
			}
        });
        
        // set auto_increment id from db to java bean attribute
        String autoIncrColum = fieldMapping.getAutoIncrementColum();
        Class<?> autoIncrType = fieldMapping.getDbFieldJavaType().get(autoIncrColum);
        if (Long.class.equals(autoIncrType)) {
        	PojoOperatorFactory.getPojoOperator().doSetter(t, autoIncrAttri, autoIncrValue.longValue());
        } else if (Integer.class.equals(autoIncrType)) {
        	PojoOperatorFactory.getPojoOperator().doSetter(t, autoIncrAttri, autoIncrValue.intValue());
        } else if (Short.class.equals(autoIncrType)) {
        	PojoOperatorFactory.getPojoOperator().doSetter(t, autoIncrAttri, autoIncrValue.shortValue());
        } else {
        	throw new IllegalArgumentException(String.format("AUTO_INCREMENT type %s not supported in Table %s", 
					autoIncrType.getSimpleName(), t.getClass().getSimpleName()));
        }
        
        return rowsAffect.get();
        
    }
    

    @Override
    public int updateObject(T t) {
        return updateObject(t, true);
    }

    @Override
    public int updateObject(T t, String...whereCondiFields) {
    	return updateObject(t, true, whereCondiFields);
    }
    
    @Override
	public int updateObject(T t, boolean affectExpected) {
    	String sqlUpdate = sqlGenerator.genSQLUpdate(t);
    	return doUpdateSql(sqlUpdate, affectExpected);
    }


	@Override
	public int updateObject(T t, boolean affectExpected, String... whereCondiFields) {
		 String sqlUpdate = sqlGenerator.genSQLUpdate(t, whereCondiFields);
		 return doUpdateSql(sqlUpdate, affectExpected);
	}

	private int doUpdateSql(String sqlUpdate, boolean affectExpected) {
		return doUpdateSql(sqlUpdate, affectExpected, false);
	}
	
	private int doUpdateSql(String sqlUpdate, boolean affectExpected, boolean isDelete) {
		LOGGER.debug("SQL: {}", sqlUpdate);
		int rows = getJdbcTemplate().update(sqlUpdate);
	    if (affectExpected && rows <= 0) {
	    	throw new IllegalStateException("no rows affected for " 
	    			+ (isDelete ? "delete" : "update") +": " + sqlUpdate);
	    }
	    return rows;
	}

	@Override
    public T findObject(T t) {
        String sqlSelect = sqlGenerator.genSQLSelect(t);
        LOGGER.debug("SQL: {}", sqlSelect);
        List<T> list = getJdbcTemplate().query(sqlSelect, new ORMRowMapper<T>(t.getClass()));
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<T> findObjectsLimit(T t, int offset, int length) {
        return findObjects(t, HQL.limit(offset, length));
    }

//    @Override
//    public List<T> findObjectsOrderByLimit(T t, int offset, int length, String...orderByFields) {
//        return findObjects(t, HQL.orderBy(orderByFields), HQL.limit(offset, length));
//    }

    @Override
    public List<T> findObjectsOrderBy(T t, String...orderByFields) {
        return findObjects(t, HQL.orderBy(orderByFields));
    }

    @Override
    public List<T> findObjects(T t) {
        String sqlSelect = sqlGenerator.genSQLSelect(t);
        LOGGER.debug("SQL: {}", sqlSelect);
        return getJdbcTemplate().query(sqlSelect, new ORMRowMapper<T>(t.getClass()));
    }


    @Override
    public <T> T findObjects(String sql, RowMapper<T> rowMapper){
        return getJdbcTemplate().queryForObject(sql,rowMapper);
    }


    @Override
    public Map<String, Object> queryForMap(String sql)
    {
        return getJdbcTemplate().queryForMap(sql);
    }



    @Override
    public <T> List<T> findObjectsOrderByLimit(T t, Pagination pagination)
    {
        String sqlSelect = sqlGenerator.genLimitAndOrderSQLSelect(t,pagination);
        PaginationResult<T> result=new PaginationResult<T>();
        LOGGER.debug("SQL: {}", sqlSelect);
        return getJdbcTemplate().query(sqlSelect, new ORMRowMapper<T>(t.getClass()));
    }

    @Override
    public List<T> findObjects(T t, HQL...hqls) {
        String sqlSelect = sqlGenerator.genSQLSelect(t, hqls);
        LOGGER.debug("SQL: {}", sqlSelect);
        return getJdbcTemplate().query(sqlSelect, new ORMRowMapper<T>(t.getClass()));
    }

    @Override
    public List<T> findObjects(String rawSQL, Class<T> mapClass) {
        LOGGER.debug("rawSQL: {}", rawSQL);
        return getJdbcTemplate().query(rawSQL, new ORMRowMapper<T>(mapClass));
    }

    @Override
    public <K> List<K> findObjectsAny(String rawSQL, Class<K> mapClass) {
        LOGGER.debug("rawSQL: {}", rawSQL);
        return getJdbcTemplate().query(rawSQL, new ORMRowMapper<K>(mapClass, false, true));
    }


	@Override
	public int removeObjects(T t) {
		return removeObjects(t, false);
	}

    @Override
    public int removeObjects(T t, boolean affectExpected) {
        String sqlDelete = sqlGenerator.genSQLDelete(t);
        LOGGER.debug("SQL: {}", sqlDelete);
        return doUpdateSql(sqlDelete, affectExpected, true);
    }

    public List findObjectsForList(String sql) {
        return getJdbcTemplate().queryForList(sql);
    }


    public int removeObjects(String sql) {
        return getJdbcTemplate().update(sql);
    }

    public int findObjectsForObject(String sql,String params) {
        if(StringUtils.isEmpty(params))
        {
            return getJdbcTemplate().queryForObject(sql,new Object[] {}, Integer.class);
        }
        else
        {
            return getJdbcTemplate().queryForObject(sql,new Object[] { params }, Integer.class);
        }

    }


    public DatabaseMetaData getMetaDataForList() throws  Exception{
        return getJdbcTemplate().getDataSource().getConnection().getMetaData();
    }


//	@Override
//	public Long queryForLong(String rawSQL) {
//		LOGGER.debug("SQL: {}", rawSQL);
//		return getJdbcTemplate().queryForLong(rawSQL);
//	}
//
//
//	@Override
//	public Integer queryForInt(String rawSQL) {
//		LOGGER.debug("SQL: {}", rawSQL);
//		return getJdbcTemplate().queryForInt(rawSQL);
//	}


	
}
