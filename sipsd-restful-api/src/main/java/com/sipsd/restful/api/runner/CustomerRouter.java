package com.sipsd.restful.api.runner;

import com.sipsd.restful.api.api.DbapiAction;
import com.sipsd.restful.api.rest.RESTful;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: gaoqiang
 * @Date: 2019-04-19 15:17
 * @Description:
 */
@Configuration
public class CustomerRouter  implements CommandLineRunner
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${server.vertx.port}")
    private int vertxPort;

    @Override
    public void run(String... args) throws Exception
    {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router mainRouter = Router.router(vertx);
        Router dbapiRouter = Router.router(vertx);
        mainRouter.mountSubRouter("/dbapi", dbapiRouter);

        Router dbmetaRouter = Router.router(vertx);
        mainRouter.mountSubRouter("/dbmeta", dbmetaRouter);

        RESTful dbapiAction = new DbapiAction();

        dbapiRouter.route().handler(routingContext -> {
            logger.debug("comming request: {}", routingContext.request().absoluteURI());
            routingContext.response().putHeader("Server", "autorest4db");
            routingContext.response().putHeader("Content-Type", "application/json;charset=UTF-8");
            routingContext.next();
        });

        //name代表注册的key-通过key可以知道是ip地址和数据库名称，tableName指的是表名
        dbapiRouter.route(HttpMethod.GET, "/query/:vertx_name/:vertx_tableName").handler(dbapiAction::getAll) ;
        dbapiRouter.route(HttpMethod.GET, "/query/:vertx_name/:vertx_tableName/:id").handler(dbapiAction::getAll);
        dbapiRouter.route(HttpMethod.GET, "/exec/:vertx_name/:mapServiceCode").handler(dbapiAction::exec);
        dbapiRouter.route("/delete/:vertx_name/:vertx_tableName").handler(dbapiAction::delete);
        dbapiRouter.route("/delete/:vertx_name/:vertx_tableName/:id").handler(dbapiAction::delete);

        dbapiRouter.route().handler(BodyHandler.create()); // For POST/PUT
        dbapiRouter.route(HttpMethod.POST, "/save/:vertx_name/:vertx_tableName").handler(dbapiAction::save);
        dbapiRouter.route(HttpMethod.POST, "/edit/:vertx_name/:vertx_tableName/:id").handler(dbapiAction::update);
        dbapiRouter.route(HttpMethod.POST, "/edit/:vertx_name/:vertx_tableName").handler(dbapiAction::update);

        // handler all requests in RequestHandler
        server.requestHandler(mainRouter::accept);
        server.listen(vertxPort);

        logger.info("autorest4db is listening on: {}", vertxPort);
    }
}
