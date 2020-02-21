package com.sipsd.restful.api.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Collection;

/**
 * @Auther: gaoqiang
 * @Date: 2019-06-22 20:00
 * @Description:
 */
public class Base
{
    public static String responseSuccess(Object obj) {
        JSONObject result = new JSONObject();
        result.element("msg", "success");
        result.element("code", 0);

        if (obj != null) {
            JSONArray jsonObj;
            if (obj instanceof Collection) {
                jsonObj = null;
                jsonObj = JSONArray.fromObject(obj);
                result.element("data", jsonObj);
            } else {
                jsonObj = null;
                JSONObject jsonObj2 = JSONObject.fromObject(obj);
                result.element("data", jsonObj2);
            }
        } else {
            result.element("data", obj);
        }

        return JsonFormatUtil2.format(result.toString());
    }

    public static String responseSuccess() {
        JSONObject result = new JSONObject();
        result.element("msg", "success");
        result.element("code", 0);
        return JsonFormatUtil2.format(result.toString());
    }

    public static String responseError(String message) {
        JSONObject result = new JSONObject();
        result.element("msg", message);
        result.element("code", 1);
        return JsonFormatUtil2.format(result.toString());
    }
}
