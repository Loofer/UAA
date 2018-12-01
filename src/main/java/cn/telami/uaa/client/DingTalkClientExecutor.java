package cn.telami.uaa.client;

import cn.telami.uaa.config.DingTalkConfig;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.DingTalkConstants;
import com.dingtalk.api.request.OapiSnsGetPersistentCodeRequest;
import com.dingtalk.api.request.OapiSnsGetSnsTokenRequest;
import com.dingtalk.api.request.OapiSnsGettokenRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoRequest;
import com.dingtalk.api.response.OapiSnsGetPersistentCodeResponse;
import com.dingtalk.api.response.OapiSnsGetSnsTokenResponse;
import com.dingtalk.api.response.OapiSnsGettokenResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoResponse;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DingTalkClientExecutor {

  private final DingTalkConfig dingTalkConfig;

  private static final String GET_ACCESS_TOKEN = "https://oapi.dingtalk.com/sns/gettoken";
  private static final String GET_PERSISTENT_CODE = "https://oapi.dingtalk.com/sns/get_persistent_code";
  private static final String GET_SNS_TOKEN = "https://oapi.dingtalk.com/sns/get_sns_token";
  private static final String GET_USER_INFO = "https://oapi.dingtalk.com/sns/getuserinfo";

  @Autowired
  public DingTalkClientExecutor(DingTalkConfig dingTalkConfig) {
    this.dingTalkConfig = dingTalkConfig;
  }

  /**
   * 获取钉钉开放应用的ACCESS_TOKEN.
   */
  public OapiSnsGettokenResponse getAccessToken() throws ApiException {
    final DingTalkClient client = new DefaultDingTalkClient(GET_ACCESS_TOKEN);
    OapiSnsGettokenRequest request = new OapiSnsGettokenRequest();
    request.setAppid(dingTalkConfig.getAppId());
    request.setAppsecret(dingTalkConfig.getAppSecret());
    request.setHttpMethod(DingTalkConstants.HTTP_METHOD_GET);
    return client.execute(request);
  }

  /**
   * 获取用户授权的持久授权码.
   *
   * @param accessToken 开放应用的token
   * @param tmpAuthCode 用户授权给钉钉开放应用的临时授权码
   */
  public OapiSnsGetPersistentCodeResponse getPersistentCode(
      String accessToken,
      String tmpAuthCode) throws ApiException {
    final DingTalkClient client = new DefaultDingTalkClient(GET_PERSISTENT_CODE);
    OapiSnsGetPersistentCodeRequest request = new OapiSnsGetPersistentCodeRequest();
    request.setTmpAuthCode(tmpAuthCode);
    return client.execute(request, accessToken);
  }

  /**
   * 获取用户授权的SNS_TOKEN.
   *
   * @param accessToken    开放应用的token
   * @param openId         用户的openid
   * @param persistentCode 用户授权给钉钉开放应用的持久授权码
   */
  public OapiSnsGetSnsTokenResponse getSnsToken(String accessToken,
                                                String openId,
                                                String persistentCode) throws ApiException {
    final DingTalkClient client = new DefaultDingTalkClient(GET_SNS_TOKEN);
    OapiSnsGetSnsTokenRequest request = new OapiSnsGetSnsTokenRequest();
    request.setOpenid(openId);
    request.setPersistentCode(persistentCode);
    return client.execute(request, accessToken);
  }

  /**
   * 获取用户授权的个人信息.
   *
   * @param snsToken 用户授权给开放应用的token
   */
  public OapiSnsGetuserinfoResponse getUserInfo(String snsToken) throws ApiException {
    final DingTalkClient client = new DefaultDingTalkClient(GET_USER_INFO);
    OapiSnsGetuserinfoRequest request = new OapiSnsGetuserinfoRequest();
    request.setSnsToken(snsToken);
    request.setHttpMethod(DingTalkConstants.HTTP_METHOD_GET);
    return client.execute(request);
  }
}
