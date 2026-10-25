package com.gustavo.dio_desafio.rede_social.service;

import com.gustavo.dio_desafio.rede_social.exception.AcessoNegadoException;
import com.gustavo.dio_desafio.rede_social.exception.ReservaConflitoException;
import com.gustavo.dio_desafio.rede_social.exception.ReservaInvalidaException;
import com.gustavo.dio_desafio.rede_social.exception.ReservaNaoEncontradaException;
import com.gustavo.dio_desafio.rede_social.model.Reserva;
import com.gustavo.dio_desafio.rede_social.model.Sala;
import com.gustavo.dio_desafio.rede_social.model.Usuario;
import com.gustavo.dio_desafio.rede_social.repository.ReservaRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservaService {

	private static final Duration DURACAO_MAXIMA = Duration.ofHours(4);

	private final ReservaRepository reservaRepository;
	private final SalaService salaService;
	private final UsuarioService usuarioService;
	private final Clock clock;

	@Transactional
	public Reserva criar(Long salaId, Long usuarioId, String titulo, LocalDateTime inicio, LocalDateTime fim) {
		Sala sala = salaService.buscarComBloqueio(salaId);
		Usuario usuario = usuarioService.buscarPorId(usuarioId);
		validarPeriodo(inicio, fim);
		if (reservaRepository.existsBySalaIdAndInicioBeforeAndFimAfter(salaId, fim, inicio)) {
			throw new ReservaConflitoException(sala.getNome());
		}
		return reservaRepository.save(new Reserva(sala, usuario, titulo.trim(), inicio, fim));
	}

	@Transactional(readOnly = true)
	public List<Reserva> listarPorSalaEDia(Long salaId, LocalDate data) {
		return reservaRepository.findBySalaIdAndInicioBeforeAndFimAfterOrderByInicioAsc(
				salaId, data.plusDays(1).atStartOfDay(), data.atStartOfDay());
	}

	@Transactional(readOnly = true)
	public List<Reserva> listarPorUsuario(Long usuarioId) {
		return reservaRepository.findByResponsavelIdOrderByInicioAsc(usuarioId);
	}

	@Transactional
	public void cancelar(Long reservaId, Long usuarioId) {
		Reserva reserva = reservaRepository.findById(reservaId)
				.orElseThrow(() -> new ReservaNaoEncontradaException(reservaId));
		if (!reserva.pertenceA(usuarioId)) {
			throw new AcessoNegadoException(reservaId);
		}
		reservaRepository.delete(reserva);
	}

	private void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
		if (!fim.isAfter(inicio)) {
			throw new ReservaInvalidaException("O fim da reserva deve ser posterior ao início");
		}
		if (!inicio.isAfter(LocalDateTime.now(clock))) {
			throw new ReservaInvalidaException("O início da reserva deve estar no futuro");
		}
		if (Duration.between(inicio, fim).compareTo(DURACAO_MAXIMA) > 0) {
			throw new ReservaInvalidaException("A reserva deve durar no máximo 4 horas");
		}
	}
}
