package com.zhu.gradleproject.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.zhu.gradleproject.dto.QueryDto;
import com.zhu.gradleproject.service.CompanyService;
import com.zhu.gradleproject.util.PageList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    private CompanyService companyInfoQueryService ;

    @GetMapping("/list")
    public String returnEsCompanyList(@RequestBody QueryDto queryDto){
        PageList<JSONObject> list = null;
        try {
            list = companyInfoQueryService.queryFromEs(queryDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().toJson(list);
    }

    @GetMapping("/associate")
    public String associateWords(String inputStr , Integer size){
        List<Map<String, Object>> list = null;
        try {
            list = companyInfoQueryService.associateWordSearch(inputStr , size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().toJson(list);
    }
}

