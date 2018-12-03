package cn.telami.uaa.authentication;

import static cn.telami.uaa.constant.Oauth2LoginPrefixConstants.ALIPAY_OAUTH2;
import static cn.telami.uaa.constant.Oauth2LoginPrefixConstants.ALIPAY_USER;

import cn.telami.uaa.client.AliPayClientExecutor;
import cn.telami.uaa.exception.BadRequestParamsException;
import cn.telami.uaa.model.Oauth2Login;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.Oauth2LoginService;
import cn.telami.uaa.service.UserService;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AliPayAuthenticationProvider extends AbstractOauth2LoginAuthenticationProvider {

  @Autowired
  private AliPayClientExecutor aliPayClientExecutor;

  @Autowired
  private Oauth2LoginService authLoginService;

  @Autowired
  private UserService userService;

  @Autowired
  RedisTemplate<String, String> redisTemplate;

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
    log.debug("AliPay authorization code = {}", aliPayCode.getAuthorizationCode());
    //get aliapy user info
    AlipayUserInfoShareResponse alipayUserInfo =
        getAlipayUserInfo(aliPayCode.getAuthorizationCode());
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
    checkBindMobile(ALIPAY_USER, ALIPAY_OAUTH2, aliPayCode, user, oauth2Login);
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
   * get alipay user info.
   *
   * @param code code.
   */
  private AlipayUserInfoShareResponse getAlipayUserInfo(String code) {
    AlipaySystemOauthTokenResponse tokenResponse = aliPayClientExecutor.getAccessToken(code);
    return aliPayClientExecutor.getUserInfo(tokenResponse.getAccessToken());
  }
}
