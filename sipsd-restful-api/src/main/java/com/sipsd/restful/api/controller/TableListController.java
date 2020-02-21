package com.sipsd.restful.api.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.sipsd.restful.api.dao.DaoMapFactory;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.CRUDDaoSupport;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util.R;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Auther: gaoqiang
 * @Date: 2020-02-19 13:32
 * @Description:
 */
@CommonsLog
@RestController
@RequestMapping("vertx/tableList")
public class TableListController
{
    /**
     * mysql
     */
    @RequestMapping("/mysql")
    public R mysqlList(@RequestParam String key){
        DruidDataSource druidDataSource = DaoMapFactory.getTableList(key);
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);
        String sql = "select table_name as tablename from information_schema.tables where table_schema='"+key+"'";
        List<Object> rows = dao.findObjectsForList(sql);
        return  R.ok().put("data",rows);
    }


    /**
     * mysql
     */
    @RequestMapping("/pg")
    public R pgList(@RequestParam String key,@RequestParam String schema){
        DruidDataSource druidDataSource = DaoMapFactory.getTableList(key);
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);
        String sql = "select tablename from pg_tables where schemaname='"+schema+"'";
        List<Object> rows = dao.findObjectsForList(sql);
        return  R.ok().put("data",rows);
    }

    /**
     * oracle
     */
    @RequestMapping("/oracle")
    public R oraclelList(@RequestParam String key){
        DruidDataSource druidDataSource = DaoMapFactory.getTableList(key);
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);
        String sql = "select table_name as tablename from user_tables";
        List<LinkedCaseInsensitiveMap> rows = dao.findObjectsForList(sql);
        List<Map> newrows = new ArrayList<>();
        Map map = null;
        for(LinkedCaseInsensitiveMap item:rows)
        {
            map = new HashMap();
            map.put("tablename",item.get("TABLENAME").toString());
            newrows.add(map);
        }
        return  R.ok().put("data",newrows);
    }


    /**
     * oracle
     */
    @RequestMapping("/sql")
    public R sqllList(@RequestParam String key){
        DruidDataSource druidDataSource = DaoMapFactory.getTableList(key);
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);
        String sql = " SELECT Name as tablename FROM SysObjects Where XType='U' ORDER BY Name";
        List<Object> rows = dao.findObjectsForList(sql);
        return  R.ok().put("data",rows);
    }
}
