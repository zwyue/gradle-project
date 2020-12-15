package com.zhu.gradleproject.service.es.impl;

import com.zhu.gradleproject.entity.es.Attach;
import com.zhu.gradleproject.entity.es.HighLight;
import com.zhu.gradleproject.entity.es.PageSortHighLight;
import com.zhu.gradleproject.entity.es.Sort;
import com.zhu.gradleproject.service.es.EsQueryService;
import com.zhu.gradleproject.util.BeanTools;
import com.zhu.gradleproject.util.JsonUtils;
import com.zhu.gradleproject.util.PageList;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.zhu.gradleproject.util.AnnotationUtil.*;
import static com.zhu.gradleproject.util.BeanTools.*;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
@Service
@Log4j2
public class EsSearchServiceImpl implements EsQueryService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public List<Map<String, Object>> search(Integer size, QueryBuilder query, String index, String... includeFields) {
        try {
            SearchResponse response = this.restHighLevelClient.search(
                    new SearchRequest().indices(index).source(
                            new SearchSourceBuilder().query(query).size(size)
                                    .fetchSource(includeFields, null)
                    )
                    , RequestOptions.DEFAULT);

            //解析返回
            if (response.status() != RestStatus.OK || response.getHits().getTotalHits().value <= 0) {
                return Collections.emptyList();
            }

            //获取source
            return Arrays.stream(response.getHits().getHits())
                    .map(SearchHit::getSourceAsMap).collect(Collectors.toList());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public <T>List<Map<String, Object>> associate(Integer size, T obj ,String inputStr) {
        try {

            //请求
            SearchResponse rp = this.restHighLevelClient.search(
                    new SearchRequest().indices(getIndexName(obj.getClass())).source(
                            new SearchSourceBuilder()
                                    .query(multiMatchQuery(inputStr,getSearchFields(obj)))
                                    .size(size).fetchSource(getIncludeFields(obj), getExcludeFields(obj))
                    ) , RequestOptions.DEFAULT);

            //解析返回
            if (rp.status() != RestStatus.OK || rp.getHits().getTotalHits().value <= 0) {
                return Collections.emptyList();
            }

            //获取source
            return Arrays.stream(rp.getHits().getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public <T> PageList<T> queryPageList( Class<T> clazz , PageSortHighLight pageSortHighLight){
        if (pageSortHighLight == null) {
            throw new NullPointerException("PageSortHighLight不能为空!");
        } else {
            Attach attach = new Attach();
            attach.setPageSortHighLight(pageSortHighLight);

            try {
                return this.search(pageSortHighLight.getEsQueryBuilder(), attach, clazz, pageSortHighLight.getIndex());
            } catch (Exception e){
                log.info("...... wrong ......");
            }

            return null ;
        }
    }

    public <T> PageList<T> search(QueryBuilder queryBuilder, Attach attach, Class<T> clazz, String... indexes) throws Exception {
        if (attach == null) {
            throw new NullPointerException("Attach不能为空!");
        } else {
            PageList<T> pageList = new PageList<>();
            List<T> list = new ArrayList<>();
            PageSortHighLight pageSortHighLight = attach.getPageSortHighLight();
            SearchRequest searchRequest = new SearchRequest(indexes);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(queryBuilder);

            Map<String,Boolean> flagMap = new HashMap<>(culInitialCapacity(2));
            flagMap.put("highLightFlag",false);
            flagMap.put("idSortFlag",false);

            ifPageSortHighLight(pageSortHighLight,searchSourceBuilder, attach,flagMap) ;

            if (attach.isSearchAfter()) {
                isSearchAfter(pageSortHighLight, searchSourceBuilder, attach,flagMap) ;
            }

            if (attach.getExcludes() != null || attach.getIncludes() != null) {
                searchSourceBuilder.fetchSource(attach.getIncludes(), attach.getExcludes());
            }

            searchRequest.source(searchSourceBuilder);
            if (StringUtils.isNotBlank(attach.getRouting())) {
                searchRequest.routing(attach.getRouting());
            }

            SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();

            for (SearchHit hit : searchHits) {
                T t = JsonUtils.string2Obj(hit.getSourceAsString(), clazz);
                if (flagMap.get("highLightFlag") && t!= null) {
                    Map<String, HighlightField> highlightMap = hit.getHighlightFields();
                    highlightMap.forEach((k, v) -> {
                        Object obj = mapToObject(highlightMap, clazz);
                        BeanUtils.copyProperties(obj, t, BeanTools.getNoValuePropertyNames(obj));
                    });
                }

                list.add(t);
                pageList.setSortValues(hit.getSortValues());
            }

            pageList.setList(list);
            pageList.setTotalElements(hits.getTotalHits().value);
            if (pageSortHighLight != null && pageSortHighLight.getPageSize() != 0) {
                pageList.setTotalPages(getTotalPages(hits.getTotalHits().value, pageSortHighLight.getPageSize()));
            }

            return pageList;
        }
    }

    private void ifPageSortHighLight(PageSortHighLight pageSortHighLight,
                                     SearchSourceBuilder searchSourceBuilder,
                                     Attach attach , Map<String,Boolean> flagMap) {
        if (pageSortHighLight != null) {
            if (pageSortHighLight.getPageSize() != 0) {
                if (!attach.isSearchAfter()) {
                    searchSourceBuilder.from((pageSortHighLight.getCurrentPage() - 1) * pageSortHighLight.getPageSize());
                }
                searchSourceBuilder.size(pageSortHighLight.getPageSize());
            }

            int i;
            if (pageSortHighLight.getSort() != null) {
                Sort sort = pageSortHighLight.getSort();
                List<Sort.Order> orders = sort.listOrders();

                for (i = 0; i < orders.size(); ++i) {
                    flagMap.put("idSortFlag","_id".equals((orders.get(i)).getProperty()));
                    searchSourceBuilder.sort((new FieldSortBuilder((orders.get(i)).getProperty())).order((orders.get(i)).getDirection()));
                }
            }

            HighLight highLight = pageSortHighLight.getHighLight();
            if (highLight != null && highLight.getHighLightList() != null && highLight.getHighLightList().size() != 0) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                if (StringUtils.isNotBlank(highLight.getPreTag()) && StringUtils.isNotBlank(highLight.getPostTag())) {
                    highlightBuilder.preTags(highLight.getPreTag());
                    highlightBuilder.postTags(highLight.getPostTag());
                }

                for (i = 0; i < highLight.getHighLightList().size(); ++i) {
                    flagMap.put("highLightFlag",true) ;
                    highlightBuilder.field(highLight.getHighLightList().get(i), 0);
                }

                searchSourceBuilder.highlighter(highlightBuilder);
            }
        }
    }

    private void isSearchAfter(PageSortHighLight pageSortHighLight,
                               SearchSourceBuilder searchSourceBuilder,
                               Attach attach , Map<String,Boolean> flagMap) {
        if (pageSortHighLight != null && pageSortHighLight.getPageSize() != 0) {
            searchSourceBuilder.size(pageSortHighLight.getPageSize());
        } else {
            searchSourceBuilder.size(10);
        }

        if (attach.getSortValues() != null && attach.getSortValues().length != 0) {
            searchSourceBuilder.searchAfter(attach.getSortValues());
        }

        if (flagMap.get("idSortFlag")) {
            Sort.Order order = new Sort.Order(SortOrder.ASC, "_id");
            assert pageSortHighLight != null;
            pageSortHighLight.getSort().and(new Sort(order));
            searchSourceBuilder.sort((new FieldSortBuilder("_id")).order(SortOrder.ASC));
        }
    }
}