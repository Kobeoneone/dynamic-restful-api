package com.sipsd.restful.api.mode;

import net.sf.cglib.beans.BeanMap;

import java.util.Set;

public class DynamicBean {

	/** real bean instance */
	private Object bean;

	private BeanMap beanMap;
	
	public DynamicBean(Object bean) {
		this.bean = bean;
		this.beanMap = BeanMap.create(bean);
	}

	/**
	 * set property of bean
	 * */
	public void setProperty(String name, Object value) {
		beanMap.put(name, value);
	}
	
	/**
	 * from http query string or form fileds
	 * */
	public void setPropertyString(String name, String value) {
		setProperty(name, toObject(value, name));
	}
	
	
	protected Object toObject(String fvalue, String fname) {
		return StringObject.toObject(fvalue, getPropertyType(fname), fname);
	}
	

	/**
	 * get property of bean
	 * */
	public Object getProperty(String name) {
		return beanMap.get(name);
	}
	
	
	public Class<?> getPropertyType(String name) {
		return beanMap.getPropertyType(name);
	}
	
	public boolean hasProperty(String name) {
		return beanMap.containsKey(name);
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getProperties() {
		return beanMap.keySet();
	}

	/**
	 * @return real bean instance
	 */
	public Object getBean() {
		return bean;
	}

	@Override
	public String toString() {
		return beanMap + " @ " + bean.getClass().getName();
	}
	
	
}
