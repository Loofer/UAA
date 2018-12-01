package cn.telami.uaa.service.impl;

import cn.pilipa.security.OAuth2AuthenticationDeserializer;
import cn.pilipa.security.OAuth2RefreshTokenDeserializer;
import cn.telami.uaa.model.AuthorizationCode;
import cn.telami.uaa.service.AuthorizationCodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class UaaAuthorizationCodeServices implements AuthorizationCodeServices {

  @Autowired
  private AuthorizationCodeService authorizationCodeService;

  private ObjectMapper objectMapper = new ObjectMapper();

  /**
   * init.
   */
  public UaaAuthorizationCodeServices() {
    SimpleModule module = new SimpleModule();
    module.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
    module.addDeserializer(OAuth2RefreshToken.class, new OAuth2RefreshTokenDeserializer());
    objectMapper.registerModule(module);
  }

  /**
   * call【/oauth/authorize?response_type=code&client_id=xxx&redirect_uri=xxx】 generate code.
   *
   * @param authentication 权限
   */
  @Override
  public String createAuthorizationCode(OAuth2Authentication authentication) {
    String method = "createAuthorizationCode";
    log.debug("Enter {}", method);
    String code = null;
    AuthorizationCode authorizationCode = null;
    log.debug("RedirectUri={}", authentication.getOAuth2Request().getRedirectUri());
    try {
      authorizationCode = AuthorizationCode.builder()
          .code(RandomStringUtils.randomAlphanumeric(16))
          .authentication(objectMapper.writeValueAsString(authentication))
          .build();
      log.debug("Create the authentication code");
      authorizationCodeService.save(authorizationCode);
      log.debug("Save the authorizationCode={}", authorizationCode);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize the authenticaiton", e);
    }
    if (authorizationCode != null) {
      code = authorizationCode.getCode();
      log.debug("Save the authentication code. code={}", code);
    }
    if (code == null) {
      log.error("Generate authorization code failed");
      throw new InvalidGrantException("Generate authorization code failed");
    }
    log.debug("Exit {}", method);
    return code;
  }

  /**
   * call【/oauth/token?grant_type=authorization_code&code=xxx&redirect_uri=xxx&scope=xxx】.
   * get access_token
   *
   * @param code 授权码
   */
  @Override
  public OAuth2Authentication consumeAuthorizationCode(String code)
      throws InvalidGrantException {
    String method = "consumeAuthorizationCode";
    log.debug("Enter {}. code={}", method, code);
    LambdaQueryWrapper<AuthorizationCode> wrapper =
        new QueryWrapper<AuthorizationCode>().lambda().eq(AuthorizationCode::getCode, code);
    AuthorizationCode authorizationCode = authorizationCodeService.getOne(wrapper);
    log.debug("Get authentication code from db");
    OAuth2Authentication auth = null;
    if (!ObjectUtils.isEmpty(authorizationCode)) {
      log.debug("Get the authorizationCode={}", authorizationCode);
      log.debug("Deserialize authentication");
      try {
        auth = objectMapper.readValue(
            authorizationCode.getAuthentication(),
            OAuth2Authentication.class
        );
      } catch (IOException e) {
        auth = null;
        log.error("Fail to deserialize the authentication", e);
      }
      if (auth != null) {
        log.debug("RedirectUri={}", auth.getOAuth2Request().getRedirectUri());
      }
    }

    if (auth == null) {
      log.error("Authentication is null");
      throw new InvalidGrantException("Invalid authorization code: " + code);
    }
    log.debug("Delete the code");
    authorizationCodeService.removeById(authorizationCode.getId());
    log.debug("Exit {}", method);
    return auth;
  }
}
