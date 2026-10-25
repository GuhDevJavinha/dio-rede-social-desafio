package com.gustavo.dio_desafio.rede_social.controller;

import com.gustavo.dio_desafio.rede_social.dto.request.ReservaRequest;
import com.gustavo.dio_desafio.rede_social.dto.response.ReservaResponse;
import com.gustavo.dio_desafio.rede_social.facade.ReservaFacade;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

	private final ReservaFacade facade;

	@PostMapping
	public ResponseEntity<ReservaResponse> criar(@RequestBody @Valid ReservaRequest request) {
		ReservaResponse response = ReservaResponse.de(facade.criarReserva(
				request.salaId(), request.usuarioId(), request.titulo(), request.inicio(), request.fim()));
		return ResponseEntity.created(URI.create("/reservas/" + response.id())).body(response);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelar(@PathVariable Long id, @RequestParam Long usuarioId) {
		facade.cancelarReserva(id, usuarioId);
	}
}
