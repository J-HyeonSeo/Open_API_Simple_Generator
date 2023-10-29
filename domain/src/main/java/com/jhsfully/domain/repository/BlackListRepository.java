package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.BlackList;
import com.jhsfully.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BlackListRepository extends JpaRepository<BlackList, Long> {

  Optional<BlackList> findByApiInfoAndMember(ApiInfo apiInfo, Member member);
  Page<BlackList> findByApiInfo(ApiInfo apiInfo, Pageable pageable);
}
