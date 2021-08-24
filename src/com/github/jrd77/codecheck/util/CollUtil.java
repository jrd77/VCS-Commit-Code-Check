package com.github.jrd77.codecheck.util;

import java.util.Collection;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/24 11:07
 */
public class CollUtil {


    public static <E> boolean isEmpty(Collection<E> collection) {

        if (collection == null) {
            return true;
        }
        if (collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static <E> boolean isNotEmpty(Collection<E> collection) {

        return !isEmpty(collection);
    }
}
