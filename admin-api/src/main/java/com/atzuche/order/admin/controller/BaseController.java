package com.atzuche.order.admin.controller;

import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * @description:
 * @author: Lu
 * @create: 2019-03-09 14:48
 **/
public class BaseController {
	/**
	 * 返回验证信息，输出错误提示
	 * @param result 验证返回的错误信息集合
	 * @return
	 */
	public ResponseData<?> validate(BindingResult result){
		ResponseData<?> responseData = new ResponseData<>();
		responseData.setResCode(ErrorCode.SUCCESS.getCode());
		responseData.setResMsg(ErrorCode.SUCCESS.getText());
		List<FieldError> errors = result.getFieldErrors();
		for(FieldError err : errors) {
			throw new DeliveryOrderException(ErrorCode.PARAMETER_ERROR.getCode(),err.getDefaultMessage());
		}
		return responseData;
	}
}
