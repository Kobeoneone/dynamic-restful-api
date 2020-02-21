/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util;

import java.lang.reflect.Method;

/**
 * POJO 反射操作器
 * 
 * @title PojoReflection
 * @description POJO 反射操作 （异常都转译成运行时异常）
 * @author liwei39
 * @date 2014-7-12
 * @version 1.0
 */
public class PojoReflection implements PojoOperator
{

    /**
     * 调用不含参的构造方法，构造一个新对象
     * 
     * @param pojoClass 构造一个新对象所参照的类
     * @return POJO类名的一个新实例
     * */
    @Override
    public Object newInstance(Class<?> pojoClass) {
        try {
            return pojoClass.newInstance();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) { // IllegalAccessException, InvocationTargetException
            throw new RuntimeException(e);
        }
    }

    /**
     * 在POJO上执行属性的Getter方法
     * 
     * @param pojoBean 执行Getter方法的POJO对象
     * @param attriName 执行Getter方法的属性名称
     * @return 执行Getter方法获得的值
     * */
    @Override
    public Object doGetter(Object pojoBean, String attriName) {
        try {
            Method method = methodGetter(pojoBean.getClass(), attriName);
            return method.invoke(pojoBean);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) { // IllegalAccessException, InvocationTargetException
            throw new RuntimeException(e);
        }
    }

    /**
     * 在POJO上执行属性的Setter方法
     * 
     * @param pojoBean 执行Setter方法的POJO对象
     * @param attriName 执行Setter方法的属性名称
     * @param attriValue 执行Setter方法的属性取值
     * */
    @Override
    public void doSetter(Object pojoBean, String attriName, Object attriValue) {
        try {
            Class<?> attriType = attriType(pojoBean.getClass(), attriName);
            Method method = methodSetter(pojoBean.getClass(), attriName, attriType);
            method.invoke(pojoBean, attriValue);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) { // IllegalAccessException, InvocationTargetException
            throw new RuntimeException(e);
        }
    }

    /**
     * POJO类执行属性对应的Getter方法返回值的数据类型
     * 
     * @param pojoClass POJO类
     * @param attriName 属性名称
     * @return 属性对应的Getter方法返回值的数据类型
     * */
    @Override
    public Class<?> attriType(Class<?> pojoClass, String attriName) {
        try {
            Method method = methodGetter(pojoClass, attriName);
            return method.getReturnType();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) { // IllegalAccessException, InvocationTargetException
            throw new RuntimeException(e);
        }
    }

    private static Method methodGetter(Class<?> pojoClass, String attriName) throws SecurityException,
            NoSuchMethodException {
        Method method = null;
        NoSuchMethodException noSuchMethodException = null;
        try {
            method = pojoClass.getMethod(assembMethodName("get", attriName));
        } catch (NoSuchMethodException nsme) {
            noSuchMethodException = nsme;
        }
        if (method != null) {
            return method;
        }
        method = pojoClass.getMethod(assembMethodName("is", attriName));
        if (method != null && Boolean.class.equals(method.getReturnType())) {
            return method;
        }
        throw noSuchMethodException;
    }

    private static Method methodSetter(Class<?> pojoClass, String attriName, Class<?> attriType)
            throws SecurityException, NoSuchMethodException {
        return pojoClass.getMethod(assembMethodName("set", attriName), attriType);
    }

    private static String assembMethodName(String prefix, String attriName) {
        if (attriName.length() <= 1) {
            return prefix + attriName.toUpperCase();
        } else {
            return prefix + (attriName.substring(0, 1).toUpperCase() + attriName.substring(1));
        }
    }

}
