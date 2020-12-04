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
public class Attach {

    private PageSortHighLight pageSortHighLight = null;
    private String[] includes;
    private String[] excludes;
    private String routing;
    private boolean searchAfter = false;
    private boolean trackTotalHits = false;
    private Object[] sortValues;

    public Attach() {
    }
}
