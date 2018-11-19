package cn.telami.uaa.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * PasswordNotSetException.
 */
public class PasswordNotSetException extends AuthenticationException {
  public PasswordNotSetException(String msg) {
    super(msg);
  }
}
