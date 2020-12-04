package com.zhu.gradleproject.entity.es;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
@Data
public class HighLight {

    private String preTag = "";
    private String postTag = "";
    private List<String> highLightList ;

    public HighLight() {
        this.highLightList = new ArrayList<>();
    }

    public HighLight field(String fieldValue) {
        this.highLightList.add(fieldValue);
        return this;
    }
}
