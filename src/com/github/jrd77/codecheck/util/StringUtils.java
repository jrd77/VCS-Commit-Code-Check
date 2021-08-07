package com.github.jrd77.codecheck.util;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/7/29 10:12
 */
public class StringUtils {
    private static final String EMPTY="";
    public static boolean isBlank(String str){

        if(str==null||EMPTY.equals(str)){
            return true;
        }
        if(EMPTY.equals(str.trim())){
            return true;
        }
        return false;
    }
}
