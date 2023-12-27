package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.dto.ProfileDto;
import com.jhsfully.api.service.MemberService;
import com.jhsfully.api.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile() {
        long memberId = MemberUtil.getMemberId();
        return ResponseEntity.ok(memberService.getProfile(memberId));
    }

}
