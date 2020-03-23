package com.atzuche.violation.common;

import com.atzuche.violation.common.annotation.FeildDescription;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @author 胡春林
 *
 * 注解处理
 */
@Slf4j
public class AnnotationHandler {

    /***
     * 获取获取的注释字段
     * @param obj
     * @return
     * @throws Exception
     */
    public static String[] getFeildDescription(Class obj) {

        Field[] declaredFields = obj.getDeclaredFields();
        if(null == declaredFields || declaredFields.length == 0)
        {
            log.info("没有找到该类的字段，请确认");
            return null;
        }
        String feildstr = "";
        for(int i = 0; i<declaredFields.length;i++)
        {
            if (declaredFields[i].isAnnotationPresent(FeildDescription.class)) {
                declaredFields[i].setAccessible(true);
                FeildDescription feildDescription = declaredFields[i].getAnnotation(FeildDescription.class);
                feildstr += feildDescription.value().toString()+",";
            }
        }
        feildstr = feildstr.substring(0,feildstr.length()-1);
        return feildstr.split("\\,");
    }
}