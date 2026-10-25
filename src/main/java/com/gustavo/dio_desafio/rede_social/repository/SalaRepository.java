package com.gustavo.dio_desafio.rede_social.repository;

import com.gustavo.dio_desafio.rede_social.model.Sala;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalaRepository extends JpaRepository<Sala, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Sala s where s.id = :id")
	Optional<Sala> buscarComBloqueio(@Param("id") Long id);
}
