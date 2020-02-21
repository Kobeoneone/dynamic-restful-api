package com.sipsd.restful.api.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.CRUDDaoSupport;

import java.util.Map;

public class DaoFactory
{
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    public CRUDDaoSupport<Object> getDao(Map map)
    {
        DruidDataSource dataSource = (DruidDataSource) map.get("resource");
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(dataSource);
        return dao;
    }
}
