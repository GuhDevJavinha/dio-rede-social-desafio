package com.gustavo.dio_desafio.rede_social.dto.response;

import com.gustavo.dio_desafio.rede_social.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email) {

	public static UsuarioResponse de(Usuario usuario) {
		return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
	}
}
