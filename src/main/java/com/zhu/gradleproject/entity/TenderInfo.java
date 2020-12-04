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
    @TableName("tender_info")
@ApiModel(value="TenderInfo对象", description="")
public class TenderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "中标金额（万元）")
      @TableField("tender_money")
    private Double tenderMoney;

      @ApiModelProperty(value = "中标日期")
      @TableField("tender_result_date")
    private LocalDateTime tenderResultDate;

      @ApiModelProperty(value = "面积（平方米）")
      @TableField("area")
    private Double area;

      @ApiModelProperty(value = "项目编号")
      @TableField("prj_num")
    private String prjNum;

      @ApiModelProperty(value = "企业id")
      @TableField("corp_id")
    private String corpId;
}
