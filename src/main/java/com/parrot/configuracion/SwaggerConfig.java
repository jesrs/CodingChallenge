package com.parrot.configuracion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*
 * Clase que nos ayuda con la configuración de Swagger
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket api() {
		
		// Se agregan los parámetros para poder enviar el JWT de pruebas en Swagger
		ParameterBuilder parameterBuilder = new ParameterBuilder();
	    parameterBuilder.name("Authorization")
			    .modelRef(new ModelRef("string"))
			    .parameterType("header")
			    .description("JWT token")
			    .required(true)
			    .build();
	    List<Parameter> parameters = new ArrayList<>();
	    parameters.add(parameterBuilder.build());
		
	    // Congiguración en donde se especifica la ubicación de los controladores
		return new Docket (DocumentationType.SWAGGER_2)
				.select()	
				.apis(
						RequestHandlerSelectors
						.basePackage("com.parrot.controlador"))
				.paths(PathSelectors.any())
				.build()
				.globalOperationParameters(parameters);
	}

}
