package com.sipsd.restful.api.biz;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.apachecommons.CommonsLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: gaoqiang
 * @Date: 2019-06-24 10:44
 * @Description:
 */
@CommonsLog
public class JdbcConnectionPool
{

    //TODO 该map可以用redis缓存替代
    public static ConcurrentHashMap<String, DruidDataSource> connectionMap = new ConcurrentHashMap<String, DruidDataSource>();


    public static DruidDataSource getMysqlConnection(Map map) throws  Exception
    {
        if (connectionMap.get(map.get("key")) != null)
        {
            return connectionMap.get(map.get("key"));
        }

        DruidDataSource druidDataSource = new DruidDataSource();
        //1,创建Druid连接池对象
        //2,为数据库添加配置文件
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        String url = map.get("url").toString();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(map.get("userName").toString());
        druidDataSource.setPassword(map.get("pwd").toString());
        //配置初始化大小、最小、最大
        druidDataSource.setInitialSize(10);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(20);
        //连接泄漏监测
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(1);
        //配置获取连接等待超时的时间
        druidDataSource.setMaxWait(20000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        druidDataSource.setValidationQuery("SELECT 'x'");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(true);
        druidDataSource.setFilters("stat,wall");
        druidDataSource.setKeepAlive(true);
        druidDataSource.setMinEvictableIdleTimeMillis(1800000);
        connectionMap.put(map.get("key").toString(), druidDataSource);
        return druidDataSource;
    }


    public static DruidDataSource getSqlConnection(Map map) throws  Exception
    {
        if(connectionMap.get(map.get("key")) !=null)
        {
            return connectionMap.get(map.get("key"));
        }

        DruidDataSource druidDataSource = new DruidDataSource();
        //1,创建Druid连接池对象
        //2,为数据库添加配置文件
        druidDataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = map.get("url").toString();
        druidDataSource.setUrl(String.format(url));
        druidDataSource.setUsername(map.get("userName").toString());
        druidDataSource.setPassword(map.get("pwd").toString());
        //配置初始化大小、最小、最大
        druidDataSource.setInitialSize(10);
        druidDataSource.setMinIdle(5);
        druidDataSource.setMaxActive(20);
        //连接泄漏监测
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(1);
        //配置获取连接等待超时的时间
        druidDataSource.setMaxWait(20000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        druidDataSource.setValidationQuery("SELECT 1");
        druidDataSource.setFilters("stat,wall");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(true);
        druidDataSource.setKeepAlive(true);
        druidDataSource.setMinEvictableIdleTimeMillis(1800000);
        connectionMap.put(map.get("key").toString(),druidDataSource);
        return  druidDataSource;
    }

    public static DruidDataSource getOracleConnection(Map map) throws Exception
    {
        if(connectionMap.get(map.get("key")) !=null)
        {
            return connectionMap.get(map.get("key"));
        }

        DruidDataSource druidDataSource = new DruidDataSource();
        //1,创建Druid连接池对象
        //2,为数据库添加配置文件
        druidDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        // String url = String.format("jdbc:oracle:thin:@"+map.get("ip").toString()+":"+map.get("name").toString()+"");
        String url = map.get("url").toString();
        druidDataSource.setUrl(String.format(url));
        druidDataSource.setUsername(map.get("userName").toString());
        druidDataSource.setPassword(map.get("pwd").toString());
        //配置初始化大小、最小、最大
        druidDataSource.setInitialSize(10);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(20);
        //连接泄漏监测
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(1);
        //配置获取连接等待超时的时间
        druidDataSource.setMaxWait(20000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(true);
        druidDataSource.setFilters("stat,wall");
        druidDataSource.setKeepAlive(true);
        druidDataSource.setMinEvictableIdleTimeMillis(1800000);
        connectionMap.put(map.get("key").toString(),druidDataSource);
        return  druidDataSource;
    }

    public static DruidDataSource getPgConnection(Map map) throws  Exception
    {
        if(connectionMap.get(map.get("key")) !=null)
        {
            return connectionMap.get(map.get("key"));
        }

        DruidDataSource druidDataSource = new DruidDataSource();
        //1,创建Druid连接池对象
        //2,为数据库添加配置文件
        druidDataSource.setDriverClassName("org.postgresql.Driver");
        String url = map.get("url").toString();
        druidDataSource.setUrl(String.format(url));
        druidDataSource.setUsername(map.get("userName").toString());
        druidDataSource.setPassword(map.get("pwd").toString());
        //配置初始化大小、最小、最大
        druidDataSource.setInitialSize(10);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(20);
        //连接泄漏监测
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(1);
        //配置获取连接等待超时的时间
        druidDataSource.setMaxWait(20000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(true);
        druidDataSource.setFilters("stat,wall");
        druidDataSource.setKeepAlive(true);
        druidDataSource.setMinEvictableIdleTimeMillis(1800000);
        connectionMap.put(map.get("key").toString(),druidDataSource);
        return  druidDataSource;
    }
}
