package com.zhu.gradleproject.util;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
public class BeanTools {

    public static String[] getNoValuePropertyNames(Object source) {
        Assert.notNull(source, "传递的参数对象不能为空");
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        Set<String> noValuePropertySet = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object propertyValue = beanWrapper.getPropertyValue(pd.getName());
            if (ObjectUtils.isEmpty(propertyValue)) {
                noValuePropertySet.add(pd.getName());
            } else {
                if (Iterable.class.isAssignableFrom(propertyValue.getClass())) {
                    if (!((Iterable) propertyValue).iterator().hasNext()) {
                        noValuePropertySet.add(pd.getName());
                    }
                }

                if (Map.class.isAssignableFrom(propertyValue.getClass())) {
                    if (((Map) propertyValue).isEmpty()) {
                        noValuePropertySet.add(pd.getName());
                    }
                }
            }

        }
        String[] result = new String[noValuePropertySet.size()];
        return noValuePropertySet.toArray(result);
    }

    /**
     * calculate map size via given the number of elements
     *
     * @author 	zwy
     * @date 	8/18/2020 10:09 AM
     *
     * @return  map size
     *
     * @param 	elemNum
     * 			the number of elements
     */
    public static int culInitialCapacity(Integer elemNum){
        return (int) Math.ceil(elemNum/0.75+1);
    }

    public static <M>Object mapToObject(Map<String, M> map, Class<?> beanClass) {
        if (map == null) {
            return null;
        } else {
            try {
                Object obj = beanClass.getDeclaredConstructor().newInstance();
                Field[] fields = obj.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if (!ObjectUtils.isEmpty(map.get(field.getName()))) {
                        int mod = field.getModifiers();
                        if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod)) {
                            field.setAccessible(true);
                            if (map.get(field.getName()) instanceof HighlightField
                                    && ((HighlightField) map.get(field.getName())).fragments().length > 0) {
                                    field.set(obj, ((HighlightField) map.get(field.getName())).fragments()[0].string());
                            }
                        }
                    }
                }
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null ;
    }

    public static int getTotalPages(long totalHits, int pageSize) {
        return pageSize == 0 ? 1 : (int)Math.ceil((double)totalHits / (double)pageSize);
    }
}
