package com.jhsfully.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "RefreshToken", timeToLive = 60 * 60 * 24 * 14)// 1분 -> 1시간 -> 1일 -> 2주(초 단위)
public class RefreshToken {
  @Id
  private String refreshToken;
  @Indexed
  private String email;
  private boolean isAdmin;
}