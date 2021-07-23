package com.parrot.controlador;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.parrot.entidades.Orden;
import com.parrot.entidades.OrdenProducto;
import com.parrot.entidades.Producto;
import com.parrot.entidades.Usuario;
import com.parrot.repositorio.OrdenProductoRepositorio;
import com.parrot.repositorio.OrdenRepositorio;
import com.parrot.repositorio.ProductoRepositorio;
import com.parrot.repositorio.UsuarioRepositorio;
import com.parrot.utilidades.DescripcionOrden;
import com.parrot.utilidades.RespuestaGenerica;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/*
 * Controlador para generar la orden por el usuario; requiere la lista de productos
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */

@RestController
@RequestMapping("/parrot")
public class OrdenControlador {
	
	private static final Logger logger = LoggerFactory.getLogger(OrdenControlador.class);
	
	// Repositorios para poder realizar la comunicación con la base de datos
	@Autowired
	UsuarioRepositorio usuarioRepositorio;
	
	@Autowired
	OrdenRepositorio ordenRepositorio;
	
	@Autowired
	OrdenProductoRepositorio ordenProductoRepositorio;
	
	@Autowired 
	ProductoRepositorio productoRepositorio;
	
	
	// Método que recibe los parámetros necesarios para poder generar la orden
	@PostMapping(value = "/crearOrden")
	@ApiOperation(value = "Creación de órdenes generadas por usuario en específico.",
	  notes = "Recibe el nombre del usuario, el precio total de la orden, el nombre del cliente y la lista de productos (mínimo un producto). <br><b>Nota: </b> Se tiene asegurado con JWT por lo que el token debe de estar incluido en los cabeceros.")
	public ResponseEntity<RespuestaGenerica> crearOrdenPorUsuario (
			@ApiParam(
			    name =  "nombreUsuario",
			    type = "String",
			    value = "Nombre del usuario que creará la orden",
			    example = "Jesús")
				@RequestParam("nombreUsuario")String nombreUsuario,
			@ApiParam(
			    name =  "precioTotalOrden",
			    type = "Double",
			    value = "El total a pagar por la orden",
			    example = "1285.36")
				@RequestParam("precioTotalOrden")Double precioTotalOrden,
			@ApiParam(
			    name =  "nombreCliente",
			    type = "String",
			    value = "Nombre del cliente que solicita la orden",
			    example = "Mary Carmen")
				@RequestParam("nombreCliente")String nombreCliente,
			@RequestBody List<DescripcionOrden> listaProductos){
		
		// Instanciamos la respuesta genérica y asignamos el Path con el que se está trabajando
		RespuestaGenerica respuestaGenerica = new RespuestaGenerica();
		respuestaGenerica.setPath("/parrot/crearOrden");
		
		logger.debug("Usuario: {}", nombreUsuario);
		
		Orden orden = null;
		
		// Validamos la lista de productos que no esté vacía
		if (listaProductos == null || listaProductos.isEmpty()) {
			respuestaGenerica.setError("Lista de productos vacía");
			respuestaGenerica.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(respuestaGenerica, HttpStatus.BAD_REQUEST);
			
		}
		
		// Validamos que el usuario que se recibió exista en la base de datos
		Usuario usuario = usuarioRepositorio.encontrarUsuarioPorNombre(nombreUsuario);
		
		// Si el usuario existe, procedemos con la operación
		if (usuario != null) {
			
			// Creamos la órden
			orden = new Orden ();
			orden.setUsuario(usuario);
			orden.setPrecioTotalOrden(precioTotalOrden);
			orden.setNombreCliente(nombreCliente);
			ordenRepositorio.save(orden);
			
			logger.debug ("correo electrónico: {}",usuario.getCorreoElectronico());
			
			
			// Obtenemos la lista de productos 
			for (DescripcionOrden descripcionOrden : listaProductos) {
				
				// Validamos que el producto exita, en caso contrario lo agregamos en la base de datos
				Producto producto = productoRepositorio.encontrarProductoPorNombre(descripcionOrden.getNombreProducto());
				if (producto == null) {
					producto = new Producto(descripcionOrden.getNombreProducto());
					productoRepositorio.save(producto);
				}
				
				
				// Generamos la entidad que va a guardar la relación de orden-producto
				OrdenProducto ordenProducto = new OrdenProducto();
				
				ordenProducto.setOrden(orden);
				ordenProducto.setProducto(producto);
				ordenProducto.setCantidad(descripcionOrden.getCantidad());
				ordenProducto.setPrecioUnitario(descripcionOrden.getPrecioUnitario());
				
				// Guardamos la entidad
				ordenProductoRepositorio.save(ordenProducto);
				
				
				logger.debug("Nombre de producto: {}",descripcionOrden.getNombreProducto());
			}
			
		} else {
			// En caso de que el usuario no existe no se generó la operación y se regresa código de error
			respuestaGenerica.setError("Usuario inexistente");
			respuestaGenerica.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(respuestaGenerica, HttpStatus.BAD_REQUEST);
		}
		
		
		//  Si todo el proceso fué correcto, se regresa el codigo de estatus OK
		respuestaGenerica.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(respuestaGenerica, HttpStatus.OK);
	}

}
