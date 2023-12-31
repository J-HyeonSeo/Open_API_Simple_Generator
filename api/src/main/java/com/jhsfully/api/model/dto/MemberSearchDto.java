package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberSearchDto {
    private long memberId;
    private String memberNickname;
    private String memberEmail;
    private String profileUrl;

    public static MemberSearchDto of(Member entity) {
        return MemberSearchDto.builder()
            .memberId(entity.getId())
            .memberNickname(entity.getNickname())
            .memberEmail(entity.getEmail())
            .profileUrl(entity.getProfileUrl())
            .build();
    }
}
