package com.jhsfully.api.model.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApprovedResponse {
  private String tid;
  private Amount amount;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Amount{
    private long total;

  }
}
