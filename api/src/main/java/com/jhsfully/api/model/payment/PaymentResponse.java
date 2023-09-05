package com.jhsfully.api.model.payment;

import com.jhsfully.api.model.dto.PaymentDto;
import java.util.List;
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
public class PaymentResponse {
  private long totalCount;
  private long dataCount;
  private List<PaymentDto> dataList;
}
