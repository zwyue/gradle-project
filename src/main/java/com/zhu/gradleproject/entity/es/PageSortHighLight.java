package com.zhu.gradleproject.entity.es;

import lombok.Data;

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
