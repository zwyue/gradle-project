package com.zhu.gradleproject.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.zhu.gradleproject.entity.CompanyInfo;
import com.zhu.gradleproject.service.CompanyInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 企业信息查询中间表 前端控制器
 * </p>
 *
 * @author zwy
 * @since 2020-12-02
 */
@RestController
@RequestMapping("/company")
public class CompanyInfoController {

    @Resource
    private CompanyInfoService companyInfoQueryService ;

    @GetMapping("/list")
    public String returnCompanyList(){
        List<CompanyInfo> list =  companyInfoQueryService.listByIds(Collections.singletonList("aaa"));
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @GetMapping("/es")
    public String returnEsCompanyList(){
        List<JSONObject> list = null;
        try {
            list = companyInfoQueryService.queryFromEs(Collections.singletonList("aaa"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}

