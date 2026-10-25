package com.gustavo.dio_desafio.rede_social.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reserva {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	private Sala sala;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	private Usuario responsavel;

	@Column(nullable = false)
	private String titulo;

	@Column(nullable = false)
	private LocalDateTime inicio;

	@Column(nullable = false)
	private LocalDateTime fim;

	public Reserva(Sala sala, Usuario responsavel, String titulo, LocalDateTime inicio, LocalDateTime fim) {
		this.sala = sala;
		this.responsavel = responsavel;
		this.titulo = titulo;
		this.inicio = inicio;
		this.fim = fim;
	}

	public boolean pertenceA(Long usuarioId) {
		return responsavel.getId().equals(usuarioId);
	}
}
