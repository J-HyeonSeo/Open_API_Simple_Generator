package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiInfoRepository extends JpaRepository<ApiInfo, Long> {

}
