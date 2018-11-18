package cn.telami.uaa.dto;

import cn.telami.uaa.exception.UaaExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response {

  /**
   * status.
   */
  private Integer status;

  /**
   * message.
   */
  private String message;

  /**
   * data.
   */
  private Object data;

  public static Response ok(Object data) {
    return new Response(data);
  }

  public static Response ok() {
    return new Response(null);
  }

  public static Response empty() {
    return build(UaaExceptionEnum.REQUEST_NULL);
  }

  public Response() {

  }

  public static Response build(Integer status, String message, Object data) {
    return new Response(status, message, data);
  }

  public static Response build(Integer status, String message) {
    return new Response(status, message, null);
  }

  public static Response build(UaaExceptionEnum uaaExceptionEnum) {
    return new Response(uaaExceptionEnum.getCode(), uaaExceptionEnum.getMessage(), null);
  }

  public static Response build(UaaExceptionEnum uaaExceptionEnum, Object data) {
    return new Response(uaaExceptionEnum.getCode(), uaaExceptionEnum.getMessage(), data);
  }

  /**
   * constructor.
   */
  private Response(Object data) {
    this.status = 200;
    this.message = "OK";
    this.data = data;
  }
}
