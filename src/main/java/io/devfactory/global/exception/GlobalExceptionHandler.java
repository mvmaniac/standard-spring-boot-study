package io.devfactory.global.exception;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

// TODO: global exception 처리
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  public void handle(HttpServletRequest request, Exception e) {
    log.debug("[dev] uri: {}, error: {}", e.getLocalizedMessage(), request.getRequestURI());
  }

}
