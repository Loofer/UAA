package cn.telami.uaa.config.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;

public class OAuth2AuthenticationDeserializer extends JsonDeserializer<OAuth2Authentication> {
  private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationDeserializer.class);

  public OAuth2AuthenticationDeserializer() {
  }

  private List<GrantedAuthority> getAuthorities(JsonNode node) {
    List<GrantedAuthority> authorities = new ArrayList();
    if (node == null) {
      return authorities;
    } else {
      Iterator iterator = node.elements();

      while (iterator.hasNext()) {
        JsonNode authItem = (JsonNode) iterator.next();
        String authValue = this.getStringValue(authItem, "authority", (String) null);
        authorities.add(new SimpleGrantedAuthority(authValue));
      }

      return authorities;
    }
  }

  private Set<String> getStringSet(JsonNode node) {
    Set<String> result = new HashSet();
    if (node == null) {
      return result;
    } else {
      Iterator iterator = node.elements();

      while (iterator.hasNext()) {
        JsonNode item = (JsonNode) iterator.next();
        String value = item.textValue();
        result.add(value);
      }

      return result;
    }
  }

  private Map<String, String> getStringMap(JsonNode node) {
    Map<String, String> result = new HashMap();
    if (node == null) {
      return result;
    } else {
      Iterator iterator = node.fieldNames();

      while (iterator.hasNext()) {
        String key = (String) iterator.next();
        if (key != null) {
          String value = node.get(key).textValue();
          result.put(key, value);
        }
      }

      return result;
    }
  }

  private OAuth2Request getStoredRequest(JsonNode node) {
    if (node == null) {
      return null;
    } else {
      String clientId = this.getStringValue(node, "clientId", (String) null);
      JsonNode scopeNode = node.has("scope") ? node.get("scope") : null;
      Set<String> scope = this.getStringSet(scopeNode);
      JsonNode requestParametersNode =
          node.has("requestParameters") ? node.get("requestParameters") : null;
      Map<String, String> requestParameters = this.getStringMap(requestParametersNode);
      JsonNode resourceIdsNode = node.has("resourceIds") ? node.get("resourceIds") : null;
      Set<String> resourceIds = this.getStringSet(resourceIdsNode);
      JsonNode authoritiesNode = node.has("authorities") ? node.get("authorities") : null;
      List<GrantedAuthority> storedAuthorities = this.getAuthorities(authoritiesNode);
      boolean approved = this.getBooleanValue(node, "approved", false);
      String redirectUri = this.getStringValue(node, "redirectUri", (String) null);
      JsonNode responseTypesNode =
          node.has("responseTypes") ? node.get("responseTypes") : null;
      Set<String> responseTypes = this.getStringSet(responseTypesNode);
      OAuth2Request storedRequest =
          new OAuth2Request(requestParameters, clientId, storedAuthorities,
              approved, scope, resourceIds, redirectUri, responseTypes, (Map) null);
      JsonNode refreshTokenRequestNode =
          node.has("refreshTokenRequest") ? node.get("refreshTokenRequest") : null;
      boolean refresh = this.getBooleanValue(node, "refresh", false);
      if (refreshTokenRequestNode != null && refresh) {
        String grantType = this.getStringValue(refreshTokenRequestNode, "grantType", (String) null);
        TokenRequest tokenRequest = new TokenRequest(requestParameters, clientId, scope, grantType);
        this.setFieldValueByReflect(storedRequest, "refresh", tokenRequest);
      }

      return storedRequest;
    }
  }

  private void setFieldValueByReflect(Object object, String fieldName, Object value) {
    try {
      Field field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(object, value);
    } catch (NoSuchFieldException var5) {
      log.error("Unable to find the field " + fieldName, var5);
      throw new RuntimeException(MessageFormatter.format(
          "Unable to get the field '{}'", fieldName).getMessage());
    } catch (IllegalAccessException var6) {
      log.error("Unable to set value to the field " + fieldName, var6);
      throw new RuntimeException(MessageFormatter.format(
          "Unable to set value to the field '{}'", fieldName).getMessage());
    }
  }

  private void setSuperFieldValueByReflect(Object object, String fieldName, Object value) {
    try {
      Field field = object.getClass().getSuperclass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(object, value);
    } catch (NoSuchFieldException var5) {
      throw new RuntimeException(var5.getMessage());
    } catch (IllegalAccessException var6) {
      throw new RuntimeException(var6.getMessage());
    }
  }

  private String getStringValue(JsonNode node, String key, String defaultValue) {
    if (node != null && node.has(key) && node.get(key) != null) {
      String value = node.get(key).textValue();
      return value == null ? defaultValue : value;
    } else {
      return defaultValue;
    }
  }

  private Boolean getBooleanValue(JsonNode node, String key, Boolean defaultValue) {
    if (node != null && node.has(key) && node.get(key) != null) {
      Boolean value = node.get(key).booleanValue();
      return value == null ? defaultValue : value;
    } else {
      return defaultValue;
    }
  }

  private User getPrincipal(JsonNode node) {
    if (node == null) {
      return null;
    } else {
      String username = this.getStringValue(node, "username", (String) null);
      boolean accountNonExpired =
          this.getBooleanValue(node, "accountNonExpired", true);
      boolean accountNonLocked =
          this.getBooleanValue(node, "accountNonLocked", true);
      boolean credentialsNonExpired =
          this.getBooleanValue(node, "credentialsNonExpired", true);
      boolean enabled = this.getBooleanValue(node, "enabled", true);
      JsonNode authoritiesNode = node.has("authorities") ? node.get("authorities") : null;
      List<GrantedAuthority> authorities = this.getAuthorities(authoritiesNode);
      User user = new User(username, "", enabled,
          accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
      user.eraseCredentials();
      return user;
    }
  }

  private Authentication getUserAuthentication(JsonNode node) {
    if (node == null) {
      return null;
    } else {
      JsonNode authoritiesNode = node.has("authorities") ? node.get("authorities") : null;
      this.getAuthorities(authoritiesNode);
      boolean authenticated = this.getBooleanValue(node, "authenticated", false);
      JsonNode detailsNode = node.has("details") ? node.get("details") : null;
      Map<String, String> details = this.getStringMap(detailsNode);
      JsonNode principalNode = node.has("principal") ? node.get("principal") : null;
      User principal = this.getPrincipal(principalNode);
      UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken(principal, (Object) null, principal.getAuthorities());
      this.setSuperFieldValueByReflect(userAuthentication, "authenticated", authenticated);
      userAuthentication.setDetails(details);
      return userAuthentication;
    }
  }

  public OAuth2Authentication deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = (JsonNode) oc.readTree(jsonParser);
    JsonNode authoritiesNode = node.has("authorities") ? node.get("authorities") : null;
    this.getAuthorities(authoritiesNode);
    boolean authenticated = this.getBooleanValue(node, "authenticated", false);
    JsonNode storedRequestNode = node.has("oauth2Request") ? node.get("oauth2Request") : null;
    OAuth2Request storedRequest = this.getStoredRequest(storedRequestNode);
    JsonNode userAuthenticationNode = node.has("userAuthentication") ? node.get("userAuthentication") : null;
    Authentication userAuthentication = this.getUserAuthentication(userAuthenticationNode);
    OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(storedRequest, userAuthentication);
    oAuth2Authentication.setAuthenticated(authenticated);
    return oAuth2Authentication;
  }
}

