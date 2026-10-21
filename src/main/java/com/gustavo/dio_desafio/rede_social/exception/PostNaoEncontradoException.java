package com.gustavo.dio_desafio.rede_social.exception;

public class PostNaoEncontradoException extends RuntimeException {

	public PostNaoEncontradoException(Long id) {
		super("Post não encontrado: " + id);
	}
}
