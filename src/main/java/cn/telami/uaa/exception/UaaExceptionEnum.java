package cn.telami.uaa.exception;

import lombok.Getter;

/**
 * uaa exception.
 */
@Getter
public enum UaaExceptionEnum {

  REQUEST_NULL(400, "没有找到业务数据"),
  SERVER_ERROR(500, "服务器异常"),
  USER_HAVE_ALREADY_REGISTERED(8000, "User has already registered"),
  MESSAGE_SEND_FAIL(8001, "Message send fail"),
  NEED_BIND(8002, "Current user need to bind phone!");

  UaaExceptionEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }

  private int code;

  private String message;
}
