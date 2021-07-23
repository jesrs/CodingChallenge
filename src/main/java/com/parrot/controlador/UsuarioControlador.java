package com.parrot.controlador;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parrot.entidades.Usuario;
import com.parrot.repositorio.UsuarioRepositorio;
import com.parrot.utilidades.RespuestaGenerica;
import com.parrot.utilidades.UsuarioAlta;

import io.swagger.annotations.ApiOperation;

/*
 * Controlador para generar el usuario que dará de alta las órdenes
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */
@RestController
@RequestMapping("/parrot")
public class UsuarioControlador {
	
	private static final Logger logger = LoggerFactory.getLogger(UsuarioControlador.class);
	
	// Repositorio para comunicación con la base de datos
	@Autowired
	UsuarioRepositorio usuarioRepositorio;
	
	// Método que da de alta el usuario
	@PostMapping(value = "/crearUsuario")
	@ApiOperation(value = "Alta de usuario que va a crear las órdenes.",
	  notes = "Este método recibe el nombre y el correo electrónico del usuario; en caso de recibir un correo electrónico que ya existe, se regresa un código 400 con la leyenda correspondiente. <br><b>Nota: </b> Se tiene asegurado con JWT por lo que el token debe de estar incluido en los cabeceros.")
	public ResponseEntity<RespuestaGenerica> crearUsuario(@RequestBody UsuarioAlta usuarioAlta) {
		
		// Instanciamos la entidad de usuario con los parámetros recibidos
		Usuario usuario = new Usuario();
		usuario.setCorreoElectronico(usuarioAlta.getCorreoElectronico());
		usuario.setNombre(usuarioAlta.getNombre());
		
		// Creamos la instancia genérica y asignamos el path de seguimiento
		RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
		respuestaGenerica.setPath("/parrot/crearUsuario");
		
		// Se crea el partón para validar que el correo electrónico esté en formato correcto
		Pattern pattern = Pattern
				.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		
		// Sl el correo electrónico no tiene el formato correcto se termina la operación y se regresa el código de error
		if (!pattern.matcher(usuario.getCorreoElectronico()).find()) {
			respuestaGenerica.setError("Formato de correo no válido");
			respuestaGenerica.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(respuestaGenerica, HttpStatus.BAD_REQUEST);
		}
		
		try {
			// Se guarda el usuario
			usuarioRepositorio.save(usuario);
		} catch (DataIntegrityViolationException diEx) {
			
			// En caso de que el correo electrónico ya exista se genera el código de error y se regresa el mensaje con la especificación
			logger.error("Error de correo duplicado: "+ diEx.getMessage());
			
			respuestaGenerica.setError("Correo electrónico duplicado");
			respuestaGenerica.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(respuestaGenerica, HttpStatus.BAD_REQUEST);
			
		} catch (Exception e){

			// Si ocurrió un error inesperado se muestra el mismo con el código de error
			logger.error("Error al generar el usuario: "+ e.getMessage());
			
			respuestaGenerica.setError("Error al generar el usuario");
			respuestaGenerica.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<>(respuestaGenerica, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// En caso de que todo sea correcto, se regresa el código de éxito
		respuestaGenerica.setStatus(HttpStatus.OK.value());
		logger.debug("Usuario creado correctamente");
        return new ResponseEntity<>(respuestaGenerica, HttpStatus.OK);
    }

}
