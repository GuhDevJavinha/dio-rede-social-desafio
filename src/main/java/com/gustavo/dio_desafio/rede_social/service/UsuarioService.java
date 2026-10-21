package com.gustavo.dio_desafio.rede_social.service;

import com.gustavo.dio_desafio.rede_social.exception.EmailDuplicadoException;
import com.gustavo.dio_desafio.rede_social.exception.UsuarioNaoEncontradoException;
import com.gustavo.dio_desafio.rede_social.model.Usuario;
import com.gustavo.dio_desafio.rede_social.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	@Transactional
	public Usuario cadastrar(String nome, String email) {
		String emailNormalizado = email.trim().toLowerCase();
		if (usuarioRepository.existsByEmail(emailNormalizado)) {
			throw new EmailDuplicadoException(emailNormalizado);
		}
		return usuarioRepository.save(new Usuario(nome.trim(), emailNormalizado));
	}

	@Transactional(readOnly = true)
	public List<Usuario> listar() {
		return usuarioRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Usuario buscarPorId(Long id) {
		return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException(id));
	}
}
