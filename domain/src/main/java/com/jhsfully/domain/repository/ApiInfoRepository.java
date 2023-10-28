package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.type.ApiState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface ApiInfoRepository extends JpaRepository<ApiInfo, Long> {

  int countByMember(Member member);
  int countByMemberAndApiState(Member member, ApiState apiState);

  @Modifying
  @Query(
      "UPDATE ApiInfo api set api.apiState='DISABLED', api.disabledAt= :dateNow where api.member= :member"
  )
  void updateApiInfoToDisabledByMember(@Param("member") Member member, @Param("dateNow")LocalDate dateNow);

}
