package cn.telami.uaa.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UaaException extends RuntimeException {
  /**
   * code.
   */
  protected int code;

  /**
   * message.
   */
  protected String message;

  /**
   * data.
   */
  private Object data;

  public UaaException(int code, String message) {
    this.setValues(code, message);
  }

  public UaaException(UaaExceptionEnum uaaExceptionEnum) {
    this.setValues(uaaExceptionEnum.getCode(), uaaExceptionEnum.getMessage());
  }

  private void setValues(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public UaaException(UaaExceptionEnum uaaExceptionEnum, Object data) {
    this.setValues(uaaExceptionEnum.getCode(), uaaExceptionEnum.getMessage());
    this.data = data;
  }
}
