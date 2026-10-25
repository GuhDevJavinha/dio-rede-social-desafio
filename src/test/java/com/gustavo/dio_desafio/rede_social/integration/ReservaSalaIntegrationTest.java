package com.gustavo.dio_desafio.rede_social.integration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gustavo.dio_desafio.rede_social.model.Sala;
import com.gustavo.dio_desafio.rede_social.repository.SalaRepository;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class ReservaSalaIntegrationTest {

	private static final AtomicInteger PROXIMO_DIA = new AtomicInteger();
	private static final LocalDate PRIMEIRO_DIA = LocalDate.of(2040, 1, 1);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SalaRepository salaRepository;

	private LocalDate dia;
	private Long salaId;
	private Long usuarioId;

	@BeforeEach
	void setUp() throws Exception {
		dia = PRIMEIRO_DIA.plusDays(PROXIMO_DIA.incrementAndGet() * 3L);
		salaId = salaRepository.save(new Sala("Sala " + UUID.randomUUID(), 6, "3º andar")).getId();
		usuarioId = criarUsuario();
	}

	private Long criarUsuario() throws Exception {
		String corpo = "{\"nome\": \"Ana\", \"email\": \"" + UUID.randomUUID() + "@email.com\"}";
		String resposta = mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		return ((Number) JsonPath.read(resposta, "$.id")).longValue();
	}

	private LocalDateTime horario(int hora) {
		return dia.atTime(hora, 0);
	}

	private String corpoReserva(Long sala, Long usuario, String titulo, LocalDateTime inicio, LocalDateTime fim) {
		return "{\"salaId\": " + sala + ", \"usuarioId\": " + usuario + ", \"titulo\": \"" + titulo
				+ "\", \"inicio\": \"" + inicio + "\", \"fim\": \"" + fim + "\"}";
	}

	private ResultActions reservar(Long sala, Long usuario, String titulo, LocalDateTime inicio, LocalDateTime fim)
			throws Exception {
		return mockMvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON)
				.content(corpoReserva(sala, usuario, titulo, inicio, fim)));
	}

	private ResultActions reservar(String titulo, LocalDateTime inicio, LocalDateTime fim) throws Exception {
		return reservar(salaId, usuarioId, titulo, inicio, fim);
	}

	private Long reservarComSucesso(Long usuario, String titulo, LocalDateTime inicio, LocalDateTime fim)
			throws Exception {
		String resposta = reservar(salaId, usuario, titulo, inicio, fim)
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		return ((Number) JsonPath.read(resposta, "$.id")).longValue();
	}

	private Long reservarComSucesso(String titulo, LocalDateTime inicio, LocalDateTime fim) throws Exception {
		return reservarComSucesso(usuarioId, titulo, inicio, fim);
	}

	@Test
	void listarSalas_aposSubirAAplicacao_retornaSalasDoSeed() throws Exception {
		mockMvc.perform(get("/salas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].nome", hasItems("Sala Azul", "Sala Verde")))
				.andExpect(jsonPath("$[?(@.nome == 'Sala Azul')].capacidade").value(8))
				.andExpect(jsonPath("$[?(@.nome == 'Sala Azul')].localizacao").value("2º andar"))
				.andExpect(jsonPath("$[?(@.nome == 'Sala Azul')].id").isNotEmpty());
	}

	@Test
	void criarReserva_comDadosValidos_retorna201ComCorpoELocation() throws Exception {
		reservar("Daily", horario(10), horario(11))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", startsWith("/reservas/")))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.salaId").value(salaId))
				.andExpect(jsonPath("$.salaNome").isNotEmpty())
				.andExpect(jsonPath("$.usuarioId").value(usuarioId))
				.andExpect(jsonPath("$.usuarioNome").value("Ana"))
				.andExpect(jsonPath("$.titulo").value("Daily"))
				.andExpect(jsonPath("$.inicio").value(horario(10).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.andExpect(jsonPath("$.fim").value(horario(11).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
	}

	@Test
	void criarReserva_comHorarioParcialmenteSobreposto_retorna409() throws Exception {
		reservarComSucesso("Daily", horario(10), horario(12));

		reservar("Outra", horario(11), horario(13))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comHorarioIdentico_retorna409() throws Exception {
		reservarComSucesso("Daily", horario(10), horario(11));

		reservar("Outra", horario(10), horario(11)).andExpect(status().isConflict());
	}

	@Test
	void criarReserva_comHorarioDentroDeReservaExistente_retorna409() throws Exception {
		reservarComSucesso("Longa", horario(9), horario(13));

		reservar("Curta", horario(10), horario(11)).andExpect(status().isConflict());
	}

	@Test
	void criarReserva_comHorarioQueEnvolveReservaExistente_retorna409() throws Exception {
		reservarComSucesso("Curta", horario(10), horario(11));

		reservar("Longa", horario(9), horario(13)).andExpect(status().isConflict());
	}

	@Test
	void criarReserva_encostadaNoFimDeOutra_retorna201() throws Exception {
		reservarComSucesso("Primeira", horario(10), horario(11));

		reservar("Segunda", horario(11), horario(12)).andExpect(status().isCreated());
	}

	@Test
	void criarReserva_encostadaNoInicioDeOutra_retorna201() throws Exception {
		reservarComSucesso("Segunda", horario(11), horario(12));

		reservar("Primeira", horario(10), horario(11)).andExpect(status().isCreated());
	}

	@Test
	void criarReserva_mesmoHorarioEmOutraSala_retorna201() throws Exception {
		reservarComSucesso("Daily", horario(10), horario(11));
		Long outraSalaId = salaRepository.save(new Sala("Sala " + UUID.randomUUID(), 4, null)).getId();

		reservar(outraSalaId, usuarioId, "Daily", horario(10), horario(11)).andExpect(status().isCreated());
	}

	@Test
	void criarReserva_comInicioNoPassado_retorna400() throws Exception {
		LocalDateTime inicio = LocalDateTime.now().minusDays(1);

		reservar("Passado", inicio, inicio.plusHours(1))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comFimIgualAoInicio_retorna400() throws Exception {
		reservar("Zero", horario(10), horario(10))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comFimAnteriorAoInicio_retorna400() throws Exception {
		reservar("Invertida", horario(11), horario(10))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comDuracaoAcimaDeQuatroHoras_retorna400() throws Exception {
		reservar("Longa", horario(8), horario(8).plusHours(4).plusMinutes(1))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comDuracaoDeQuatroHorasExatas_retorna201() throws Exception {
		reservar("Workshop", horario(8), horario(12)).andExpect(status().isCreated());
	}

	@Test
	void criarReserva_semTitulo_retorna400() throws Exception {
		reservar("   ", horario(10), horario(11))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_semCamposObrigatorios_retorna400ComMensagem() throws Exception {
		mockMvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comDataEmFormatoInvalido_retorna400() throws Exception {
		String corpo = "{\"salaId\": " + salaId + ", \"usuarioId\": " + usuarioId
				+ ", \"titulo\": \"Daily\", \"inicio\": \"amanha\", \"fim\": \"depois\"}";

		mockMvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON).content(corpo))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comSalaInexistente_retorna404() throws Exception {
		reservar(999_999L, usuarioId, "Daily", horario(10), horario(11))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comUsuarioInexistente_retorna404() throws Exception {
		reservar(salaId, 999_999L, "Daily", horario(10), horario(11))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void criarReserva_comSalaInexistenteEPeriodoInvalido_retorna404() throws Exception {
		reservar(999_999L, usuarioId, "Daily", horario(11), horario(10)).andExpect(status().isNotFound());
	}

	@Test
	void listarReservasDaSala_retornaApenasReservasDoDiaOrdenadasPorInicio() throws Exception {
		reservarComSucesso("Tarde", horario(15), horario(16));
		reservarComSucesso("Manha", horario(9), horario(10));
		reservarComSucesso("Outro dia", horario(9).plusDays(1), horario(10).plusDays(1));

		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].titulo").value("Manha"))
				.andExpect(jsonPath("$[1].titulo").value("Tarde"))
				.andExpect(jsonPath("$[0].salaNome").isNotEmpty())
				.andExpect(jsonPath("$[0].usuarioNome").value("Ana"));
	}

	@Test
	void listarReservasDaSala_naoRetornaReservasDeOutraSala() throws Exception {
		Long outraSalaId = salaRepository.save(new Sala("Sala " + UUID.randomUUID(), 4, null)).getId();
		reservar(outraSalaId, usuarioId, "Da outra", horario(10), horario(11)).andExpect(status().isCreated());
		reservarComSucesso("Desta", horario(10), horario(11));

		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.toString()))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].titulo").value("Desta"));
	}

	@Test
	void listarReservasDaSala_comReservaQueAtravessaAMeiaNoite_apareceNosDoisDias() throws Exception {
		reservarComSucesso("Virada", horario(23), horario(23).plusHours(2));

		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.toString()))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].titulo").value("Virada"));
		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.plusDays(1).toString()))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].titulo").value("Virada"));
		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.plusDays(2).toString()))
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarReservasDaSala_comReservaQueTerminaExatamenteNaMeiaNoite_naoApareceNoDiaSeguinte() throws Exception {
		reservarComSucesso("Ate meia-noite", horario(22), dia.plusDays(1).atStartOfDay());

		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.toString()))
				.andExpect(jsonPath("$", hasSize(1)));
		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.plusDays(1).toString()))
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarReservasDaSala_comReservaQueComecaExatamenteNaMeiaNoite_apareceApenasNoProprioDia() throws Exception {
		reservarComSucesso("Desde meia-noite", dia.atStartOfDay(), horario(2));

		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.toString()))
				.andExpect(jsonPath("$", hasSize(1)));
		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.minusDays(1).toString()))
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarReservasDaSala_semReservasNoDia_retornaListaVazia() throws Exception {
		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", dia.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarReservasDaSala_comSalaInexistente_retorna404() throws Exception {
		mockMvc.perform(get("/salas/{id}/reservas", 999_999L).param("data", dia.toString()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void listarReservasDaSala_semParametroData_retorna400() throws Exception {
		mockMvc.perform(get("/salas/{id}/reservas", salaId))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void listarReservasDaSala_comDataInvalida_retorna400() throws Exception {
		mockMvc.perform(get("/salas/{id}/reservas", salaId).param("data", "22/10/2040"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void listarReservasDoUsuario_retornaApenasAsDoUsuarioOrdenadasPorInicio() throws Exception {
		Long outroUsuarioId = criarUsuario();
		reservarComSucesso("Depois", horario(15), horario(16));
		reservarComSucesso("Antes", horario(9), horario(10));
		reservarComSucesso(outroUsuarioId, "De outro", horario(12), horario(13));

		mockMvc.perform(get("/usuarios/{id}/reservas", usuarioId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].titulo").value("Antes"))
				.andExpect(jsonPath("$[1].titulo").value("Depois"))
				.andExpect(jsonPath("$[*].usuarioId", containsInAnyOrder(usuarioId.intValue(), usuarioId.intValue())));
	}

	@Test
	void listarReservasDoUsuario_semReservas_retornaListaVazia() throws Exception {
		mockMvc.perform(get("/usuarios/{id}/reservas", usuarioId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void listarReservasDoUsuario_comUsuarioInexistente_retorna404() throws Exception {
		mockMvc.perform(get("/usuarios/{id}/reservas", 999_999L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void cancelarReserva_peloResponsavel_retorna204ELiberaOHorario() throws Exception {
		Long reservaId = reservarComSucesso("Daily", horario(10), horario(11));

		mockMvc.perform(delete("/reservas/{id}", reservaId).param("usuarioId", usuarioId.toString()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/usuarios/{id}/reservas", usuarioId))
				.andExpect(jsonPath("$", hasSize(0)));
		reservar("Nova", horario(10), horario(11)).andExpect(status().isCreated());
	}

	@Test
	void cancelarReserva_porOutroUsuario_retorna403EMantemReserva() throws Exception {
		Long reservaId = reservarComSucesso("Daily", horario(10), horario(11));
		Long outroUsuarioId = criarUsuario();

		mockMvc.perform(delete("/reservas/{id}", reservaId).param("usuarioId", outroUsuarioId.toString()))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());

		mockMvc.perform(get("/usuarios/{id}/reservas", usuarioId))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(reservaId));
	}

	@Test
	void cancelarReserva_comUsuarioInexistente_retorna403() throws Exception {
		Long reservaId = reservarComSucesso("Daily", horario(10), horario(11));

		mockMvc.perform(delete("/reservas/{id}", reservaId).param("usuarioId", "999999"))
				.andExpect(status().isForbidden());
	}

	@Test
	void cancelarReserva_inexistente_retorna404() throws Exception {
		mockMvc.perform(delete("/reservas/{id}", 999_999L).param("usuarioId", usuarioId.toString()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void cancelarReserva_semUsuarioId_retorna400() throws Exception {
		Long reservaId = reservarComSucesso("Daily", horario(10), horario(11));

		mockMvc.perform(delete("/reservas/{id}", reservaId))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").isNotEmpty());
	}

	@Test
	void cancelarReserva_jaCancelada_retorna404() throws Exception {
		Long reservaId = reservarComSucesso("Daily", horario(10), horario(11));
		mockMvc.perform(delete("/reservas/{id}", reservaId).param("usuarioId", usuarioId.toString()))
				.andExpect(status().isNoContent());

		mockMvc.perform(delete("/reservas/{id}", reservaId).param("usuarioId", usuarioId.toString()))
				.andExpect(status().isNotFound());
	}

	@Test
	void erros_retornamApenasOCampoMensagem() throws Exception {
		mockMvc.perform(get("/salas/{id}/reservas", 999_999L).param("data", dia.toString()))
				.andExpect(jsonPath("$.mensagem").isString())
				.andExpect(jsonPath("$.*", hasSize(1)))
				.andExpect(jsonPath("$.mensagem", not("")));
	}
}
