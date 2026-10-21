package com.gustavo.dio_desafio.rede_social.strategy;

import com.gustavo.dio_desafio.rede_social.enums.OrdemFeed;
import com.gustavo.dio_desafio.rede_social.model.PostFeed;
import java.util.List;

public interface OrdenacaoFeedStrategy {

	OrdemFeed ordem();

	List<PostFeed> ordenar(List<PostFeed> posts);
}
