package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.api.CreateApiInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  API의 등록,
 *  API데이터의 추가/수정/삭제
 */

@RestController
@RequestMapping("/api")
public class ApiController {

  @GetMapping
  public ResponseEntity<?> getApiDate(){
    return ResponseEntity.ok().build();
  }

  @PostMapping
  public ResponseEntity<?> createOpenApi(@RequestBody CreateApiInput input){

    System.out.println(input.getApiName());
    System.out.println(input.getApiIntroduce());
    System.out.println(input.getSchemeStructure());
    System.out.println(input.getQueryParameter());
    System.out.println(input.isPublic());
//    System.out.println(file.getOriginalFilename());

    return null;
  }

}
