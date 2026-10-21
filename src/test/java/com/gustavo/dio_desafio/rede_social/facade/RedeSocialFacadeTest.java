package com.gustavo.dio_desafio.rede_social.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gustavo.dio_desafio.rede_social.exception.OrdemFeedInvalidaException;
import com.gustavo.dio_desafio.rede_social.model.Post;
import com.gustavo.dio_desafio.rede_social.model.PostFeed;
import com.gustavo.dio_desafio.rede_social.model.Usuario;
import com.gustavo.dio_desafio.rede_social.service.CurtidaService;
import com.gustavo.dio_desafio.rede_social.service.PostService;
import com.gustavo.dio_desafio.rede_social.service.UsuarioService;
import com.gustavo.dio_desafio.rede_social.strategy.OrdenacaoPorCurtidas;
import com.gustavo.dio_desafio.rede_social.strategy.OrdenacaoPorRecentes;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RedeSocialFacadeTest {

	@Mock
	private UsuarioService usuarioService;

	@Mock
	private PostService postService;

	@Mock
	private CurtidaService curtidaService;

	private RedeSocialFacade facade;

	@BeforeEach
	void setUp() {
		facade = new RedeSocialFacade(
				usuarioService,
				postService,
				curtidaService,
				List.of(new OrdenacaoPorRecentes(), new OrdenacaoPorCurtidas()));
	}

	@Test
	void cadastraUsuarioDelegandoParaOUsuarioService() {
		Usuario usuario = usuario(1L);
		when(usuarioService.cadastrar("Ana", "ana@email.com")).thenReturn(usuario);

		assertThat(facade.cadastrarUsuario("Ana", "ana@email.com")).isSameAs(usuario);
	}

	@Test
	void listaUsuariosDelegandoParaOUsuarioService() {
		List<Usuario> usuarios = List.of(usuario(1L));
		when(usuarioService.listar()).thenReturn(usuarios);

		assertThat(facade.listarUsuarios()).isSameAs(usuarios);
	}

	@Test
	void criaPostDelegandoParaOPostService() {
		Post post = post(1L, usuario(1L), Instant.now());
		when(postService.criar(1L, "Olá")).thenReturn(post);

		assertThat(facade.criarPost(1L, "Olá")).isSameAs(post);
	}

	@Test
	void curtePostDelegandoParaOCurtidaService() {
		facade.curtirPost(1L, 2L);

		verify(curtidaService).curtir(1L, 2L);
	}

	@Test
	void feedRecentesOrdenaDoMaisRecenteParaOMaisAntigo() {
		Usuario ana = usuario(1L);
		Post antigo = post(1L, ana, Instant.parse("2026-01-01T10:00:00Z"));
		Post recente = post(2L, ana, Instant.parse("2026-01-02T10:00:00Z"));
		when(postService.listar()).thenReturn(List.of(antigo, recente));
		when(curtidaService.contarPorPost(anyLong())).thenReturn(0L);

		List<PostFeed> feed = facade.listarFeed("recentes");

		assertThat(feed).extracting(PostFeed::id).containsExactly(2L, 1L);
	}

	@Test
	void feedCurtidasOrdenaDoMaisCurtidoParaOMenosCurtido() {
		Usuario ana = usuario(1L);
		Post poucasCurtidas = post(1L, ana, Instant.parse("2026-01-02T10:00:00Z"));
		Post muitasCurtidas = post(2L, ana, Instant.parse("2026-01-01T10:00:00Z"));
		when(postService.listar()).thenReturn(List.of(poucasCurtidas, muitasCurtidas));
		when(curtidaService.contarPorPost(1L)).thenReturn(1L);
		when(curtidaService.contarPorPost(2L)).thenReturn(5L);

		List<PostFeed> feed = facade.listarFeed("curtidas");

		assertThat(feed).extracting(PostFeed::id).containsExactly(2L, 1L);
		assertThat(feed).extracting(PostFeed::totalCurtidas).containsExactly(5L, 1L);
	}

	@Test
	void feedSemOrdemUsaRecentes() {
		Usuario ana = usuario(1L);
		Post antigo = post(1L, ana, Instant.parse("2026-01-01T10:00:00Z"));
		Post recente = post(2L, ana, Instant.parse("2026-01-02T10:00:00Z"));
		when(postService.listar()).thenReturn(List.of(antigo, recente));
		when(curtidaService.contarPorPost(anyLong())).thenReturn(0L);

		List<PostFeed> feed = facade.listarFeed(null);

		assertThat(feed).extracting(PostFeed::id).containsExactly(2L, 1L);
	}

	@Test
	void feedComOrdemInvalidaLancaExcecao() {
		assertThatThrownBy(() -> facade.listarFeed("aleatoria"))
				.isInstanceOf(OrdemFeedInvalidaException.class);
	}

	private Usuario usuario(Long id) {
		Usuario usuario = new Usuario("Ana", "ana@email.com");
		ReflectionTestUtils.setField(usuario, "id", id);
		return usuario;
	}

	private Post post(Long id, Usuario usuario, Instant criadoEm) {
		Post post = new Post(usuario, "Conteúdo");
		ReflectionTestUtils.setField(post, "id", id);
		ReflectionTestUtils.setField(post, "criadoEm", criadoEm);
		return post;
	}
}
