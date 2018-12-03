package cn.telami.uaa.authentication;

import cn.telami.uaa.model.Oauth2Login;
import cn.telami.uaa.model.User;
import cn.telami.uaa.utils.ValidatorUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
public abstract class AbstractOauth2LoginAuthenticationProvider
    extends AbstractUserDetailsAuthenticationProvider {

  private ObjectMapper objectMapper = new ObjectMapper();

  private RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

  @Override
  protected abstract void additionalAuthenticationChecks(
      UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException;

  protected abstract UserDetails retrieveUser(
      String username,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException;

  /**
   * Check whether the current login user is bound to the phone.
   *
   * @param oauth2LoginCode oauth2LoginCode
   * @param user            user
   */
  protected void checkBindMobile(String prefixUser, String prefixOauth2,
                                 Oauth2LoginCode oauth2LoginCode,
                                 User user, Oauth2Login oauth2Login) {
    String sessionId = oauth2LoginCode.getSessionId();
    //check
    if (ValidatorUtils.isMobile(user.getMobile())) {
      return;
    }
    //save info to redis
    try {
      ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();
      String userJson = objectMapper.writeValueAsString(user);
      String oauth2LoginJson = objectMapper.writeValueAsString(oauth2Login);
      stringValueOperations.set(
          prefixUser + ":" + sessionId, userJson, 300, TimeUnit.SECONDS);
      stringValueOperations.set(
          prefixOauth2 + ":" + sessionId, oauth2LoginJson, 300, TimeUnit.SECONDS);
      log.debug("The current user has not bind phone");
    } catch (JsonProcessingException e) {
      log.warn("Convert user to json failed {}", user);
    }
  }
}
