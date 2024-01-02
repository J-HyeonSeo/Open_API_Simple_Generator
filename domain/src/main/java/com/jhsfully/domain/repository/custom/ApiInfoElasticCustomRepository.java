package com.jhsfully.domain.repository.custom;

import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.type.SearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApiInfoElasticCustomRepository {
  Page<ApiInfoElastic> search(String searchText, SearchType type, Pageable pageable);
  Page<ApiInfoElastic> searchForOwner(Long ownerMemberId, String searchText, SearchType type, Pageable pageable);
  Page<ApiInfoElastic> searchForAccessor(Long accessMemberId, String searchText, SearchType type, Pageable pageable);
  void changeToDisabledByMemberId(Long memberId);
  void deleteAccessors(Long apiInfoId);
}
