package com.atzuche.order.delivery.vo.delivery.convert;


import java.util.function.Function;

/**
 * @author 胡春林
 * 转换抽象类
 */
public abstract class Converter<T,R> implements Function<T,R> {
    /**
     * 转换为entity
     * @param t
     * @return
     */
    protected abstract R doForWard(T t);

    /**
     * 转换为DTO
     * @param r
     * @return
     */
    protected abstract T doBackWard(R r);


}
