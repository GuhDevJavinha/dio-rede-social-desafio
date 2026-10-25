package com.gustavo.dio_desafio.rede_social.exception;

public class ReservaConflitoException extends RuntimeException {

	public ReservaConflitoException(String salaNome) {
		super("Já existe uma reserva neste horário para a sala " + salaNome);
	}
}
