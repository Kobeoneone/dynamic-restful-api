package com.sipsd.restful.api.mode;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: gaoqiang
 * @Date: 2020-01-16 14:03
 * @Description:
 */
@Data
public class SystemBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    /*
     * 数据库ip地址
     */
    private  String id;

    /*
     * 编码
     */
    private  String code;


    /*
     * 标识符号（1：有效；0：失效）
     */
    private  Integer flag;


    /*
     * 状态符号（1：正常；0：删除）
     */
    private  Integer status;


    /*
     * key
     */
    private  String name;


    /*
     * 数据库ip地址
     */
    private  String ip;


    /*
     * 数据库类型（SqlServer、Oracle）
     */
    private  String dbType;


    /*
     * 数据库别名
     */
    private  String alias;


    /*
     * 数据库版本
     */
    private  String version;


    /*
     * 数据库地址
     */
    private  String source;


    /*
     * 描述
     */
    private  String remark;


    /*
     * 数据库连接字符串
     */
    private  String connStr;


    /*
     * 创建人编号
     */
    private  Integer createId;


    /*
     * 创建时间
     */
    private Date createTime;


    /*
     * 修改人编号
     */
    private  Integer updateId;


    /*
     * 修改时间
     */
    private Date updateTime;

    /*
     * 区域
     */
    private  String region;


    /*
     * 区域id
     */
    private  Integer areaId;

    /*
     * 编目ID
     */
    private  Integer catalogueId;


    /*
     * 数据库名
     */
    private  String dbName;

    /*
     * 用户名
     */
    private  String userName;

    /*
     * 密码
     */
    private  String pwd;

    /*
     * 标签
     */
    private  String tags;


}
