package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.BlackList;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BlackListDto {
  private long apiId;
  private long memberId;
  private String memberEmail;
  private LocalDateTime registeredAt;

  public static BlackListDto of(BlackList entity){
    return BlackListDto.builder()
        .apiId(entity.getId())
        .memberId(entity.getMember().getId())
        .memberEmail(entity.getMember().getEmail())
        .registeredAt(entity.getRegisteredAt())
        .build();
  }
}
