package com.gustavo.dio_desafio.rede_social.repository;

import com.gustavo.dio_desafio.rede_social.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
