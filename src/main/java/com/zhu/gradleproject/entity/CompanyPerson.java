package com.zhu.gradleproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhu.gradleproject.annotation.ESIndexData;
import com.zhu.gradleproject.annotation.EsRoutingId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
    @TableName(value = "company_person")
@ESIndexData(indexName = "company_info")
public class CompanyPerson implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 注册企业ID
     */
      @TableField("corp_id")
      @EsRoutingId
    private String corpId;

      /**
     * 对应人员表id
     */
      @TableField("per_id")
    private String perId;

      /**
     * 人员姓名
     */
      @TableField("per_name")
    private String perName;

      /**
     * 注册类型
     */
      @TableField("person_cert")
    private String personCert;
}
