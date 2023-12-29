package com.jhsfully.api.exception;

import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_UNAUTHORIZED;

import com.jhsfully.api.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<?> inputArgsExceptionHandler(BindingResult bindingResult) {
    String message = bindingResult.getFieldError().getDefaultMessage();

    if (message == null) {
      message = "요청된 값이 올바르지 않습니다.";
    }

    return ResponseEntity.badRequest()
        .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<?> authenticationExceptionHandler(AuthenticationException e) {

    if (AUTHENTICATION_UNAUTHORIZED == e.getAuthenticationErrorType()) {
      return ResponseEntity.status(401).body(
          new ErrorResponse(
              HttpStatus.UNAUTHORIZED.value(),
              e.getMessage()
          )
      );
    }

    return ResponseEntity.internalServerError().body(
        new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage())
    );
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<?> customExceptionHandler(CustomException e) {

    return ResponseEntity.internalServerError().body(
        new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage())
    );

  }

}