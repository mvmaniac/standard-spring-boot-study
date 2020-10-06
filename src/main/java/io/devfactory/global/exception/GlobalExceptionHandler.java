package io.devfactory.global.exception;

import javax.servlet.http.HttpServletRequest;
import io.devfactory.account.domain.Account;
import io.devfactory.global.config.security.service.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  public String handle(@CurrentUser Account account, HttpServletRequest req, RuntimeException e) {
    if (Objects.nonNull(account)) {
      log.info("'{}' requested '{}'", account.getNickname(), req.getRequestURI());
    } else {
      log.info("requested '{}'", req.getRequestURI());
    }

    if (e instanceof AccessDeniedException) {
      return "error/403";
    }

    return "error/5xx";
  }

}
