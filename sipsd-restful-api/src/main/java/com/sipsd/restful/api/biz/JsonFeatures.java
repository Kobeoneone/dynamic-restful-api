package com.sipsd.restful.api.biz;

import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @Auther: gaoqiang
 * @Date: 2019-06-24 14:24
 * @Description:
 */
public class JsonFeatures
{
    private static SerializerFeature[] features;

    public static SerializerFeature[] getFeatures() {
        if (features == null) {
            features = new SerializerFeature[] {
                    SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullListAsEmpty,
                    SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteDateUseDateFormat
            };
        }
        return features;
    }
}
