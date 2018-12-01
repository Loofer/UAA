package cn.telami.uaa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "authentication.oauth2.dingtalk")
@Getter
@Setter
public class DingTalkConfig {

  /**
   * appId.
   */
  private String appId;
  /**
   * appSecret.
   */
  private String appSecret;
}
