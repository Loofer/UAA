package cn.telami.uaa.model;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户第三方登录.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@TableName("oauth2_login")
public class Oauth2Login extends BaseModel {

  /**
   * 用户id.
   */
  @Setter
  private String userId;

  /**
   * 第三方登陆类型.
   */
  private Type type;

  /**
   * 用户统一标识.
   */
  private String unionid;

  /**
   * 普通用户的标识，对当前开发者帐号唯一.
   */
  private String openid;

  /**
   * 昵称.
   */
  private String nickname;

  /**
   * 性别.
   */
  private String sex;

  /**
   * 头像.
   */
  private String avatar;

  /**
   * 省份.
   */
  private String province;

  /**
   * 城市.
   */
  private String city;

  /**
   * 国家.
   */
  private String country;

  public enum Type {
    WeChat, Alipay, DingTalk
  }

  /**
   * builder.
   */
  @Builder(toBuilder = true)
  public Oauth2Login(String id, LocalDateTime createTime, LocalDateTime updateTime,
                     String userId, Type type, String unionid,
                     String openid, String nickname, String sex, String avatar, String province,
                     String city, String country) {
    super(id, createTime, updateTime, BaseModel.DEL_FLAG_NORMAL);
    this.userId = userId;
    this.type = type;
    this.unionid = unionid;
    this.openid = openid;
    this.nickname = nickname;
    this.sex = sex;
    this.avatar = avatar;
    this.province = province;
    this.city = city;
    this.country = country;
  }
}
