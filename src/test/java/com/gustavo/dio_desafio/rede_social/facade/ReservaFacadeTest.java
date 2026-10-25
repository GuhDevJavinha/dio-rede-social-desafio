package com.gustavo.dio_desafio.rede_social.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.gustavo.dio_desafio.rede_social.exception.SalaNaoEncontradaException;
import com.gustavo.dio_desafio.rede_social.exception.UsuarioNaoEncontradoException;
import com.gustavo.dio_desafio.rede_social.model.Reserva;
import com.gustavo.dio_desafio.rede_social.model.Sala;
import com.gustavo.dio_desafio.rede_social.model.Usuario;
import com.gustavo.dio_desafio.rede_social.service.ReservaService;
import com.gustavo.dio_desafio.rede_social.service.SalaService;
import com.gustavo.dio_desafio.rede_social.service.UsuarioService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservaFacadeTest {

	private static final LocalDateTime INICIO = LocalDateTime.of(2040, 1, 10, 10, 0);
	private static final LocalDate DIA = INICIO.toLocalDate();

	@Mock
	private SalaService salaService;

	@Mock
	private ReservaService reservaService;

	@Mock
	private UsuarioService usuarioService;

	private ReservaFacade facade;
	private Reserva reserva;

	@BeforeEach
	void setUp() {
		facade = new ReservaFacade(salaService, reservaService, usuarioService);
		reserva = new Reserva(new Sala("Sala Azul", 8, null), new Usuario("Ana", "ana@email.com"),
				"Daily", INICIO, INICIO.plusHours(1));
	}

	@Test
	void listarSalas_delegaAoSalaService() {
		List<Sala> salas = List.of(new Sala("Sala Azul", 8, null));
		when(salaService.listar()).thenReturn(salas);

		assertThat(facade.listarSalas()).isSameAs(salas);
	}

	@Test
	void listarReservasDaSala_comSalaExistente_validaSalaAntesDeListar() {
		when(reservaService.listarPorSalaEDia(1L, DIA)).thenReturn(List.of(reserva));

		List<Reserva> resultado = facade.listarReservasDaSala(1L, DIA);

		InOrder ordem = inOrder(salaService, reservaService);
		ordem.verify(salaService).buscarPorId(1L);
		ordem.verify(reservaService).listarPorSalaEDia(1L, DIA);
		assertThat(resultado).containsExactly(reserva);
	}

	@Test
	void listarReservasDaSala_comSalaInexistente_lancaNaoEncontradaSemListar() {
		when(salaService.buscarPorId(1L)).thenThrow(new SalaNaoEncontradaException(1L));

		assertThatThrownBy(() -> facade.listarReservasDaSala(1L, DIA))
				.isInstanceOf(SalaNaoEncontradaException.class);

		verifyNoInteractions(reservaService);
	}

	@Test
	void listarReservasDoUsuario_comUsuarioExistente_validaUsuarioAntesDeListar() {
		when(reservaService.listarPorUsuario(10L)).thenReturn(List.of(reserva));

		List<Reserva> resultado = facade.listarReservasDoUsuario(10L);

		InOrder ordem = inOrder(usuarioService, reservaService);
		ordem.verify(usuarioService).buscarPorId(10L);
		ordem.verify(reservaService).listarPorUsuario(10L);
		assertThat(resultado).containsExactly(reserva);
	}

	@Test
	void listarReservasDoUsuario_comUsuarioInexistente_lancaNaoEncontradaSemListar() {
		when(usuarioService.buscarPorId(10L)).thenThrow(new UsuarioNaoEncontradoException(10L));

		assertThatThrownBy(() -> facade.listarReservasDoUsuario(10L))
				.isInstanceOf(UsuarioNaoEncontradoException.class);

		verifyNoInteractions(reservaService);
	}

	@Test
	void criarReserva_delegaTodosOsParametrosAoReservaService() {
		when(reservaService.criar(1L, 10L, "Daily", INICIO, INICIO.plusHours(1))).thenReturn(reserva);

		Reserva criada = facade.criarReserva(1L, 10L, "Daily", INICIO, INICIO.plusHours(1));

		assertThat(criada).isSameAs(reserva);
	}

	@Test
	void cancelarReserva_delegaAoReservaService() {
		facade.cancelarReserva(100L, 10L);

		verify(reservaService).cancelar(100L, 10L);
	}
}
