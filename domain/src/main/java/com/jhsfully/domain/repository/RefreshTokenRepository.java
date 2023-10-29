package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
  List<RefreshToken> findByEmail(String email);
}
