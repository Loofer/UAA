package cn.telami.uaa.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * BadRequestParamsException.
 */
public class BadRequestParamsException extends AuthenticationException {
  public BadRequestParamsException(String msg) {
    super(msg);
  }
}
