package com.sipsd.restful.api.biz;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Auther: gaoqiang
 * @Date: 2019-06-23 10:24
 * @Description:
 */
@Component
@Configuration
@Getter
@Setter
public class appConfig
{
    @Value("${Pagination.pageNo}")
    public  Integer pageNo;

    @Value("${Pagination.pageSize}")
    public  Integer pageSize;

    @Value("${Pagination.tableInfo}")
    public  String tableInfo;

    @Value("${metdata.jdbcDriver}")
    public  String jdbcDriver;

    @Value("${metdata.url}")
    public  String url;

    @Value("${metdata.ip}")
    public  String ip;

    @Value("${metdata.dbName}")
    public  String dbName;

    @Value("${metdata.userName}")
    public  String userName;

    @Value("${metdata.pwd}")
    public  String pwd;

}
