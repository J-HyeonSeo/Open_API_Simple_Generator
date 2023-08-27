package com.jhsfully.api.util;

import org.springframework.security.core.context.SecurityContextHolder;

/*
    -1 is for unit-test
 */
public class MemberUtil {

  public static Long getMemberId(){
    if(SecurityContextHolder.getContext() == null){
      return -1L;
    }
    if(SecurityContextHolder.getContext().getAuthentication() == null){
      return -1L;
    }
    if(SecurityContextHolder.getContext().getAuthentication().getName() == null){
      return -1L;
    }

    String stringMemberId = SecurityContextHolder.getContext().getAuthentication().getName();
    long result;
    try{
      result = Long.parseLong(stringMemberId);
    }catch (Exception e){
      result = -1L;
    }
    return result;
  }

}