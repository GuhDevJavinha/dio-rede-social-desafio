package com.gustavo.dio_desafio.rede_social.dto.response;

import com.gustavo.dio_desafio.rede_social.model.PostFeed;
import java.time.Instant;

public record PostFeedResponse(
		Long id,
		Long usuarioId,
		String usuarioNome,
		String conteudo,
		Instant criadoEm,
		long totalCurtidas) {

	public static PostFeedResponse de(PostFeed postFeed) {
		return new PostFeedResponse(
				postFeed.id(),
				postFeed.usuarioId(),
				postFeed.usuarioNome(),
				postFeed.conteudo(),
				postFeed.criadoEm(),
				postFeed.totalCurtidas());
	}
}
