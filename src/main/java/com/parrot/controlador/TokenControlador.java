package com.parrot.controlador;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.parrot.utilidades.RespuestaGenerica;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/*
 * Controlador para generar el token JWT
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */
@RestController
@RequestMapping("/parrot")
public class TokenControlador {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenControlador.class);
	
	// Obtenemos las credenciales y la llave de encriptación necesarios para validar generar el Token
	@Value("${com.parrot.llaveEncriptacion}")
	private String llaveEncriptacion; 
	
	@Value("${com.parrot.usuarioToken}")
	private String usuarioToken;
	
	@Value("${com.parrot.passwordToken}")
	private String passwordToken; 
	
	
	
	
	// Método que recibe las credenciales y si estas son correctas, regresa el token
	@PostMapping("/generarToken")
	@ApiOperation(value = "Genera el Token con el cuál se consumen los demás servicos.",
	  notes = "<b>Nota: </b> El método no requiere un Token, por lo que en Swagger se le puede enviar cualquier cadena.<br><b>Por conveniencia, en la variable error se regresa el valor del Token.</b>")
	public ResponseEntity<RespuestaGenerica> login(
			@ApiParam(
			    name =  "user",
			    type = "String",
			    value = "Usuario con permisos para generar el Token, en este ambiente el valor correcto a utilizar es <b>usuarioParrot</b>",
			    example = "usuarioParrot")
			@RequestParam("user") String username, 
			@ApiParam(
				    name =  "password",
				    type = "String",
				    value = "Password del usuario para generar el Token, en este ambiente el valor correcto a utilizar es <b>p@sswordParrot</b>",
				    example = "p@sswordParrot")
			@RequestParam("password") String pwd) {
		
		
		
		logger.debug("llaveEncriptacion - dbg: {}",llaveEncriptacion);
		logger.debug("llaveEncriptacion: {}",llaveEncriptacion);
		logger.debug("username: {}",username);
		logger.debug("usuarioToken: {}",usuarioToken);
		logger.debug("password: {}",pwd);
		
		// Instanciamos el desencriptor utilizando la llave de endriptación recuperada anteriormente
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPasswordCharArray(llaveEncriptacion.toCharArray());
		
		// Desencriptamos el password
		logger.debug("password: {}",passwordToken);
		String plainText = textEncryptor.decrypt(passwordToken);
		logger.debug("password: {}",plainText);
		
		// Generamos la respuesta genérica y asignamos el path de seguimiento
		RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
		respuestaGenerica.setPath("/parrot/generarToken");


		// Validamos que las credenciales del usuario sean correctas, si son correctas continuamos el proceso
		if (usuarioToken.equals(username)&&pwd.equals(textEncryptor.decrypt(passwordToken))) {
			
			try {
				// Generamos el token y lo almacenamos en la respuesta genérica
				String token = getJWTToken(username);
				respuestaGenerica.setError(token);
			} catch (Exception e) {
				
				logger.error("Error al generar el token: {}",e.getMessage());
				
				respuestaGenerica.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				respuestaGenerica.setError("Error al generar el token");
				return new ResponseEntity<>(respuestaGenerica, HttpStatus.INTERNAL_SERVER_ERROR);
				
			}
			
			// En caso de éxito asignamos el estatus OK y regresamos la respuesta genérica
			respuestaGenerica.setStatus(HttpStatus.OK.value());
			
			return new ResponseEntity<>(respuestaGenerica, HttpStatus.OK);
		}
		
		// Si la validación de las credenciales no fué correcta se regresa el código de error
		respuestaGenerica.setError("Error de validación de usuario");
		respuestaGenerica.setStatus(HttpStatus.FORBIDDEN.value());
		
		return new ResponseEntity<>(respuestaGenerica, HttpStatus.FORBIDDEN);	
	}

	// Método que genera el JWT
	private String getJWTToken(String username) {
		String secretKey = "parrotKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList("ROLE_USER");
		
		String token = Jwts
				.builder()
				.setSubject(username)
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS512,
						secretKey.getBytes()).compact();

		return "Bearer " + token;
	}

}
