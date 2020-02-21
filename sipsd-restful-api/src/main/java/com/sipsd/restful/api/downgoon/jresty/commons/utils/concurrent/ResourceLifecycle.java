package com.sipsd.restful.api.downgoon.jresty.commons.utils.concurrent;

import java.util.Map;

public interface ResourceLifecycle<T> {

	public T buildResource(Map map) throws Exception;

	public void destoryResource(Map map, T resource) throws Exception;
}
