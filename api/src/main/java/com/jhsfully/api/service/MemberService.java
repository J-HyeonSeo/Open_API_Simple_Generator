package com.jhsfully.api.service;

import com.jhsfully.api.model.dto.MemberSearchDto;
import com.jhsfully.api.model.dto.ProfileDto;

public interface MemberService {
    ProfileDto getProfile(long memberId);
    MemberSearchDto memberSearch(String email);
    void changeNickname(long memberId, String nickname);
}
