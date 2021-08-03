package com.github.jrd77.codecheck.util;

import java.util.Collection;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/7/29 10:02
 */
public class CollectionUtils {

    public static <E> boolean isEmpty(Collection<E> collection){

        if(collection==null){
            return true;
        }
        if(collection.isEmpty()){
            return true;
        }
        return false;
    }
}
