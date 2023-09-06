package com.jhsfully.api.model.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReadyResponseForClient {
  private String next_redirect_mobile_url;
  private String next_redirect_pc_url;
}
