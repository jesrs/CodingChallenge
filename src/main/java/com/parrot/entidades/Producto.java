package com.parrot.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/*
 * Entidad en donde se almacenan los productos
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */
@Entity
public class Producto {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String nombre;
	
	
	
	public Producto() {
		super();
	}
	
	
	
	public Producto(String nombre) {
		super();
		this.nombre = nombre;
	}



	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	

}
