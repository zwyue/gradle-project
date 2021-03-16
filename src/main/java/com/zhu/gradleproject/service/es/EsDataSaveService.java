package com.zhu.gradleproject.service.es;

import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.List;

/**
 * <pre>
 *     es 存储 service
 * </pre>
 *
 * @author zwy
 * @date 12/2/2020
 */
public interface EsDataSaveService {

    /**
     * 存储文档
     *
     * @param list 数据
     *
     * @param mapping 索引mapping
     *
     * @return BulkResponse
     *
     * @author zwy
     * @date 12/2/2020 3:03 PM
     */
    <T> BulkResponse save(List<T> list, XContentBuilder mapping ) throws Exception;

    /**
     * 存储子文档
     *
     * @param list 数据
     *
     * @param mapping 索引mapping
     *
     * @return BulkResponse
     *
     * @author zwy
     * @date 12/3/2020 6:06 PM
     */
    <T> BulkResponse saveSubDoc(List<T> list, XContentBuilder mapping ) throws Exception;

    /**
     * 删除索引
     * @param index 索引名
     *
     * @author zwy
     * @date 12/3/2020 6:06 PM
     */
    void deleteIndex(String... index) throws IOException;
}
