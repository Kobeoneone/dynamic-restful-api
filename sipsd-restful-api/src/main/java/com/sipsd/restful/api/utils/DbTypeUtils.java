package com.sipsd.restful.api.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.sipsd.restful.api.biz.JdbcConnectionPool;
import com.sipsd.restful.api.mode.JDBC.DbType;

import java.util.Map;

/**
 * @Auther: gaoqiang
 * @Date: 2019-06-22 20:00
 * @Description:
 */
public class DbTypeUtils
{
    public static DbType getDbType(String dbType)
    {
        switch (dbType.toLowerCase()) {
            case "mysql":
                return DbType.mysql;

            case "sqlserver":
                return DbType.sqlserver;

            case "oracle":
                return DbType.oracle;

            case "postgresql":
                return DbType.postgresql;

            default:
                return DbType.mysql;
        }
    }

    public static DruidDataSource getDruidDataSource(Map map) throws  Exception
    {
        String dbType = map.get("dbType").toString();
        switch (dbType.toLowerCase()) {
            case "mysql":
                return  JdbcConnectionPool.getMysqlConnection(map);

            case "sqlserver":
                return  JdbcConnectionPool.getSqlConnection(map);

            case "oracle":
                return  JdbcConnectionPool.getOracleConnection(map);

            case "postgresql":
                return  JdbcConnectionPool.getPgConnection(map);

            default:
                return  JdbcConnectionPool.getMysqlConnection(map);
        }
    }

}
