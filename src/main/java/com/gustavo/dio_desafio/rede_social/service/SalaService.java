package com.gustavo.dio_desafio.rede_social.service;

import com.gustavo.dio_desafio.rede_social.exception.SalaNaoEncontradaException;
import com.gustavo.dio_desafio.rede_social.model.Sala;
import com.gustavo.dio_desafio.rede_social.repository.SalaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalaService {

	private final SalaRepository salaRepository;

	@Transactional(readOnly = true)
	public List<Sala> listar() {
		return salaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Sala buscarPorId(Long id) {
		return salaRepository.findById(id).orElseThrow(() -> new SalaNaoEncontradaException(id));
	}

	@Transactional
	public Sala buscarComBloqueio(Long id) {
		return salaRepository.buscarComBloqueio(id).orElseThrow(() -> new SalaNaoEncontradaException(id));
	}
}
