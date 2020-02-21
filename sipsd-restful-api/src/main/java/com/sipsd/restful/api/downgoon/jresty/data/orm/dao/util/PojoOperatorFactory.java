/**
 * Copyright (C) 2014 Baidu, Inc. All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util;

/**
 * POJO操作器的工厂：决定是否采用带CACHE的POJO操作器
 * 
 * @title PojoOperatorFactory
 * @description POJO操作器的工厂：决定是否采用带CACHE的POJO操作器
 * @author liwei39
 * @date 2014-10-12
 * @version 1.0
 */
public class PojoOperatorFactory {

    private static PojoOperator pojoOperator = new PojoReflectionCached(new PojoReflection());

    /**
     * 决定是否采用带CACHE的POJO操作器
     * 
     * @return 返回POJO操作器
     * */
    public static PojoOperator getPojoOperator() {
        return pojoOperator;
    }

}
