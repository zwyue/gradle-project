package com.zhu.gradleproject.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.zhu.gradleproject.dto.QueryDto;
import com.zhu.gradleproject.service.CompanyService;
import com.zhu.gradleproject.util.PageList;
import io.vavr.control.Try;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        PageList<JSONObject> list =
        Try
                .of(()->companyInfoQueryService.queryFromEs(queryDto))
                .onFailure(Throwable::printStackTrace).getOrElse(new PageList<>()) ;

        return new Gson().toJson(list);
    }

    /**
     * 企业名称关键词联想
     *
     * @author zwy
     * @date 1/28/2021 5:03 PM
     */
    @GetMapping("/associate")
    public String associateWords(String inputStr , Integer size){

        List<Map<String, Object>> list =
        Try
                .of(()->companyInfoQueryService.associateWordSearch(inputStr , size))
                .onFailure(Throwable::printStackTrace).getOrElse(new ArrayList<>()) ;

        return new Gson().toJson(list);
    }

    /**
     * 查询父文档（包含子文档 - inner_hits 同理嵌套文档）
     *
     * @author zwy
     * @date 1/28/2021 5:09 PM
     */
    @GetMapping("/parent/with/child")
    public String parentWithChild(@RequestBody QueryDto queryDto){

        List<Map<String, Object>> list =
        Try
                .of(()->companyInfoQueryService.parentWithChild(queryDto))
                .onFailure(Throwable::printStackTrace).getOrElse(new ArrayList<>()) ;

        return new Gson().toJson(list);
    }
}

