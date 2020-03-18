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
import com.atzuche.order.commons.enums.OrderSettleEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.coreapi.entity.vo.SupplementVO;
import com.atzuche.order.coreapi.modifyorder.exception.SupplementAmtException;
import com.atzuche.order.coreapi.modifyorder.exception.SupplementCanNotSupportException;
import com.atzuche.order.coreapi.service.SupplementService;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SupplementController {
	
	@Autowired
	private SupplementService supplementService;
	@Autowired
	private OrderStatusService orderStatusService;

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
		// 获取订单结算状态
		OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(orderSupplementDetailDTO.getOrderNo());
		if (orderStatus != null) {
			if (orderStatus.getStatus() != null && orderStatus.getStatus().intValue() == OrderStatusEnum.CLOSED.getStatus()) {
				orderSupplementDetailDTO.setCashType(2);
			} else if (orderStatus.getWzSettleStatus() != null && orderStatus.getWzSettleStatus() == 1) {
				orderSupplementDetailDTO.setCashType(2);
			} else if (orderStatus.getCarDepositSettleStatus() != null && orderStatus.getCarDepositSettleStatus() == 1) {
				orderSupplementDetailDTO.setCashType(1);
			} else {
				throw new SupplementCanNotSupportException();
			}
		} else {
			// 订单状态异常
			log.error("order/supplement/add 订单状态异常");
		}
		if (orderSupplementDetailDTO.getAmt() >= 0 && 
				orderSupplementDetailDTO.getCashType() != null && orderSupplementDetailDTO.getCashType() == 2) {
			throw new SupplementAmtException();
		}
		supplementService.saveSupplement(orderSupplementDetailDTO);
		return ResponseData.success();
    }
	
	
	/**
	 * 获取补补列表
	 * @param orderNo
	 * @return ResponseData<List<OrderSupplementDetailEntity>>
	 */
	@GetMapping("/order/supplement/list")
    public ResponseData<SupplementVO> listSupplement(@RequestParam(value="orderNo",required = true) String orderNo) {
		log.info("order/supplement/list orderNo=[{}]", orderNo);
		SupplementVO supplementVO = new SupplementVO();
		// 获取订单结算状态
		OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(orderNo);
		// 获取补付列表
		List<OrderSupplementDetailEntity> list = supplementService.listOrderSupplementDetailEntityByOrderNo(orderNo);
		supplementVO.setList(list);
		if (orderStatus != null) {
			if (orderStatus.getStatus() != null && orderStatus.getStatus().intValue() == OrderStatusEnum.CLOSED.getStatus()) {
				supplementVO.setOrderSettle(OrderSettleEnum.ORDER_CANCEL.getCode());
				supplementVO.setOrderSettleTxt(OrderSettleEnum.ORDER_CANCEL.getDesc());
			} else if (orderStatus.getWzSettleStatus() != null && orderStatus.getWzSettleStatus() == 1) {
				supplementVO.setOrderSettle(OrderSettleEnum.ILLEGAL_DEPOSIT_SETTLED.getCode());
				supplementVO.setOrderSettleTxt(OrderSettleEnum.ILLEGAL_DEPOSIT_SETTLED.getDesc());
			} else if (orderStatus.getCarDepositSettleStatus() != null && orderStatus.getCarDepositSettleStatus() == 1) {
				supplementVO.setOrderSettle(OrderSettleEnum.ILLEGAL_DEPOSIT_NOT_SETTLED.getCode());
				supplementVO.setOrderSettleTxt(OrderSettleEnum.ILLEGAL_DEPOSIT_NOT_SETTLED.getDesc());
			} else {
				supplementVO.setOrderSettle(OrderSettleEnum.DEPOSIT_NOT_SETTLED.getCode());
				supplementVO.setOrderSettleTxt(OrderSettleEnum.DEPOSIT_NOT_SETTLED.getDesc());
			}
		}
		return ResponseData.success(supplementVO);
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
