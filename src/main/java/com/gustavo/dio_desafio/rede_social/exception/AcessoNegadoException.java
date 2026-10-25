package com.gustavo.dio_desafio.rede_social.exception;

public class AcessoNegadoException extends RuntimeException {

	public AcessoNegadoException(Long reservaId) {
		super("Apenas o responsável pode cancelar a reserva " + reservaId);
	}
}
