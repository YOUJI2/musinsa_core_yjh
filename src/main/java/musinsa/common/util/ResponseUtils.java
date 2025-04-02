package musinsa.common.util;

import static musinsa.common.model.ErrorCode.FAIL_RESPONSE_UTIL_ERROR;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ResponseObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ResponseUtils {

  private ResponseUtils() {
    throw new BusinessException(FAIL_RESPONSE_UTIL_ERROR);
  }

  public static HttpHeaders getDefaultHttpHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    MediaType mediaType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
    httpHeaders.setContentType(mediaType);
    return httpHeaders;
  }

  public static <T> ResponseEntity<ResponseObject<T>> createResponseEntityByException(
      BusinessException ex) {
    return new ResponseEntity<>(ex.getBody(), getDefaultHttpHeaders(), ex.getHttpStatus());
  }

  public static <T> ResponseEntity<ResponseObject<T>> createResponseEntityByGlobalException(HttpStatus status) {
    return new ResponseEntity<>(null, getDefaultHttpHeaders(), status);
  }

  public static <T> ResponseEntity<ResponseObject<T>> createResponseEntity(T data, HttpStatus status) {
    return new ResponseEntity<>(ResponseObject.ofData(data), status);
  }

  public static <T> ResponseEntity<ResponseObject<T>> createResponseEntityBySuccess() {
    return new ResponseEntity<>(null, HttpStatus.OK);
  }
}

