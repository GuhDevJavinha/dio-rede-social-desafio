package com.gustavo.dio_desafio.rede_social.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.gustavo.dio_desafio.rede_social.enums.OrdemFeed;
import com.gustavo.dio_desafio.rede_social.model.PostFeed;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrdenacaoPorRecentesTest {

	private final OrdenacaoPorRecentes estrategia = new OrdenacaoPorRecentes();

	@Test
	void informaAOrdemRecentes() {
		assertThat(estrategia.ordem()).isEqualTo(OrdemFeed.RECENTES);
	}

	@Test
	void ordenaDoMaisRecenteParaOMaisAntigo() {
		PostFeed antigo = post(1L, Instant.parse("2026-01-01T10:00:00Z"), 10);
		PostFeed recente = post(2L, Instant.parse("2026-01-02T10:00:00Z"), 0);

		List<PostFeed> ordenados = estrategia.ordenar(List.of(antigo, recente));

		assertThat(ordenados).containsExactly(recente, antigo);
	}

	@Test
	void emCasoDeMesmoInstanteUsaOMaiorIdPrimeiro() {
		Instant momento = Instant.parse("2026-01-01T10:00:00Z");
		PostFeed primeiro = post(1L, momento, 0);
		PostFeed segundo = post(2L, momento, 0);

		List<PostFeed> ordenados = estrategia.ordenar(List.of(primeiro, segundo));

		assertThat(ordenados).containsExactly(segundo, primeiro);
	}

	private PostFeed post(Long id, Instant criadoEm, long totalCurtidas) {
		return new PostFeed(id, 1L, "Ana", "Conteúdo", criadoEm, totalCurtidas);
	}
}
