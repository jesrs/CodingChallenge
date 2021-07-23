package com.parrot.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parrot.entidades.Producto;

public interface ProductoRepositorio extends JpaRepository<Producto, Long>{
	
	@Query("SELECT u FROM Producto u WHERE u.nombre = :nombre")
	public Producto encontrarProductoPorNombre(
	  @Param("nombre") String nombre);

}
