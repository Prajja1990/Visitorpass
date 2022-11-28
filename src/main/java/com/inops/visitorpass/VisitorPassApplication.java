package com.inops.visitorpass;

import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ServletContextAware;

import com.google.common.collect.ImmutableMap;

@SpringBootApplication
public class VisitorPassApplication implements ServletContextAware {

	public static void main(String[] args) {
		SpringApplication.run(VisitorPassApplication.class, args);
	}

	@Bean
	public static CustomScopeConfigurer viewScope() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.setScopes(new ImmutableMap.Builder<String, Object>().put("view", new ViewScope()).build());
		return configurer;
	}

	@Bean
	public ServletRegistrationBean<FacesServlet> servletRegistrationBean() {
		ServletRegistrationBean<FacesServlet> servletRegistrationBean = new ServletRegistrationBean<>(
				new FacesServlet(), "*.xhtml");
		servletRegistrationBean.setLoadOnStartup(1);
		return servletRegistrationBean;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// Iniciar el contexto de JSF
		// http://stackoverflow.com/a/25509937/1199132
		servletContext.setInitParameter("javax.faces.FACELETS_LIBRARIES", "/WEB-INF/springsecurity.taglib.xml");
		servletContext.setInitParameter("javax.faces.FACELETS_LIBRARIES", "/WEB-INF/showcase.taglib.xml");
		servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
		servletContext.setInitParameter("javax.faces.STATE_SAVING_METHOD", "server");
		servletContext.setInitParameter("javax.faces.PROJECT_STAGE", "Production");
		servletContext.setInitParameter("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE", "true");
		servletContext.setInitParameter("javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL", "true");
		servletContext.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", "true");
		servletContext.setInitParameter("primefaces.FONT_AWESOME", "true");
		servletContext.setInitParameter("com.sun.faces.expressionFactory", "com.sun.el.ExpressionFactoryImpl");
		
	}

}
