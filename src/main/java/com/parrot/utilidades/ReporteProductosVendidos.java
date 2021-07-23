package com.parrot.utilidades;

/*
 * Clase que contiene los valores para generar el reporte de productos vendidos
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */
public class ReporteProductosVendidos {
	
	private String nombreProducto;
	private long cantidadTotal;
	private double precioTotal;
	
	public String getNombreProducto() {
		return nombreProducto;
	}
	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}
	public long getCantidadTotal() {
		return cantidadTotal;
	}
	public void setCantidadTotal(long cantidadTotal) {
		this.cantidadTotal = cantidadTotal;
	}
	public double getPrecioTotal() {
		return precioTotal;
	}
	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}

	
	
}
