package com.test.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.test.service.api.WebService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Component
@Path("/")
@Api(value = "/user", description = "Operations about user")
public class WebServiceImpl implements WebService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("test/{text}")
	@ApiOperation(value = "Test method", notes = "This only a test method for this example service.")
	public Map<String, String> test(@PathParam("text") String text) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("Hello", "World");
		return result;
	}

}
