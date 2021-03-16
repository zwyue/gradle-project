package com.zhu.gradleproject.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
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
import com.zhu.gradleproject.entity.es.PageSortHighLight;
import com.zhu.gradleproject.entity.es.Sort;
import com.zhu.gradleproject.mapper.CompanyInfoDao;
import com.zhu.gradleproject.service.CompanyPersonService;
import com.zhu.gradleproject.service.CompanyService;
import com.zhu.gradleproject.service.es.EsDataSaveService;
import com.zhu.gradleproject.service.es.EsQueryService;
import com.zhu.gradleproject.util.EsMappingCorp;
import com.zhu.gradleproject.util.PageList;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.zhu.gradleproject.constant.Constant.ES_INDEX;
import static com.zhu.gradleproject.constant.Constant.LETTER_REGEX;
import static com.zhu.gradleproject.util.BeanTools.culInitialCapacity;
import static org.elasticsearch.index.query.QueryBuilders.*;

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
public class CompanyInfoServiceImpl extends ServiceImpl<CompanyInfoDao, CompanyInfo> implements CompanyService {

    @Resource
    private EsQueryService esSearchService ;

    @Resource
    private EsDataSaveService esDataSaveService ;

    @Resource
    private CompanyInfoDao companyInfoDao ;

    @Resource
    private CompanyPersonService companyPersonService ;

    @Resource
    private ProjectInfoServiceImpl projectInfoService ;

    @Override
    public PageList<JSONObject> queryFromEs(QueryDto queryDto) {

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

            if(ObjectUtil.isNotEmpty(awardLimit.get("awardTime"))){
                boolQueryBuilder.must(rangeQuery("companyAwards.awardTime").from(awardLimit.get("awardTime")));
            }

            if(ObjectUtil.isNotEmpty(awardLimit.get("name"))){
                boolQueryBuilder.must(matchQuery("companyAwards.name",awardLimit.get("name")));
            }

            if(ObjectUtil.isNotEmpty(awardLimit.get("level"))){
                boolQueryBuilder.must(matchQuery("companyAwards.level",awardLimit.get("level")));
            }
            queryBuilder.must(nestedQuery("companyAwards",boolQueryBuilder, ScoreMode.Max));
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

        //保证查询结果一定是企业
        queryBuilder.must(existsQuery("name"));

        PageSortHighLight psh = new PageSortHighLight(queryDto.getPageNum(), queryDto.getPageSize());
        psh.setIndex("company_info");
        psh.setEsQueryBuilder(queryBuilder);

        Sort.Order orderScore = new Sort.Order(SortOrder.DESC, "_score");
        psh.setSort(new Sort(orderScore));

        return esSearchService.queryPageList(JSONObject.class,psh) ;
    }

    @Override
    public void saveCompanyBaseInfoToEs() throws Exception {
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
    public void saveCompanyPersonInfoToEs() throws Exception {
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
    public void saveCompanyProjectInfoToEs() throws Exception {
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
    public void saveCompanyPersonProjectInfoToEs() throws Exception {
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
    public void deleteIndex(String[] indexName) throws IOException {
        esDataSaveService.deleteIndex(indexName);
    }

    @Override
    public List<Map<String, Object>> associateWordSearch(String inputStr , Integer size) throws IOException {

        //是否包含汉字，用于拼音匹配
        if(inputStr.matches(LETTER_REGEX)) {

            List<Map<String, Object>> first = esSearchService.search(size ,wildcardQuery("name.keyword","*"+inputStr+"*")
                    ,"company_info","id","name","creditCode") ;

            List<Map<String, Object>> keywords = new LinkedList<>(first);
            if (first.size() != size) {
                List<Map<String, Object>> second = esSearchService.search(size, matchQuery("name", inputStr)
                        , ES_INDEX, "id", "name", "creditCode");
                if (CollectionUtil.isNotEmpty(second)) {
                    first.forEach(second::remove);
                    keywords.addAll(second.size() + first.size() > size ? second.subList(0, size - first.size()) : second);
                }
            }
            return keywords ;
        }

        return esSearchService.associate(size,new CompanyInfoDto(),inputStr);
    }

    @Override
    public List<Map<String, Object>> parentWithChild(QueryDto queryDto) throws Exception {

        BoolQueryBuilder parentQueryBuilder = QueryBuilders.boolQuery();

        Optional.ofNullable(queryDto.getPerName()).ifPresent(perName->
            parentQueryBuilder.must(wildcardQuery("perName" ,"*"+perName+"*")));

//        ScriptScoreFunctionBuilder scoreFunction = ScoreFunctionBuilders
//                .scriptFunction("_score * doc['prjDate'].value.toInstant().toEpochMilli()");
//
        BoolQueryBuilder projectQuery = QueryBuilders.boolQuery();
        projectQuery.must(existsQuery("prjName"));
        projectQuery.must(nestedQuery("tenderInfoList",existsQuery("tenderInfoList"),ScoreMode.Avg));

        HasChildQueryBuilder childQueryBuilder
                = new HasChildQueryBuilder("personProject" ,projectQuery,ScoreMode.Max);

        InnerHitBuilder innerHitBuilder = new InnerHitBuilder();
        innerHitBuilder.setSize(1);
        innerHitBuilder.addSort(SortBuilders.fieldSort("endDate").order(SortOrder.DESC));
        innerHitBuilder.setFetchSourceContext(new FetchSourceContext(true,
                new String[]{"tenderInfoList","prjName"},null));

        childQueryBuilder.innerHit(innerHitBuilder);
        parentQueryBuilder.must(childQueryBuilder);

        Sort.Order order = new Sort.Order(SortOrder.DESC,"_score");

        //分页
        PageSortHighLight psh = new PageSortHighLight(queryDto.getPageNum(), queryDto.getPageSize());
        //排序
        psh.setSort(new Sort(order));

        PageList<JSONObject> pageList = esSearchService.searchHasInnerHits(parentQueryBuilder, psh
                ,new String[]{"perName","perId","personCerts"},ES_INDEX);

        return null ;
    }
}
