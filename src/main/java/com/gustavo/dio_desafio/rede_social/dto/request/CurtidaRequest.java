package com.gustavo.dio_desafio.rede_social.dto.request;

import jakarta.validation.constraints.NotNull;

public record CurtidaRequest(@NotNull(message = "O usuarioId é obrigatório") Long usuarioId) {
}
