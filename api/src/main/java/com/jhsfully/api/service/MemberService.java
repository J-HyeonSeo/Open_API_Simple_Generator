package com.jhsfully.api.service;

import com.jhsfully.api.model.dto.ProfileDto;

public interface MemberService {

    ProfileDto getProfile(long memberId);
}
