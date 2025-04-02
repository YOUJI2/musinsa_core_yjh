package musinsa.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ResponseObject;
import musinsa.common.util.ResponseUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(1)
public class BusinessExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  protected <T> ResponseEntity<ResponseObject<T>> handleBusinessException(BusinessException e) {
    if (e.isClientError()) {
      log.warn("Client 에러 : {}", e.getMessage());
    } else {
      log.error("Server 에러 : {}", e.getMessage(), e);
    }
    return ResponseUtils.createResponseEntityByException(e);
  }
}
