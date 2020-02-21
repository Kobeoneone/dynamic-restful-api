package com.sipsd.restful.api.downgoon.jresty.data.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ORMField {

    /**
     * @return  对应的DB字段名
     * */
    public String name();

    /**
     * @return  字段是否需要跳过ORM，默认FALSE（不跳过，需ORM）
     * */
    public boolean isSkip() default false;

    /**
     * @return  字段是否是主键或联合主键，默认FALSE（不是主键，是普通字段）
     * */
    public boolean isKey() default false;
    
    /**
     * @return 在字段为主键的前提下，只有一个字段可以设置为 AutoIncrement，而且数据必须为整形（Short/Int/Long）
     * */
    public boolean isAutoIncrement() default false;

}
