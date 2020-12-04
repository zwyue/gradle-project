package com.zhu.gradleproject.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
@Data
@NoArgsConstructor
public class PageList<T> {

    List<T> list;
    private int totalPages = 0;
    private long totalElements = 0L;
    private Object[] sortValues;
    private int currentPage;
    private int pageSize;
}
