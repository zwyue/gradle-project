package com.zhu.gradleproject.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.zhu.gradleproject.dto.CompanyInfoDto;
import com.zhu.gradleproject.dto.CompanyPersonDto;
import com.zhu.gradleproject.dto.ProjectInfoDto;
import com.zhu.gradleproject.dto.QueryDto;
import com.zhu.gradleproject.entity.CompanyInfo;
import com.zhu.gradleproject.entity.CompanyPerson;
import com.zhu.gradleproject.entity.ProjectInfo;
import com.zhu.gradleproject.mapper.CompanyInfoDao;
import com.zhu.gradleproject.service.CompanyInfoService;
import com.zhu.gradleproject.service.CompanyPersonService;
import com.zhu.gradleproject.service.es.EsDataSaveService;
import com.zhu.gradleproject.service.es.EsSearchService;
import com.zhu.gradleproject.util.EsMappingCorp;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.zhu.gradleproject.constant.Constant.LETTER_REGEX;
import static com.zhu.gradleproject.util.BeanTools.culInitialCapacity;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * <p>
 * 企业信息查询中间表 服务实现类
 * </p>
 *
 * @author zwy
 * @since 2020-12-02
 */
@Service
@Log4j2
public class CompanyInfoServiceImpl extends ServiceImpl<CompanyInfoDao, CompanyInfo> implements CompanyInfoService {

    @Resource
    private EsSearchService esSearchService ;

    @Resource
    private EsDataSaveService esDataSaveService ;

    @Resource
    private CompanyInfoDao companyInfoDao ;

    @Resource
    private CompanyPersonService companyPersonService ;

    @Resource
    private ProjectInfoServiceImpl projectInfoService ;

    @Override
    public List<JSONObject> queryFromEs(QueryDto queryDto) throws Exception {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if(StringUtils.isNotBlank(queryDto.getName())) {
            queryBuilder.must(matchQuery("name",queryDto.getName()));
        }

        if(StringUtils.isNotBlank(queryDto.getDistrict())) {
            queryBuilder.must(matchQuery("district",queryDto.getDistrict()));
        }

        if(queryDto.getLicenseCapital()!=null) {
            queryBuilder.must(rangeQuery("licenseCapital").gt(queryDto.getLicenseCapital()));
        }

        Map<String,Object> awardLimit = queryDto.getAwardLimit() ;
        if(CollUtil.isNotEmpty(awardLimit)) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(rangeQuery("companyAwards.awardTime").from(awardLimit.get("awardTime")));
            boolQueryBuilder.must(rangeQuery("companyAwards.name").from(awardLimit.get("name")));
            boolQueryBuilder.must(rangeQuery("companyAwards.level").from(awardLimit.get("level")));
            queryBuilder.must(nestedQuery("tenderInfoList",boolQueryBuilder, ScoreMode.Max));
        }

