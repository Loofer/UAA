package cn.telami.uaa.config.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

public class OAuth2RefreshTokenDeserializer extends JsonDeserializer<OAuth2RefreshToken> {
  private static final Logger log = LoggerFactory.getLogger(OAuth2RefreshTokenDeserializer.class);

  public OAuth2RefreshTokenDeserializer() {
  }

  private String getStringValue(JsonNode node, String key, String defaultValue) {
    if (node != null && node.has(key) && node.get(key) != null) {
      String value = node.get(key).textValue();
      return value == null ? defaultValue : value;
    } else {
      return defaultValue;
    }
  }

  private Long getLongValue(JsonNode node, String key, Long defaultValue) {
    if (node != null && node.has(key) && node.get(key) != null) {
      Long value = node.get(key).longValue();
      return value == null ? defaultValue : value;
    } else {
      return defaultValue;
    }
  }

  public OAuth2RefreshToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = (JsonNode)oc.readTree(jsonParser);
    String value = this.getStringValue(node, "value", (String)null);
    Long expiration = this.getLongValue(node, "expiration", (Long)null);
    return (OAuth2RefreshToken)(expiration == null ? new DefaultOAuth2RefreshToken(value) : new DefaultExpiringOAuth2RefreshToken(value, new Date(expiration)));
  }
}
