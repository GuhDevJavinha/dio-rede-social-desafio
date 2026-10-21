package com.gustavo.dio_desafio.rede_social.exception;

public class EmailDuplicadoException extends RuntimeException {

	public EmailDuplicadoException(String email) {
		super("E-mail já cadastrado: " + email);
	}
}
