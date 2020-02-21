package com.sipsd.restful.api.mode.JDBC;

import com.alibaba.druid.pool.DruidDataSource;
import com.sipsd.restful.api.biz.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: gaoqiang
 * @Date: 2020-01-21 17:03
 * @Description:
 */
public class jdbcTest
{
    public static void main(String[] args)
    {
        // 1.加载驱动程序
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // 2.获得数据库链接
            Map map = new HashMap<>();
            map.put("key", "SIP_BDCDBNew");
            map.put("url", "jdbc:sqlserver://192.168.126.17:1433;DatabaseName=SCMDB_ZD");
            map.put("userName", "sa");
            map.put("pwd", "!QAZ2wsx");
            DruidDataSource druidDataSource = JdbcConnectionPool.getSqlConnection(map);
            Connection conn = druidDataSource.getConnection();
            // 3.通过数据库的连接操作数据库，实现增删改查（使用Statement类）
            //预编译
            String sql = "select * from Base_User";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            // 4.处理数据库的返回结果(使用ResultSet类)
            while (rs.next())
            {
                System.out.println(rs.getString("UserId") + " " + rs.getString("Account"));
            }

            // 关闭资源
            conn.close();
            rs.close();
            statement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
