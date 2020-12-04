package com.zhu.gradleproject.service.es.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhu.gradleproject.constant.Constant;
import com.zhu.gradleproject.entity.es.Attach;
import com.zhu.gradleproject.entity.es.HighLight;
import com.zhu.gradleproject.entity.es.PageSortHighLight;
import com.zhu.gradleproject.entity.es.Sort;
import com.zhu.gradleproject.service.es.EsSearchService;
import com.zhu.gradleproject.util.BeanTools;
import com.zhu.gradleproject.util.JsonUtils;
import com.zhu.gradleproject.util.PageList;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.util.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static com.zhu.gradleproject.util.AnnotationUtil.getIndexName;
import static com.zhu.gradleproject.util.BeanTools.culInitialCapacity;
import static org.elasticsearch.search.fetch.subphase.FetchSourceContext.DO_NOT_FETCH_SOURCE;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
@Service
@Log4j2
public class EsSearchServiceImpl implements EsSearchService {


    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        return this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    }

    @Override
    public <T> List<T> search(QueryBuilder queryBuilder, Class<T> clazz) throws Exception {
        String indexName = getIndexName(clazz);
        return this.search(queryBuilder, clazz, indexName);
    }

    @Override
    public <T> List<T> search(QueryBuilder queryBuilder, Class<T> clazz, String... indexes) throws Exception {

        List<T> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexes);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(Constant.DEFAULT_PAGE_SIZE);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {
            T t = JsonUtils.string2Obj(hit.getSourceAsString(), clazz);
            list.add(t);
        }

        return list;
    }

    @Override
    public Map<String, List<Map<String, Object>>> innerHitsSearch(QueryBuilder queryBuilder, Boolean fetchSource
            , String index, String... includes) throws Exception {

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);

        if (fetchSource) {
            searchSourceBuilder.fetchSource(includes, null);
        } else {
            //此处相当于设置 "_source":false ,即只返回 inner_hits , 不返回 source
            searchSourceBuilder.fetchSource(DO_NOT_FETCH_SOURCE);
        }

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits searchHits = searchResponse.getHits();

        Map<String, List<Map<String, Object>>> resultMap = new HashMap<>(culInitialCapacity(searchHits.getHits().length));

        if (fetchSource) {
            resultMap.put("source", Arrays.stream(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsMap).collect(Collectors.toList()));
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            Map<String, SearchHits> map = hit.getInnerHits();
            if (map != null) {
                log.info(map);
                for (String key : map.keySet()) {
                    map.get(key);
                    List<Map<String, Object>> l =
                            Arrays.stream(map.get(key).getHits())
                                    .map(SearchHit::getSourceAsMap)
                                    .collect(Collectors.toList());
                    list.addAll(l);
                }
            }
        }

        resultMap.put("innerHits", list);

        return resultMap;
    }


    @Override
    public <T> List<T> searchMore(QueryBuilder queryBuilder, int limitSize, Class<T> clazz) throws Exception {
        String indexName = getIndexName(clazz);
        return this.searchMore(queryBuilder, limitSize, clazz, indexName);
    }

    @Override
    public <T> List<T> searchMore(QueryBuilder queryBuilder, int limitSize, Class<T> clazz, String... indexes) throws Exception {
        PageSortHighLight pageSortHighLight = new PageSortHighLight(1, limitSize);
        PageList<T> pageList = this.search(queryBuilder, pageSortHighLight, clazz, indexes);
        return pageList != null ? pageList.getList() : null;
    }

    @Override
    public <T> PageList<T> search(QueryBuilder queryBuilder, PageSortHighLight pageSortHighLight, Class<T> clazz, String... indexes) throws Exception {
        if (pageSortHighLight == null) {
            throw new NullPointerException("PageSortHighLight不能为空!");
        } else {
            Attach attach = new Attach();
            attach.setPageSortHighLight(pageSortHighLight);
            return this.search(queryBuilder, attach, clazz, indexes);
        }
    }

    @Override
    public List<Map<String, Object>> search(Integer size, QueryBuilder query, String index, String... includeFields) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            //条件
            sourceBuilder.query(query).size(size);

            //返回和排除列
            if (!CollectionUtils.isEmpty(includeFields)) {
                sourceBuilder.fetchSource(includeFields, null);
            }

            SearchRequest request = new SearchRequest();
            //索引
            request.indices(index);
            //各种组合条件
            request.source(sourceBuilder);

            //请求
            log.info(request.source().toString());
            SearchResponse response = this.restHighLevelClient.search(request, RequestOptions.DEFAULT);

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
    public PageList<JsonObject> searchHasInnerHits(BoolQueryBuilder parentQueryBuilder, PageSortHighLight psh,
                                                   String[] includeField, String index) throws Exception {
        if (psh == null) {
            throw new NullPointerException("PageSortHighLight不能为空!");
        } else {
            Attach attach = new Attach();
            attach.setPageSortHighLight(psh);
            attach.setIncludes(includeField);
            return this.search(parentQueryBuilder,null, attach, index);
        }
    }


    public PageList<JsonObject> search(QueryBuilder queryBuilder, String collapseField ,
                                 Attach attach, String indexes ) throws Exception {
        if (attach == null) {
            throw new NullPointerException("Attach不能为空!");
        } else {
            PageList<JsonObject> pageList = new PageList<>();
            List<JsonObject> list = new ArrayList<>();
            PageSortHighLight pageSortHighLight = attach.getPageSortHighLight();
            SearchRequest searchRequest = new SearchRequest(indexes);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if(collapseField!=null){
                searchSourceBuilder.collapse(new CollapseBuilder(collapseField));
            }
            searchSourceBuilder.query(queryBuilder);
            boolean highLightFlag = false;
            boolean idSortFlag = false;
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

                    for(i = 0; i < orders.size(); ++i) {
                        if ("_id".equals((orders.get(i)).getProperty())) {
                            idSortFlag = true;
                        }

                        searchSourceBuilder.sort((new FieldSortBuilder((orders.get(i)).getProperty())).order((orders.get(i)).getDirection()));
                    }
                }

                HighLight highLight = pageSortHighLight.getHighLight();
                if (highLight != null && highLight.getHighLightList() != null && highLight.getHighLightList().size() != 0) {
                    HighlightBuilder highlightBuilder = new HighlightBuilder();
                    if (StringUtils.isNotEmpty(highLight.getPreTag()) && StringUtils.isNotEmpty(highLight.getPostTag())) {
                        highlightBuilder.preTags(highLight.getPreTag());
                        highlightBuilder.postTags(highLight.getPostTag());
                    }

                    for(i = 0; i < highLight.getHighLightList().size(); ++i) {
                        highLightFlag = true;
                        highlightBuilder.field(highLight.getHighLightList().get(i), 0);
                    }

                    searchSourceBuilder.highlighter(highlightBuilder);
                }
            }

            if (attach.isSearchAfter()) {
                if (pageSortHighLight != null && pageSortHighLight.getPageSize() != 0) {
                    searchSourceBuilder.size(pageSortHighLight.getPageSize());
                } else {
                    searchSourceBuilder.size(10);
                }

                if (attach.getSortValues() != null && attach.getSortValues().length != 0) {
                    searchSourceBuilder.searchAfter(attach.getSortValues());
                }

                if (!idSortFlag) {
                    Sort.Order order = new Sort.Order(SortOrder.ASC, "_id");
                    assert pageSortHighLight != null;
                    pageSortHighLight.getSort().and(new Sort(order));
                    searchSourceBuilder.sort((new FieldSortBuilder("_id")).order(SortOrder.ASC));
                }
            }

            if (attach.getExcludes() != null || attach.getIncludes() != null) {
                searchSourceBuilder.fetchSource(attach.getIncludes(), attach.getExcludes());
            }

            searchRequest.source(searchSourceBuilder);
            if (!StringUtils.isEmpty(attach.getRouting())) {
                searchRequest.routing(attach.getRouting());
            }

            SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();

            for (SearchHit hit : searchHits) {
                JsonObject t = JsonUtils.string2Obj(hit.getSourceAsString(), JsonObject.class);

                if(t == null){
                    continue;
                }
                if (highLightFlag) {
                    Map<String, HighlightField> highlightMap = hit.getHighlightFields();
                    highlightMap.forEach((k, v) -> {
                        try {
                            Object obj = this.mapToObject(highlightMap, Gson.class);
                            BeanUtils.copyProperties(obj, t, BeanTools.getNoValuePropertyNames(obj));
                        } catch (Exception var7) {
                            var7.printStackTrace();
                        }

                    });
                }

                Map<String, SearchHits> innerHits = hit.getInnerHits();

                if(innerHits != null){

                    List<JsonObject> jsonObjectList = new ArrayList<>();
                    innerHits.keySet().forEach(key->{
                        SearchHits innerSearchHits      = innerHits.get(key);
                        SearchHit[] innerSearchHitArray = innerSearchHits.getHits();

                        for (SearchHit innerSearchHit : innerSearchHitArray) {
                            JsonObject jo = JsonUtils.string2Obj(innerSearchHit.getSourceAsString(), JsonObject.class);
                            jsonObjectList.add(jo);
                        }
                        t.addProperty("innerHitsTotal",innerSearchHits.getTotalHits().value);
                    });
                    t.addProperty("innerHits",new Gson().toJson(jsonObjectList));
                }

                list.add(t);
                pageList.setSortValues(hit.getSortValues());
            }

            pageList.setList(list);
            pageList.setTotalElements(hits.getTotalHits().value);
            if (pageSortHighLight != null && pageSortHighLight.getPageSize() != 0) {
                pageList.setTotalPages(this.getTotalPages(hits.getTotalHits().value, pageSortHighLight.getPageSize()));
            }

            return pageList;
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
            boolean highLightFlag = false;
            boolean idSortFlag = false;
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
                        if ("_id".equals((orders.get(i)).getProperty())) {
                            idSortFlag = true;
                        }

                        searchSourceBuilder.sort((new FieldSortBuilder((orders.get(i)).getProperty())).order((orders.get(i)).getDirection()));
                    }
                }

                HighLight highLight = pageSortHighLight.getHighLight();
                if (highLight != null && highLight.getHighLightList() != null && highLight.getHighLightList().size() != 0) {
                    HighlightBuilder highlightBuilder = new HighlightBuilder();
                    if (!StringUtils.isEmpty(highLight.getPreTag()) && !StringUtils.isEmpty(highLight.getPostTag())) {
                        highlightBuilder.preTags(highLight.getPreTag());
                        highlightBuilder.postTags(highLight.getPostTag());
                    }

                    for (i = 0; i < highLight.getHighLightList().size(); ++i) {
                        highLightFlag = true;
                        highlightBuilder.field(highLight.getHighLightList().get(i), 0);
                    }

                    searchSourceBuilder.highlighter(highlightBuilder);
                }
            }

            if (attach.isSearchAfter()) {
                if (pageSortHighLight != null && pageSortHighLight.getPageSize() != 0) {
                    searchSourceBuilder.size(pageSortHighLight.getPageSize());
                } else {
                    searchSourceBuilder.size(10);
                }

                if (attach.getSortValues() != null && attach.getSortValues().length != 0) {
                    searchSourceBuilder.searchAfter(attach.getSortValues());
                }

                if (!idSortFlag) {
                    Sort.Order order = new Sort.Order(SortOrder.ASC, "_id");
                    assert pageSortHighLight != null;
                    pageSortHighLight.getSort().and(new Sort(order));
                    searchSourceBuilder.sort((new FieldSortBuilder("_id")).order(SortOrder.ASC));
                }
            }

            if (attach.getExcludes() != null || attach.getIncludes() != null) {
                searchSourceBuilder.fetchSource(attach.getIncludes(), attach.getExcludes());
            }

            searchRequest.source(searchSourceBuilder);
            if (!StringUtils.isEmpty(attach.getRouting())) {
                searchRequest.routing(attach.getRouting());
            }

            SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();

            for (SearchHit hit : searchHits) {
                T t = JsonUtils.string2Obj(hit.getSourceAsString(), clazz);

                if(t==null){
                    continue;
                }
                if (highLightFlag) {
                    Map<String, HighlightField> highlightMap = hit.getHighlightFields();
                    highlightMap.forEach((k, v) -> {
                        try {
                            Object obj = this.mapToObject(highlightMap, clazz);
                            BeanUtils.copyProperties(obj, t, BeanTools.getNoValuePropertyNames(obj));
                        } catch (Exception var7) {
                            var7.printStackTrace();
                        }

                    });
                }

                list.add(t);
                pageList.setSortValues(hit.getSortValues());
            }

            pageList.setList(list);
            pageList.setTotalElements(hits.getTotalHits().value);
            if (pageSortHighLight != null && pageSortHighLight.getPageSize() != 0) {
                pageList.setTotalPages(this.getTotalPages(hits.getTotalHits().value, pageSortHighLight.getPageSize()));
            }

            return pageList;
        }
    }

    private int getTotalPages(long totalHits, int pageSize) {
        return pageSize == 0 ? 1 : (int) Math.ceil((double) totalHits / (double) pageSize);
    }

    private <M> Object mapToObject(Map<String, M> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        } else {
            Object obj = beanClass.getDeclaredConstructor().newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (map.get(field.getName()) != null && map.get(field.getName())!=null) {
                    int mod = field.getModifiers();
                    if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod)) {
                        field.setAccessible(true);
                        if (map.get(field.getName()) instanceof HighlightField && ((HighlightField) map.get(field.getName())).fragments().length > 0) {
                            field.set(obj, ((HighlightField) map.get(field.getName())).fragments()[0].string());
                        }
                    }
                }
            }

            return obj;
        }
    }
}