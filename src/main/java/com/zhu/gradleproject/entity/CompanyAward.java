package com.zhu.gradleproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author zwy
 * @since 2020-12-03
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("company_award")
public class CompanyAward implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 奖项级别
     */
      @TableField("level")
    private String level;

      /**
     * 奖项名称
     */
      @TableField("name")
    private String name;

      /**
     * 颁奖时间
     */
      @TableField("award_time")
    private LocalDateTime awardTime;

      /**
     * 企业id
     */
      @TableField("corp_id")
    private String corpId;


}
