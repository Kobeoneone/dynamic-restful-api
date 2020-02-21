/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @title ORMTable
 * @description TODO 
 * @author liwei39
 * @date 2014-7-10
 * @version 1.0
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ORMTable {

    /**
     * @return  对应ORM的数据库表名称
     * */
    public String name();
}
