package com.zhu.gradleproject.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.gradleproject.dto.QueryDto;
import com.zhu.gradleproject.entity.CompanyInfo;
import com.zhu.gradleproject.util.PageList;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业信息查询中间表 服务类
 * </p>
 *
 * @author zwy
 * @since 2020-12-02
 */
public interface CompanyService extends IService<CompanyInfo> {

    /**
     * es 列表查询
     *
     * @param queryDto 查询条件
     *
     * @return PageList<JSONObject>
     *
     * @author zwy
     * @date 12/2/2020 4:10 PM
     */
    PageList<JSONObject> queryFromEs(QueryDto queryDto) ;

    /**
     * 存储企业信息至ES
     *
     * @author zwy
     * @date 12/2/2020 3:03 PM
     */
    void saveCompanyBaseInfoToEs() ;

    /**
     * 保存企业人员信息至ES
     *
     * @author zwy
     * @date 12/3/2020 9:02 AM
     */
    void saveCompanyPersonInfoToEs();

    /**
     * 保存企业业绩
     *
     * @author zwy
     * @date 12/3/2020 7:20 PM
     */
    void saveCompanyProjectInfoToEs();

    /**
     * 保存企业人员业绩
     *
     * @author zwy
     * @date 12/4/2020 11:26 AM
     */
    void saveCompanyPersonProjectInfoToEs();

    /**
     * 删除索引
     * @param indexName 索引名称
     *
     * @author zwy
     * @date 12/3/2020 6:24 PM
     */
    void deleteIndex(String[] indexName);

    /**
     * 输入框联想词关联
     *
     * @param inputStr 输入词
     *
     * @param size 结果返回数量
     *
     * @return List<Map<String, Object>>
     *
     * @author zwy
     * @date 12/7/2020 9:32 AM
     */
    List<Map<String, Object>> associateWordSearch(String inputStr , Integer size) ;
}
