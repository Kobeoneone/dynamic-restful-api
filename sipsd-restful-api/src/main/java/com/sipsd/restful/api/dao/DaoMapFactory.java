package com.sipsd.restful.api.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.sipsd.restful.api.biz.ApplicationContextUtil;
import com.sipsd.restful.api.biz.JdbcConnectionPool;
import com.sipsd.restful.api.biz.appConfig;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.CRUDDaoSupport;
import com.sipsd.restful.api.utils.DbTypeUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

@CommonsLog
public class DaoMapFactory
{
    public static Map getMapDao(String key,String tableName)
    {
        ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
        appConfig config = (appConfig) applicationContext.getBean("appConfig");
        try
        {
            //先连接默认的注册地址的数据库
            Map map = new HashMap();
            //中枢数据库默认别名为default
            map.put("key", "defaultKey");
            map.put("url", config.getUrl());
            map.put("userName", config.getUserName());
            map.put("pwd", config.getPwd());
            //获取德鲁数据源池中的数据源
            DruidDataSource druidDataSource = JdbcConnectionPool.getMysqlConnection(map);
            CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
            dao.setDataSource(druidDataSource);
            //设置sql，根据注册的key来查询到具体需要自动化接口的数据库
            String sql = "select * from " + config.getTableInfo() + " where Name = '" + key + "'";

            map = dao.queryForMap(sql);

            if (map.isEmpty())
            {
                log.error("查询的sql语句是:" + sql + "------结果:无数据");
                throw new IllegalStateException("查询的sql语句是:" + sql + "------结果:无数据");
            }

            //将表中的service_key当作alias
            Map bs = new HashMap();
            bs.put("key", map.get("Name").toString());
            bs.put("userName", map.get("user_id").toString());
            bs.put("url", map.get("ConnStr2").toString());
            bs.put("pwd", map.get("pwd").toString());
            //判断是哪个数据库类型
            String dbType = DbTypeUtils.getDbType(map.get("DBType").toString()).name();
            bs.put("dbType", dbType);

            //根据map中dbType类型来确定传入何种数据源
            druidDataSource = DbTypeUtils.getDruidDataSource(bs);
            bs.put("resource", druidDataSource);
            bs.put("tableName", tableName);
            return bs;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static DruidDataSource getTableList(String key)
    {
        ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
        appConfig config = (appConfig) applicationContext.getBean("appConfig");
        try
        {
            //先连接默认的注册地址的数据库
            Map map = new HashMap();
            //中枢数据库默认别名为default
            map.put("key", "defaultKey");
            map.put("url", config.getUrl());
            map.put("userName", config.getUserName());
            map.put("pwd", config.getPwd());
            //获取德鲁数据源池中的数据源
            DruidDataSource druidDataSource = JdbcConnectionPool.getMysqlConnection(map);
            CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
            dao.setDataSource(druidDataSource);
            //设置sql，根据注册的key来查询到具体需要自动化接口的数据库
            String sql = "select * from " + config.getTableInfo() + " where Name = '" + key + "'";

            map = dao.queryForMap(sql);

            if (map.isEmpty())
            {
                log.error("查询的sql语句是:" + sql + "------结果:无数据");
                throw new IllegalStateException("查询的sql语句是:" + sql + "------结果:无数据");
            }

            //将表中的service_key当作alias
            Map bs = new HashMap();
            bs.put("key", map.get("Name").toString());
            bs.put("userName", map.get("user_id").toString());
            bs.put("url", map.get("ConnStr2").toString());
            bs.put("pwd", map.get("pwd").toString());
            //判断是哪个数据库类型
            String dbType = DbTypeUtils.getDbType(map.get("DBType").toString()).name();
            bs.put("dbType", dbType);

            //根据map中dbType类型来确定传入何种数据源
            druidDataSource = DbTypeUtils.getDruidDataSource(bs);
            return druidDataSource;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }
    }
}
