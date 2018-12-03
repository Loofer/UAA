package cn.telami.uaa.authentication;

import cn.telami.uaa.model.Oauth2Login;
import cn.telami.uaa.model.User;
import cn.telami.uaa.utils.ValidatorUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public abstract class AbstractOauth2LoginAuthenticationProvider
    extends AbstractUserDetailsAuthenticationProvider {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  /**
   * Check whether the current login user is bound to the phone.
   */
  protected void checkBindMobile(String prefix,
                                 Oauth2LoginCode oauth2LoginCode,
                                 User user, Oauth2Login oauth2Login) {
    String sessionId = oauth2LoginCode.getSessionId();
    //check
    if (ValidatorUtils.isMobile(user.getMobile())) {
      return;
    }
    //save info to redis
    try {
      ListOperations<String, String> stringListOperations = redisTemplate.opsForList();
      String userJson = objectMapper.writeValueAsString(user);
      String oauth2LoginJson = objectMapper.writeValueAsString(oauth2Login);
      ArrayList<String> strings = Lists.newArrayList(userJson, oauth2LoginJson);
      stringListOperations.rightPushAll(prefix + ":" + sessionId, strings);
      log.debug("Current user need to bind phone");
    } catch (JsonProcessingException e) {
      log.warn("Convert user to json failed {}", user);
    }
  }
}
