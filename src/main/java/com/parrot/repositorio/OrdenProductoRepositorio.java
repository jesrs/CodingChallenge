package com.parrot.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parrot.entidades.Orden;
import com.parrot.entidades.OrdenProducto;

public interface OrdenProductoRepositorio extends JpaRepository<OrdenProducto, Long>{
	
	@Query("SELECT u FROM OrdenProducto u WHERE u.orden in (:listaOrdenes)")
	public List<OrdenProducto> obtenerProductosPorOrdenes (
			@Param("listaOrdenes") List<Orden> listaOrdenes
			);

}
