package com.zhu.gradleproject.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhu.gradleproject.annotation.ESIndexData;
import com.zhu.gradleproject.annotation.EsId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @TableName(value = "company_info" ,resultMap = "BaseResultMap")
@ESIndexData(indexName = "company_info")
public class CompanyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 企业id
     */
        @TableId("id")
        @EsId
      private String id;

      /**
     * 企业名称
     */
      @TableField("name")
    private String name;

      /**
     * 企业信用代码
     */
      @TableField("credit_code")
    private String creditCode;

      /**
     * 企业所在地
     */
      @TableField("district")
    private String district;

      /**
     * 企业所在地名称
     */
      @TableField("district_name")
    private String districtName;

      /**
     * 创建时间
     */
      @TableField("create_time")
    private LocalDateTime createTime;

      /**
     * 最后更新时间
     */
      @TableField("last_update")
    private LocalDateTime lastUpdate;

      /**
     * 业绩数
     */
      @TableField("performance_total")
    private Integer performanceTotal;

      /**
     * 注册人员数
     */
      @TableField("reg_person_num")
    private Integer regPersonNum;

      /**
     * 工商注册资本
     */
      @TableField("license_capital")
    private Float licenseCapital;

    /**
     * 此处 需要在 tableName 中显式声明 resultMap ，
     * 且需要在 生成的 mybatis 中 加入 collection ，
     * associate 同理
     */
    private List<CompanyAward> companyAwards ;

    /**
     * 父子文档在此处声明关系，关系中必须包含，即 map 中必须有 key -> 'name'
     */
    private Map<String,String> companyPersonRelation ;
}
