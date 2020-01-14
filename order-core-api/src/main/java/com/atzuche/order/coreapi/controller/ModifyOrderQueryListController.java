/**
 * 
 */
package com.atzuche.order.coreapi.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.commons.vo.req.ModifyOrderMainQueryReqVO;
import com.atzuche.order.commons.vo.req.ModifyOrderQueryReqVO;
import com.atzuche.order.commons.vo.res.ModifyOrderMainResVO;
import com.atzuche.order.commons.vo.res.ModifyOrderResVO;
import com.atzuche.order.coreapi.service.ModifyOrderQueryListService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@RestController
@Slf4j
public class ModifyOrderQueryListController {
	
	@Autowired
	ModifyOrderQueryListService modifyOrderQueryListService;
	
	@PostMapping("/order/modify/query")
	public ResponseData<ModifyOrderResVO> queryModifyOrderList(@Valid @RequestBody ModifyOrderQueryReqVO req, BindingResult bindingResult) {
		log.info("修改订单列表 ModifyOrderQueryReq params=[{}]", req.toString());
		if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
		try {
			ModifyOrderResVO resVo = modifyOrderQueryListService.queryModifyOrderList(req.getOrderNo(), req.getRenterOrderNo());
			return ResponseData.success(resVo);
		} catch (Exception e) {
			log.error("查询租客修改订单列表异常:",e);
			return ResponseData.error();
		}
	}
	
	@PostMapping("/order/modify/get")
	public ResponseData<ModifyOrderMainResVO> getModifyOrderMain(@Valid @RequestBody ModifyOrderMainQueryReqVO req, BindingResult bindingResult) {
		log.info("修改订单列表 ModifyOrderQueryReq params=[{}]", req.toString());
		if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
		try {
			ModifyOrderMainResVO resVo = modifyOrderQueryListService.getModifyOrderMain(req.getOrderNo(), req.getMemNo());
			return ResponseData.success(resVo);
		} catch (Exception e) {
			log.error("查询租客修改订单列表异常:",e);
			return ResponseData.error();
		}
	}
	
	
}
