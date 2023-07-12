package br.com.uniamerica.estacionamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@SpringBootApplication
public class EstacionamentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstacionamentoApplication.class, args);

	}
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("*")
				.allowedHeaders("*")
				.allowCredentials(true);
	}
}
