package com.jhsfully.domain.repository;

import com.jhsfully.domain.dto.AccessibleDto;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ApiUserPermissionRepository extends JpaRepository<ApiUserPermission, Long> {
  Optional<ApiUserPermission> findByApiInfoAndMember(ApiInfo apiInfo, Member member);
  Page<ApiUserPermission> findByApiInfo(ApiInfo apiInfo, Pageable pageable);
  int countByApiInfo(ApiInfo apiInfo);
  @Query("SELECT new com.jhsfully.domain.dto.AccessibleDto"
      + "(p.apiInfo.id) from ApiUserPermission p "
      + "where p.member.id = :memberId AND p.apiInfo.id IN :apiIdList")
  List<AccessibleDto> findByApiIdListAndMemberId(@Param("apiIdList") List<Long> apiId, @Param("memberId") long memberId);
}
