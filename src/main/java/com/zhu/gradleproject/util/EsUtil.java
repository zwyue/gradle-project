package com.zhu.gradleproject.util;

import java.util.HashMap;
import java.util.Map;

import static com.zhu.gradleproject.util.BeanTools.culInitialCapacity;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/2/2020
 */
public class EsUtil {

    public static Map<String,Integer> getSettings(){
        Map<String, Integer> settings = new HashMap<>(culInitialCapacity(2));
        //分片数量
        settings.put("shards", 1);
        //复制数量
        settings.put("replicas", 1);
//        settings.put("nestedLimit", 100000);//嵌套文档限制大小（默认10000）
        return settings ;
    }
}
