package com.parrot.controlador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.parrot.entidades.Orden;
import com.parrot.entidades.OrdenProducto;
import com.parrot.repositorio.OrdenProductoRepositorio;
import com.parrot.repositorio.OrdenRepositorio;
import com.parrot.repositorio.ProductoRepositorio;
import com.parrot.utilidades.ReporteProductosVendidos;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/*
 * Controlador para generar el reporte de productos vendidos en un rango de días
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */
@RestController
@RequestMapping("/parrot")
public class ReporteProductosControlador {
	
	private static final Logger logger = LoggerFactory.getLogger(ReporteProductosControlador.class);
	
	
	// Repositorios para poder realizar la consulta a base de datos
	@Autowired
	OrdenRepositorio ordenRepositorio;
	
	@Autowired 
	ProductoRepositorio productoRepositorio;
	
	@Autowired
	OrdenProductoRepositorio ordenProductoRepositorio;
	
	
	//Método que recibe las fechas y genera el reporte 
	@GetMapping(value = "/reporteProductosVendidos")
	@ApiOperation(value = "Genera e reporte de productos vendidos.",
	  notes = "El reporte se genera en base a fecha inicio y fecha fin. <br><b>Nota: </b> Se tiene asegurado con JWT por lo que el token debe de estar incluido en los cabeceros.")
	public List<ReporteProductosVendidos> reporteProductosVendidos (
			@ApiParam(
			    name =  "fechaInicio",
			    type = "String",
			    value = "Fecha de INICIO con la que se obtiene el reporte en formato <b>dd/MM/yyyy</b>",
			    example = "01/07/2021")
			@RequestParam("fechaInicio") String sFechaInicio,
			@ApiParam(
			    name =  "fechaFin",
			    type = "String",
			    value = "Fecha de FIN con la que se obtiene el reporte en formato <b>dd/MM/yyyy</b>",
			    example = "31/07/2021")
			@RequestParam("fechaFin") String sFechaFin) throws ParseException{
		
		// Cambiamos las fechas de cadena a Date
		Date fechaInicio=new SimpleDateFormat("dd/MM/yyyy").parse(sFechaInicio);
		Date fechaFin=new SimpleDateFormat("dd/MM/yyyy").parse(sFechaFin); 

		// Sumamos un día para poder contemplar el rango completo
		Calendar c = Calendar.getInstance();
        c.setTime(fechaFin);
        c.add(Calendar.DATE, 1); 
        fechaFin = c.getTime();
		
		
        logger.debug("Fecha inical: {}",fechaInicio);
        logger.debug("Fecha fin: {}",fechaFin);
		
        //Obtenemos las órdenes de los días solicitados
		List<Orden> ordenes = ordenRepositorio.obtenerOrdenesPorFecha(fechaInicio, fechaFin);
		
		//Obtenemos los productos de las órdenes recuperadas
		List<OrdenProducto> ordenesProducto = ordenProductoRepositorio.obtenerProductosPorOrdenes(ordenes);
		
		//De acuerdo a las órdenes obtenemos la lista con la cantidad de cada producto así como el precio unitario de cada uno
		List<ReporteProductosVendidos> reporteProductosVendidosList = new ArrayList<ReporteProductosVendidos>();
		
		for (OrdenProducto ordenProducto : ordenesProducto) {
			
			// A cada producto le asignamos la cantidad y el precio para poder generarlo en el reporte
			String nombreProducto = ordenProducto.getProducto().getNombre();
			ordenProducto.getCantidad();
			ordenProducto.getPrecioUnitario();
			
			//Buscamos si el producto ya existe en la lista de reporte
			ReporteProductosVendidos producto = reporteProductosVendidosList.stream()
					  .filter(reporteProductosVendidos -> nombreProducto.equals(reporteProductosVendidos.getNombreProducto()))
					  .findAny()
					  .orElse(null);
			
			// Si el producto no existe, lo agregamos a la lista
			if (producto==null) {
				
				producto = new ReporteProductosVendidos();
				producto.setNombreProducto(nombreProducto);
				producto.setCantidadTotal(ordenProducto.getCantidad());
				producto.setPrecioTotal(ordenProducto.getPrecioUnitario() * ordenProducto.getCantidad());
				
				reporteProductosVendidosList.add(producto);
			} else {
				
			// En caso contrario, si el producto ya existe en la lista le sumamos la cantidad y el precio total respectivamente	
				producto.setCantidadTotal(producto.getCantidadTotal() + ordenProducto.getCantidad());
				producto.setPrecioTotal(producto.getPrecioTotal() + (ordenProducto.getPrecioUnitario() * ordenProducto.getCantidad()) );
				
			}
			
		}
		
		//Ordenamos el reporte para tenerlo de mayor a menor por la cantidad vendida
		reporteProductosVendidosList.sort(Comparator.comparingDouble(ReporteProductosVendidos::getCantidadTotal)
                .reversed());
		
		// Regresamos el reporte
		return reporteProductosVendidosList;
		
	}

}
