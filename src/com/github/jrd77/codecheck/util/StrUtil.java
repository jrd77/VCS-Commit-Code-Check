package com.github.jrd77.codecheck.util;

import java.util.Locale;

/**
 * @author zhen.wang
 * @description str handle tool
 * @date 2021/7/29 10:12
 */
public class StrUtil {
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

    public static String lowerFirst(String str){
        if (isBlank(str)){
            return str;
        }
        String substring = str.substring(0, 1);
        String prefix = substring.toLowerCase(Locale.ROOT);
        return prefix + str.substring(1);
    }
}
