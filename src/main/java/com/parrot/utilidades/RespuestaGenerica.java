package com.parrot.utilidades;

import java.sql.Timestamp;

/*
 * Respuesta genérica para entregar en peticiones Post
 * Jesús Rodríguez Salazar jesrs@yahoo.com
 * v1.0
 * Fecha de creación: 22/07/2021
 */
public class RespuestaGenerica {
	
	private Timestamp timestamp; 
	private int status;
	private String error;
	private String path;
	
	
	
	public RespuestaGenerica() {
		timestamp = new Timestamp(System.currentTimeMillis());
	}
	
	public Timestamp getTimestap() {
		return timestamp;
	}
	public void setTimestap(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	

}
