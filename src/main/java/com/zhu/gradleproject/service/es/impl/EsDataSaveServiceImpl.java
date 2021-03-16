package com.zhu.gradleproject.service.es.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.zhu.gradleproject.service.es.EsDataSaveService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.zhu.gradleproject.util.AnnotationUtil.*;
import static com.zhu.gradleproject.util.EsUtil.getSettings;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/2/2020
 */
@Service
@Log4j2
public class EsDataSaveServiceImpl implements EsDataSaveService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    private boolean isNotExists(String index) throws Exception {
        GetIndexRequest request = new GetIndexRequest(index);
        return !this.restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    private void createIndex(XContentBuilder mapping, String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder()
                .put("index.number_of_shards", getSettings().get("shards"))
                .put("index.number_of_replicas", getSettings().get("replicas"))
                .put("analysis.filter.autocomplete_filter.type", "edge_ngram")
                .put("analysis.filter.autocomplete_filter.min_gram", 1)
                .put("analysis.filter.autocomplete_filter.max_gram", 20)
                .put("analysis.analyzer.autocomplete.type", "custom")
                .put("analysis.analyzer.autocomplete.tokenizer", "standard")
                .putList("analysis.analyzer.autocomplete.filter",
                        "lowercase", "autocomplete_filter")
        );

        request.mapping(mapping);

        CreateIndexResponse createIndexResponse
                = this.restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        boolean acknowledged = createIndexResponse.isAcknowledged();

        log.info(acknowledged);
    }

    @Override
    public <T> BulkResponse save(List<T> list, XContentBuilder mapping) throws Exception {

        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        log.info("......start to save......");

        String indexName = getIndexName(list.get(0).getClass());
        if(isNotExists(indexName)){
            createIndex(mapping,indexName);
        }

        BulkRequest bulkRequest = new BulkRequest();

        for (T tt : list) {
            bulkRequest.add(new IndexRequest(indexName).id(getEsId(tt)).source(JSON.toJSONString(tt), XContentType.JSON));
        }
        BulkResponse bulkResponse = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        BulkItemResponse[] responses = bulkResponse.getItems();

        log.info("... insert success {} ",responses.length);

        Arrays.stream(responses).forEach(response->{
            if(StringUtils.isEmpty(response.getId())){
                log.info("... response id is empty ...");
            }
            if(response.getFailure()!=null){
                log.info("... response failure ...");
            }
        });
        if(bulkResponse.hasFailures()){
            log.info("... save failure {} ..." , bulkResponse.buildFailureMessage());
        }

        return bulkResponse;
    }

    @Override
    public <T> BulkResponse saveSubDoc(List<T> list, XContentBuilder mapping) throws Exception {
        BulkResponse bulkResponse = null ;
        if (CollUtil.isNotEmpty(list)) {
            String indexName = getIndexName(list.get(0).getClass());

            if(isNotExists(indexName)){
                createIndex(mapping,indexName);
            }

            BulkRequest bulkRequest = new BulkRequest();
            for (T tt : list) {

                IndexRequest request = new IndexRequest(indexName).id(getEsId(tt)).routing(getRoutingId(tt));

                //当 routing 被赋值后，清空原有字段值 ，如果本来就需要冗余字段 ，则此处代码可注释
                setAnnotationValue(tt,null);

                bulkRequest.add(request.source(JSON.toJSONString(tt), XContentType.JSON));
            }

            bulkResponse = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

            BulkItemResponse[] responses = bulkResponse.getItems();
            log.info("... insert success {} ",responses.length);

            if(bulkResponse.hasFailures()){
                log.info("... save failure {} ..." , bulkResponse.buildFailureMessage());
            }
        }
        return bulkResponse;
    }

    @Override
    public void deleteIndex(String... index) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        deleteIndexRequest.indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        AcknowledgedResponse response = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        log.info("...... delete index {} ......",response.isAcknowledged());
    }
}
