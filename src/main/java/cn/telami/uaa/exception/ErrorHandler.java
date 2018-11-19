package cn.telami.uaa.exception;

import cn.telami.uaa.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@ControllerAdvice
@RestController
public class ErrorHandler {

  @ExceptionHandler(Exception.class)
  private Response exception(Exception exception) {
    log.error("Exception", exception);
    return Response.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
  }

  @ExceptionHandler(UaaException.class)
  private Response uaaException(UaaException exception) {
    log.error("UaaException", exception.getMessage());
    return Response.build(exception.getCode(), exception.getMessage(), exception.getData());
  }
}