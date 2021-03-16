package com.zhu.gradleproject.constant;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/1/2020
 */
public class Constant {
    public Constant() {
    }

    public static int DEFAULT_PAGE_SIZE = 200;

    /**
     * 用于匹配汉字
     *
     * @author zwy
     * @date 2019/12/9 17:53
     */
    public final static String LETTER_REGEX = "^[\u4e00-\u9fa5]+$" ;


    public final static String ES_INDEX = "company_info" ;
}
