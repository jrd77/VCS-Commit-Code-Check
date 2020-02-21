package com.atzuche.order.coreapi.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.group.Second;
import com.atzuche.order.commons.vo.OwnerTransAddressReqVO;
import com.atzuche.order.coreapi.entity.vo.GetReturnCarInfoVO;
import com.atzuche.order.coreapi.service.ModifyOwnerAddrService;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ModifyOwnerAddrController {
	
	@Autowired
	private ModifyOwnerAddrService modifyOwnerAddrService;

	/**
	 * 车主获取订单取还车地址信息
	 * @param ownerTransAddressReqVO
	 * @param bindingResult
	 * @return ResponseData<GetReturnCarInfoVO>
	 */
	@PostMapping("/order/owner/modify/addrdetail")
	public ResponseData<GetReturnCarInfoVO> getOwnerAddrInfo(@Valid @RequestBody OwnerTransAddressReqVO ownerTransAddressReqVO, BindingResult bindingResult) {
		log.info("车主获取订单取还车地址信息ownerTransAddressReqVO=[{}] ", ownerTransAddressReqVO);
		BindingResultUtil.checkBindingResult(bindingResult);
		GetReturnCarInfoVO getReturnCarInfoVO = modifyOwnerAddrService.getGetReturnCarInfoVO(ownerTransAddressReqVO);
		return ResponseData.success(getReturnCarInfoVO);
	}
	
	
	/**
	 * 车主修改交接车地址
	 * @param ownerTransAddressReqVO
	 * @param bindingResult
	 * @return ResponseData
	 */
	@PostMapping("/order/owner/modify/updateaddr")
	public ResponseData<GetReturnCarInfoVO> updateOwnerAddrInfo(@Validated({Second.class}) @RequestBody OwnerTransAddressReqVO ownerTransAddressReqVO, BindingResult bindingResult) {
		log.info("车主修改交接车地址ownerTransAddressReqVO=[{}] ", ownerTransAddressReqVO);
		BindingResultUtil.checkBindingResult(bindingResult);
		modifyOwnerAddrService.updateOwnerAddr(ownerTransAddressReqVO);
		return ResponseData.success();
	}
}
