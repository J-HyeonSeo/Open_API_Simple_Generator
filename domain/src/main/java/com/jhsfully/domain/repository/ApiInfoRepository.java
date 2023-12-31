package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.type.ApiState;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ApiInfoRepository extends JpaRepository<ApiInfo, Long> {

  int countByMember(Member member);
  int countByMemberAndApiState(Member member, ApiState apiState);
  boolean existsByDataCollectionName(String dataCollectionName);
  Optional<ApiInfo> findByDataCollectionName(String dataCollectionName);
  @Modifying
  @Query(
      "UPDATE ApiInfo api set api.apiState='DISABLED', api.disabledAt= :dateNow where api.member= :member"
  )
  void updateApiInfoToDisabledByMember(@Param("member") Member member, @Param("dateNow") LocalDateTime dateNow);

}
