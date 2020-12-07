package com.zhu.gradleproject.util;

import com.zhu.gradleproject.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
     * 获取 标注 IncludeField 的字段名称集合
     * @param obj 对象
     * @return String
     * @throws Exception e
     */
    public static String[] getIncludeFields(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();

        List<String> fieldNames = new ArrayList<>();
        for (Field f : fields) {
            f.setAccessible(true);
            IncludeField includeField = f.getAnnotation(IncludeField.class);
            if (includeField != null) {
                fieldNames.add(f.getName()) ;
            }
        }
        return fieldNames.size()>0 ? fieldNames.toArray(new String[0]) : null;
    }

    /**
     * 获取 标注 IncludeField 的字段名称集合
     * @param obj 对象
     * @return String
     * @throws Exception e
     */
    public static String[] getExcludeFields(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();

        List<String> fieldNames = new ArrayList<>();
        for (Field f : fields) {
            f.setAccessible(true);
            ExcludeField includeField = f.getAnnotation(ExcludeField.class);
            if (includeField != null) {
                fieldNames.add(f.getName()) ;
            }
        }
        return fieldNames.size()>0 ? fieldNames.toArray(new String[0]) : null;
    }

    /**
     * 获取查询字段名称
     *
     * @author zwy
     * @date 12/7/2020 2:20 PM
     */
    public static String[] getSearchFields(Object obj) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            SearchField searchField = field.getAnnotation(SearchField.class);

            if (searchField != null) {
                fieldNames.add(field.getName());
            }
        }
        return fieldNames.toArray(new String[0]);
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
