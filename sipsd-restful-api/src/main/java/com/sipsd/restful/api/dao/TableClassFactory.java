package com.sipsd.restful.api.dao;


import com.sipsd.restful.api.downgoon.jresty.commons.utils.concurrent.ConcurrentResourceContainer;
import com.sipsd.restful.api.downgoon.jresty.commons.utils.concurrent.ResourceLifecycle;
import com.sipsd.restful.api.mode.DynamicPojo;
import com.sipsd.restful.api.mode.TableSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TableClassFactory {

	private static Logger LOG = LoggerFactory.getLogger(TableClassFactory.class);

	private static final String packageName = "io.downgoon.dynamic";

	private final TableSchemaFetcher tableSchemaFetcher = new TableSchemaFetcher();

	private ConcurrentResourceContainer<DynamicPojo> container = new ConcurrentResourceContainer<>(
			new ResourceLifecycle<DynamicPojo>() {
				@Override
				public DynamicPojo buildResource(Map map) throws Exception {
					TableSchema beanSchema = tableSchemaFetcher.getTableSchemas(map);
					
					String pojoClassName = packageName + "." + map.get("key").toString() +"."   + beanSchema.getClassName();
					DynamicPojo dynamicPojo = new DynamicPojo(pojoClassName, beanSchema.getPropertyTypes(),
							beanSchema.getPrimaryKeys(), beanSchema.isAutoIncr());

					if (LOG.isDebugEnabled()) {
						// show dynamic class definition
						LOG.debug("dynamic class for {}.{} {}", map, dynamicPojo);
					}
					return dynamicPojo;

				}

				@Override
				public void destoryResource(Map map, DynamicPojo resource) throws Exception
				{

				}
			});

	/**
	 * @param map
	 *            database table name
	 * @return return NULL, if tableName not found
	 */
	public DynamicPojo getTableClass(Map map) {
		DynamicPojo dc = null;
		try {
			dc = container.getResource(map);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (dc == null) {
			throw new IllegalStateException("table not found: " + map);
		}
		return dc;
	}

}
