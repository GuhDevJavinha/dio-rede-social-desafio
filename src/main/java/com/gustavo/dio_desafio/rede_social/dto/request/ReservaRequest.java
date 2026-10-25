package com.gustavo.dio_desafio.rede_social.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservaRequest(
		@NotNull(message = "O salaId é obrigatório") Long salaId,
		@NotNull(message = "O usuarioId é obrigatório") Long usuarioId,
		@NotBlank(message = "O título é obrigatório") String titulo,
		@NotNull(message = "O início é obrigatório") LocalDateTime inicio,
		@NotNull(message = "O fim é obrigatório") LocalDateTime fim) {
}
