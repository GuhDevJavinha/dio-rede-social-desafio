package com.gustavo.dio_desafio.rede_social.model;

import java.time.Instant;

public record PostFeed(
		Long id,
		Long usuarioId,
		String usuarioNome,
		String conteudo,
		Instant criadoEm,
		long totalCurtidas) {

	public static PostFeed de(Post post, long totalCurtidas) {
		return new PostFeed(
				post.getId(),
				post.getUsuario().getId(),
				post.getUsuario().getNome(),
				post.getConteudo(),
				post.getCriadoEm(),
				totalCurtidas);
	}
}
