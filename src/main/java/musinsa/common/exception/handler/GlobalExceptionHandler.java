package musinsa.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import musinsa.common.model.ResponseObject;
import musinsa.common.util.ResponseUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(2)
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  protected <T> ResponseEntity<ResponseObject<T>> handleExceptionHandler(Exception e) {
    log.error("GlobalException : ", e);
    return ResponseUtils.createResponseEntityByGlobalException(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
