package com.atzuche.order.admin.controller;

import java.util.Optional;

import javax.validation.Valid;

import com.atzuche.order.commons.BindingResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.service.AdminSupplementService;
import com.atzuche.order.admin.vo.req.DelSupplementReqVO;
import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.commons.vo.res.rentcosts.SupplementVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AutoDocVersion(version = "订单补付")
public class AdminSupplementController {
	@Autowired
	private AdminSupplementService adminSupplementService;
	
	
    @AutoDocMethod(description = "订单补付列表", value = "订单补付列表",response = SupplementVO.class)
    @RequestMapping(value="console/order/supplement/list",method = RequestMethod.GET)
    public ResponseData<SupplementVO> listSupplement(@RequestParam(value="orderNo",required = true) String orderNo){
        log.info("AdminSupplementController.listSupplement orderNo=[{}]", orderNo);
        return ResponseData.success(adminSupplementService.listSupplement(orderNo));
    }
	
	
    @AutoDocMethod(description = "新增补付", value = "新增补付",response = ResponseData.class)
    @RequestMapping(value="console/order/supplement/add",method = RequestMethod.POST)
    public ResponseData addSupplement(@Valid @RequestBody OrderSupplementDetailDTO orderSupplementDetailDTO, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);
        log.info("AdminSupplementController.addSupplement orderSupplementDetailDTO=[{}]", JSON.toJSONString(orderSupplementDetailDTO));
        adminSupplementService.addSupplement(orderSupplementDetailDTO);
        return ResponseData.success();
    }
	
	
    @AutoDocMethod(description = "删除补付", value = "删除补付",response = ResponseData.class)
    @RequestMapping(value="console/order/supplement/del",method = RequestMethod.POST)
    public ResponseData delSupplement(@Valid @RequestBody DelSupplementReqVO delReq, BindingResult bindingResult){
    	BindingResultUtil.checkBindingResult(bindingResult);
        log.info("AdminSupplementController.delSupplement delReq=[{}]", delReq);
        adminSupplementService.delSupplement(delReq.getId());
        return ResponseData.success();
    }
}
