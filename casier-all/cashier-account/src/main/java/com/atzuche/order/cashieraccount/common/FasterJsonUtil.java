package com.atzuche.order.cashieraccount.common;

import com.atzuche.order.cashieraccount.exception.OrderPaySignParamException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * FasterXml json 公共包 ，对接支付系统 json 格式化 跟支付系统保持一致
 */
@Slf4j
public class FasterJsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object obj1) {
        try {
            return objectMapper.writeValueAsString(obj1);
        } catch (JsonProcessingException e) {
            log.error("FasterJsonUtil toJson error e: [{}]",e);
            throw new OrderPaySignParamException();
        }
    }

    public static Object parseObject(String jsonStr, Class targetClass) {
        try {
            return objectMapper.readValue(jsonStr, targetClass);
        } catch (IOException e) {
            log.error("FasterJsonUtil toJson error e: [{}]",e);
            throw new OrderPaySignParamException();
        }
    }

}
