package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiInfoRepository extends JpaRepository<ApiInfo, Long> {

  int countByMember(Member member);

  @Modifying
  @Query(
      "UPDATE ApiInfo api set api.apiState='DISABLED' where api.member= :member"
  )
  void updateApiInfoToDisabledByMember(@Param("member") Member member);

}
