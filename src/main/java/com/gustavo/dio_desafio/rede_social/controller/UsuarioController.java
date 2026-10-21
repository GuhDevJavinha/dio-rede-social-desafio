package com.gustavo.dio_desafio.rede_social.controller;

import com.gustavo.dio_desafio.rede_social.dto.request.UsuarioRequest;
import com.gustavo.dio_desafio.rede_social.dto.response.UsuarioResponse;
import com.gustavo.dio_desafio.rede_social.facade.RedeSocialFacade;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final RedeSocialFacade facade;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse cadastrar(@RequestBody @Valid UsuarioRequest request) {
		return UsuarioResponse.de(facade.cadastrarUsuario(request.nome(), request.email()));
	}

	@GetMapping
	public List<UsuarioResponse> listar() {
		return facade.listarUsuarios().stream().map(UsuarioResponse::de).toList();
	}
}
