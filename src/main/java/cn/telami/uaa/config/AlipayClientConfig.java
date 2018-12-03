package cn.telami.uaa.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "authentication.oauth2.alipay")
@Setter
@Getter
public class AlipayClientConfig {

  /**
   * 网关.
   */
  private String gatewayUrl;
  /**
   * appid.
   */
  private String appId;
  /**
   * 私钥.
   */
  private String appPrivateKey;
  /**
   * 格式 默认json.
   */
  private String format;
  /**
   * 编码 默认utf-8.
   */
  private String charset;
  /**
   * 支付宝公钥.
   */
  private String alipayPublicKey;
  /**
   * 签名方式.
   */
  private String signType;

  @Bean
  AlipayClient alipayClient() {
    return new DefaultAlipayClient(
        getGatewayUrl(),
        getAppId(),
        getAppPrivateKey(),
        getFormat(),
        getCharset(),
        getAlipayPublicKey(),
        getSignType()
    );
  }
}
