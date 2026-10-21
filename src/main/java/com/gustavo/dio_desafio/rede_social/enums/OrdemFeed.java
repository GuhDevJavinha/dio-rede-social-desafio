package com.gustavo.dio_desafio.rede_social.enums;

import com.gustavo.dio_desafio.rede_social.exception.OrdemFeedInvalidaException;
import java.util.Arrays;

public enum OrdemFeed {
	RECENTES,
	CURTIDAS;

	public static OrdemFeed de(String valor) {
		if (valor == null || valor.isBlank()) {
			return RECENTES;
		}
		return Arrays.stream(values())
				.filter(ordem -> ordem.name().equalsIgnoreCase(valor.trim()))
				.findFirst()
				.orElseThrow(() -> new OrdemFeedInvalidaException(valor));
	}
}
