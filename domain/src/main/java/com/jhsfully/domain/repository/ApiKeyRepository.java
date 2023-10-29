package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiKey;
import com.jhsfully.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
  Optional<ApiKey> findByApiInfoAndMember(ApiInfo apiInfo, Member member);

  Optional<ApiKey> findByApiInfoAndAuthKey(ApiInfo apiInfo, String authKey);
}
