package com.gustavo.dio_desafio.rede_social.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	private Usuario usuario;

	@Column(nullable = false, length = 280)
	private String conteudo;

	@Column(nullable = false, updatable = false)
	private Instant criadoEm;

	public Post(Usuario usuario, String conteudo) {
		this.usuario = usuario;
		this.conteudo = conteudo;
		this.criadoEm = Instant.now();
	}
}
