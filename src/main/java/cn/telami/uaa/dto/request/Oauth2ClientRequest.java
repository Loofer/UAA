package cn.telami.uaa.dto.request;

import cn.telami.uaa.model.Oauth2Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Oauth2ClientRequest {

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
  public Oauth2Client builderOauth2Client() {
    return Oauth2Client.builder()
        .scope(this.scope)
        .name(this.name)
        .type(this.type)
        .note(this.note)
        .authorizedGrantTypes(this.authorizedGrantTypes)
        .resourceIds(this.resourceIds)
        .webServerRedirectUri(this.webServerRedirectUri)
        .authorities(this.authorities)
        .accessTokenValidity(this.accessTokenValidity)
        .refreshTokenValidity(this.refreshTokenValidity)
        .additionalInformation(this.additionalInformation)
        .autoapprove(this.autoapprove)
        .build();
  }
}
