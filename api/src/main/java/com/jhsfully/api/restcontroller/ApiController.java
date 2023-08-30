package com.jhsfully.api.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.model.api.DeleteApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataInput;
import com.jhsfully.api.model.api.UpdateApiDataInput;
import com.jhsfully.api.service.ApiHistoryService;
import com.jhsfully.api.service.ApiService;
import com.jhsfully.api.util.MemberUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  private final ApiHistoryService apiHistoryService;

  /*
      사용자가 OpenAPI를 만들기 위해서 호출하는 컨트롤러
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createOpenApi(
      @ModelAttribute CreateApiInput input) throws JsonProcessingException {

    apiService.createOpenApi(input);

    return ResponseEntity.ok().build();
  }

  /*
      사용자가 OpenAPI에 데이터를 추가하기 위해서 호출하는 컨트롤러
      dataId 값을 응답으로 줘야함.
   */
  @PostMapping("/data/manage")
  public ResponseEntity<?> insertApiData(@RequestBody InsertApiDataInput input){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiService.insertApiData(input, memberId)
    );
  }

  /*
      사용자가 OpenAPI에 데이터를 수정하기 위해서 호출하는 컨트롤러
   */
  @PatchMapping("/data/manage")
  public ResponseEntity<?> updateApiData(@RequestBody UpdateApiDataInput input){
    long memberId = MemberUtil.getMemberId();
    apiService.updateApiData(input, memberId);
    return ResponseEntity.ok().build();
  }

  /*
      사용자가 OpenAPI에 데이터를 삭제하기 위해서 호출하는 컨트롤러
   */
  @DeleteMapping("/data/manage")
  public ResponseEntity<?> deleteApiData(@RequestBody DeleteApiDataInput input){
    long memberId = MemberUtil.getMemberId();
    apiService.deleteApiData(input, memberId);
    return ResponseEntity.ok().build();
  }

  /*
      OpenAPI를 영구적으로 삭제하는 컨트롤러
      - MongoDB의 데이터는 전부 삭제함.
      - history의 데이터도 전부 삭제함.
      - apiInfo에 연관 데이터가 발생했을 경우에는 삭제 여부만 false로 지정,
      - apiInfo에 연관 데이터가 없을 경우에는, api자체를 삭제하도록 함.
   */
  @DeleteMapping("/{apiId}")
  public ResponseEntity<?> deleteOpenApi(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();

    return null;
  }

  /*
      API소유주는 startDate ~ endDate 기간의 히스토리 데이터를 조회할 수 있음.
   */
  @GetMapping("history/{apiId}/{pageSize}/{pageIdx}")
  public ResponseEntity<?> getApiHistories(
      @PathVariable long apiId,
      @PathVariable int pageSize,
      @PathVariable int pageIdx,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ){
    long memberId = MemberUtil.getMemberId();

    return ResponseEntity.ok(
      apiHistoryService.getApiHistories(
          apiId, memberId, pageSize, pageIdx,
          startDate, endDate
      )
    );
  }
}
