/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.sql;

import java.util.Map;

/**
 * @title ORValueMapping
 * @description TODO 
 * @author liwei39
 * @date 2014-8-18
 * @version 1.0
 */
public class ORValueMapping {

    private String dbTalbeName;

    private Map<String, Object> dbKeysKV;

    private Map<String, Object> dbFieldKV;

    ORValueMapping(String dbTableName, Map<String, Object> dbKeysKV, Map<String, Object> dbFieldKV) {
        super();
        this.dbTalbeName = dbTableName;
        this.dbKeysKV = dbKeysKV;
        this.dbFieldKV = dbFieldKV;
    }

    public String getDbTalbeName() {
        return dbTalbeName;
    }

    public Map<String, Object> getDbKeysKV() {
        return dbKeysKV;
    }

    public Map<String, Object> getDbFieldKV() {
        return dbFieldKV;
    }

}
