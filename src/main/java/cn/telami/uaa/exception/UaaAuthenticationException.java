package cn.telami.uaa.exception;

import org.springframework.security.core.AuthenticationException;

public class UaaAuthenticationException extends AuthenticationException {
  public UaaAuthenticationException(String msg, Throwable t) {
    super(msg, t);
  }

  public UaaAuthenticationException(String msg) {
    super(msg);
  }
}
