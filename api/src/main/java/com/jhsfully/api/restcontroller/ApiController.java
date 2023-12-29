package com.jhsfully.api.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.model.api.DeleteApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataInput;
import com.jhsfully.api.model.api.UpdateApiDataInput;
import com.jhsfully.api.model.api.UpdateApiInput;
import com.jhsfully.api.service.ApiHistoryService;
import com.jhsfully.api.service.ApiSearchService;
import com.jhsfully.api.service.ApiService;
import com.jhsfully.api.util.MemberUtil;
import com.jhsfully.domain.type.SearchType;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
      @ModelAttribute @Valid CreateApiInput input) throws JsonProcessingException {
    long memberId = MemberUtil.getMemberId();
    apiService.createOpenApi(input, memberId);

    return ResponseEntity.ok().build();
  }

  /*
      사용자가 OpenAPI에 데이터를 추가하기 위해서 호출하는 컨트롤러
      dataId 값을 응답으로 줘야함.
   */
  @PostMapping("/data/manage/{apiId}")
  public ResponseEntity<?> insertApiData(
      @PathVariable long apiId,
      @RequestBody InsertApiDataInput input){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiService.insertApiData(input, apiId, memberId, LocalDateTime.now())
    );
  }

  /*
      사용자가 OpenAPI에 데이터를 수정하기 위해서 호출하는 컨트롤러
   */
  @PutMapping("/data/manage/{apiId}")
  public ResponseEntity<?> updateApiData(
      @PathVariable long apiId,
      @RequestBody UpdateApiDataInput input){
    long memberId = MemberUtil.getMemberId();
    apiService.updateApiData(input, apiId, memberId, LocalDateTime.now());
    return ResponseEntity.ok().build();
  }

  /*
      사용자가 OpenAPI에 데이터를 삭제하기 위해서 호출하는 컨트롤러
   */
  @DeleteMapping("/data/manage/{apiId}")
  public ResponseEntity<?> deleteApiData(
      @PathVariable long apiId,
      @RequestBody DeleteApiDataInput input){
    long memberId = MemberUtil.getMemberId();
    apiService.deleteApiData(input, apiId, memberId, LocalDateTime.now());
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
    apiService.enableOpenApi(apiId, memberId, LocalDate.now());
    return ResponseEntity.ok().build();
  }

  /*
      API의 제목/내용을 수정함.
   */
  @PatchMapping("/{apiId}")
  public ResponseEntity<?> updateOpenApi(
      @PathVariable long apiId, @RequestBody @Valid UpdateApiInput input) {
    long memberId = MemberUtil.getMemberId();
    apiService.updateOpenApi(input, apiId, memberId);
    return ResponseEntity.ok().build();
  }

  /*
      Elastic Search 로 공개된 OpenAPI 를 검색해서, 리스트 반환
   */
  @GetMapping("/public/{pageIdx}/{pageSize}")
  public ResponseEntity<?> getOpenApiList(
      @PathVariable int pageIdx,
      @PathVariable int pageSize,
      @RequestParam(required = false) String searchText,
      @RequestParam(required = false) SearchType type
  ){
    long memberId = MemberUtil.getMemberId(); //memberId *Optional
    return ResponseEntity.ok(
        apiSearchService.getOpenApiList(searchText, type, PageRequest.of(pageIdx, pageSize), memberId)
    );
  }

  /*
    Elastic Search 에 질의하여, 자신 소유의 API 데이터 조회.
 */
  @GetMapping("/owner/{pageIdx}/{pageSize}")
  public ResponseEntity<?> getApiListForOwner(
      @PathVariable int pageIdx,
      @PathVariable int pageSize,
      @RequestParam(required = false) String searchText,
      @RequestParam(required = false) SearchType type
  ){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiSearchService.getOpenApiListForOwner(
            memberId, searchText, type, PageRequest.of(pageIdx, pageSize)
        )
    );
  }

  /*
  Elastic Search 에 질의하여, 자신이 접근 가능한 API목록 조회(본인 소유 미포함)
*/
  @GetMapping("/access/{pageIdx}/{pageSize}")
  public ResponseEntity<?> getApiListForAccess(
      @PathVariable int pageIdx,
      @PathVariable int pageSize,
      @RequestParam(required = false) String searchText,
      @RequestParam(required = false) SearchType type
  ){
    long memberId = MemberUtil.getMemberId();

    return ResponseEntity.ok(
        apiSearchService.getOpenApiListForAccess(
            memberId, searchText, type, PageRequest.of(pageIdx, pageSize)
        )
    );
  }

  /*
      MySQL에 질의하여, Api상세 데이터를 가져옴.
   */
  @GetMapping("/public/{apiId}")
  public ResponseEntity<?> getOpenApiDetail(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiSearchService.getOpenApiDetail(apiId, memberId)
    );
  }


  /*
      API소유주는 startDate ~ endDate 기간의 히스토리 데이터를 조회할 수 있음.
   */
  @GetMapping("/history/{apiId}/{pageIdx}/{pageSize}")
  public ResponseEntity<?> getApiHistories(
      @PathVariable long apiId,
      @PathVariable int pageIdx,
      @PathVariable int pageSize,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ){
    long memberId = MemberUtil.getMemberId();

    return ResponseEntity.ok(
      apiHistoryService.getApiHistories(
          apiId, memberId, startDate, endDate, PageRequest.of(pageIdx, pageSize)
      )
    );
  }
}
