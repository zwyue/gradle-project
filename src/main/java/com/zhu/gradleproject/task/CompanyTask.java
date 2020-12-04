package com.zhu.gradleproject.task;

import com.zhu.gradleproject.service.CompanyInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <pre>
 *     es 存入企业信息
 * </pre>
 *
 * @author zwy
 * @date 12/2/2020
 */
@RestController
@RequestMapping("task/company")
public class CompanyTask {

    @Resource
    private CompanyInfoService companyInfoService ;

    @PostMapping("/base/save")
    public String saveCompanyBaseInfo(){
        try {
            companyInfoService.saveCompanyBaseInfoToEs();
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
        return "success";
    }

    @PostMapping("/person/save")
    public String saveCompanyPersonInfo(){
        try {
            companyInfoService.saveCompanyPersonInfoToEs();
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
        return "success";
    }

    @PostMapping("/project/save")
    public String saveCompanyProjectInfo(){
        try {
            companyInfoService.saveCompanyProjectInfoToEs();
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
        return "success";
    }

    @PostMapping("/person/project/save")
    public String saveCompanyPersonProjectInfo(){
        try {
            companyInfoService.saveCompanyPersonProjectInfoToEs();
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
        return "success";
    }

    @PostMapping("/delete/index")
    public String deleteIndex(String[] indexName) {
        try {
            companyInfoService.deleteIndex(indexName);
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
        return "success";
    }
}
