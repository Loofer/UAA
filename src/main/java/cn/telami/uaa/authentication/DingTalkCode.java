package cn.telami.uaa.authentication;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DingTalkCode {

  /**
   * 微信授权码.
   */
  private String code;

  /**
   * 没有绑定手机时，记录下当前请求.
   */
  private String sessionId;

  /**
   * 手机登录后，用于绑定微信号.
   */
  private String mobile;
}
