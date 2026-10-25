package com.gustavo.dio_desafio.rede_social.exception;

public class SalaNaoEncontradaException extends RuntimeException {

	public SalaNaoEncontradaException(Long id) {
		super("Sala não encontrada: " + id);
	}
}
