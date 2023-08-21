package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.RefreshToken;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
  List<RefreshToken> findByEmail(String email);
}
