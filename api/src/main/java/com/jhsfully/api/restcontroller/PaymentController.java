package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.payment.PaymentReadyResponseForClient;
import com.jhsfully.api.model.payment.PaymentResponse;
import com.jhsfully.api.service.PaymentService;
import com.jhsfully.api.util.MemberUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
     결제 관련 컨트롤러, 결제 조회/수행/환불 에 관련된 컨트롤러
 */

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping("/{pageIdx}/{pageSize}")
  public ResponseEntity<?> getPaymentList(
      @PathVariable int pageIdx,
      @PathVariable int pageSize
  ){
    long memberId = MemberUtil.getMemberId();
    PaymentResponse response = paymentService.getPaymentList(memberId,
        PageRequest.of(pageIdx, pageSize, Sort.by("paidAt").descending()));
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{gradeId}")
  public ResponseEntity<?> payment(
      @PathVariable long gradeId
  ){
    long memberId = MemberUtil.getMemberId();
    PaymentReadyResponseForClient urlResponse = paymentService.payment(memberId, gradeId);
    return ResponseEntity.ok(urlResponse);
  }

  @PatchMapping("/{paymentId}")
  public ResponseEntity<?> refund(
      @PathVariable long paymentId
  ){
    long memberId = MemberUtil.getMemberId();
    paymentService.refund(memberId, paymentId);
    return ResponseEntity.ok().build();
  }


  /*
      ############## Payment Success #################
   */

  @GetMapping("/redirect/success")
  public void paymentSuccess(
      @RequestParam("payment_uuid") String paymentUUID,
      @RequestParam("pg_token") String pgToken,
      HttpServletResponse response
  ) throws IOException {

    paymentService.approvePayment(paymentUUID, pgToken);

    response.sendRedirect("/");
  }
}
