package com.github.jrd77.codecheck.data.persistent.convert;


import com.github.jrd77.codecheck.util.ReflectUtil;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/25 11:34
 */
public abstract class AbstractEntityJsonConverter<T> extends Converter<T> {

    /*
     * 获取泛型类Class对象，不是泛型类则返回null
     */
    public static Class<?> getActualTypeArgument(Class<?> clazz) {
        Class<?> entitiClass = null;
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass)
                    .getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                entitiClass = (Class<?>) actualTypeArguments[0];
            }
        }

        return entitiClass;
    }

    @Override
    public @Nullable
    T fromString(@NotNull String s) {
        JSONParser jsonParser = new JSONParser();
        Type genericSuperclass = getClass().getGenericSuperclass();
        T t = null;
        try {
            t = getGenericClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Object parse = jsonParser.parse(s);
            t = (T) ReflectUtil.mapToEntity((JSONObject) parse, getGenericClass());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public @Nullable
    String toString(@NotNull T t) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> map = ReflectUtil.entityToMap(t, false);
        for (String s : map.keySet()) {
            jsonObject.put(s, map.get(s));
        }
        return jsonObject.toJSONString();
    }

    abstract Class<T> getGenericClass();
}
