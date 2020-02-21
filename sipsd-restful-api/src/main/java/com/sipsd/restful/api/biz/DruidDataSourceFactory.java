package com.sipsd.restful.api.biz;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @Auther: gaoqiang
 * @Date: 2019-06-24 14:24
 * @Description:
 */
public class DruidDataSourceFactory
{
    private static DruidDataSource druidDataSource;

    public static DruidDataSource getDruidDataSource() {
        if (druidDataSource == null) {
            druidDataSource = new DruidDataSource();
        }
        return druidDataSource;
    }
}
