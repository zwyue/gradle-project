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
import java.util.List;

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
    @TableName(value = "project_info" , resultMap = "BaseResultMap")
@ApiModel(value="ProjectInfo对象", description="")
public class ProjectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty(value = "项目编码")
      @TableField("prj_num")
    private String prjNum;

      @ApiModelProperty(value = "企业id")
      @TableField("corp_id")
    private String corpId;

      @ApiModelProperty(value = "人员id")
      @TableField("per_id")
    private String perId;

      @ApiModelProperty(value = "项目名称")
      @TableField("prj_name")
    private String prjName;

      @ApiModelProperty(value = "总投资")
      @TableField("all_invest")
    private Double allInvest;

      @ApiModelProperty(value = "开始时间")
      @TableField("begin_date")
    private LocalDateTime beginDate;

      @ApiModelProperty(value = "结束时间")
      @TableField("end_date")
    private LocalDateTime endDate;

      private List<TenderInfo> tenderInfoList ;

      private List<BuilderLicence> builderLicences ;
}
