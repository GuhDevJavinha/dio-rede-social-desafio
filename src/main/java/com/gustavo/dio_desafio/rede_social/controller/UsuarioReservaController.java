package com.gustavo.dio_desafio.rede_social.controller;

import com.gustavo.dio_desafio.rede_social.dto.response.ReservaResponse;
import com.gustavo.dio_desafio.rede_social.facade.ReservaFacade;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioReservaController {

	private final ReservaFacade facade;

	@GetMapping("/{id}/reservas")
	public List<ReservaResponse> listarReservas(@PathVariable Long id) {
		return facade.listarReservasDoUsuario(id).stream().map(ReservaResponse::de).toList();
	}
}
