package com.github.jrd77.codecheck.util;

/**
 * @author zhen.wang
 * @description 布尔值判断工具
 * @date 2021/8/4 14:57
 */
public class BooleanUtil {

    public static boolean isNotTrue(Boolean flag){
        if(flag==null||flag.equals(Boolean.FALSE)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
