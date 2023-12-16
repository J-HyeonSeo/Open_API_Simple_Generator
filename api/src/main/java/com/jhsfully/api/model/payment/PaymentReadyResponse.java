package com.jhsfully.api.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReadyResponse {
  private String tid;
  @JsonProperty("next_redirect_mobile_url")
  private String nextRedirectMobileUrl;
  @JsonProperty("next_redirect_pc_url")
  private String nextRedirectPcUrl;
}
