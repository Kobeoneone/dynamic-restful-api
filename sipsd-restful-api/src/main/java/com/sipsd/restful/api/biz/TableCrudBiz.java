package com.sipsd.restful.api.biz;


import com.sipsd.restful.api.dao.DaoFactory;
import com.sipsd.restful.api.dao.TableClassFactory;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.CRUDDaoSupport;
import com.sipsd.restful.api.mode.AutoRestException;
import com.sipsd.restful.api.mode.DynamicBean;
import com.sipsd.restful.api.mode.DynamicPojo;
import com.sipsd.restful.api.mode.JDBC.Pagination;
import lombok.extern.apachecommons.CommonsLog;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CommonsLog
public class TableCrudBiz {

	
	private TableClassFactory tableClassFactory = new TableClassFactory();
	
	private DaoFactory daoFactory = new DaoFactory();

	public List<Object> getList(Map map) throws  Exception {

		DynamicPojo tableClass = tableClassFactory.getTableClass(map);
		DynamicBean tableInstance = tableClass.newInstance();
		CRUDDaoSupport<Object> dao = daoFactory.getDao(map);
		return dao.findObjects(tableInstance.getBean());

	}

	public List<Object> getList(Map map, Map<String, Object> params) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(map);
		DynamicBean tableInstance = tableClass.newInstance();
		CRUDDaoSupport<Object> dao = daoFactory.getDao(map);

		// params condition
		Set<String> props = tableInstance.getProperties();
		for (String prop : props) {
			if (params.get(prop) == null) {
				continue;
			}
			if (params.get(prop) instanceof String) {
				tableInstance.setPropertyString(prop, (String) params.get(prop) );
			} else {
				tableInstance.setProperty(prop, params.get(prop));
			}

		}

		return dao.findObjects(tableInstance.getBean());
	}


	public List<Object> getLimitAndOrderList(Map map,Pagination pagination){
		DynamicPojo tableClass = tableClassFactory.getTableClass(map);
		DynamicBean tableInstance = tableClass.newInstance();
		CRUDDaoSupport<Object> dao = daoFactory.getDao(map);
		List<Object> list =  dao.findObjectsOrderByLimit(tableInstance.getBean(),pagination);
		return  list;
	}

	public List<Object> getLimitAndOrderList(Map map, Map<String, Object> params, Pagination pagination) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(map);
		DynamicBean tableInstance = tableClass.newInstance();
		CRUDDaoSupport<Object> dao = daoFactory.getDao(map);

		// params condition
		Set<String> props = tableInstance.getProperties();
		for (String prop : props) {
			if (params.get(prop) == null) {
				continue;
			}
			if (params.get(prop) instanceof String) {
				tableInstance.setPropertyString(prop, (String) params.get(prop));
			} else {
				tableInstance.setProperty(prop, params.get(prop));
			}

		}

		List<Object> list =  dao.findObjectsOrderByLimit(tableInstance.getBean(),pagination);
		return  list;
	}


	public Object create(Map<String, Object> bs, Map<String, Object> record) {

		DynamicPojo tableClass = tableClassFactory.getTableClass(bs);
		DynamicBean tableInstance = tableClass.newInstance();
		// fill object
		fillDyamicBean(record, tableInstance);

		// exec dao
		CRUDDaoSupport<Object> dao = daoFactory.getDao(bs);
		dao.saveObject(tableInstance.getBean());
		return tableInstance.getBean();
	}

	public int update(Map<String, Object> bs, Map<String, Object> record) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(bs);
		DynamicBean tableInstance = tableClass.newInstance();
		//判断有无传入更新的主键或者自带主键


		// fill primary key
		//fillPrimaryKeys(bs.get("primaryKey").toString(), tableClass.getPrimaryKeys(), tableInstance);

		// fill object
		fillDyamicBean(record, tableInstance);

		// exec dao
		CRUDDaoSupport<Object> dao = daoFactory.getDao(bs);
		int rows = dao.updateObject(tableInstance.getBean());
		return rows;
	}


	public int remove(Map<String, Object> map,Map<String, Object> params) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(map);
		DynamicBean tableInstance = tableClass.newInstance();
		CRUDDaoSupport<Object> dao = daoFactory.getDao(map);
		// params condition
		Set<String> props = tableInstance.getProperties();
		for (String prop : props) {
			if (params.get(prop) == null) {
				continue;
			}
			if (params.get(prop) instanceof String) {
				tableInstance.setPropertyString(prop, (String) params.get(prop) );
			} else {
				tableInstance.setProperty(prop, params.get(prop));
			}

		}

		int rows = dao.removeObjects(tableInstance.getBean());
		return rows;
	}
	
	public Object getDetail(Map map) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(map);
		DynamicBean tableInstance = tableClass.newInstance();
		
		fillPrimaryKeys(map.get("id").toString(), tableClass.getPrimaryKeys(), tableInstance);
	
		CRUDDaoSupport<Object> dao = daoFactory.getDao(map);
		
		return dao.findObject(tableInstance.getBean());
	}

	/**
	 * fill dynamic object with record
	 *
	 * @param	record
	 * 				input argument
	 * @param	tableInstance
	 * 				output argument
	 * */
	protected void  fillDyamicBean(Map<String, Object> record, DynamicBean tableInstance) {
		Iterator<Map.Entry<String, Object>> fields = record.entrySet().iterator();
		while (fields.hasNext()) {
			Map.Entry<String, Object> f = fields.next();
			//如果是Long型的id则需要在此转换-此处为pg做的调整int(8)类型
			if(f.getKey().equals("id") && tableInstance.getPropertyType("id").getName().equals("java.lang.Long"))
			{
				tableInstance.setProperty(f.getKey(), Long.parseLong(f.getValue().toString()));
			}
			//为oracle做的调整-oracle id为numberic类型
			else if(f.getKey().equals("id") && tableInstance.getPropertyType("id").getName().equals("java.math.BigDecimal"))
			{
				BigDecimal bigDecimal=new BigDecimal(f.getValue().toString());
				tableInstance.setProperty(f.getKey(), bigDecimal);
			}
			else
			{
				tableInstance.setProperty(f.getKey(), f.getValue());
			}
		}
	}
	
	/**
	 * fill primary keys with id
	 * */
	protected void fillPrimaryKeys(String id, Set<String> keys, DynamicBean tableInstance) {
		if (keys == null || keys.size() == 0) {
			return ;
		}
		if (keys.size() == 1) { 
			String keyName = keys.iterator().next();
			tableInstance.setPropertyString(keyName, id);
			return ;
		}
		
		// 约定：联合主键的ID取值用短横线连接
		String[] keyValues = id.split("-");  
		if (keyValues == null || keyValues.length != keys.size()) {
			throw new AutoRestException("invalid keys value for " + tableInstance.getClass().getSimpleName());
		}
		
		int i = 0;
		for (String keyName : keys) {
			tableInstance.setPropertyString(keyName, keyValues[i]);
			i ++;
		}
		
	}
	
	
}
