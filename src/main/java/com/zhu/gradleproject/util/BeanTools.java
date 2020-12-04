package com.zhu.gradleproject.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

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
            if (StringUtils.isEmpty(propertyValue)) {
                noValuePropertySet.add(pd.getName());
            } else {
                if (Iterable.class.isAssignableFrom(propertyValue.getClass())) {
                    Iterable iterable = (Iterable) propertyValue;
                    Iterator iterator = iterable.iterator();
                    if (!iterator.hasNext()) {
                        noValuePropertySet.add(pd.getName());
                    }
                }

                if (Map.class.isAssignableFrom(propertyValue.getClass())) {
                    Map map = (Map) propertyValue;
                    if (map.isEmpty()) {
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

}
