package cn.telami.uaa.authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class AliPayCode extends Oauth2LoginCode {

  @Builder
  public AliPayCode(String authorizationCode, String sessionId, String mobile) {
    super(authorizationCode, sessionId, mobile);
  }
}
