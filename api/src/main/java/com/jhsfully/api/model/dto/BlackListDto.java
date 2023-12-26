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
  private long id;
  private long memberId;
  private String memberNickname;
  private String profileUrl;
  private LocalDateTime registeredAt;

  public static BlackListDto of(BlackList entity){
    return BlackListDto.builder()
        .id(entity.getId())
        .memberId(entity.getMember().getId())
        .memberNickname(entity.getMember().getEmail())
        .registeredAt(entity.getRegisteredAt())
        .build();
  }
}
