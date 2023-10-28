package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ApiUserPermissionRepository extends JpaRepository<ApiUserPermission, Long> {
  Optional<ApiUserPermission> findByApiInfoAndMember(ApiInfo apiInfo, Member member);
  Page<ApiUserPermission> findByApiInfo(ApiInfo apiInfo, Pageable pageable);
  int countByApiInfo(ApiInfo apiInfo);
}
