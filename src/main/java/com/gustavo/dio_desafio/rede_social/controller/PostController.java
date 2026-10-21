package com.gustavo.dio_desafio.rede_social.controller;

import com.gustavo.dio_desafio.rede_social.dto.request.CurtidaRequest;
import com.gustavo.dio_desafio.rede_social.dto.request.PostRequest;
import com.gustavo.dio_desafio.rede_social.dto.response.PostFeedResponse;
import com.gustavo.dio_desafio.rede_social.dto.response.PostResponse;
import com.gustavo.dio_desafio.rede_social.facade.RedeSocialFacade;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

	private final RedeSocialFacade facade;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PostResponse criar(@RequestBody @Valid PostRequest request) {
		return PostResponse.de(facade.criarPost(request.usuarioId(), request.conteudo()));
	}

	@GetMapping
	public List<PostFeedResponse> listarFeed(@RequestParam(defaultValue = "recentes") String ordem) {
		return facade.listarFeed(ordem).stream().map(PostFeedResponse::de).toList();
	}

	@PostMapping("/{id}/curtidas")
	@ResponseStatus(HttpStatus.CREATED)
	public void curtir(@PathVariable Long id, @RequestBody @Valid CurtidaRequest request) {
		facade.curtirPost(id, request.usuarioId());
	}
}
