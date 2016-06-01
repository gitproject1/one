package com.github.gitproject1.one.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.github.gitproject1.one.service.DynamicEntityService;

public class ContextListener implements ServletContextListener{

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		ServletContext ctx = servletContextEvent.getServletContext();
		WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ctx);
		
		DynamicEntityService dynamicEntityService = (DynamicEntityService) springContext.getBean("dynamicEntityService");
		dynamicEntityService.loadSystemEntities();
		dynamicEntityService.loadDynamicEntities();
	}

	public void contextDestroyed(ServletContextEvent arg0) {
	}
}
