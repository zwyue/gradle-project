package com.zhu.gradleproject.entity.es;

import com.zhu.gradleproject.annotation.AttributeValue;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
@Data
public class PageSortHighLight {

    private int currentPage;
    private int pageSize;
    Sort sort = new Sort();
    private HighLight highLight = new HighLight();

    @AttributeValue
    private QueryBuilder esQueryBuilder ;

    @AttributeValue
    private String index ;

    public PageSortHighLight(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public PageSortHighLight(int currentPage, int pageSize, Sort sort) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.sort = sort;
    }
}
