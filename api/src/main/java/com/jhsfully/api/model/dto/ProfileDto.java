package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProfileDto {

    private long memberId;
    private String nickname;
    private String email;
    private String profileUrl;

    public static ProfileDto of(Member entity) {
        return ProfileDto.builder()
            .memberId(entity.getId())
            .nickname(entity.getNickname())
            .email(entity.getEmail())
            .profileUrl(entity.getProfileUrl())
            .build();
    }
}
