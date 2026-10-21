package com.gustavo.dio_desafio.rede_social.dto.response;

import com.gustavo.dio_desafio.rede_social.model.Post;
import java.time.Instant;

public record PostResponse(Long id, Long usuarioId, String conteudo, Instant criadoEm) {

	public static PostResponse de(Post post) {
		return new PostResponse(post.getId(), post.getUsuario().getId(), post.getConteudo(), post.getCriadoEm());
	}
}
