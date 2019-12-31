/*
package com.atzuche.delivery.exception;

import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

*/
/**
 * 自定义RUNTIME处理类
 * 组装为JSON格式数据，用于前端接收
 *//*

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseData<?> operateExp(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        if (ex instanceof DeliveryBusinessException) {
            ResponseData<?> responseData = new ResponseData<>();
            responseData.setResCode(((DeliveryBusinessException) ex).getErrorCode());
            responseData.setResMsg(ex.getMessage());
            return responseData;
        } else {
            log.error("invoke api [{}] exception.", request.getRequestURL(), ex);
            Cat.logError("invoke api [{" +request.getRequestURL()+ "}] exception.", ex);
            return ResponseData.error();
        }
    }

}
*/
