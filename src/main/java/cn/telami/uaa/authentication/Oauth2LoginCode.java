package cn.telami.uaa.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Oauth2LoginCode {
  /**
   * 授权码.
   */
  private String authorizationCode;

  /**
   * 没有绑定手机时，记录下当前请求.
   */
  private String sessionId;

  /**
   * 手机登录后，用于绑定第三方账户.
   */
  private String mobile;
}
