package cn.telami.uaa.dto.response;

import cn.telami.uaa.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
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
public class UserResponseDto implements Serializable {

  /**
   * id.
   */
  private String id;
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
   * 手机号.
   */
  private String mobileNumber;
  /**
   * 邮箱.
   */
  private String email;
  /**
   * 创建时间.
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;

  /**
   * 权限.
   */
  private String authorities;

  /**
   * 可用状态.
   */
  private Boolean enabled = true;

  /**
   * 是否设置密码.
   */
  private Boolean hasPassword;

  /**
   * 构造 Profile.
   */
  public static UserResponseDto builderProfile(User user) {
    return UserResponseDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .nickname(user.getNickname())
        .avatar(user.getAvatar())
        .mobileNumber(user.getMobile())
        .email(user.getEmail())
        .createTime(user.getCreateTime())
        .enabled(user.getEnabled())
        .authorities(user.getAuthorities())
        .hasPassword(user.getPassword() != null)
        .build();
  }
}