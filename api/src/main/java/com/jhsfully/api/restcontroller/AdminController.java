package com.jhsfully.api.restcontroller;

import com.jhsfully.api.security.TokenProvider;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *  관리자 기능을 수행하는 컨트롤러
 */
@RestController
public class AdminController {



  /*
      테스트를 위한 토큰 발급
      TODO 나중에 반드시 지워야함!
   */
  @Autowired
  private TokenProvider tokenProvider;
  @Autowired
  private MemberRepository memberRepository;
  @GetMapping("/test/{memberId}")
  public ResponseEntity<?> getAccessToken(@PathVariable long memberId){
    Member member = memberRepository.findById(memberId).orElseThrow();
    return ResponseEntity.ok(
        tokenProvider.generateAccessToken(member.getId(), member.isAdmin())
    );
  }

}
