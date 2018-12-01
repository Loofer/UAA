package cn.telami.uaa.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * BadRequestParamsException.
 */
public class AuthorizationCodeEmptyException extends AuthenticationException {
  public AuthorizationCodeEmptyException(String msg) {
    super(msg);
  }
}
