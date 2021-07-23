package com.parrot.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parrot.entidades.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long>{
	

	@Query("SELECT u FROM Usuario u WHERE u.nombre = :nombre")
	public Usuario encontrarUsuarioPorNombre(
	  @Param("nombre") String nombre);

}
