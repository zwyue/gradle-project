package com.zhu.gradleproject.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.gradleproject.mapper.CompanyInfoDao;
import com.zhu.gradleproject.dto.CompanyPersonDto;
import com.zhu.gradleproject.dto.ProjectInfoDto;
import com.zhu.gradleproject.entity.CompanyInfo;
import com.zhu.gradleproject.entity.CompanyPerson;
import com.zhu.gradleproject.entity.ProjectInfo;
import com.zhu.gradleproject.service.CompanyInfoService;
import com.zhu.gradleproject.service.CompanyPersonService;
import com.zhu.gradleproject.service.es.EsDataSaveService;
import com.zhu.gradleproject.service.es.EsSearchService;
import com.zhu.gradleproject.util.EsMappingCorp;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zhu.gradleproject.util.BeanTools.culInitialCapacity;

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
    public List<JSONObject> queryFromEs(List<String> aaa) throws Exception {
        return esSearchService.search(QueryBuilders.matchAllQuery(),JSONObject.class,"company_info") ;
    }

    @Override
    public void saveCompanyBaseInfoToEs() {
        QueryWrapper<CompanyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time").select("*") ;

        List<CompanyInfo> esCompanies = companyInfoDao.selectList(queryWrapper);

        BulkResponse responses = esDataSaveService.save(esCompanies, EsMappingCorp.corpMapping());

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
}
