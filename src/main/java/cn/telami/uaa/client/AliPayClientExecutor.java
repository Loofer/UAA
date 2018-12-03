package cn.telami.uaa.client;

import cn.telami.uaa.exception.BadRequestParamsException;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliPayClientExecutor {

  @Autowired
  private AlipayClient alipayClient;

  private static final String AUTHORIZATION_CODE = "authorization_code";

  /**
   * get access token.
   *
   * @param authorizationCode 授权码
   */
  public AlipaySystemOauthTokenResponse getAccessToken(String authorizationCode) {
    AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
    request.setCode(authorizationCode);
    request.setGrantType(AUTHORIZATION_CODE);
    try {
      return alipayClient.execute(request);
    } catch (AlipayApiException e) {
      throw new BadRequestParamsException(e.getErrMsg());
    }
  }

  /**
   * get user info.
   *
   * @param accessToken access token
   */
  public AlipayUserInfoShareResponse getUserInfo(String accessToken) {
    AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
    try {
      return alipayClient.execute(request, accessToken);
    } catch (AlipayApiException e) {
      throw new BadRequestParamsException(e.getErrMsg());
    }
  }
}
