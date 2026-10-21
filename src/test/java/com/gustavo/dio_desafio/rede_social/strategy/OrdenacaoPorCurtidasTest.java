package com.gustavo.dio_desafio.rede_social.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.gustavo.dio_desafio.rede_social.enums.OrdemFeed;
import com.gustavo.dio_desafio.rede_social.model.PostFeed;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrdenacaoPorCurtidasTest {

	private final OrdenacaoPorCurtidas estrategia = new OrdenacaoPorCurtidas();

	@Test
	void informaAOrdemCurtidas() {
		assertThat(estrategia.ordem()).isEqualTo(OrdemFeed.CURTIDAS);
	}

	@Test
	void ordenaDoMaisCurtidoParaOMenosCurtido() {
		PostFeed poucasCurtidas = post(1L, Instant.parse("2026-01-02T10:00:00Z"), 1);
		PostFeed muitasCurtidas = post(2L, Instant.parse("2026-01-01T10:00:00Z"), 5);

		List<PostFeed> ordenados = estrategia.ordenar(List.of(poucasCurtidas, muitasCurtidas));

		assertThat(ordenados).containsExactly(muitasCurtidas, poucasCurtidas);
	}

	@Test
	void emCasoDeEmpateDeCurtidasUsaOMaisRecentePrimeiro() {
		PostFeed antigo = post(1L, Instant.parse("2026-01-01T10:00:00Z"), 3);
		PostFeed recente = post(2L, Instant.parse("2026-01-02T10:00:00Z"), 3);

		List<PostFeed> ordenados = estrategia.ordenar(List.of(antigo, recente));

		assertThat(ordenados).containsExactly(recente, antigo);
	}

	@Test
	void emCasoDeEmpateTotalUsaOMaiorIdPrimeiro() {
		Instant momento = Instant.parse("2026-01-01T10:00:00Z");
		PostFeed primeiro = post(1L, momento, 3);
		PostFeed segundo = post(2L, momento, 3);

		List<PostFeed> ordenados = estrategia.ordenar(List.of(primeiro, segundo));

		assertThat(ordenados).containsExactly(segundo, primeiro);
	}

	private PostFeed post(Long id, Instant criadoEm, long totalCurtidas) {
		return new PostFeed(id, 1L, "Ana", "Conteúdo", criadoEm, totalCurtidas);
	}
}
