package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiInfoRepository extends JpaRepository<ApiInfo, Long> {

  @Modifying
  @Query(
      "UPDATE ApiInfo api set api.apiState=DISABLED where api.member=?0"
  )
  void updateApiInfoToDisabledByMember(Member member);

}
