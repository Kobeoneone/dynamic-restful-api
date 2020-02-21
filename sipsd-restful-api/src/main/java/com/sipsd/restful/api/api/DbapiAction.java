package com.sipsd.restful.api.api;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.sipsd.restful.api.biz.JsonFeatures;
import com.sipsd.restful.api.biz.PaginationExec;
import com.sipsd.restful.api.biz.TableCrudBiz;
import com.sipsd.restful.api.dao.DaoMapFactory;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.CRUDDaoSupport;
import com.sipsd.restful.api.downgoon.jresty.data.orm.dao.util.R;
import com.sipsd.restful.api.mode.JDBC.PaginationResult;
import com.sipsd.restful.api.rest.RESTful;
import io.netty.util.internal.StringUtil;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CommonsLog
public class DbapiAction implements RESTful
{
    private TableCrudBiz crud = new TableCrudBiz();
    public static Logger logger = LoggerFactory.getLogger(DbapiAction.class);


    /*
     *已经将getAll和getId集成在一起了
     */
    @Override
    public void getAll(RoutingContext routingContext)
    {
        //获取key
        String name = routingContext.request().getParam("vertx_name");
        //获取表名
        String tableName = routingContext.request().getParam("vertx_tableName");

        PaginationResult<Object> paginationResult = new PaginationResult<>();
        //将数据源的dao放到map中key为resource
        Map map = DaoMapFactory.getMapDao(name,tableName);
        DruidDataSource  druidDataSource = (DruidDataSource)map.get("resource");
        String sql = "select count(*) from " + map.get("tableName").toString() + "";
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);
        int rowCount = dao.findObjectsForObject(sql, "");
        //设置分页的总记录数
        paginationResult.setTotalCount(rowCount);
        paginationResult.setDbType(map.get("dbType").toString());
        //默认分页为1-10
        List<Object> records = null;
        if (hasQueryParam(routingContext.request()))
        {
            Map<String, Object> mapParams = toMapFormat(routingContext.request().params());

            if (mapParams.containsKey("pageSize") && !StringUtil.isNullOrEmpty(mapParams.get("pageSize").toString()))
            {
                paginationResult.setPageSize(Integer.parseInt(mapParams.get("pageSize").toString()));
            }
            if (mapParams.containsKey("currentPage") && !StringUtil.isNullOrEmpty(mapParams.get("currentPage").toString()))
            {
                paginationResult.setCurrentPage(Integer.parseInt(mapParams.get("currentPage").toString()));
            }

            records = crud.getLimitAndOrderList(map, mapParams, paginationResult);
        }
        else
        {
            records = crud.getLimitAndOrderList(map, paginationResult);
        }

        if (paginationResult == null)
        {
            routingContext.fail(404);
            return;
        }

        paginationResult.setData(records);
        int pageCount = paginationResult.getTotalCount() % paginationResult.getPageSize();
        paginationResult.setTotalPage(pageCount == 0 ? (paginationResult.getTotalCount() / paginationResult.getPageSize()) : (paginationResult.getTotalCount() / paginationResult.getPageSize() + 1));
        R r = R.ok(paginationResult);
        routingContext.response().end(JSON.toJSONString(r, JsonFeatures.getFeatures()));

    }

    @Override
    public void save(RoutingContext routingContext)
    {
        //获取key
        String name = routingContext.request().getParam("vertx_name");
        //获取表名
        String tableName = routingContext.request().getParam("vertx_tableName");
        JsonObject bodyJson = routingContext.getBodyAsJson();
        @SuppressWarnings("unchecked")
        Map<String, Object> bodyMap = bodyJson.mapTo(Map.class);
        //将数据源的dao放到map中key为resource
        Map map = DaoMapFactory.getMapDao(name,tableName);
        DruidDataSource  druidDataSource = (DruidDataSource)map.get("resource");
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);
        Object record = crud.create(map, bodyMap);

        if (record == null) {
            routingContext.fail(404);
            return;
        }

        routingContext.response().end(JSON.toJSONString(new R()));
    }

    @Override
    public void update(RoutingContext routingContext)
    {
        //获取key
        String name = routingContext.request().getParam("vertx_name");
        //获取表名
        String tableName = routingContext.request().getParam("vertx_tableName");
        JsonObject bodyJson = routingContext.getBodyAsJson();
        @SuppressWarnings("unchecked")
        Map<String, Object> bodyMap = bodyJson.mapTo(Map.class);
        //将数据源的dao放到map中key为resource
        Map map = DaoMapFactory.getMapDao(name,tableName);
        DruidDataSource  druidDataSource = (DruidDataSource)map.get("resource");
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);

        //获取主键名称
        String primaryKey = routingContext.request().getParam("id");
        if(!StringUtils.isEmpty(primaryKey))
        {
            map.put("primaryKey",primaryKey);
        }

        Object record = crud.update(map, bodyMap);

        if (record == null) {
            routingContext.fail(404);
            return;
        }

        routingContext.response().end(JSON.toJSONString(new R()));
    }

    @Override
    public void delete(RoutingContext routingContext) {
        //获取key
        String name = routingContext.request().getParam("vertx_name");
        //获取表名
        String tableName = routingContext.request().getParam("vertx_tableName");
        JsonObject bodyJson = routingContext.getBodyAsJson();
        //将数据源的dao放到map中key为resource
        Map map = DaoMapFactory.getMapDao(name,tableName);
        DruidDataSource  druidDataSource = (DruidDataSource)map.get("resource");
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);
        //获取主键名称
        Map<String, Object> mapParams  = null;
        if (hasQueryParam(routingContext.request()))
        {
             mapParams = toMapFormat(routingContext.request().params());
        }

        int rows = crud.remove(map,mapParams);
        if (rows < 0) {
            routingContext.fail(404);
            return;
        }

        routingContext.response().end(JSON.toJSONString(new R().put("data",rows)));
    }

    @Override
    public void exec(RoutingContext routingContext)
    {
        //获取key
        String name = routingContext.request().getParam("vertx_name");
        //获取表名
        String mapServiceCode = routingContext.request().getParam("mapServiceCode");
        //将数据源的dao放到map中key为resource
        Map map = DaoMapFactory.getMapDao(name,"");
        DruidDataSource  druidDataSource = (DruidDataSource)map.get("resource");
        CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
        dao.setDataSource(druidDataSource);
        String sql = "select * from sm_sys_restfulapi where map_service_code = '" + mapServiceCode + "'";
        map = dao.queryForMap(sql);
        if (map.isEmpty())
        {
            log.error("查询的sql语句是:" + sql + "------结果:无数据");
            throw new IllegalStateException("查询的sql语句是:" + sql + "------结果:无数据");
        }
        sql = map.get("sqls").toString();
        List<Object> rows = dao.findObjectsForList(sql);
        //设置分页的总记录数
        PaginationExec<Object> paginationExec = new PaginationExec();
        paginationExec.setData(rows);
        R r = R.ok(paginationExec);
        routingContext.response().end(JSON.toJSONString(r));

    }


    private boolean hasQueryParam(HttpServerRequest request)
    {
        boolean b = request.query() != null && request.query().length() > 0 && request.params().size() > 0;
        if (!StringUtils.isEmpty(request.params().get("id")))
        {
            b = true;
        }
        return b;
    }

    private Map<String, Object> toMapFormat(MultiMap form)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        Set<String> formFields = form.names();
        for (String fieldName : formFields)
        {
            map.put(fieldName, form.get(fieldName));
        }
        //移除参数key和表名 以防字段中出现相同的名称
        map.remove("vertx_name");
        map.remove("vertx_tableName");
        return map;
    }
}
