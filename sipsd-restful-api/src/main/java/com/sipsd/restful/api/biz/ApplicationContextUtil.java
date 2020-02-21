package com.sipsd.restful.api.biz;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Auther: gaoqiang
 * @Date: 2019-06-23 10:57
 * @Description:
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware
{
    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }
}
