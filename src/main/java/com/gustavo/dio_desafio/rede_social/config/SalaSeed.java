package com.gustavo.dio_desafio.rede_social.config;

import com.gustavo.dio_desafio.rede_social.model.Sala;
import com.gustavo.dio_desafio.rede_social.repository.SalaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SalaSeed implements ApplicationRunner {

	private final SalaRepository salaRepository;

	@Override
	public void run(ApplicationArguments args) {
		if (salaRepository.count() == 0) {
			salaRepository.saveAll(List.of(
					new Sala("Sala Azul", 8, "2º andar"),
					new Sala("Sala Verde", 4, "1º andar")));
		}
	}
}
