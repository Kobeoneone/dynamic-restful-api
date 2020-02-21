/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @title DateUtil
 * @description TODO 
 * @author liwei39
 * @date 2014-7-10
 * @version 1.0
 */
public class DateUtil {

    public static final String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static final String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }
}
