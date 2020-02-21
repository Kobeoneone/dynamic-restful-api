package com.sipsd.restful.api.downgoon.jresty.commons.utils.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author downgoon
 * @since 2016-12-31
 */
public class ConcurrentResourceContainer<T> {

	private final Map<String, T> resourceContainer = new HashMap<String, T>();

	private final ConcurrentHashMap<String, ReentrantLock> stuntmanContainer = new ConcurrentHashMap<String, ReentrantLock>();

	private ResourceLifecycle<T> lifecycle;

	public ConcurrentResourceContainer(ResourceLifecycle<T> lifecycle) {
		this.lifecycle = lifecycle;
	}

	/**
	 * 
	 * get or create named resource
	 * http://blog.csdn.net/tenebaul/article/details/17144059
	 */
	public T getResource(Map map) throws Exception {
		String key = map.get("tableName").toString();
		T resource = resourceContainer.get(key);
		if (resource == null)
		{
			ReentrantLock stuntman = getStuntmanInstanceByName((key));
			try
			{
				stuntman.lock();
				resource = lifecycle.buildResource(map); // build exception
				if (resource != null)
				{
					resourceContainer.put(key, resource);
				}

			} finally {
				stuntman.unlock();
			}

		}

		return resource;
	}

	public T addResource(Map map) throws Exception {
		return getResource(map);
	}

	public T removeResource(Map map) throws Exception {
		T resource = resourceContainer.get(map.get("key").toString());
		if (resource == null) {
			return null;
		}

		ReentrantLock stuntman = getStuntmanInstanceByName(map.get("key").toString());
		try {
			stuntman.lock();

			lifecycle.destoryResource(map, resource); // destroy exception
			resourceContainer.remove(map.get("key").toString());
			stuntmanContainer.remove(map.get("key").toString());

		} finally {
			stuntman.unlock();
		}

		return resource;
	}

	private ReentrantLock getStuntmanInstanceByName(String name) {
		ReentrantLock stuntman = stuntmanContainer.get(name);
		if (stuntman == null) {
			ReentrantLock newStuntman = new ReentrantLock();
			ReentrantLock preStuntman = stuntmanContainer.putIfAbsent(name, newStuntman);
			stuntman = (preStuntman != null ? preStuntman : newStuntman);
		}
		return stuntman;
	}
	
	public int size() {
		return stuntmanContainer.size();
	}
	
	public Map<String, T> container() {
		return resourceContainer;
	}
	
	public boolean containsName(String name) {
		return stuntmanContainer.containsKey(name);
	}

}
