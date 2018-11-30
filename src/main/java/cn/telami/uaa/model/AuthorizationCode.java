package cn.telami.uaa.model;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 授权码.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@TableName("authorization_code")
public class AuthorizationCode extends BaseModel {

  /**
   * 授权码.
   */
  private String code;

  /**
   * 权限.
   */
  private String authentication;

  /**
   * 删除标识   0：未删除，1：已删除.
   */
  private Integer delFlag = DEL_FLAG_NORMAL;

  /**
   * builder.
   */
  @Builder
  public AuthorizationCode(String id, LocalDateTime createTime, LocalDateTime updateTime,
                           String code, String authentication) {
    super(id, createTime, updateTime, BaseModel.DEL_FLAG_NORMAL);
    this.code = code;
    this.authentication = authentication;
  }
}
