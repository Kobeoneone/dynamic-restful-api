package com.sipsd.restful.api.dao;


import com.alibaba.druid.pool.DruidDataSource;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.CRUDDaoSupport;
import com.sipsd.restful.api.mode.SqlTypeClass;
import com.sipsd.restful.api.mode.TableSchema;
import lombok.extern.apachecommons.CommonsLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Map;

/**
 * create a connection and pull metadata from database (no cache)
 */
@CommonsLog
public class TableSchemaFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(TableSchemaFetcher.class);

	public TableSchema getTableSchemas(Map map) throws Exception {
		CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>();
		DruidDataSource druidDataSource = (DruidDataSource)map.get("resource");
		dao.setDataSource(druidDataSource);
		final TableSchema tableSchema;
		String tableName = map.get("tableName").toString();
		try {
			tableSchema = new TableSchema(tableName);
			String columnNamePattern = null;
			String catalog = null;
			String schemaPattern = null;
			DatabaseMetaData dbmeta = dao.getMetaDataForList();

			if(map.get("dbType").toString().toLowerCase().equals("oracle"))
				schemaPattern = map.get("userName").toString();

			ResultSet columns = dbmeta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);

			String columnName;
			int columnType;
//
//			if(columns.first())
//			{
//				columnName = normName(columns.getString(4));
//				columnType = columns.getInt(5);
//				tableSchema.addProperty(columnName, SqlTypeClass.toClass(columnType));
//				//以下只针对mysql数据库
//				if ("YES".equalsIgnoreCase(columns.getString("IS_AUTOINCREMENT"))) {
//					tableSchema.autoIncr(); // always executed 0 or 1 time
//				} else {
//					// Quick&Dirty: 约定只要名字为id，并且是整数类型，而且是主键，则当自增处理
//					if ("id".equalsIgnoreCase(columnName)
//							&& (columnType == Types.INTEGER || columnType == Types.BIGINT)
//							&& tableSchema.getPrimaryKeys().contains(columnName)) {
//						tableSchema.autoIncr();
//					}
//				}
//			}

			while (columns.next())
			{
				columnName = normName(columns.getString(4));
				columnType = columns.getInt(5);
				tableSchema.addProperty(columnName, SqlTypeClass.toClass(columnType));
			}

			return tableSchema;
		}
		catch (Exception e)
		{
			log.info(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

	private class PriKey implements Comparable<PriKey> {
		String keyName;
		int keySeq;

		PriKey(String keyName, int keySeq) {
			this.keyName = keyName;
			this.keySeq = keySeq;
		}

		@Override
		public int compareTo(PriKey o) {
			return keySeq - o.keySeq;
		}
	}

	private static String normName(String nameInDataSpace) {
		String nameInJava = nameInDataSpace.toLowerCase();
		if (nameInJava.startsWith("`") && nameInJava.endsWith("`")) {
			return nameInJava.substring(1, nameInJava.length() - 1);
		}
		return nameInJava;
	}
}
