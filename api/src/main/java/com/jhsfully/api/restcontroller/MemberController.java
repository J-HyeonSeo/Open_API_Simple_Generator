package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.dto.MemberSearchDto;
import com.jhsfully.api.model.dto.ProfileDto;
import com.jhsfully.api.model.member.NicknameChangeInput;
import com.jhsfully.api.service.MemberService;
import com.jhsfully.api.util.MemberUtil;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/search")
    public ResponseEntity<MemberSearchDto> memberSearch(
        @RequestParam String email
    ) {
        return ResponseEntity.ok(memberService.memberSearch(email));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<?> changeNickname(@RequestBody @Valid NicknameChangeInput input) {
        long memberId = MemberUtil.getMemberId();
        memberService.changeNickname(memberId, input.getNickname());
        return ResponseEntity.ok().build();
    }

}
