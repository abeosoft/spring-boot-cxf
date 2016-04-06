package com.test.app;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAutoConfiguration
@EnableSwagger2
@ComponentScan({ "com.test.service.impl" })
@ImportResource({ "classpath:META-INF/cxf/cxf.xml" })
public class CXFApplication {
	@Autowired
	private ApplicationContext ctx;

	private boolean logRequests = true;

	@Bean
	public ServletRegistrationBean dispatcherCXFServlet() {
		return new ServletRegistrationBean(new CXFServlet(), "/rest-api/*");
	}

	@Bean
	public Server jaxRsServer() {
		// Find all beans annotated with @Path
		List<Object> serviceBeans = new ArrayList<Object>(ctx.getBeansWithAnnotation(Path.class).values());
		System.out.println("Registering service beans: " + serviceBeans);

		// Find all beans annotated with @Providers
		List<Object> providers = new ArrayList<Object>(ctx.getBeansWithAnnotation(Provider.class).values());
		System.out.println("Registering providers: " + providers);
		
		providers.add(this.swaggerWriter());
		serviceBeans.add(this.swaggerResource());

		JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
		factory.setBus(ctx.getBean(SpringBus.class));
		factory.setAddress("/");
		factory.setServiceBeans(serviceBeans);
		factory.setProviders(providers);
		Server server = factory.create();

		if (logRequests) {
			server.getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
		}

		return server;
	}

	@Bean
	@ConditionalOnMissingBean
	public JacksonJsonProvider jsonProvider(ObjectMapper objectMapper) {
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		provider.setMapper(objectMapper);
		return provider;
	}

	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public Docket newsApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.test.service.impl")).paths(PathSelectors.any()).build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("My REST API", "Some custom description of API.", "API TOS", "Terms of service",
				"myeaddress@company.com", "License of API", "API license URL");
		return apiInfo;
	}
	
	@Bean
	public SwaggerSerializers swaggerWriter(){
		return new SwaggerSerializers();
	}
	
	@Bean ApiListingResource swaggerResource(){
		return new ApiListingResource();
	}
}