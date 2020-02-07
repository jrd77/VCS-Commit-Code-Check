package com.atzuche.order.coreapi.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.atzuche.order.commons.BindingResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.coreapi.service.SupplementService;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SupplementController {
	
	@Autowired
	private SupplementService supplementService;

	/**
	 * 新增补付
	 * @param orderSupplementDetailDTO
	 * @param bindingResult
	 * @return ResponseData
	 */
	@PostMapping("/order/supplement/add")
	public ResponseData<?> addSupplement(@Valid @RequestBody OrderSupplementDetailDTO orderSupplementDetailDTO, BindingResult bindingResult) {
		log.info("order/supplement/add orderSupplementDetailDTO=[{}]", orderSupplementDetailDTO);
		BindingResultUtil.checkBindingResult(bindingResult);
		supplementService.saveSupplement(orderSupplementDetailDTO);
		return ResponseData.success();
    }
	
	
	/**
	 * 获取补补列表
	 * @param orderNo
	 * @return ResponseData<List<OrderSupplementDetailEntity>>
	 */
	@GetMapping("/order/supplement/list")
    public ResponseData<List<OrderSupplementDetailEntity>> listSupplement(@RequestParam(value="orderNo",required = true) String orderNo) {
		log.info("order/supplement/list orderNo=[{}]", orderNo);
		List<OrderSupplementDetailEntity> list = supplementService.listOrderSupplementDetailEntityByOrderNo(orderNo);
    	return ResponseData.success(list);
    }
	
	
	/**
	 * 删除补付
	 * @param id
	 * @return ResponseData
	 */
	@PostMapping("/order/supplement/del")
	public ResponseData<?> delSupplement(@RequestParam(value="id",required = true) Integer id) {
		log.info("order/supplement/del id=[{}]", id);
		supplementService.deleteSupplement(id);
		return ResponseData.success();
    }
}
