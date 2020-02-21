/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao;


import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql.HQL;
import com.sipsd.restful.api.mode.JDBC.Pagination;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;

/**
 * 数据库记录的增删改查操作接口
 * 
 * @title CRUDDao
 * @description 数据库记录的增删改查操作接口
 * @author liwei39
 * @date 2014-7-11
 * @version 1.0
 */
public interface CRUDDao<T> {

    /**
     * 保存一个对象（所有非空属性都会被INSERT到数据库记录）
     * 
     * @param t 待保存的POJO对象
     * @return 保存成功，返回影响行数；失败，则运行时异常。
     * */
    int saveObject(T t);

    /**
     * 更新操作：以表的主键为where条件，所有非空字段都会在set字句中更新
     * 
     * @param t 待更新的POJO对象
     * @return 返回更新的行数
     * */
    int updateObject(T t);
    
    /**
     * @param	affectExpected	影响记录数低于1的，报异常。
     * */
    int updateObject(T t, boolean affectExpected);

    /**
     * 更新操作：以指定字段列表为where条件（不再以表的主键作为条件），所有非空字段都会在set字句中更新。 <br/>
     * <p/>
     * 例如： updateObject(student,"location","age"); 则SQL语句为： UPDATE student_table set name=$student.name where
     * location=$student.location and age=$student.age
     * 
     * @param t 待更新的POJO对象
     * @param whereCondiFields WHERE条件的字段列表，字段间关系是与的关系。
     * @return 返回更新的行数
     * */
    int updateObject(T t, String... whereCondiFields);

    /**
     * @param	affectExpected	影响记录数低于1的，报异常。
     * */
    int updateObject(T t, boolean affectExpected, String... whereCondiFields);

    /**
     * 查询符合条件的对象列表 <br/>
     * <p/>
     * 例如：t是student对象，其中name和location字段非空，age字段为空，那么findObjects(student)的SQL是： SELECT * FROM name=$student.name and
     * location=$student.location
     *
     * @param t WHERE条件的对象，非空字段间是与关系
     * @return 返回所有满足条件的对象 查询操作：以T对象的非空字段为WHERE条件
     * */
    List<T> findObjects(T t);

    /**
     * 查询符合条件的对象列表，同时支持投影、排序和分页子句。
     *
     * @param t WHERE条件的对象，非空字段间是与关系
     * @param hqls 支持任意组合的HQL字句，比如：SELECT 字段投影, ORDER BY 字段排序 和 LIMIT 分页 查询操作：以T对象的非空字段为WHERE条件
     * @return 返回所有满足条件的对象
     * */
    List<T> findObjects(T t, HQL... hqls);

    /**
     * 支持直接写原生SQL，以应对诸如比较条件和范围条件
     *
     * @param rawSQL 原生SQL
     * @param tClass ORMapping的POJO类的空对象
     * @return 结果集
     * */
    List<T> findObjects(String rawSQL, Class<T> tClass);


    /**
     * 支持直接写原生SQL，以应对诸如比较条件和范围条件
     *
     * @param rawSQL 原生SQL，必须为SELECT
     * @param tClass 可以支持任何类型，可以不跟表对应起来。
     * @return 结果集
     * */
    public <K> List<K> findObjectsAny(String rawSQL, Class<K> mapClass);

    /***
     * 查询符合条件的对象列表，同时分页子句。
     *
     * @param t 以T对象的非空字段为WHERE条件
     * @param offset 分页起始地址，从0开始编号
     * @param length 分页的页大小，大于0的正整数
     * @return 返回所有满足条件的对象
     * */
    List<T> findObjectsLimit(T t, int offset, int length);


    /***
     * 查询符合条件的对象列表，同时分页和排序子句。
     *
     * @param t 以T对象的非空字段为WHERE条件
     * @param offset 分页起始地址，从0开始编号
     * @param length 分页的页大小，大于0的正整数
     * @param orderByFields 排序字段：字段名称既可以是JAVA对象的属性，也可以是DB的字段
     * @return 返回所有满足条件的对象
     * */
   // List<T> findObjectsOrderByLimit(T t, int offset, int length, String... orderByFields);

    <T> List<T> findObjectsOrderByLimit(T t, Pagination pagination);

    /***
     * 查询符合条件的对象列表，同时排序子句。
     *
     * @param t 以T对象的非空字段为WHERE条件
     * @param orderByFields 排序字段：字段名称既可以是JAVA对象的属性，也可以是DB的字段
     * @return 返回所有满足条件的对象
     * */
    List<T> findObjectsOrderBy(T t, String... orderByFields);

    /**
     * 最多只返回一个对象，即使有多个 查询操作：以T对象的非空字段为WHERE条件
     * 
     * @param t 以T对象的非空字段为WHERE条件
     * @return 满足条件的对象，如果有多个满足条件，则只返回第一个。
     * */
    T findObject(T t);

    public <T> T findObjects(String sql, RowMapper<T> rowMapper) throws DataAccessException;

    /**
     * 删除记录：以t为动态条件
     * @return	影响的行数。
     * */
    int removeObjects(T t);
    
    public int removeObjects(T t, boolean affectExpected);
    
    /** 执行类似 COUNT(*) 的查询功能 */
//    public Long queryForLong(String rawSQL);
////
////    public Integer queryForInt(String rawSQL);


    public Map<String, Object> queryForMap(String sql);
}
