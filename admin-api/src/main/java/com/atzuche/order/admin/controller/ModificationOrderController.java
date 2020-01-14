package com.atzuche.order.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.admin.service.ModificationOrderService;
import com.atzuche.order.admin.vo.req.order.ModificationOrderRequestVO;
import com.atzuche.order.admin.vo.resp.order.ModificationOrderListResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class ModificationOrderController {

    private static final Logger logger = LoggerFactory.getLogger(ModificationOrderController.class);
    @Autowired
    ModificationOrderService modificationOrderService;
    
	@AutoDocMethod(description = "订单修改信息列表", value = "订单修改信息列表", response = ModificationOrderListResponseVO.class)
	@RequestMapping(value="modifacion/infomation/list",method = RequestMethod.POST)
	public ResponseData queryModifyList(@RequestBody ModificationOrderRequestVO modificationOrderRequestVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	ModificationOrderListResponseVO respVo = modificationOrderService.queryModifyList(modificationOrderRequestVO);
        	return ResponseData.success(respVo);
		} catch (Exception e) {
			logger.error("获取修改订单列表异常:params="+modificationOrderRequestVO.toString(),e);
			return ResponseData.error();
		}
        
		
	}




}
