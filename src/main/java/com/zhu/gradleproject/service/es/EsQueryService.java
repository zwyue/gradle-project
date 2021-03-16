package com.zhu.gradleproject.service.es;

import com.alibaba.fastjson.JSONObject;
import com.zhu.gradleproject.entity.es.PageSortHighLight;
import com.zhu.gradleproject.util.PageList;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
public interface EsQueryService {

    /**
     * 类似于 {@link #associate(Integer, Object, String)}
     *
     * @param size 查询大小
     *
     * @param query 查询条件
     *
     * @param index 查询索引
     *
     * @param include 返回列
     *
     * @return List<Map<String, Object>>
     *
     * @author zwy
     * @date 12/8/2020 1:30 PM
     */
    List<Map<String, Object>> search(Integer size , QueryBuilder query, String index ,String... include) ;

    /**
     * 关联查询
     *
     * @param size 查询大小
     *
     * @param obj 包含查询字段的实体 （工具人罢了）
     *
     * @param inputStr 查询输入
     *
     * @return List<Map<String, Object>>
     *
     * @author zwy
     * @date 12/7/2020 9:36 AM
     */
    <T>List<Map<String, Object>> associate(Integer size ,T obj , String inputStr) throws IOException;



    /**
     * es分页查询
     *
     * @author zwy
     * @date 5/22/2020 4:21 PM
     *
     * @param clazz 查询结果接收实体类
     *
     * @param pageSortHighLight 分页查询信息（分页、排序、索引、条件）
     *
     * @return PageList 返回列表
     */
    <T> PageList<T> queryPageList(Class<T> clazz  , PageSortHighLight pageSortHighLight) ;

    /**
     * 父文档携带子文档
     *
     * @author zwy
     * @date 9/3/2020 5:15 PM
     */
    PageList<JSONObject> searchHasInnerHits(BoolQueryBuilder parentQueryBuilder, PageSortHighLight psh, String[] strings, String index) throws Exception;
}
