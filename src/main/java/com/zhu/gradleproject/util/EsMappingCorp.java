package com.zhu.gradleproject.util;

import org.elasticsearch.common.xcontent.XContentBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/2/2020
 */
public class EsMappingCorp {

    private static XContentBuilder company(){
        XContentBuilder mapping = null ;
        try {
            mapping = jsonBuilder()
                    .startObject()
                        .startObject("properties")
                            .startObject("id")               .field("type","keyword").endObject()
                            .startObject("creditCode")       .field("type","keyword").endObject()
                            .startObject("createTime")       .field("type","date")   .endObject()
                            .startObject("lastUpdate")       .field("type","date")   .endObject()
                            .startObject("routingId")        .field("type","keyword").endObject()
                            .startObject("districtName")     .field("type","keyword").endObject()
                            .startObject("name")
                                .field("search_analyzer","ik_smart")
                                .field("analyzer"       ,"ik_max_word")
                                .field("type"           ,"text")
                                    .startObject("fields")
                                        .startObject("keyword").field("type","keyword").endObject()
                                    .endObject()
                            .endObject()
                            .startObject("pinyin")
                                .field("analyzer"       ,"autocomplete")
                                .field("search_analyzer","standard")
                                .field("type"           ,"text").endObject()
                            .startObject("abbrPinyin")
                                .field("analyzer"       ,"autocomplete")
                                .field("search_analyzer","standard")
                                .field("type"           ,"text").endObject()
                            .startObject("district")
                                .field("search_analyzer","standard")
                                .field("analyzer"       ,"autocomplete")
                                .field("type"           ,"text").endObject()
                            .startObject("companyAwards").field("type","nested")
                                .startObject("properties")
                                    .startObject("level")    .field("type","keyword").endObject()
                                    .startObject("name")     .field("type","keyword").endObject()
                                    .startObject("awardTime").field("type","date")   .endObject()
                                .endObject()
                            .endObject()
                            .startObject("performanceTotal")     .field("type","integer").endObject()
                            .startObject("regPersonNum")         .field("type","integer").endObject()
                            .startObject("licenseCapital")       .field("type","float")  .endObject();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mapping ;
    }

    private static XContentBuilder project(){
        XContentBuilder mapping = null ;
        try {
            mapping = company()
                    .startObject("prjName")
                        .field("type","text")
                        .startObject("fields")
                            .startObject("ik")
                                .field("type","text")
                                .field("analyzer","ik_max_word")
                            .endObject()
                        .endObject()
                    .endObject()
                    .startObject("allInvest")               .field("type","float")  .endObject()
                    .startObject("beginDate")               .field("type","date")   .endObject()
                    .startObject("endDate")                 .field("type","date")   .endObject()
                    .startObject("tenderInfoList")          .field("type","nested")
                        .startObject("properties")
                            .startObject("tenderMoney")     .field("type","float").endObject()
                            .startObject("tenderResultDate").field("type","date").endObject()
                            .startObject("area")            .field("type","float").endObject()
                        .endObject()
                    .endObject()
                    .startObject("builderLicences").field("type","nested")
                        .startObject("properties")
                            .startObject("contractMoney")   .field("type","float").endObject()
                            .startObject("releaseDate")     .field("type","date").endObject()
                            .startObject("area")            .field("type","float").endObject()
                        .endObject()
                    .endObject()
            ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return mapping ;
    }

    public static XContentBuilder corpMapping(){
        XContentBuilder mapping = null ;
        try {
            mapping = project()
                    .startObject("personCerts").field("type","keyword").endObject()
                    .startObject("perName")
                        .field("search_analyzer","ik_smart")
                        .field("analyzer"       ,"ik_max_word")
                        .field("type"           ,"text")
                        .startObject("fields")
                            .startObject("keyword").field("type","keyword").endObject()
                        .endObject()
                    .endObject()
                    .startObject("perId")      .field("type","keyword").endObject()
                    .startObject("companyPersonRelation")
                        .field("type","join")
                        .startObject("relations")
                            .field("company",new String[]{"person","corpProject"})
                            .field("person","personProject")
                        .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mapping ;
    }
}
