package cn.telami.uaa.authentication;

import cn.telami.uaa.client.AliPayClientExecutor;
import cn.telami.uaa.exception.BadRequestParamsException;
import cn.telami.uaa.model.Oauth2Login;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.Oauth2LoginService;
import cn.telami.uaa.service.UserService;
import cn.telami.uaa.utils.ValidatorUtils;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class AliPayAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  @Autowired
  private AliPayClientExecutor aliPayClientExecutor;

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

  public static final String PREX_DINGTALK_BIND_MOBILE = "LOGIN:ALIPAY:BIND:MOBILE";
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
    AliPayCode aliPayCode = (AliPayCode) authentication.getCredentials();
    log.debug("AliPay authorization code = {}", aliPayCode.getCode());
    //get aliapy user info
    AlipayUserInfoShareResponse alipayUserInfo = getAlipayUserInfo(aliPayCode.getCode());
    if (!alipayUserInfo.isSuccess()) {
      throw new BadRequestParamsException(alipayUserInfo.getSubMsg());
    }
    //query db get oauth2Login
    Oauth2Login oauth2Login = authLoginService.getOne(new LambdaQueryWrapper<Oauth2Login>()
        .eq(Oauth2Login::getUnionid, alipayUserInfo.getUserId()));
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
          .type(Oauth2Login.Type.Alipay)
          .unionid(alipayUserInfo.getUserId())
          .nickname(alipayUserInfo.getNickName())
          .avatar(alipayUserInfo.getAvatar())
          .city(alipayUserInfo.getCity())
          .country(alipayUserInfo.getCountryCode())
          .province(alipayUserInfo.getProvince())
          .build();
    } else {
      user = updateUserInfo(oauth2Login, alipayUserInfo);
    }
    user = checkBindMobile(aliPayCode, user, oauth2Login);
    log.debug("Exit {}.", method);
    return user.buildUserDetails();
  }

  /**
   * 更新用户支付宝登录信息.
   *
   * @param oauth2Login    第三方登录信息.
   * @param alipayUserInfo 登录的支付宝用户信息.
   */
  private User updateUserInfo(Oauth2Login oauth2Login,
                              AlipayUserInfoShareResponse alipayUserInfo) {
    User user = userService.getOne(new LambdaQueryWrapper<User>()
        .eq(User::getId, oauth2Login.getUserId()));
    Oauth2Login build = oauth2Login.toBuilder()
        .nickname(alipayUserInfo.getNickName())
        .avatar(alipayUserInfo.getAvatar())
        .city(alipayUserInfo.getCity())
        .country(alipayUserInfo.getCountryCode())
        .province(alipayUserInfo.getProvince())
        .build();
    authLoginService.updateById(build);
    return user;
  }

  /**
   * 查看当前钉钉用户是否已绑定手机.
   *
   * @param aliPayCode dingcode
   * @param user       用户
   */
  private User checkBindMobile(AliPayCode aliPayCode, User user, Oauth2Login oauth2Login) {
    String sessionId = aliPayCode.getSessionId();
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
   * get alipay user info.
   *
   * @param code code.
   */
  private AlipayUserInfoShareResponse getAlipayUserInfo(String code) {
    AlipaySystemOauthTokenResponse tokenResponse = aliPayClientExecutor.getAccessToken(code);
    return aliPayClientExecutor.getUserInfo(tokenResponse.getAccessToken());
  }
}
