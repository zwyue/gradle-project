package com.zhu.gradleproject.service.es;

import com.google.gson.JsonObject;
import com.zhu.gradleproject.entity.es.PageSortHighLight;
import com.zhu.gradleproject.util.PageList;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
public interface EsSearchService {

    SearchResponse search(SearchRequest var1) throws Exception;

    <T> List<T> search (QueryBuilder var1, Class<T> var2) throws Exception;

    <T>List<T> search(QueryBuilder var1, Class<T> var2, String... var3) throws Exception;

    /**
     * 该查询除正常返回的 _source 数据外 ，当查询条件中存在 <i>嵌套查询</i> 或 <i>父子查询</i> ，
     * 且这两种查询中设置了 inner_hits ,则同时返回嵌套文档中符合条件的文档 ，该文档存在于 inner_hits 中。
     * 如若不需返回 _source , 则令 fetchSource 为 false
     *
     * @param   queryBuilder
     *          设置的查询条件
     *
     * @param   fetchSource
     *          是否返回 _source , false 相当于设置 "_source":false
     *
     * @param   index
     *          查询索引
     *
     * @param   includes
     *          当 fetchSource 为 true 时 ，设置的_source返回字段，因为多数时候并不需要返回全部字段
     *
     * @return  map 将 _source 和 inner_hits 组装到 map 里 返回
     *
     * @author  zwy
     * @date    8/18/2020 1:38 PM
     */
    Map<String,List<Map<String,Object>>> innerHitsSearch (QueryBuilder queryBuilder , Boolean fetchSource, String index , String... includes ) throws Exception;

    List<Map<String, Object>> search(Integer size , QueryBuilder var1, String index ,String... include) throws Exception;

    <T> PageList<T> search(QueryBuilder var1, PageSortHighLight var2, Class<T> var3, String... var4) throws Exception;

    <T>List<T> searchMore(QueryBuilder var1, int var2, Class<T> var3) throws Exception;

    <T>List<T> searchMore(QueryBuilder var1, int var2, Class<T> var3, String... var4) throws Exception;



    /**
     * 父文档携带子文档
     *
     * @author zwy
     * @date 9/3/2020 5:15 PM
     */
    PageList<JsonObject> searchHasInnerHits(BoolQueryBuilder parentQueryBuilder, PageSortHighLight psh, String[] strings, String index) throws Exception;
}