        Map<String,Object> perLimit = queryDto.getPerLimit();
        if(CollUtil.isNotEmpty(perLimit)) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchQuery("perName",perLimit.get("perName")));
            List<String> certs = Convert.toList(String.class,perLimit.get("personCerts"));

            if(CollUtil.isNotEmpty(certs)) {
                certs.forEach(cert->boolQueryBuilder.must(matchQuery("personCerts",cert)));
            }

            queryBuilder.must(new HasChildQueryBuilder("person", boolQueryBuilder, ScoreMode.Avg));
        }

        Map<String,Object> perPrjLimit = queryDto.getPerPrjLimit();
        if(CollUtil.isNotEmpty(perPrjLimit)) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchQuery("prjName",perPrjLimit.get("prjName")));

            boolQueryBuilder.must(
                    nestedQuery("tenderInfoList",rangeQuery("tenderInfoList").gte(perPrjLimit.get("tenderMoney")),ScoreMode.Avg));

            queryBuilder.must(new HasChildQueryBuilder("person",
                    new HasChildQueryBuilder("personProject",boolQueryBuilder,ScoreMode.Avg), ScoreMode.Avg));
        }

        Map<String,Object> corpPrjLimit = queryDto.getCorpPrjLimit();
        if(CollUtil.isNotEmpty(corpPrjLimit)) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(matchQuery("prjName",corpPrjLimit.get("prjName")));

            boolQueryBuilder.must(
                    nestedQuery("tenderInfoList",rangeQuery("tenderInfoList").gte(corpPrjLimit.get("tenderMoney")),ScoreMode.Avg));

            queryBuilder.must(new HasChildQueryBuilder("corpProject", boolQueryBuilder, ScoreMode.Avg));
        }

        return esSearchService.search(QueryBuilders.matchAllQuery(),JSONObject.class,"company_info") ;
    }

    @Override
    public void saveCompanyBaseInfoToEs() {
        QueryWrapper<CompanyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time").select("*") ;

        List<CompanyInfo> companyInfoList = companyInfoDao.selectList(queryWrapper);

        List<CompanyInfoDto> dtoList = new ArrayList<>();

        companyInfoList.forEach(company->{
            CompanyInfoDto dto = new CompanyInfoDto() ;
            BeanUtils.copyProperties(company ,dto) ;

            dto.setPinyin(PinyinHelper.toPinyin(company.getName(), PinyinStyleEnum.NORMAL).replaceAll(" ","")) ;
            dto.setAbbrPinyin(PinyinHelper.toPinyin(company.getName(),PinyinStyleEnum.FIRST_LETTER).replaceAll(" ","")) ;

            dtoList.add(dto) ;
        });

        BulkResponse responses = esDataSaveService.save(dtoList, EsMappingCorp.corpMapping());

        log.info(" ...... save success {} millis ...... " ,responses.getIngestTookInMillis() );
    }

    @Override
    public void saveCompanyPersonInfoToEs() {
        List<CompanyPerson> companyPersonList = companyPersonService.list();
        List<CompanyPersonDto> dtoList = new ArrayList<>();

        //按人员id分组
        Map<String,List<CompanyPerson>> groupMap =
                companyPersonList.stream().collect(Collectors.groupingBy(CompanyPerson::getPerId)) ;

        groupMap.keySet().forEach(key -> {
            List<CompanyPerson> cp =  groupMap.get(key) ;

            CompanyPersonDto dto = new CompanyPersonDto() ;
            BeanUtils.copyProperties(cp.get(0) ,dto);

            dto.setPersonCerts(cp.stream().map(CompanyPerson::getPersonCert).collect(Collectors.toList()));
            Map<String,String> relationMap = new HashMap<>(culInitialCapacity(2));
            relationMap.put("name", "person");
            relationMap.put("parent", dto.getCorpId());
            dto.setCompanyPersonRelation(relationMap);
            dtoList.add(dto);
        });

        BulkResponse responses = esDataSaveService.saveSubDoc(dtoList, EsMappingCorp.corpMapping());
        log.info(" ...... save success {} millis ...... " ,responses.getIngestTookInMillis() );
    }

    @Override
    public void saveCompanyProjectInfoToEs() {
        List<ProjectInfo> projectInfos = projectInfoService.list() ;

        List<ProjectInfoDto> dtoList = new ArrayList<>();

        projectInfos.forEach(prj->{
            ProjectInfoDto dto = new ProjectInfoDto();
            BeanUtils.copyProperties(prj ,dto);

            dto.setId(prj.getCorpId() + prj.getPrjNum());
            dto.setRoutingId(prj.getCorpId());

            Map<String,String> relationMap = new HashMap<>(culInitialCapacity(2));
            relationMap.put("name", "corpProject");
            relationMap.put("parent", prj.getCorpId());
            dto.setCompanyPersonRelation(relationMap);

            dtoList.add(dto) ;
        });

        BulkResponse responses = esDataSaveService.saveSubDoc(dtoList, EsMappingCorp.corpMapping());
        log.info(" ...... save success {} millis ...... " ,responses.getIngestTookInMillis() );
    }

    @Override
    public void saveCompanyPersonProjectInfoToEs() {
        List<ProjectInfo> projectInfos = projectInfoService.list() ;

        List<ProjectInfoDto> dtoList = new ArrayList<>();

        projectInfos
                .stream()
                .filter(prj-> StringUtils.isNotBlank(prj.getPerId())&&(!"0".equals(prj.getPerId())))
                .forEach(prj->{
            ProjectInfoDto dto = new ProjectInfoDto();
            BeanUtils.copyProperties(prj ,dto);

            dto.setId(prj.getPerId() + prj.getPrjNum());
            dto.setRoutingId(prj.getPerId());

            Map<String,String> relationMap = new HashMap<>(culInitialCapacity(2));
            relationMap.put("name", "personProject");
            relationMap.put("parent", prj.getPerId());
            dto.setCompanyPersonRelation(relationMap);

            dtoList.add(dto) ;
        });

        BulkResponse responses = esDataSaveService.saveSubDoc(dtoList, EsMappingCorp.corpMapping());
        log.info(" ...... save success {} millis ...... " ,responses.getIngestTookInMillis() );
    }

    @Override
    public void deleteIndex(String[] indexName) {
        esDataSaveService.deleteIndex(indexName);
    }

    @Override
    public List<Map<String, Object>> associateWordSearch(String inputStr , Integer size) throws Exception {

        //是否包含汉字，用于拼音匹配
        if(inputStr.matches(LETTER_REGEX)) {

            List<Map<String, Object>> first = esSearchService.search(size ,wildcardQuery("name.keyword","*"+inputStr+"*")
                    ,"company_info","id","name","creditCode") ;

            List<Map<String, Object>> keywords = new LinkedList<>(first);
            if (first.size() != size) {
                List<Map<String, Object>> second = esSearchService.search(size, matchQuery("name", inputStr)
                        , "company_info", "id", "name", "creditCode");
                if (CollectionUtil.isNotEmpty(second)) {
                    first.forEach(second::remove);
                    keywords.addAll(second.size() + first.size() > size ? second.subList(0, size - first.size()) : second);
                }
            }
            return keywords ;
        }

        return esSearchService.associate(size,new CompanyInfoDto(),inputStr);
    }
}
