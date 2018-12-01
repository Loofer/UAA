package cn.telami.uaa.authentication;

import cn.telami.uaa.client.DingTalkClientExecutor;
import cn.telami.uaa.exception.BadRequestParamsException;
import cn.telami.uaa.model.Oauth2Login;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.Oauth2LoginService;
import cn.telami.uaa.service.UserService;
import cn.telami.uaa.utils.ValidatorUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dingtalk.api.response.OapiSnsGetPersistentCodeResponse;
import com.dingtalk.api.response.OapiSnsGetSnsTokenResponse;
import com.dingtalk.api.response.OapiSnsGettokenResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.ApiException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DingTalkAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  @Autowired
  private DingTalkClientExecutor dingTalkClient;

  @Autowired
  private Oauth2LoginService authLoginService;

  @Autowired
  private UserService userService;

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Value("${message.expire}")
  private int expire = 300;

  public static final String PREX_DINGTALK_BIND_MOBILE = "LOGIN:DINGTALK:BIND:MOBILE";
  public static final String USER = PREX_DINGTALK_BIND_MOBILE + ":USER";
  public static final String AUTH = PREX_DINGTALK_BIND_MOBILE + ":AUTH";


  @Override
  protected void additionalAuthenticationChecks(
      UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
  }

  @Override
  protected UserDetails retrieveUser(
      String username,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    String method = "retrieveUser";
    log.debug("Enter {}.", method);
    //get authentication code
    DingTalkCode dingTalkCode = (DingTalkCode) authentication.getCredentials();
    log.debug("DingTalk Authorization code={}", dingTalkCode.getCode());
    //get dingTalk user info
    OapiSnsGetuserinfoResponse.UserInfo dingTalkUserInfo =
        getDingTalkUserInfo(dingTalkCode.getCode());
    //query db get oauth2Login
    Oauth2Login oauth2Login = authLoginService.getOne(new LambdaQueryWrapper<Oauth2Login>()
        .eq(Oauth2Login::getUnionid, dingTalkUserInfo.getUnionid()));
    User user;
    if (Objects.isNull(oauth2Login)) {
      //新建用户
      user = User.builder()
          .username(RandomStringUtils.randomAlphanumeric(20))
          .mobile(RandomStringUtils.randomAlphanumeric(20))
          .enabled(true)
          .authorities(User.ROLE_NORMAL)
          .build();
      //新建第三方登录方式与之绑定
      oauth2Login = Oauth2Login.builder()
          .type(Oauth2Login.Type.DingTalk)
          .unionid(dingTalkUserInfo.getUnionid())
          .openid(dingTalkUserInfo.getOpenid())
          .nickname(dingTalkUserInfo.getNick())
          .build();
    } else {
      user = updateUserInfo(oauth2Login, dingTalkUserInfo);
    }
    user = checkBindMobile(dingTalkCode, user, oauth2Login);
    log.debug("Exit {}.", method);
    return user.buildUserDetails();
  }

  /**
   * 更新用户钉钉登录信息.
   *
   * @param oauth2Login    第三方登录信息.
   * @param dingTalkUserInfo 登录的钉钉用户信息.
   */
  private User updateUserInfo(Oauth2Login oauth2Login,
                              OapiSnsGetuserinfoResponse.UserInfo dingTalkUserInfo) {
    User user = userService.getOne(new LambdaQueryWrapper<User>()
        .eq(User::getId, oauth2Login.getUserId()));
    Oauth2Login build = oauth2Login.toBuilder()
        .nickname(dingTalkUserInfo.getNick())
        .openid(dingTalkUserInfo.getOpenid())
        .build();
    authLoginService.updateById(build);
    return user;
  }

  /**
   * 查看当前钉钉用户是否已绑定手机.
   *
   * @param dingTalkCode dingcode
   * @param user         用户
   */
  private User checkBindMobile(DingTalkCode dingTalkCode, User user, Oauth2Login oauth2Login) {
    String sessionId = dingTalkCode.getSessionId();
    //当前用户已绑定手机
    if (ValidatorUtils.isMobile(user.getMobile())) {
      return user;
    }
    //存入redis
    try {
      ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
      String userJson = objectMapper.writeValueAsString(user);
      String oauth2LoginJson = objectMapper.writeValueAsString(oauth2Login);
      stringValueOperations.set(
          USER + ":" + sessionId, userJson, expire, TimeUnit.SECONDS
      );
      stringValueOperations.set(
          AUTH + ":" + sessionId, oauth2LoginJson, expire, TimeUnit.SECONDS
      );
      log.debug("The current user has not bind phone");
    } catch (JsonProcessingException e) {
      log.warn("convert user to json fail {}", user);
    }
    return user;
  }

  /**
   * 获取dingTalk用户信息.
   *
   * @param code 授权码.
   */
  private OapiSnsGetuserinfoResponse.UserInfo getDingTalkUserInfo(String code) {
    OapiSnsGetuserinfoResponse.UserInfo userInfo = null;
    try {
      OapiSnsGettokenResponse gettokenResponse = dingTalkClient.getAccessToken();
      if (!gettokenResponse.isSuccess()) {
        log.warn("can not get dingtalk access_token!");
        throw new BadRequestParamsException("can not get dingtalk access_token!");
      }
      String accessToken = gettokenResponse.getAccessToken();
      OapiSnsGetPersistentCodeResponse getPersistentCodeResponse =
          dingTalkClient.getPersistentCode(accessToken, code);
      if (!getPersistentCodeResponse.isSuccess()) {
        log.warn("can not get dingtalk persistent_code!");
        throw new BadRequestParamsException("can not get dingtalk persistent_code!");
      }
      //TODO save db
      OapiSnsGetSnsTokenResponse getSnsTokenResponse = dingTalkClient.getSnsToken(
          accessToken,
          getPersistentCodeResponse.getOpenid(),
          getPersistentCodeResponse.getPersistentCode()
      );
      if (!getSnsTokenResponse.isSuccess()) {
        log.warn("can not get dingtalk sns_token!");
        throw new BadRequestParamsException("can not get dingtalk sns_token!");
      }
      OapiSnsGetuserinfoResponse getuserinfoResponse =
          dingTalkClient.getUserInfo(getSnsTokenResponse.getSnsToken());
      if (!getuserinfoResponse.isSuccess()) {
        log.warn("can not get dingtalk user_info!");
        throw new BadRequestParamsException("can not get dingtalk user_info!");
      }
      userInfo = getuserinfoResponse.getUserInfo();
    } catch (ApiException e) {
      log.error("can not get dingtalk user_info!", e);
    }
    return userInfo;
  }
}
