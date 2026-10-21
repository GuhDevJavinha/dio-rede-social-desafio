package com.gustavo.dio_desafio.rede_social.exception;

public class CurtidaDuplicadaException extends RuntimeException {

	public CurtidaDuplicadaException(Long usuarioId, Long postId) {
		super("Usuário " + usuarioId + " já curtiu o post " + postId);
	}
}
