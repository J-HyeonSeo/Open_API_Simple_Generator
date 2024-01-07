package com.jhsfully.domain.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@Builder
@RedisHash(value = "ExcelParseInfo", timeToLive = 60 * 60 * 24)// 1분 -> 1시간 -> 1일 (초단위)
public class ExcelParseInfo {
  @Id
  private String dataCollectionName;
  private int parsedRow; //100(CHUNK SIZE)개 단위로 기록되는 ParsedRow 데이터
  private boolean isDone; //이미 파싱이 끝났는지 확인하기!
}