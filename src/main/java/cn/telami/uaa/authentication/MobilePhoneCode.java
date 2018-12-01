package cn.telami.uaa.authentication;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MobilePhoneCode {
  /**
   * 验证码.
   */
  private String captcha;
  /**
   * 手机号 登录后，用于绑定微信号.
   */
  private String mobile;
  /**
   * 没有绑定手机时，记录下当前请求.
   */
  private String sessionId;
}
