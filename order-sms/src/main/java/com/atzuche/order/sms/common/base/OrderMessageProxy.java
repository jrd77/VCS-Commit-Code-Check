package com.atzuche.order.sms.common.base;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.sms.common.annatation.Push;
import com.atzuche.order.sms.common.annatation.SMS;
import com.atzuche.order.sms.utils.SmsParamsMapUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.ExceptionUtil;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
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
        method = orderRouteKeyMessage.getDeclaredMethod(method.getName(), method.getParameterTypes());
        Annotation[] annotations = method.getDeclaredAnnotations();
        if (Objects.isNull(annotations) || annotations.length == 0) {
            throw new RuntimeException("该service方法没有标明注解无法获取短信标识串，请确认");
        }
        if (Objects.isNull(args) || args.length == 0) {
            throw new RuntimeException("该service方法没有参数无法发送短信，请确认");
        }
        OrderMessage orderMessage = OrderMessage.builder().build();
        if (args[0] instanceof OrderMessage) {
            orderMessage = (OrderMessage) args[0];
        }
        if (Objects.isNull(orderMessage) || Objects.isNull(orderMessage.getMessage())) {
            throw new RuntimeException("没有找到必须的orderMessage数据,orderMessage:" + orderMessage);
        }
        JSONObject jsonObject = (JSONObject)orderMessage.getMessage();
        if(Objects.isNull(jsonObject) || !jsonObject.containsKey("orderNo"))
        {
            throw new RuntimeException("没有找到必须的orderNo数据,orderMessage:" + orderMessage);
        }
        Push push = method.getDeclaredAnnotation(Push.class);
        if (Objects.nonNull(push)) {
            orderMessage = getOrderMessageFromAnnotation(orderMessage, push, jsonObject.getString("orderNo"));
        }
        SMS sms = method.getDeclaredAnnotation(SMS.class);
        if (Objects.nonNull(sms)) {
            orderMessage = getOrderMessageFromAnnotation(orderMessage, sms, jsonObject.getString("orderNo"));
        }
        return orderMessage;
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

    public OrderMessage getOrderMessageFromAnnotation(OrderMessage orderMessage,Annotation annotation,String orderNo){
        if (annotation instanceof Push) {
            Push push = (Push) annotation;
            Map pushMap = SmsParamsMapUtil.getParamsMap(orderNo, push.renterFlag(), push.ownerFlag(), hasPushElseOtherParams(orderNo));
            orderMessage.setPushMap(pushMap);
        }
        if (annotation instanceof SMS) {
            SMS sms = (SMS) annotation;
            Map smsMap = SmsParamsMapUtil.getParamsMap(orderNo, sms.renterFlag(), sms.ownerFlag(), hasSMSElseOtherParams(orderNo));
            orderMessage.setMap(smsMap);
        }
        return orderMessage;
    }

    public Map hasSMSElseOtherParams(String orderNo) {
        try {
            Method method = orderRouteKeyMessage.getDeclaredMethod("hasSMSElseOtherParams", Map.class);
            Map smsMap = Maps.newHashMap();
            smsMap.put("orderNo",orderNo);
            Map paramsMap = (Map) method.invoke(orderRouteKeyMessage,smsMap);
            return paramsMap;
        } catch (Exception e) {
            log.info("没有找到对应的SMS.method方法，没有特殊参数,orderRouteKeyMessageName:[{}]", orderRouteKeyMessage.getName());
        }
        return null;
    }

    public Map hasPushElseOtherParams(String orderNo) {
        try {
            Method method = orderRouteKeyMessage.getDeclaredMethod("hasPushElseOtherParams", Map.class);
            Map pushMap = Maps.newHashMap();
            pushMap.put("orderNo", orderNo);
            Map paramsMap = (Map) method.invoke(orderRouteKeyMessage, pushMap);
            return paramsMap;
        } catch (Exception e) {
            log.info("没有找到对应的Push.method方法，没有特殊参数,orderRouteKeyMessageName:[{}]", orderRouteKeyMessage.getName());
        }
        return null;
    }

}
