package cn.telami.uaa.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BaseModel {

  /**
   * 主键.
   */
  @TableId(value = "id", type = IdType.ID_WORKER_STR)
  protected String id;

  /**
   * 创建时间.
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  protected LocalDateTime createTime;

  /**
   * 修改时间.
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  protected LocalDateTime updateTime;

  /**
   * 删除标识   0：未删除，1：已删除.
   */
  @TableLogic
  protected Integer delFlag = DEL_FLAG_NORMAL;

  /**
   * 未删除.
   */
  public static final Integer DEL_FLAG_NORMAL = 0;
  /**
   * 已删除.
   */
  public static final Integer DEL_FLAG_DELETE = 1;
}
