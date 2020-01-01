package com.atzuche.order.coreapi.base;

import com.atzuche.order.commons.OrderException;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 拦截所有的异常，将异常转换成满足前端要求的数据格式
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 5:57 下午
 **/
@ControllerAdvice
public class GlobalExceptionAdvisor {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseData handleException(Exception e){
        ResponseData responseData = ResponseData.createErrorCodeResponse(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        return responseData;
    }

    @ResponseBody
    @ExceptionHandler(OrderException.class)
    public ResponseData handleOrderException(OrderException e){
        Cat.logError(e);
        ResponseData responseData = ResponseData.createErrorCodeResponse(e.getErrorCode(),e.getErrorMsg());
        responseData.setData(e.getExtra());
        return responseData;
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseData handleException(IllegalArgumentException exception){
        Cat.logError(exception);
        ResponseData responseData = ResponseData.createErrorCodeResponse(ErrorCode.INPUT_ERROR.getCode(),ErrorCode.INPUT_ERROR.getText());
        return responseData;
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public ResponseData handleException(BindException exception){
        Cat.logError(exception);
        ResponseData responseData = ResponseData.createErrorCodeResponse(ErrorCode.INPUT_ERROR.getCode(),ErrorCode.INPUT_ERROR.getText());
        return responseData;
    }


}
