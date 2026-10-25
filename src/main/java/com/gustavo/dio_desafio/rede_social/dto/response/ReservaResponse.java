package com.gustavo.dio_desafio.rede_social.dto.response;

import com.gustavo.dio_desafio.rede_social.model.Reserva;
import java.time.LocalDateTime;

public record ReservaResponse(
		Long id,
		Long salaId,
		String salaNome,
		Long usuarioId,
		String usuarioNome,
		String titulo,
		LocalDateTime inicio,
		LocalDateTime fim) {

	public static ReservaResponse de(Reserva reserva) {
		return new ReservaResponse(
				reserva.getId(),
				reserva.getSala().getId(),
				reserva.getSala().getNome(),
				reserva.getResponsavel().getId(),
				reserva.getResponsavel().getNome(),
				reserva.getTitulo(),
				reserva.getInicio(),
				reserva.getFim());
	}
}
