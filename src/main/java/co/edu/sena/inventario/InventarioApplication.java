package co.edu.sena.inventario;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2servletRegistration() {
        ServletRegistrationBean<JakartaWebServlet> registration = new ServletRegistrationBean<>(new JakartaWebServlet());
        registration.addUrlMappings("/h2-console/*");
        return registration;
    }
}