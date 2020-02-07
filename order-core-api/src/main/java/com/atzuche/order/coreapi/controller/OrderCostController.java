/**
 * 
 */
package com.atzuche.order.coreapi.controller;

import java.util.Optional;

import javax.validation.Valid;

import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.open.vo.RenterCostShortDetail;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.service.RenterCostFacadeService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.atzuche.order.coreapi.service.OrderCostService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@RestController
@Slf4j
public class OrderCostController {
	@Autowired
	private OrderCostService orderCostService;

	@Autowired
	private RenterCostFacadeService facadeService;
	@Autowired
	private OrderService orderService;

	@Autowired
	private RenterOrderService renterOrderService;

	@Autowired
	private CashierQueryService cashierQueryService;
	
	@PostMapping("/order/cost/renter/get")
	public ResponseData<OrderRenterCostResVO> orderCostRenterGet(@Valid @RequestBody OrderCostReqVO req, BindingResult bindingResult) {
		log.info("租客子订单费用详细 orderCostRenterGet params=[{}]", req.toString());
		BindingResultUtil.checkBindingResult(bindingResult);
		OrderRenterCostResVO resVo = orderCostService.orderCostRenterGet(req);
		return ResponseData.success(resVo);
	}

	/**
	 * 获取租客的费用简况
	 * @param orderNo
	 * @return
	 */
	@GetMapping("/order/renter/cost/shortDetail")
	public ResponseData<RenterCostShortDetail> getRenterCostShortDetail(String orderNo){
         OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
         if(orderEntity==null){
         	throw new OrderNotFoundException(orderNo);
		 }
         String memNo = orderEntity.getMemNoRenter();
		 RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		 if(renterOrderEntity==null){
			 throw new OrderNotFoundException(orderNo);
		 }
		 String renterOrderNo = renterOrderEntity.getRenterOrderNo();

		 int totalRentCostAmtWithoutFine = facadeService.getTotalRenterCostWithoutFine(orderNo,renterOrderNo,memNo);
		 int totalFineAmt = facadeService.getTotalFine(orderNo,renterOrderNo,memNo);

		 int toPayDepositAmt = cashierQueryService.getTotalToPayDepositAmt(orderNo);
		 int toPayWzDepositAmt = cashierQueryService.getTotalToPayWzDepositAmt(orderNo);

		RenterCostShortDetail shortDetail = new RenterCostShortDetail();

		shortDetail.setTotalRentCostAmt(-totalRentCostAmtWithoutFine);
		shortDetail.setTotalFineAmt(-totalFineAmt);
		shortDetail.setToPayDeposit(-toPayDepositAmt);
		shortDetail.setToPayWzDeposit(-toPayWzDepositAmt);
		shortDetail.setExpReturnDeposit(-toPayDepositAmt);
		shortDetail.setExpReturnWzDeposit(-toPayWzDepositAmt);
		shortDetail.setOrderNo(orderNo);

		return ResponseData.success(shortDetail);

	}


	
	@PostMapping("/order/cost/owner/get")
	public ResponseData<OrderOwnerCostResVO> orderCostOwnerGet(@Valid @RequestBody OrderCostReqVO req, BindingResult bindingResult) {
		log.info("车主子订单费用详细 orderCostOwnerGet params=[{}]", req.toString());
		BindingResultUtil.checkBindingResult(bindingResult);

		OrderOwnerCostResVO resVo = orderCostService.orderCostOwnerGet(req);
		return ResponseData.success(resVo);

	}


	
}
