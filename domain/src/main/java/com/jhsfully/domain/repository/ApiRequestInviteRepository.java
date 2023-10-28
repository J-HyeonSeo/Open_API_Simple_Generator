package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.type.ApiRequestStateType;
import com.jhsfully.domain.type.ApiRequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ApiRequestInviteRepository extends JpaRepository<ApiRequestInvite, Long> {

  Page<ApiRequestInvite> findByMemberAndApiRequestType(
      Member member, ApiRequestType requestType, Pageable pageable);

  Page<ApiRequestInvite> findByApiInfoAndApiRequestType(
      ApiInfo apiInfo, ApiRequestType requestType, Pageable pageable);

  Optional<ApiRequestInvite> findByMemberAndApiInfoAndApiRequestTypeAndRequestStateType(
      Member member, ApiInfo apiInfo, ApiRequestType requestType, ApiRequestStateType requestStateType);
}
