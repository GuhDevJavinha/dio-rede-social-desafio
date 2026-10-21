package com.gustavo.dio_desafio.rede_social.facade;

import com.gustavo.dio_desafio.rede_social.enums.OrdemFeed;
import com.gustavo.dio_desafio.rede_social.model.Post;
import com.gustavo.dio_desafio.rede_social.model.PostFeed;
import com.gustavo.dio_desafio.rede_social.model.Usuario;
import com.gustavo.dio_desafio.rede_social.service.CurtidaService;
import com.gustavo.dio_desafio.rede_social.service.PostService;
import com.gustavo.dio_desafio.rede_social.service.UsuarioService;
import com.gustavo.dio_desafio.rede_social.strategy.OrdenacaoFeedStrategy;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RedeSocialFacade {

	private final UsuarioService usuarioService;
	private final PostService postService;
	private final CurtidaService curtidaService;
	private final Map<OrdemFeed, OrdenacaoFeedStrategy> estrategiasPorOrdem = new EnumMap<>(OrdemFeed.class);

	public RedeSocialFacade(
			UsuarioService usuarioService,
			PostService postService,
			CurtidaService curtidaService,
			List<OrdenacaoFeedStrategy> estrategias) {
		this.usuarioService = usuarioService;
		this.postService = postService;
		this.curtidaService = curtidaService;
		estrategias.forEach(estrategia -> estrategiasPorOrdem.put(estrategia.ordem(), estrategia));
	}

	public Usuario cadastrarUsuario(String nome, String email) {
		return usuarioService.cadastrar(nome, email);
	}

	public List<Usuario> listarUsuarios() {
		return usuarioService.listar();
	}

	public Post criarPost(Long usuarioId, String conteudo) {
		return postService.criar(usuarioId, conteudo);
	}

	@Transactional(readOnly = true)
	public List<PostFeed> listarFeed(String ordem) {
		OrdenacaoFeedStrategy estrategia = estrategiasPorOrdem.get(OrdemFeed.de(ordem));
		List<PostFeed> feed = postService.listar().stream()
				.map(post -> PostFeed.de(post, curtidaService.contarPorPost(post.getId())))
				.toList();
		return estrategia.ordenar(feed);
	}

	public void curtirPost(Long postId, Long usuarioId) {
		curtidaService.curtir(postId, usuarioId);
	}
}
