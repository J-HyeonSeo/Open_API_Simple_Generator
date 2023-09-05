package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiPermissionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiPermissionDetailRepository extends JpaRepository<ApiPermissionDetail, Long> {

}
