package com.zhu.gradleproject.entity.es;

import lombok.Data;

import java.util.List;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
@Data
public class ScrollResponse<T> {

    private List<T> list;
    private String scrollId;

    public ScrollResponse(List<T> list, String scrollId) {
        this.list = list;
        this.scrollId = scrollId;
    }
}
