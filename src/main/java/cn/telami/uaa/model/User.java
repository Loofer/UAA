package cn.telami.uaa.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@TableName("user")
public class User extends BaseModel {

  public static final String ROLE_NORMAL = "NORMAL";

  public static final String ROLES_PREFIX = "ROLE_";

  /**
   * 用户名.
   */
  private String username;

  /**
   * 昵称.
   */
  private String nickname;

  /**
   * 头像.
   */
  private String avatar;

  /**
   * 性别.
   */
  private Gender gender;

  /**
   * 手机号码.
   */
  private String mobile;

  /**
   * 密码.
   */
  private String password;

  /**
   * 邮箱.
   */
  private String email;

  /**
   * 权限.
   */
  private String authorities;

  /**
   * 可用状态.
   */
  private Boolean enabled = true;

  public enum Gender {
    Boy, Girl
  }

  @TableField(exist = false)
  final Collection<? extends GrantedAuthority> defaultAuthorities =
      AuthorityUtils.commaSeparatedStringToAuthorityList(ROLES_PREFIX + ROLE_NORMAL);

  /**
   * get authorities.
   */
  public Collection<? extends GrantedAuthority> buildAuthorities() {
    List<GrantedAuthority> list = Lists.newArrayList();
    for (String authority : authorities.split(",")) {
      GrantedAuthority grantedAuthority =
          new SimpleGrantedAuthority(User.ROLES_PREFIX + authority.toUpperCase());
      list.add(grantedAuthority);
    }
    return list;
  }

  //For some login types that do not require a password
  public org.springframework.security.core.userdetails.User buildUserDetails() {
    return this.buildUserDetails("");
  }

  /**
   * return spring security user.
   *
   * @param password password
   */
  public org.springframework.security.core.userdetails.User buildUserDetails(String password) {
    //权限
    Collection<? extends GrantedAuthority> grantedAuthorities;
    if (StringUtils.isNotEmpty(authorities)) {
      grantedAuthorities = this.buildAuthorities();
    } else {
      grantedAuthorities = defaultAuthorities;
    }
    return new org.springframework.security.core.userdetails.User(
        username,
        password,
        enabled,
        true,
        true,
        true,
        grantedAuthorities
    );
  }

  /**
   * builder.
   */
  @Builder
  public User(String id, LocalDateTime createTime, LocalDateTime updateTime,
              String username, String nickname, String avatar, Gender gender, String mobile,
              String password, String email, String authorities, Boolean enabled) {
    super(id, createTime, updateTime, BaseModel.DEL_FLAG_NORMAL);
    this.username = username;
    this.nickname = nickname;
    this.avatar = avatar;
    this.gender = gender;
    this.mobile = mobile;
    this.password = password;
    this.email = email;
    this.authorities = authorities;
    this.enabled = enabled;
  }
}
