package com.parrot.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parrot.entidades.Orden;

public interface OrdenRepositorio  extends JpaRepository<Orden, Long>{
	
	@Query("SELECT u FROM Orden u WHERE u.fecha >= :fechaInicio and u.fecha <=:fechaFin")
	public List<Orden> obtenerOrdenesPorFecha (
			@Param("fechaInicio") Date fechaInicio,
			@Param("fechaFin") Date fechaFin
			);

}
