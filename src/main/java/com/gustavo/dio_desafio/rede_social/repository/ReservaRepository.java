package com.gustavo.dio_desafio.rede_social.repository;

import com.gustavo.dio_desafio.rede_social.model.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	boolean existsBySalaIdAndInicioBeforeAndFimAfter(Long salaId, LocalDateTime fim, LocalDateTime inicio);

	@EntityGraph(attributePaths = {"sala", "responsavel"})
	List<Reserva> findBySalaIdAndInicioBeforeAndFimAfterOrderByInicioAsc(
			Long salaId, LocalDateTime fim, LocalDateTime inicio);

	@EntityGraph(attributePaths = {"sala", "responsavel"})
	List<Reserva> findByResponsavelIdOrderByInicioAsc(Long usuarioId);
}
