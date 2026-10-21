package com.gustavo.dio_desafio.rede_social.service;

import com.gustavo.dio_desafio.rede_social.exception.CurtidaDuplicadaException;
import com.gustavo.dio_desafio.rede_social.model.Curtida;
import com.gustavo.dio_desafio.rede_social.model.Post;
import com.gustavo.dio_desafio.rede_social.model.Usuario;
import com.gustavo.dio_desafio.rede_social.repository.CurtidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurtidaService {

	private final CurtidaRepository curtidaRepository;
	private final UsuarioService usuarioService;
	private final PostService postService;

	@Transactional
	public Curtida curtir(Long postId, Long usuarioId) {
		Post post = postService.buscarPorId(postId);
		Usuario usuario = usuarioService.buscarPorId(usuarioId);
		if (curtidaRepository.existsByUsuarioIdAndPostId(usuarioId, postId)) {
			throw new CurtidaDuplicadaException(usuarioId, postId);
		}
		return curtidaRepository.save(new Curtida(usuario, post));
	}

	@Transactional(readOnly = true)
	public long contarPorPost(Long postId) {
		return curtidaRepository.countByPostId(postId);
	}
}
