package com.atzuche.order.open.service;

import javax.validation.Valid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atzuche.order.commons.group.Second;
import com.atzuche.order.commons.vo.GetReturnCarInfoVO;
import com.atzuche.order.commons.vo.OwnerTransAddressReqVO;
import com.autoyol.commons.web.ResponseData;

@FeignClient(name="order-center-api")
public interface FeignModifyOwnerAddrService {

	@PostMapping("/order/owner/modify/addrdetail")
	public ResponseData<GetReturnCarInfoVO> getOwnerAddrInfo(@Valid @RequestBody OwnerTransAddressReqVO ownerTransAddressReqVO);

	@PostMapping("/order/owner/modify/updateaddr")
	public ResponseData<?> updateOwnerAddrInfo(@Validated({Second.class}) @RequestBody OwnerTransAddressReqVO ownerTransAddressReqVO);
}
