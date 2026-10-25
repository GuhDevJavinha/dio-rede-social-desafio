package com.gustavo.dio_desafio.rede_social.controller;

import com.gustavo.dio_desafio.rede_social.dto.response.ReservaResponse;
import com.gustavo.dio_desafio.rede_social.dto.response.SalaResponse;
import com.gustavo.dio_desafio.rede_social.facade.ReservaFacade;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

	private final ReservaFacade facade;

	@GetMapping
	public List<SalaResponse> listar() {
		return facade.listarSalas().stream().map(SalaResponse::de).toList();
	}

	@GetMapping("/{id}/reservas")
	public List<ReservaResponse> listarReservas(
			@PathVariable Long id,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
		return facade.listarReservasDaSala(id, data).stream().map(ReservaResponse::de).toList();
	}
}
