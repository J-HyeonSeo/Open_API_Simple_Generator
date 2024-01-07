package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.redis.ExcelParseInfo;
import org.springframework.data.repository.CrudRepository;

public interface ExcelParseInfoRepository extends CrudRepository<ExcelParseInfo, String> {
}
