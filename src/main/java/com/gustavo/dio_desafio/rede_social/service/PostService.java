package com.gustavo.dio_desafio.rede_social.service;

import com.gustavo.dio_desafio.rede_social.exception.PostNaoEncontradoException;
import com.gustavo.dio_desafio.rede_social.model.Post;
import com.gustavo.dio_desafio.rede_social.model.Usuario;
import com.gustavo.dio_desafio.rede_social.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final UsuarioService usuarioService;

	@Transactional
	public Post criar(Long usuarioId, String conteudo) {
		Usuario usuario = usuarioService.buscarPorId(usuarioId);
		return postRepository.save(new Post(usuario, conteudo.trim()));
	}

	@Transactional(readOnly = true)
	public List<Post> listar() {
		return postRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Post buscarPorId(Long id) {
		return postRepository.findById(id).orElseThrow(() -> new PostNaoEncontradoException(id));
	}
}
