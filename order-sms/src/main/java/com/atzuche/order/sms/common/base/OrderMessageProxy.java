package com.atzuche.order.sms.common.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.ExceptionUtil;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @param <T>
 * @author 胡春林
 * OrderMessage代理类
 */
@Slf4j
public class OrderMessageProxy<T> implements InvocationHandler, Serializable {

    private Class<T> orderRouteKeyMessage;

    public OrderMessageProxy(Class<T> iOrderRouteKeyMessage) {
        this.orderRouteKeyMessage = iOrderRouteKeyMessage;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }
            if (this.isDefaultMethod(method)) {
                return this.invokeDefaultMethod(proxy, method, args);
            }
        } catch (Throwable var5) {
            throw ExceptionUtil.unwrapThrowable(var5);
        }
        //获取method注解
        Annotation[] annotations = method.getDeclaredAnnotations();
        if (Objects.isNull(annotations) || annotations.length == 0) {
            throw new RuntimeException("该service方法没有标明注解无法获取短信标识串，请确认");
        }


        return null;
    }

    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }

        Class<?> declaringClass = method.getDeclaringClass();
        return (constructor.newInstance(declaringClass, 15)).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    private boolean isDefaultMethod(Method method) {
        return (method.getModifiers() & 1033) == 1 && method.getDeclaringClass().isInterface();
    }

    public Class<T> getOrderRouteKeyMessage() {
        return orderRouteKeyMessage;
    }
}
