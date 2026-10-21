package com.gustavo.dio_desafio.rede_social.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRequest(
		@NotNull(message = "O usuarioId é obrigatório") Long usuarioId,
		@NotBlank(message = "O conteúdo é obrigatório")
		@Size(max = 280, message = "O conteúdo deve ter no máximo 280 caracteres") String conteudo) {
}
