package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.dto.MemberSearchDto;
import com.jhsfully.api.model.dto.ProfileDto;
import com.jhsfully.api.service.MemberService;
import com.jhsfully.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public ProfileDto getProfile(long memberId) {
        return ProfileDto.of(memberRepository.findById(memberId)
            .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND)));
    }

    @Override
    public MemberSearchDto memberSearch(String email) {
        return MemberSearchDto.of(memberRepository.findByEmail(email)
            .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND)));
    }


}
