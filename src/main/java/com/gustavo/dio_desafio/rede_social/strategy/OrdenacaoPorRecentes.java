package com.gustavo.dio_desafio.rede_social.strategy;

import com.gustavo.dio_desafio.rede_social.enums.OrdemFeed;
import com.gustavo.dio_desafio.rede_social.model.PostFeed;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrdenacaoPorRecentes implements OrdenacaoFeedStrategy {

	private static final Comparator<PostFeed> MAIS_RECENTES_PRIMEIRO = Comparator
			.comparing(PostFeed::criadoEm).reversed()
			.thenComparing(Comparator.comparing(PostFeed::id).reversed());

	@Override
	public OrdemFeed ordem() {
		return OrdemFeed.RECENTES;
	}

	@Override
	public List<PostFeed> ordenar(List<PostFeed> posts) {
		return posts.stream().sorted(MAIS_RECENTES_PRIMEIRO).toList();
	}
}
