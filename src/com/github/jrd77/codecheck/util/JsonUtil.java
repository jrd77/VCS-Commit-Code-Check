package com.github.jrd77.codecheck.util;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author zhen.wang
 * @description json handler
 * @date 2021/8/19 14:54
 */
public class JsonUtil {

    private static Logger logger = Logger.getLogger(JsonUtil.class.getName());

    public static String toJson(Object obj) {

        return new Gson().toJson(obj);
    }

    public static <T> List<String> toJsonList(List<T> obj) {

        Gson gson = new Gson();
        return obj.stream().map(gson::toJson).collect(Collectors.toList());
    }

    public static <T> T fromJson(String jsonStr, Class<T> tClass) {

        return new Gson().fromJson(jsonStr, tClass);
    }

    public static <T> List<T> fromJson(List<String> jsonStrs, Class<T> tClass) {
        Gson gson = new Gson();
        return jsonStrs.stream().map(x -> gson.fromJson(x, tClass)).collect(Collectors.toList());
    }


    private static List<Class> BASE_CLASSES = Arrays.asList(String.class,
            Short.class, Byte.class, Integer.class, Long.class,
            Boolean.class,
            Character.class,
            Float.class, Double.class,
            short.class, byte.class, int.class, long.class, boolean.class, char.class, float.class, double.class);






}
