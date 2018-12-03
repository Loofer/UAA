package cn.telami.uaa.authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class DingTalkCode extends Oauth2LoginCode {

  @Builder
  public DingTalkCode(String authorizationCode, String sessionId, String mobile) {
    super(authorizationCode, sessionId, mobile);
  }
}
