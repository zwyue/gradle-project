package com.zhu.gradleproject.entity.es;

import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
public class Sort {
    private List<Order> orders ;

    public List<Sort.Order> listOrders() {
        return this.orders;
    }

    public Sort(Sort.Order... ods) {
        this.orders = new ArrayList<>();
        int var3 = ods.length;

        this.orders.addAll(Arrays.asList(ods).subList(0, var3));

    }

    public Sort and(Sort sort) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }

        this.orders.addAll(sort.orders);
        return this;
    }

    public static class Order implements Serializable {
        private final SortOrder direction;
        private final String property;

        public Order(SortOrder direction, String property) {
            this.direction = direction;
            this.property = property;
        }

        public SortOrder getDirection() {
            return this.direction;
        }

        public String getProperty() {
            return this.property;
        }
    }
}
