package com.jhsfully.api.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.model.api.DeleteApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataInput;
import com.jhsfully.api.model.api.UpdateApiDataInput;
import com.jhsfully.api.service.ApiHistoryService;
import com.jhsfully.api.service.ApiSearchService;
import com.jhsfully.api.service.ApiService;
import com.jhsfully.api.util.MemberUtil;
import com.jhsfully.domain.type.SearchType;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *  API의 등록,
 *  API데이터의 추가/수정/삭제
 *  API 검색
 */

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

  private final ApiService apiService; //api 생성/삭제, 데이터 추가/수정/삭제
  private final ApiHistoryService apiHistoryService; //history 데이터 조회/추가
  private final ApiSearchService apiSearchService; //elastic search 로 api 검색

  /*
      사용자가 OpenAPI를 만들기 위해서 호출하는 컨트롤러
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createOpenApi(
      @ModelAttribute CreateApiInput input) throws JsonProcessingException {
    long memberId = MemberUtil.getMemberId();
    apiService.createOpenApi(input, memberId);

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
  @PutMapping("/data/manage")
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
      OpenAPI를 데이터를 삭제함. MySQL데이터는 SOFT DELETE로 처리함.
   */
  @DeleteMapping("/{apiId}")
  public ResponseEntity<?> deleteOpenApi(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    apiService.deleteOpenApi(apiId, memberId);
    return ResponseEntity.ok().build();
  }

  /*
      비활성화된, OpenAPI를 활성화시킴.
   */
  @PatchMapping("/enable/{apiId}")
  public ResponseEntity<?> enableOpenApi(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    apiService.enableOpenApi(apiId, memberId);
    return ResponseEntity.ok().build();
  }

  /*
      Elastic Search 로 공개된 OpenAPI 를 검색해서, 리스트 반환
   */
  @GetMapping("/{pageSize}/{pageIdx}")
  public ResponseEntity<?> getOpenApiList(
      @PathVariable int pageSize,
      @PathVariable int pageIdx,
      @RequestParam String searchText,
      @RequestParam SearchType type
  ){

    return ResponseEntity.ok(
        apiSearchService.getOpenApiList(pageSize, pageIdx, searchText, type)
    );
  }

  /*
    Elastic Search 에 질의하여, 자신 소유의 API 데이터 조회.
 */
  @GetMapping("/owner/{pageSize}/{pageIdx}")
  public ResponseEntity<?> getApiListForOwner(
      @PathVariable int pageSize,
      @PathVariable int pageIdx,
      @RequestParam String searchText,
      @RequestParam SearchType type
  ){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiSearchService.getOpenApiListForOwner(
            memberId, pageSize, pageIdx, searchText, type
        )
    );
  }

  /*
  Elastic Search 에 질의하여, 자신이 접근 가능한 API목록 조회(본인 소유 미포함)
*/
  @GetMapping("/access/{pageSize}/{pageIdx}")
  public ResponseEntity<?> getApiListForAccess(
      @PathVariable int pageSize,
      @PathVariable int pageIdx,
      @RequestParam String searchText,
      @RequestParam SearchType type
  ){
    long memberId = MemberUtil.getMemberId();

    return ResponseEntity.ok(
        apiSearchService.getOpenApiListForAccess(
            memberId, pageSize, pageIdx, searchText, type
        )
    );
  }

  /*
      MySQL에 질의하여, Api상세 데이터를 가져옴.
   */
  @GetMapping("/{apiId}")
  public ResponseEntity<?> getOpenApiDetail(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiSearchService.getOpenApiDetail(apiId, memberId)
    );
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
