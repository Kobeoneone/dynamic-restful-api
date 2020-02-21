/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;

/**
 * @title ORMBuilderFacade
 * @description 提供更简洁的API，对一个对象，抽取FieldMapping和ValueMapping。 注意：此类非线程安全的，但本身是轻量级对象，每次新建一个即可。
 * @author liwei39
 * @date 2014-8-18
 * @version 1.0
 */
public class ORMBuilderFacade {
    /*
     * 区分ORFieldMappingBuilder和ORValueMappingBuilder是为了做到职责分离， 但导致上层调用者需要知道太多细节，为此引入ORMBuilderFacade以简化API
     */

    /** 待执行OR Mapping的POJO对象 */
    private Object pojoBean;

    /** 生成类的字段映射时，是否允许缓存 */
    private boolean fieldMappingCached;

    /** 类的字段映射工具 */
    private ORFieldMappingBuilder fieldMappingBuilder;

    /** 类的字段映射结果缓存 */
    private ORFieldMapping cachedFieldMapping = null;

    /**
     * OR Mapping内部实现包含类的字段映射和对象的字段取值映射两个部分。 为了用户使用方便，不用关注实现细节，引入 ORMBuilderFacade类。
     *
     * @param pojoBean 待执行OR Mapping的POJO对象
     * @param fieldMappingCached 生成类的字段映射时，是否允许缓存，以提高性能。
     * */
    public ORMBuilderFacade(Object pojoBean, boolean fieldMappingCached) {
        super();
        this.pojoBean = pojoBean;
        this.fieldMappingCached = fieldMappingCached;
        this.fieldMappingBuilder = new ORFieldMappingBuilder(pojoBean.getClass());
    }

    /**
     * 字段映射设置缓存的 ORMBuilderFacade
     * */
    public ORMBuilderFacade(Object pojoBean) {
        this(pojoBean, true);
    }

    public boolean isFieldMappingCached() {
        return fieldMappingCached;
    }

    /**
     * 生成映射
     * */
    public ORFieldMapping buildFieldMapping() {
        if (cachedFieldMapping != null && fieldMappingCached) {
            return this.cachedFieldMapping;
        }
        ORFieldMapping flyInstance = fieldMappingBuilder.buildFieldMapping();
        if (fieldMappingCached) {
            this.cachedFieldMapping = flyInstance;
        }
        return flyInstance;
    }

    public ORValueMapping buildValueMapping() {
        return new ORValueMappingBuilder(pojoBean, buildFieldMapping()).buildValueMapping();
    }

    ORFieldMapping getCachedFieldMapping() {
        return cachedFieldMapping;
    }

}
