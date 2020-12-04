package com.zhu.gradleproject.dto;

import com.zhu.gradleproject.annotation.AttributeValue;
import com.zhu.gradleproject.annotation.ESIndexData;
import com.zhu.gradleproject.annotation.EsId;
import com.zhu.gradleproject.annotation.EsRoutingId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;



/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/2/2020
 */
@ESIndexData(indexName = "company_info")
@Data
public class CompanyPersonDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @EsRoutingId
    @AttributeValue()
    private String corpId;

    @EsId
    private String perId;

    private String perName;

    private List<String> personCerts;

    private Map<String,String> companyPersonRelation ;
}
