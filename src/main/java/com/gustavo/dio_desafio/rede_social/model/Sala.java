package com.gustavo.dio_desafio.rede_social.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Sala {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String nome;

	@Column(nullable = false)
	private int capacidade;

	private String localizacao;

	public Sala(String nome, int capacidade, String localizacao) {
		this.nome = nome;
		this.capacidade = capacidade;
		this.localizacao = localizacao;
	}
}
