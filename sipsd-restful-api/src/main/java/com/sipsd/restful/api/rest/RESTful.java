package com.sipsd.restful.api.rest;

import io.vertx.ext.web.RoutingContext;

public interface RESTful {

	void getAll(RoutingContext routingContext);

	void exec(RoutingContext routingContext);

	void delete(RoutingContext routingContext);

	void save(RoutingContext routingContext);

	void update(RoutingContext routingContext);

}