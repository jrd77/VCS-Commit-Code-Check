package com.github.jrd77.codecheck.util;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author zhen.wang
 * @description json handler
 * @date 2021/8/19 14:54
 */
public class JsonUtil {

    private static Logger logger = Logger.getLogger(JsonUtil.class.getName());

    public static String toJson(Object obj){

        return new Gson().toJson(obj);
    }

    public static <T> T fromJson(String jsonStr,Class<T> tClass){

        return new Gson().fromJson(jsonStr,tClass);
    }




    private static List<Class> BASE_CLASSES =Arrays.asList(String.class,
                                        Short.class,Byte.class,Integer.class,Long.class,
                                        Boolean.class,
                                        Character.class,
                                        Float.class,Double.class,
                                        short.class,byte.class,int.class,long.class,boolean.class,char.class,float.class,double.class);






}
