package com.github.jrd77.codecheck.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author zhen.wang
 * @description 反射工具
 * @date 2021/8/19 17:25
 */
@Deprecated
public class ReflectUtil {
    private static Logger logger = Logger.getLogger(ReflectUtil.class.getName());

    private static List<Class> BASE_CLASSES = Arrays.asList(String.class,
            Short.class, Byte.class, Integer.class, Long.class,
            Boolean.class,
            Character.class,
            Float.class, Double.class,
            short.class, byte.class, int.class, long.class, boolean.class, char.class, float.class, double.class);


    private static <T> Map<String, ? extends Class<?>> getFields(Class<T> tClass) {
        Field[] declaredFields = tClass.getDeclaredFields();
        Map<String, ? extends Class<?>> fieldMap = Arrays.stream(declaredFields).collect(Collectors.toMap(Field::getName, Field::getType));
        return fieldMap;
    }

    private static <T> Map<String, Method> getFieldSetterMethods(Class<T> tClass) {
        Method[] declaredFields = tClass.getDeclaredMethods();
        Map<String, Method> fieldMap = Arrays.stream(declaredFields)
                .filter(x -> x.getName().startsWith("set") && x.getParameterCount() == 1)
                .collect(Collectors.toMap(x -> StrUtil.lowerFirst(x.getName().replace("set", "")), x -> x));
        return fieldMap;
    }

    public static <T> T mapToEntity(Map<String, Object> map, Class<T> tClass) {

        if (map == null || map.size() == 0) {
            return null;
        }
        Map<String, ? extends Class<?>> fieldMap = getFields(tClass);
//        logger.info(JSONObject.valueToString(fieldMap));
        Map<String, Method> methodMap = getFieldSetterMethods(tClass);
//        logger.info(JSONObject.valueToString(methodMap));
        T t = null;
        try {
            t = tClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            logger.info("there no default Constructor for class"+tClass.getName());
        }
        if(t==null){
            throw new IllegalArgumentException("there no default Constructor for class"+tClass.getName());
        }
        for (String key : fieldMap.keySet()) {
            Object value = map.get(key);
            if(value!=null&&methodMap.containsKey(key)){
                Method method = methodMap.get(key);
                method.setAccessible(true);
                try {
                    method.invoke(t, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return t;
    }

    /**
     * 实体类转map,
     * 只限于有getter方法的对象,属性为基础类型和包装类和字符串的
     *
     * @param obj
     * @param needNullValue
     * @return
     */
    public static Map<String, Object> entityToMap(Object obj, boolean needNullValue) {

        Class<?> aClass = obj.getClass();
        Map<String, ? extends Class<?>> fieldMap = getFields(aClass);
        Method[] declaredMethods = aClass.getDeclaredMethods();
        Map<String, Object> map = new HashMap<>();
        for (Method declaredMethod : declaredMethods) {
            declaredMethod.setAccessible(true);
            String name = declaredMethod.getName();
            // not getter method
            if (!name.startsWith("get") || declaredMethod.getParameterCount() != 0) {
                continue;
            }
            String fieldName = StrUtil.lowerFirst(name.replace("get", ""));
            //not field getter method
            if(!fieldMap.containsKey(fieldName)){
                continue;
            }
            try {
                Object value = declaredMethod.invoke(obj);
                if(needNullValue){
                    map.put(fieldName,value);
                }else if(Objects.nonNull(value)){
                    //no need null value
                    map.put(fieldName,value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return map;
    }


}
