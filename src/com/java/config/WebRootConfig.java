package com.java.config;

import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class WebRootConfig extends AbstractContextLoaderInitializer{

	@Override
	protected WebApplicationContext createRootApplicationContext() {
		AnnotationConfigWebApplicationContext ctx= new AnnotationConfigWebApplicationContext();
		/*ctx.register(new Class[] {RootConfig.class});*/
		return ctx;
	}

}
