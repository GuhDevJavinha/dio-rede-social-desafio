package com.gustavo.dio_desafio.rede_social.dto.response;

import com.gustavo.dio_desafio.rede_social.model.Sala;

public record SalaResponse(Long id, String nome, int capacidade, String localizacao) {

	public static SalaResponse de(Sala sala) {
		return new SalaResponse(sala.getId(), sala.getNome(), sala.getCapacidade(), sala.getLocalizacao());
	}
}
