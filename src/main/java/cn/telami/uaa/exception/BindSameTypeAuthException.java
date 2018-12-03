package cn.telami.uaa.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * bind同类型登录方式异常.
 */
public class BindSameTypeAuthException extends AuthenticationException {
  public BindSameTypeAuthException(String msg) {
    super(msg);
  }
}