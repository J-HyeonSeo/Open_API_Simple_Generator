package com.jhsfully.api.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  API의 등록,
 *  API데이터의 추가/수정/삭제
 */

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

  private final ApiService apiService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createOpenApi(
      @ModelAttribute CreateApiInput input) throws JsonProcessingException {

    apiService.createOpenApi(input);

    return ResponseEntity.ok().build();
  }

}
