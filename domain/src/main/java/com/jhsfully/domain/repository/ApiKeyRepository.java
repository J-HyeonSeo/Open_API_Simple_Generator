package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiKey;
import com.jhsfully.domain.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
  Optional<ApiKey> findByApiInfoAndMember(ApiInfo apiInfo, Member member);

  Optional<ApiKey> findByApiInfoAndAuthKey(ApiInfo apiInfo, String authKey);
}
