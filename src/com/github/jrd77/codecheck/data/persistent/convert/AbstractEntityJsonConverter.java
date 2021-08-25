package com.github.jrd77.codecheck.data.persistent.convert;


import com.github.jrd77.codecheck.util.JsonUtil;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
        return JsonUtil.fromJson(s, getGenericClass());
    }

    @Override
    public @Nullable
    String toString(@NotNull T t) {

        return JsonUtil.toJson(t);
    }

    abstract Class<T> getGenericClass();
}
