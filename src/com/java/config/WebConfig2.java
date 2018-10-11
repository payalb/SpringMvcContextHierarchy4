package com.java.config;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

//Don't use @Configuration
//Equivalent of web.xml

public class WebConfig2 extends AbstractDispatcherServletInitializer{


	protected WebApplicationContext createRootApplicationContext() {
		return null;
	}

	@Override
	protected WebApplicationContext createServletApplicationContext() {
		AnnotationConfigWebApplicationContext ctx= new AnnotationConfigWebApplicationContext();
		ctx.register(RootConfig.class);
		
		return ctx;
		/*	By-default it is true here
		servletOne.setAsyncSupported(true);
		*/
	}

	/*Url mapping for child app context i.e. for Dispatcher servlet*/
	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

	//Cannot have 2 with same name as dispatcher
	@Override
	protected String getServletName() {
		return "dispatcher1";
	}

}
