package com.gustavo.dio_desafio.rede_social.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.gustavo.dio_desafio.rede_social.exception.AcessoNegadoException;
import com.gustavo.dio_desafio.rede_social.exception.ReservaConflitoException;
import com.gustavo.dio_desafio.rede_social.exception.ReservaInvalidaException;
import com.gustavo.dio_desafio.rede_social.exception.ReservaNaoEncontradaException;
import com.gustavo.dio_desafio.rede_social.exception.SalaNaoEncontradaException;
import com.gustavo.dio_desafio.rede_social.exception.UsuarioNaoEncontradoException;
import com.gustavo.dio_desafio.rede_social.model.Reserva;
import com.gustavo.dio_desafio.rede_social.model.Sala;
import com.gustavo.dio_desafio.rede_social.model.Usuario;
import com.gustavo.dio_desafio.rede_social.repository.ReservaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

	private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 21, 9, 0);
	private static final LocalDateTime INICIO = AGORA.plusDays(1).withHour(10);

	@Mock
	private ReservaRepository reservaRepository;

	@Mock
	private SalaService salaService;

	@Mock
	private UsuarioService usuarioService;

	private ReservaService service;
	private Sala sala;
	private Usuario usuario;

	@BeforeEach
	void setUp() {
		Clock relogioFixo = Clock.fixed(AGORA.atZone(ZONA).toInstant(), ZONA);
		service = new ReservaService(reservaRepository, salaService, usuarioService, relogioFixo);
		sala = new Sala("Sala Azul", 8, "2º andar");
		ReflectionTestUtils.setField(sala, "id", 1L);
		usuario = new Usuario("Ana", "ana@email.com");
		ReflectionTestUtils.setField(usuario, "id", 10L);
	}

	private void salaEUsuarioExistem() {
		when(salaService.buscarComBloqueio(1L)).thenReturn(sala);
		when(usuarioService.buscarPorId(10L)).thenReturn(usuario);
	}

	private Reserva reservaDe(Usuario responsavel) {
		Reserva reserva = new Reserva(sala, responsavel, "Daily", INICIO, INICIO.plusHours(1));
		ReflectionTestUtils.setField(reserva, "id", 100L);
		return reserva;
	}

	@Test
	void criar_comDadosValidos_salvaReservaComTituloSemEspacos() {
		salaEUsuarioExistem();
		when(reservaRepository.existsBySalaIdAndInicioBeforeAndFimAfter(1L, INICIO.plusHours(1), INICIO))
				.thenReturn(false);
		when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		Reserva criada = service.criar(1L, 10L, "  Daily  ", INICIO, INICIO.plusHours(1));

		ArgumentCaptor<Reserva> salva = ArgumentCaptor.forClass(Reserva.class);
		verify(reservaRepository).save(salva.capture());
		assertThat(criada).isSameAs(salva.getValue());
		assertThat(criada.getTitulo()).isEqualTo("Daily");
		assertThat(criada.getSala()).isSameAs(sala);
		assertThat(criada.getResponsavel()).isSameAs(usuario);
		assertThat(criada.getInicio()).isEqualTo(INICIO);
		assertThat(criada.getFim()).isEqualTo(INICIO.plusHours(1));
	}

	@Test
	void criar_comSalaInexistente_lancaNaoEncontradaSemConsultarUsuario() {
		when(salaService.buscarComBloqueio(1L)).thenThrow(new SalaNaoEncontradaException(1L));

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", INICIO, INICIO.plusHours(1)))
				.isInstanceOf(SalaNaoEncontradaException.class);

		verifyNoInteractions(usuarioService, reservaRepository);
	}

	@Test
	void criar_comUsuarioInexistente_lancaNaoEncontradaSemSalvar() {
		when(salaService.buscarComBloqueio(1L)).thenReturn(sala);
		when(usuarioService.buscarPorId(10L)).thenThrow(new UsuarioNaoEncontradoException(10L));

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", INICIO, INICIO.plusHours(1)))
				.isInstanceOf(UsuarioNaoEncontradoException.class);

		verifyNoInteractions(reservaRepository);
	}

	@Test
	void criar_comSalaInexistenteEPeriodoInvalido_priorizaNaoEncontrada() {
		when(salaService.buscarComBloqueio(1L)).thenThrow(new SalaNaoEncontradaException(1L));

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", INICIO, INICIO.minusHours(1)))
				.isInstanceOf(SalaNaoEncontradaException.class);
	}

	@Test
	void criar_comFimIgualAoInicio_lancaReservaInvalida() {
		salaEUsuarioExistem();

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", INICIO, INICIO))
				.isInstanceOf(ReservaInvalidaException.class)
				.hasMessageContaining("posterior");

		verifyNoInteractions(reservaRepository);
	}

	@Test
	void criar_comFimAnteriorAoInicio_lancaReservaInvalida() {
		salaEUsuarioExistem();

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", INICIO, INICIO.minusMinutes(30)))
				.isInstanceOf(ReservaInvalidaException.class)
				.hasMessageContaining("posterior");
	}

	@Test
	void criar_comInicioNoPassado_lancaReservaInvalida() {
		salaEUsuarioExistem();

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", AGORA.minusHours(2), AGORA.minusHours(1)))
				.isInstanceOf(ReservaInvalidaException.class)
				.hasMessageContaining("futuro");

		verifyNoInteractions(reservaRepository);
	}

	@Test
	void criar_comInicioIgualAoAgora_lancaReservaInvalida() {
		salaEUsuarioExistem();

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", AGORA, AGORA.plusHours(1)))
				.isInstanceOf(ReservaInvalidaException.class)
				.hasMessageContaining("futuro");
	}

	@Test
	void criar_comInicioUmMinutoNoFuturo_aceita() {
		salaEUsuarioExistem();
		when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		Reserva criada = service.criar(1L, 10L, "Daily", AGORA.plusMinutes(1), AGORA.plusHours(1));

		assertThat(criada.getInicio()).isEqualTo(AGORA.plusMinutes(1));
	}

	@Test
	void criar_comDuracaoDeQuatroHorasExatas_aceita() {
		salaEUsuarioExistem();
		when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		Reserva criada = service.criar(1L, 10L, "Workshop", INICIO, INICIO.plusHours(4));

		assertThat(criada.getFim()).isEqualTo(INICIO.plusHours(4));
	}

	@Test
	void criar_comDuracaoAcimaDeQuatroHoras_lancaReservaInvalida() {
		salaEUsuarioExistem();

		assertThatThrownBy(() -> service.criar(1L, 10L, "Workshop", INICIO, INICIO.plusHours(4).plusMinutes(1)))
				.isInstanceOf(ReservaInvalidaException.class)
				.hasMessageContaining("4 horas");

		verifyNoInteractions(reservaRepository);
	}

	@Test
	void criar_comPeriodoInvalido_naoConsultaConflito() {
		salaEUsuarioExistem();

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", INICIO, INICIO.plusHours(5)))
				.isInstanceOf(ReservaInvalidaException.class);

		verify(reservaRepository, never()).existsBySalaIdAndInicioBeforeAndFimAfter(anyLong(), any(), any());
	}

	@Test
	void criar_comHorarioSobreposto_lancaConflitoSemSalvar() {
		salaEUsuarioExistem();
		when(reservaRepository.existsBySalaIdAndInicioBeforeAndFimAfter(1L, INICIO.plusHours(1), INICIO))
				.thenReturn(true);

		assertThatThrownBy(() -> service.criar(1L, 10L, "Daily", INICIO, INICIO.plusHours(1)))
				.isInstanceOf(ReservaConflitoException.class)
				.hasMessageContaining("Sala Azul");

		verify(reservaRepository, never()).save(any());
	}

	@Test
	void cancelar_peloResponsavel_removeReserva() {
		Reserva reserva = reservaDe(usuario);
		when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));

		service.cancelar(100L, 10L);

		verify(reservaRepository).delete(reserva);
	}

	@Test
	void cancelar_porOutroUsuario_lancaAcessoNegadoSemRemover() {
		when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaDe(usuario)));

		assertThatThrownBy(() -> service.cancelar(100L, 99L))
				.isInstanceOf(AcessoNegadoException.class);

		verify(reservaRepository, never()).delete(any());
	}

	@Test
	void cancelar_reservaInexistente_lancaNaoEncontrada() {
		when(reservaRepository.findById(100L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.cancelar(100L, 10L))
				.isInstanceOf(ReservaNaoEncontradaException.class);

		verify(reservaRepository, never()).delete(any());
	}

	@Test
	void listarPorSalaEDia_consultaIntervaloDoDiaCompleto() {
		LocalDate dia = LocalDate.of(2026, 10, 22);
		Reserva reserva = reservaDe(usuario);
		when(reservaRepository.findBySalaIdAndInicioBeforeAndFimAfterOrderByInicioAsc(
				1L, LocalDateTime.of(2026, 10, 23, 0, 0), LocalDateTime.of(2026, 10, 22, 0, 0)))
				.thenReturn(List.of(reserva));

		assertThat(service.listarPorSalaEDia(1L, dia)).containsExactly(reserva);
	}

	@Test
	void listarPorUsuario_delegaAoRepository() {
		Reserva reserva = reservaDe(usuario);
		when(reservaRepository.findByResponsavelIdOrderByInicioAsc(10L)).thenReturn(List.of(reserva));

		assertThat(service.listarPorUsuario(10L)).containsExactly(reserva);
	}
}
