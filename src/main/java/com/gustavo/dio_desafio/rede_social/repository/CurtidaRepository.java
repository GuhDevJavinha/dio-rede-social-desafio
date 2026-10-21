package com.gustavo.dio_desafio.rede_social.repository;

import com.gustavo.dio_desafio.rede_social.model.Curtida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurtidaRepository extends JpaRepository<Curtida, Long> {

	boolean existsByUsuarioIdAndPostId(Long usuarioId, Long postId);

	long countByPostId(Long postId);
}
