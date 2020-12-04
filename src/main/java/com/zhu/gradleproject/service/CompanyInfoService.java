package com.zhu.gradleproject.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.gradleproject.entity.CompanyInfo;

import java.util.List;

/**
 * <p>
 * 企业信息查询中间表 服务类
 * </p>
 *
 * @author zwy
 * @since 2020-12-02
 */
public interface CompanyInfoService extends IService<CompanyInfo> {

    /**
     * @param ids 查询id
     *
     * @author zwy
     * @date 12/2/2020 4:10 PM
     */
    List<JSONObject> queryFromEs(List<String> ids) throws Exception;

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
}
