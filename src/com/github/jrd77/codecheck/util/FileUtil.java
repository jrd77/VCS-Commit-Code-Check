package com.github.jrd77.codecheck.util;

public class FileUtil {


    public static String extName(String filePath) {
        if(StrUtil.isBlank(filePath)) return null;
        String[] split = filePath.split("\\.");
        return split[split.length-1];
    }
}
