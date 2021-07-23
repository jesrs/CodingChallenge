package com.parrot.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/*
 * Entidad en donde se almacenan las órdenes
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */
@Entity
public class Orden {
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    private Date fecha;
    private double precioTotalOrden;
    private String nombreCliente;
    
    public Orden() {
    	fecha = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getPrecioTotalOrden() {
		return precioTotalOrden;
	}

	public void setPrecioTotalOrden(double precioTotalOrden) {
		this.precioTotalOrden = precioTotalOrden;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
    
    
    
}
