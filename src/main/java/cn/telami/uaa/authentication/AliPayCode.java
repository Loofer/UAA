package cn.telami.uaa.authentication;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AliPayCode {

  /**
   * 支付宝授权码.
   */
  private String code;

  /**
   * 没有绑定手机时，记录下当前请求.
   */
  private String sessionId;

  /**
   * 手机登录后，用于绑定支付宝.
   */
  private String mobile;
}
