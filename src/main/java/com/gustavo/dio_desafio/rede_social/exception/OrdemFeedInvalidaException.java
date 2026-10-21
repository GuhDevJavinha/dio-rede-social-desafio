package com.gustavo.dio_desafio.rede_social.exception;

public class OrdemFeedInvalidaException extends RuntimeException {

	public OrdemFeedInvalidaException(String ordem) {
		super("Ordem inválida: '" + ordem + "'. Use 'recentes' ou 'curtidas'");
	}
}
