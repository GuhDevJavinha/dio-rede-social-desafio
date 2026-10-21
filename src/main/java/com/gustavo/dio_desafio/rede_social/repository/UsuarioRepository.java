package com.gustavo.dio_desafio.rede_social.repository;

import com.gustavo.dio_desafio.rede_social.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	boolean existsByEmail(String email);
}
