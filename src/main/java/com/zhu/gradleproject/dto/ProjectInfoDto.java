package com.zhu.gradleproject.dto;

import com.zhu.gradleproject.annotation.AttributeValue;
import com.zhu.gradleproject.annotation.ESIndexData;
import com.zhu.gradleproject.annotation.EsId;
import com.zhu.gradleproject.annotation.EsRoutingId;
import com.zhu.gradleproject.entity.BuilderLicence;
import com.zhu.gradleproject.entity.TenderInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/4/2020
 */
@Data
@ESIndexData(indexName = "company_info")
public class ProjectInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @EsId
    private String id;

    @EsRoutingId
    @AttributeValue()
    private String routingId;

    private String prjName;

    private Double allInvest;

    private LocalDateTime beginDate;

    private LocalDateTime endDate;

    private List<TenderInfo> tenderInfoList ;

    private List<BuilderLicence> builderLicences ;

    private Map<String,String> companyPersonRelation ;
}
