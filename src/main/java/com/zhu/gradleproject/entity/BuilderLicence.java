package com.zhu.gradleproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * @since 2020-12-04
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("builder_licence")
@ApiModel(value="BuilderLicence对象", description="")
public class BuilderLicence implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "合同金额")
      @TableField("contract_money")
    private Double contractMoney;

      @ApiModelProperty(value = "发生日期")
      @TableField("release_date")
    private LocalDateTime releaseDate;

      @ApiModelProperty(value = "面积")
      @TableField("area")
    private Double area;

      @ApiModelProperty(value = "企业id")
      @TableField("corp_id")
    private String corpId;

      @ApiModelProperty(value = "项目编码")
      @TableField("prj_num")
    private String prjNum;


}
