package cn.telami.uaa.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * CaptchaIncorrectException.
 */
public class CaptchaIncorrectException extends AuthenticationException {
  public CaptchaIncorrectException(String msg) {
    super(msg);
  }
}
