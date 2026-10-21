package com.gustavo.dio_desafio.rede_social.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
		@NotBlank(message = "O nome é obrigatório") String nome,
		@NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") String email) {
}
