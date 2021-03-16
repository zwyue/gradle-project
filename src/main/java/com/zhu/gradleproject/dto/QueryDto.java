package com.zhu.gradleproject.dto;

import lombok.Data;

import java.util.Map;

/**
 * copyright     <a href="http://ditop.tech/>德拓科技</a>
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/7/2020
 */
@Data
public class QueryDto {

    private String name ;

    private String perName ;

    private String district ;

    private Float licenseCapital ;

    private Map<String,Object> awardLimit ;

    private Map<String,Object> perLimit ;

    private Map<String,Object> corpPrjLimit ;

    private Map<String,Object> perPrjLimit ;

    private Integer pageSize = 5 ;

    private Integer pageNum = 1 ;
}
