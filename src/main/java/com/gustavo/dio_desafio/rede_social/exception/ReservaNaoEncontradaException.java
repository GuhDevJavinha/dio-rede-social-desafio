package com.gustavo.dio_desafio.rede_social.exception;

public class ReservaNaoEncontradaException extends RuntimeException {

	public ReservaNaoEncontradaException(Long id) {
		super("Reserva não encontrada: " + id);
	}
}
