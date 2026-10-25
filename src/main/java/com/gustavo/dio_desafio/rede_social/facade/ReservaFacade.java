package com.gustavo.dio_desafio.rede_social.facade;

import com.gustavo.dio_desafio.rede_social.model.Reserva;
import com.gustavo.dio_desafio.rede_social.model.Sala;
import com.gustavo.dio_desafio.rede_social.service.ReservaService;
import com.gustavo.dio_desafio.rede_social.service.SalaService;
import com.gustavo.dio_desafio.rede_social.service.UsuarioService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservaFacade {

	private final SalaService salaService;
	private final ReservaService reservaService;
	private final UsuarioService usuarioService;

	public List<Sala> listarSalas() {
		return salaService.listar();
	}

	public List<Reserva> listarReservasDaSala(Long salaId, LocalDate data) {
		salaService.buscarPorId(salaId);
		return reservaService.listarPorSalaEDia(salaId, data);
	}

	public List<Reserva> listarReservasDoUsuario(Long usuarioId) {
		usuarioService.buscarPorId(usuarioId);
		return reservaService.listarPorUsuario(usuarioId);
	}

	public Reserva criarReserva(Long salaId, Long usuarioId, String titulo, LocalDateTime inicio, LocalDateTime fim) {
		return reservaService.criar(salaId, usuarioId, titulo, inicio, fim);
	}

	public void cancelarReserva(Long reservaId, Long usuarioId) {
		reservaService.cancelar(reservaId, usuarioId);
	}
}
