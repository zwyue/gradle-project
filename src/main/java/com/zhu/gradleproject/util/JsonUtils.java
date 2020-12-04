package com.zhu.gradleproject.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JsonUtils() {
    }

    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        } else {
            try {
                return obj instanceof String ? (String)obj : OBJECT_MAPPER.writeValueAsString(obj);
            } catch (Exception var2) {
                var2.printStackTrace();
                return null;
            }
        }
    }

    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StringUtils.isNotBlank(str) && clazz != null) {
            try {
                return clazz.equals(String.class) ? clazz.cast(str) : OBJECT_MAPPER.readValue(str, clazz);
            } catch (IOException var3) {
                var3.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
