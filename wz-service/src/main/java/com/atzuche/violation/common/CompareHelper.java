package com.atzuche.violation.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * CompareHelper
 *
 * @author shisong
 * @date 2020/1/7
 */
public class CompareHelper<T> {

    private T fromDb;

    private T fromApp;

    private Map<String,String> paramNames;


    public CompareHelper(T fromDb, T fromApp, Map<String,String> paramNames) {
        this.fromDb = fromDb;
        this.fromApp = fromApp;
        this.paramNames = paramNames;
    }

    @SuppressWarnings("unchecked")
    public String compare() throws Exception{
        if(fromApp == null || CollectionUtils.isEmpty(paramNames)){
            return "";
        }
        if(fromDb == null){
            try {
                fromDb = (T) fromApp.getClass().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String paramName : paramNames.keySet()) {
            String s = paramNames.get(paramName);
            String targetValue = getDeclaredFieldValueByField(fromDb, paramName);
            String sourceValue = getDeclaredFieldValueByField(fromApp, paramName);
            if(StringUtils.isBlank(targetValue) && StringUtils.isBlank(sourceValue)){
                continue;
            }
            if(StringUtils.isNotBlank(targetValue) && StringUtils.isNotBlank(sourceValue) && targetValue.equals(sourceValue)){
                continue;
            }
            sb.append("将【");
            sb.append(s);
            sb.append("】从'");
            if(StringUtils.isNotBlank(targetValue) && !"null".equals(targetValue)){
                sb.append(targetValue);
            }
            sb.append("'改成'");
            if(StringUtils.isNotBlank(sourceValue) && !"null".equals(sourceValue)){
                sb.append(sourceValue);
            }
            sb.append("';");
        }
        return sb.toString();
    }

    private String getDeclaredFieldValueByField(T t, String fieldName) throws Exception{
        Field field = t.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return String.valueOf(field.get(t));
    }


}
