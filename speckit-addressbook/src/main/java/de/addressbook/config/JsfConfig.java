package de.addressbook.config;

import jakarta.faces.webapp.FacesServlet;
import jakarta.servlet.ServletContext;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsfConfig {

    @Bean
    public ServletContextInitializer facesContextParametersInitializer() {
        return (final ServletContext servletContext) -> {
            servletContext.setInitParameter("jakarta.faces.PROJECT_STAGE", "Development");
            servletContext.setInitParameter("jakarta.faces.STATE_SAVING_METHOD", "client");
            servletContext.setInitParameter("jakarta.faces.FACELETS_SKIP_COMMENTS", "true");

            servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
            servletContext.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", Boolean.TRUE.toString());

            // WEB-INF/resources/THEME/theme.css
            servletContext.setInitParameter("primefaces.THEME", "arya");
        };
    }

    @Bean
    public ServletRegistrationBean<FacesServlet> facesServletRegistration() {
        final ServletRegistrationBean<FacesServlet> registration = new ServletRegistrationBean<>(new FacesServlet(), "*.xhtml");
        registration.setName("Faces Servlet");
        registration.setLoadOnStartup(1);

        return registration;
    }
}
