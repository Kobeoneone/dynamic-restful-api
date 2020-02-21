/**
 * Copyright (C) 2014 Baidu, Inc. All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util;

/**
 * POJO 常用操作器
 * 
 * @title PojoOperator
 * @description POJO 常用操作：Getter/Setter和构造新对象
 * @author liwei39
 * @date 2014-7-12
 * @version 1.0
 */
public interface PojoOperator {

    /**
     * 调用不含参的构造方法，构造一个新对象
     * 
     * @param pojoClass 构造一个新对象所参照的类
     * @return POJO类名的一个新实例
     * */
    Object newInstance(Class<?> pojoClass);

    /**
     * 在POJO上执行属性的Getter方法
     * 
     * @param pojoBean 执行Getter方法的POJO对象
     * @param attriName 执行Getter方法的属性名称
     * @return 执行Getter方法获得的值
     * */
    Object doGetter(Object pojoBean, String attriName);

    /**
     * 在POJO上执行属性的Setter方法
     * 
     * @param pojoBean 执行Setter方法的POJO对象
     * @param attriName 执行Setter方法的属性名称
     * @param attriValue 执行Setter方法的属性取值
     * */
    void doSetter(Object pojoBean, String attriName, Object attriValue);

    /**
     * POJO类执行属性对应的Getter方法返回值的数据类型
     * 
     * @param pojoClass POJO类
     * @param attriName 属性名称
     * @return 属性对应的Getter方法返回值的数据类型
     * */
    Class<?> attriType(Class<?> pojoClass, String attriName);

}
