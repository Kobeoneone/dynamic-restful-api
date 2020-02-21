package com.sipsd.restful.api.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.sipsd.restful.api.biz.JdbcConnectionPool;
import com.sipsd.restful.api.downgoon.jresty.commons.utils.concurrent.ConcurrentResourceContainer;
import com.sipsd.restful.api.downgoon.jresty.commons.utils.concurrent.ResourceLifecycle;

import java.util.Map;

public class DataSourceFactory {
	
	private ConcurrentResourceContainer<DruidDataSource> container = new ConcurrentResourceContainer<DruidDataSource>(
			
			new ResourceLifecycle<DruidDataSource>() {

				@Override
				public DruidDataSource buildResource(Map map) throws Exception {
					DruidDataSource dataSource = JdbcConnectionPool.getMysqlConnection(map);
					return dataSource;
				}

				@Override
				public void destoryResource(Map map, DruidDataSource resource) throws Exception
				{

				}
			});
	
	
	public DruidDataSource getDataSource(Map map) {
		DruidDataSource ds = null;
		try {
			ds = container.getResource(map);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (ds == null) {
			throw new IllegalStateException("database not found: " + map);
		}
		return ds;
	}

}
