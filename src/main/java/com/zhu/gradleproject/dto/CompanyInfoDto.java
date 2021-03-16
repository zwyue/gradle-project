package com.zhu.gradleproject.dto;

import com.zhu.gradleproject.annotation.ESIndexData;
import com.zhu.gradleproject.annotation.EsId;
import com.zhu.gradleproject.annotation.IncludeField;
import com.zhu.gradleproject.annotation.SearchField;
import com.zhu.gradleproject.entity.CompanyAward;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.zhu.gradleproject.constant.Constant.ES_INDEX;

/**
 * copyright     <a href="http://ditop.tech/>德拓科技</a>
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/4/2020
 */
@Data
@ESIndexData(indexName = ES_INDEX)
public class CompanyInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @EsId
    @IncludeField
    private String id;

    @IncludeField
    private String name;

    @IncludeField
    private String creditCode;

    private String district;

    private String districtName;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdate;

    private Integer performanceTotal;

    private Integer regPersonNum;

    private Float licenseCapital;

    @SearchField
    private String pinyin ;

    @SearchField
    private String abbrPinyin ;

    private List<CompanyAward> companyAwards ;

    private Map<String,String> companyPersonRelation ;
}
