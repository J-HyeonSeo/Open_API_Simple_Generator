package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.query.QueryInput;
import com.jhsfully.api.model.query.QueryResponse;
import com.jhsfully.api.service.QueryService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
@RequiredArgsConstructor
public class ApiQueryController {

  private final QueryService queryService;

  @GetMapping("/{apiId}/{authKey}/{pageSize}/{pageIdx}")
  public ResponseEntity<QueryResponse> getOpenAPIDataList(
      @PathVariable long apiId,
      @PathVariable String authKey,
      @PathVariable int pageSize,
      @PathVariable int pageIdx,
      @RequestParam Map<String, Object> queryParameter
  ){
    QueryInput input = QueryInput.builder()
        .apiId(apiId)
        .authKey(authKey)
        .pageSize(pageSize)
        .pageIdx(pageIdx)
        .queryParameter(queryParameter)
        .build();

    return ResponseEntity.ok(
        queryService.getDataList(input)
    );
  }

}
