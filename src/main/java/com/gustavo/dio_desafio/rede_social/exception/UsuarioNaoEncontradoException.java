package com.gustavo.dio_desafio.rede_social.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {

	public UsuarioNaoEncontradoException(Long id) {
		super("Usuário não encontrado: " + id);
	}
}
