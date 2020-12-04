package com.zhu.gradleproject.util;

import com.zhu.gradleproject.annotation.AttributeValue;
import com.zhu.gradleproject.annotation.ESIndexData;
import com.zhu.gradleproject.annotation.EsId;
import com.zhu.gradleproject.annotation.EsRoutingId;

import java.lang.reflect.Field;

/**
 * <pre>
 *     ES 工具类
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
public class AnnotationUtil {
    /**
     * 获取ES index 名称
     * @param clazz 类
     * @return String
     */
    public static String getIndexName(Class<?> clazz) {

        if (clazz.getAnnotation(ESIndexData.class) == null) {
            return null;
        }
        return clazz.getAnnotation(ESIndexData.class).indexName();
    }

    /**
     * 获取ES路径
     * @param obj 对象
     * @author zwy
     * @date   5/19/2020 2:52 PM
     * @return String
     * @throws Exception e
     */
    public static String getEsId(Object obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            EsId esid = f.getAnnotation(EsId.class);
            if (esid != null) {
                return f.get(obj) == null ? null :f.get(obj).toString();
            }
        }
        return null;
    }

    /**
     * 获取ES子路径
     * @param obj 对象
     * @return String
     * @throws Exception e
     */
    public static String getRoutingId(Object obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            EsRoutingId routingId = f.getAnnotation(EsRoutingId.class);
            if (routingId != null) {
                return f.get(obj) == null ? null :f.get(obj).toString();
            }
        }
        return null;
    }


    /**
     * 设置注解中的字段值
     */
    public static void setAnnotationValue(Object obj,Object value){

        Field[] declaredFields = obj.getClass().getDeclaredFields();

        boolean notExistAnnotation = true ;
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(AttributeValue.class)){
                try {
                    declaredField.setAccessible(true);
                    declaredField.set(obj,value);
                    notExistAnnotation = false ;
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if(notExistAnnotation){
            declaredFields = obj.getClass().getSuperclass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(AttributeValue.class)){
                    try {
                        declaredField.setAccessible(true);
                        declaredField.set(obj,value); break;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
