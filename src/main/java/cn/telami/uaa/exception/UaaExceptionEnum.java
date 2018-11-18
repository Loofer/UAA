package cn.telami.uaa.exception;

import lombok.Getter;

/**
 * uaa exception.
 */
@Getter
public enum UaaExceptionEnum {

  REQUEST_NULL(400, "没有找到业务数据"),
  SERVER_ERROR(500, "服务器异常");

  UaaExceptionEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }

  private int code;

  private String message;
}
