package com.test.service.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.test.service.api.WebService;

@Component
@Path("/sample-webservice")
@Api(value = "Sample Web Service - An example CXF service")
@Produces({ "application/json" })
public class WebServiceImpl implements WebService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("test/{text}")
	@ApiOperation(value = "Test method", notes = "This is a test method.", response = Map.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Test service was executed successfully! :)"),
							@ApiResponse(code = 404, message = "An exception was thrown") })
	public Response test(
			@PathParam("text") @ApiParam(allowableValues = "Monkey,Donkey,EXCEPTION", required=true) String text) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("Hello", "World " + text);

		Response response;

		if(text.equals("EXCEPTION")){
			response = Response.status(Response.Status.NOT_FOUND).entity(new Exception("Whoaa, you threw an exception"))
				.build();
		}
		else{
			response = Response.status(Response.Status.OK).entity(result)
					.build();
		}
		
		return response;
	}

}
