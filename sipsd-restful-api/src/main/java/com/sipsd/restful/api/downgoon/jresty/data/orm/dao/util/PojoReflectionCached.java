/**
 * Copyright (C) 2014 Baidu, Inc. All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 带缓存的POJO 常用操作器
 * 
 * @title PojoReflectionCached
 * @description POJO 常用操作 （带缓存的）
 * @author liwei39
 * @date 2014-7-12
 * @version 1.0
 */
public class PojoReflectionCached implements PojoOperator
{

    private PojoOperator delegateOperator;

    private InnerCache attriValueCache = new MemInnerCache();

    private DualLock attriValueLock = new DualLock();

    private InnerCache attriTypeCache = new MemInnerCache();

    /**
     * 带缓存的POJO操作<br/>
     * <p/>
     * 设计时职责分离，把跟缓存无关的操作，委派给代理对象去执行；缓存操作，则本类负责。
     * 
     * @param delegateOperator 非缓存操作的委派对象（反射操作器）
     * */
    public PojoReflectionCached(PojoOperator delegateOperator) {
        super();
        this.delegateOperator = delegateOperator;
    }

    @Override
    public Object newInstance(Class<?> pojoClass) {
        return delegateOperator.newInstance(pojoClass); // NO Cache
    }

    @Override
    public Object doGetter(Object pojoBean, String attriName) {
        Object cachedValue = attriValueCache.get(pojoBean, attriName);
        if (cachedValue == null) {
            cachedValue = delegateOperator.doGetter(pojoBean, attriName);
            attriValueLock.getLock(pojoBean, attriName).writeLock().lock();
            try {
                attriValueCache.put(pojoBean, attriName, cachedValue);
            } finally {
                attriValueLock.getLock(pojoBean, attriName).writeLock().unlock();
            }
        }
        return cachedValue;

    }

    @Override
    public void doSetter(Object pojoBean, String attriName, Object attriValue) {
        attriValueLock.getLock(pojoBean, attriName).writeLock().lock();
        try {
            delegateOperator.doSetter(pojoBean, attriName, attriValue);
            attriValueCache.put(pojoBean, attriName, attriValue);
        } finally {
            attriValueLock.getLock(pojoBean, attriName).writeLock().unlock();
        }
    }

    @Override
    public Class<?> attriType(Class<?> pojoClass, String attriName) {
        Class<?> cachedType = (Class<?>) attriTypeCache.get(pojoClass, attriName);
        if (cachedType == null) {
            cachedType = delegateOperator.attriType(pojoClass, attriName);
            attriTypeCache.put(pojoClass, attriName, cachedType);
        }
        return cachedType;
    }

    /**
     * 二级Cache接口
     * 
     * @title InnerCache
     * @description 二级Cache
     * @author liwei39
     * @date 2014-7-12
     * @version 1.0
     */
    static interface InnerCache {

        /**
         * 二级缓存的GET操作
         * 
         * @param groupKey 分组KEY（第一层）
         * @param subKey 子KEY（第二层）
         * @return 返回 groupKey.subKey对应的VALUE
         * */
        Object get(Object groupKey, String subKey);

        /**
         * 二级缓存的PUT操作
         * 
         * @param groupKey 分组KEY（第一层）
         * @param subKey 子KEY（第二层）
         * @param 设置groupKey.subKey的VALUE
         * @return 返回 groupKey.subKey对应的VALUE
         * */
        Object put(Object groupKey, String subKey, Object value);

    }

    /** 线程安全 */
    private static class MemInnerCache implements InnerCache {
        private ConcurrentHashMap<Object, ConcurrentHashMap<String, Object>> dualMap =
                new ConcurrentHashMap<Object, ConcurrentHashMap<String, Object>>();

        @Override
        public Object get(Object groupKey, String subKey) {
            ConcurrentHashMap<String, Object> group = getGroup(groupKey);
            return group.get(subKey);
        }

        @Override
        public Object put(Object groupKey, String subKey, Object value) {
            ConcurrentHashMap<String, Object> group = getGroup(groupKey);
            if (value == null) {
                return group.remove(subKey);
            } else {
                return group.put(subKey, value);
            }
        }

        /**
         * 存在则返回已有的；否则，新建并返回。
         * */
        private ConcurrentHashMap<String, Object> getGroup(Object groupKey) {
            ConcurrentHashMap<String, Object> groupFound = dualMap.get(groupKey);
            if (groupFound != null) {
                return groupFound;
            }
            ConcurrentHashMap<String, Object> groupCreate = new ConcurrentHashMap<String, Object>();
            ConcurrentHashMap<String, Object> groupExisted = dualMap.putIfAbsent(groupKey, groupCreate);
            if (groupExisted != null) {
                return groupExisted;
            }
            return groupCreate;
        }

    }

    /**
     * 二级HASH锁：控制到第二层的粒度
     * 
     * @title DualLock
     * @description 控制到第二层的粒度
     * @author liwei39
     * @date 2014-7-12
     * @version 1.0
     */
    private static class DualLock {

        private ConcurrentHashMap<Object, ConcurrentHashMap<String, ReadWriteLock>> dualLockMap =
                new ConcurrentHashMap<Object, ConcurrentHashMap<String, ReadWriteLock>>();

        public ReadWriteLock getLock(Object groupKey, String subKey) {
            ConcurrentHashMap<String, ReadWriteLock> group = getGroup(groupKey);
            ReadWriteLock lockFound = group.get(subKey);
            if (lockFound != null) {
                return lockFound;
            }
            ReadWriteLock lockCreate = new ReentrantReadWriteLock(); // 轻量级对象，即使碰撞多创建一个也没有关系。
            ReadWriteLock lockExisted = group.putIfAbsent(subKey, lockCreate);
            if (lockExisted != null) {
                return lockExisted;
            }
            return lockCreate;

        }

        private ConcurrentHashMap<String, ReadWriteLock> getGroup(Object groupKey) {
            ConcurrentHashMap<String, ReadWriteLock> groupFound = dualLockMap.get(groupKey);
            if (groupFound != null) {
                return groupFound;
            }
            ConcurrentHashMap<String, ReadWriteLock> groupCreate = new ConcurrentHashMap<String, ReadWriteLock>();
            ConcurrentHashMap<String, ReadWriteLock> groupExisted = dualLockMap.putIfAbsent(groupKey, groupCreate);
            if (groupExisted != null) {
                return groupExisted;
            }
            return groupCreate;
        }
    }
}
