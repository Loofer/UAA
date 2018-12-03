package cn.telami.uaa.authentication;

import cn.telami.uaa.enums.Oauth2LoginPrefixEnum;
import cn.telami.uaa.exception.BindSameTypeAuthException;
import cn.telami.uaa.model.Oauth2Login;
import cn.telami.uaa.model.User;
import cn.telami.uaa.service.CommonService;
import cn.telami.uaa.service.Oauth2LoginService;
import cn.telami.uaa.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class MobilePhoneAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  @Autowired
  private CommonService commonService;

  @Autowired
  public UserService userService;

  @Autowired
  public Oauth2LoginService oauth2LoginService;

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

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
    log.debug("Enter {}. param={}", method, authentication);
    MobilePhoneCode mobilePhoneCode = (MobilePhoneCode) authentication.getCredentials();
    String mobile = mobilePhoneCode.getMobile();
    final String sessionId = mobilePhoneCode.getSessionId();
    //check verification code
    commonService.validatePhoneCode(mobile, mobilePhoneCode.getCaptcha());
    //get user from redis
    ListOperations<String, String> listOperations = redisTemplate.opsForList();
    String userInRedisJson = null;
    String oauth2LoginInRedisJson = null;
    String prefix = null;
    for (int i = 0; i < Oauth2LoginPrefixEnum.values().length; i++) {
      prefix = Oauth2LoginPrefixEnum.values()[i].name() + ":" + sessionId;
      if (listOperations.size(prefix) > 0) {
        userInRedisJson = listOperations.index(prefix, 0);
        oauth2LoginInRedisJson = listOperations.index(prefix, 1);
        break;
      }
    }
    User userInRedis = null;
    Oauth2Login oauth2LoginInRedis = null;
    if (!StringUtils.isEmpty(userInRedisJson) && !StringUtils.isEmpty(oauth2LoginInRedisJson)) {
      try {
        userInRedis = objectMapper.readValue(userInRedisJson, User.class);
        oauth2LoginInRedis = objectMapper.readValue(oauth2LoginInRedisJson, Oauth2Login.class);
      } catch (IOException e) {
        log.error("user {} and oauth2Login {} in redis can't convert successful.",
            userInRedisJson, oauth2LoginInRedisJson
        );
      }
    }
    User userFromDb =
        userService.getOne(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile));
    log.debug("retrieveUser: userFromDb = {}, userInRedis = {}", userFromDb, userInRedis);
    User loadUser = null;
    if (Objects.isNull(userInRedis) || Objects.isNull(oauth2LoginInRedis)) {
      log.debug("this is only phone login");
      loadUser = null == userFromDb ? saveNewUser(mobile) : userFromDb;
    } else {
      //第三方登录绑定手机过程.
      if (Objects.isNull(userFromDb)) {
        if (StringUtils.isEmpty(userInRedis.getId())) {
          //第三方登录账号未入库，注册
          loadUser = createOauth2LoginWithMobile(mobile, userInRedis, oauth2LoginInRedis);
        }
      } else {
        //手机号已入库
        loadUser = bindOauth2LoginWithPhone(userFromDb, oauth2LoginInRedis);
      }
    }
    // 绑定成功删除
    if (null != prefix) {
      redisTemplate.delete(prefix);
    }
    return loadUser.buildUserDetails();
  }

  private User saveNewUser(String mobile) {
    //新手机用户
    User user = User.builder()
        .username(mobile)
        .mobile(mobile)
        .enabled(true)
        .authorities(User.ROLE_NORMAL)
        .build();
    userService.save(user);
    return user;
  }

  private User createOauth2LoginWithMobile(String mobile,
                                           User userInRedis,
                                           Oauth2Login oauth2LoginInRedis) {
    userInRedis.setMobile(mobile);
    userInRedis.setUsername(mobile);
    userService.save(userInRedis);
    oauth2LoginInRedis.setUserId(userInRedis.getId());
    oauth2LoginService.save(oauth2LoginInRedis);
    return userInRedis;
  }

  private User bindOauth2LoginWithPhone(User userFromDb, Oauth2Login oauth2LoginInRedis) {
    List<Oauth2Login> oauth2Logins = oauth2LoginService.list(
        new LambdaQueryWrapper<Oauth2Login>().eq(Oauth2Login::getUserId, userFromDb.getId()));
    List<Oauth2Login.Type> list = oauth2Logins.stream()
        .map(Oauth2Login::getType)
        .collect(Collectors.toList());
    if (list.contains(oauth2LoginInRedis.getType())) {
      throw new BindSameTypeAuthException("用户已绑定当前登录方式！");
    }
    oauth2LoginInRedis.setUserId(userFromDb.getId());
    oauth2LoginService.save(oauth2LoginInRedis);
    return userFromDb;
  }
}
