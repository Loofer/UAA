package cn.telami.uaa.dto.response;

import cn.telami.uaa.model.Oauth2Client;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Oauth2ClientResponse {
  /**
   * id.
   */
  private String id;
  /**
   * clientId.
   */
  private String clientId;
  /**
   * clientSecret.
   */
  private String clientSecret;
  /**
   * 应用的所有权限名称.
   */
  private String scope;
  /**
   * 应用名称.
   */
  private String name;
  /**
   * 应用备注.
   */
  private String note;
  /**
   * 应用类型.
   */
  private Oauth2Client.Type type;
  /**
   * 授权方式.
   */
  private String authorizedGrantTypes;
  /**
   * 资源ids.
   */
  private String resourceIds;
  /**
   * 重定向urls.
   */
  private String webServerRedirectUri;
  /**
   * 权限.
   */
  private String authorities;
  /**
   * accessToken有效时间.
   */
  private Integer accessTokenValidity;
  /**
   * refreshToken有效时间.
   */
  private Integer refreshTokenValidity;
  /**
   * 额外信息.
   */
  private String additionalInformation;
  /**
   * 自动批准.
   */
  private String autoapprove;

  /**
   * 构建Oauth2Client.
   */
  public static Oauth2ClientResponse builderOauth2Client(Oauth2Client client) {
    return Oauth2ClientResponse.builder()
        .id(client.getId())
        .clientId(client.getClientId())
        .clientSecret(client.getClientSecret())
        .scope(client.getScope())
        .name(client.getName())
        .type(client.getType())
        .note(client.getNote())
        .authorizedGrantTypes(client.getAuthorizedGrantTypes())
        .resourceIds(client.getResourceIds())
        .webServerRedirectUri(client.getWebServerRedirectUri())
        .authorities(client.getAuthorities())
        .accessTokenValidity(client.getAccessTokenValidity())
        .refreshTokenValidity(client.getRefreshTokenValidity())
        .additionalInformation(client.getAdditionalInformation())
        .autoapprove(client.getAutoapprove())
        .build();
  }
}
