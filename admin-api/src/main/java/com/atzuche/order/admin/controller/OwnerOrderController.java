package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.order.OwnerOrderRequestVO;
import com.atzuche.order.admin.vo.req.order.RenterOrderRequestVO;
import com.atzuche.order.admin.vo.resp.order.OwnerOrderListResponseVO;
import com.atzuche.order.admin.vo.resp.order.RenterOrderListResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class OwnerOrderController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerOrderController.class);


	@AutoDocMethod(description = "获取车主子订单列表", value = "获取车主子订单列表", response = OwnerOrderListResponseVO.class)
	@GetMapping("owner/list")
	public ResponseData ownerList(@RequestBody OwnerOrderRequestVO ownerOrderRequestVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
		return ResponseData.success(null);
	}




}
