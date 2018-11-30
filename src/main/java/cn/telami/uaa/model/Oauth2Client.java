package cn.telami.uaa.model;

import cn.telami.uaa.enums.Description;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@TableName("oauth2_client")
public class Oauth2Client extends BaseModel {

  /**
   * clientId.
   */
  @Setter
  private String clientId;
  /**
   * clientSecret.
   */
  @Setter
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
  private Type type;
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
   * 用户状态.
   */
  public enum Type implements Description {

    Internal("内部"), External("外部");

    Type(String description) {
      this.description = description;
    }

    private final String description;

    @Override
    public String description() {
      return this.description;
    }
  }

  /**
   * 构造函数.
   */
  @Builder(toBuilder = true)
  public Oauth2Client(String id, LocalDateTime createTime, LocalDateTime updateTime,
                      String name, String note, String clientId, String clientSecret,
                      String scope, Type type,
                      String authorizedGrantTypes, String resourceIds,
                      String webServerRedirectUri, String authorities,
                      Integer accessTokenValidity, Integer refreshTokenValidity,
                      String additionalInformation, String autoapprove) {
    super(id, createTime, updateTime, BaseModel.DEL_FLAG_NORMAL);
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.scope = scope;
    this.name = name;
    this.type = type;
    this.note = note;
    this.authorizedGrantTypes = authorizedGrantTypes;
    this.resourceIds = resourceIds;
    this.webServerRedirectUri = webServerRedirectUri;
    this.authorities = authorities;
    this.accessTokenValidity = accessTokenValidity;
    this.refreshTokenValidity = refreshTokenValidity;
    this.additionalInformation = additionalInformation;
    this.autoapprove = autoapprove;
  }
}
