package com.jhsfully.api.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.model.dto.PaymentDto;
import com.jhsfully.api.model.payment.PaymentReadyResponseForClient;
import com.jhsfully.api.model.payment.PaymentResponse;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.PaymentService;
import com.jhsfully.domain.type.PaymentStateType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = PaymentController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class PaymentControllerTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final long TEST_ID = -1L;

    @Test
    void getPaymentList() throws Exception {
        //given
        PaymentResponse response = PaymentResponse.builder()
            .totalCount(1L)
            .dataCount(1L)
            .dataList(List.of(
                PaymentDto.builder()
                    .id(1L)
                    .grade("GOLD")
                    .paymentAmount(3000L)
                    .refundAmount(null)
                    .paidAt(LocalDateTime.of(2023, 12, 1, 9 , 3 , 3))
                    .refundAt(null)
                    .paymentState(PaymentStateType.SUCCESS)
                    .build()
            ))
            .build();
        given(paymentService.getPaymentList(anyLong(), any()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/payment/0/1").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.totalCount").value(1),
                jsonPath("$.dataCount").value(1),
                jsonPath("$.dataList.[0].id").value(1),
                jsonPath("$.dataList.[0].grade").value("GOLD"),
                jsonPath("$.dataList.[0].paymentAmount").value(3000),
                jsonPath("$.dataList.[0].refundAmount").isEmpty(),
                jsonPath("$.dataList.[0].paidAt").value(
                    LocalDateTime.of(2023, 12, 1, 9 , 3 , 3).toString()
                ),
                jsonPath("$.dataList.[0].refundAt").isEmpty(),
                jsonPath("$.dataList.[0].paymentState").value("SUCCESS")
            );
    }

    @Test
    void paymentRequest() throws Exception {
        //given
        PaymentReadyResponseForClient response = new PaymentReadyResponseForClient(
            "http://mobile.com", "http://pc.com"
        );
        given(paymentService.paymentRequest(anyLong(), anyLong(), any()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(post("/payment/1")
            .with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.next_redirect_mobile_url").value("http://mobile.com"),
                jsonPath("$.next_redirect_pc_url").value("http://pc.com")
            );
    }

    @Test
    void refund() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(patch("/payment/1")
            .with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(paymentService, times(1)).refund(
            eq(TEST_ID), eq(1L), any()
        );
    }

    @Test
    void paymentSuccess() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(get("/payment/redirect/success?payment_uuid=uuid&pg_token=pgToken")
            .with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().is3xxRedirection());
        verify(paymentService, times(1)).approvePayment(
            eq("uuid"), eq("pgToken"), any()
        );
    }
}