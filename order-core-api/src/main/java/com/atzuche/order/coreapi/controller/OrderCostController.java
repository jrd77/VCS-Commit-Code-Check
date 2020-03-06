/**
 * 
 */
package com.atzuche.order.coreapi.controller;

import java.util.List;

import javax.validation.Valid;

import com.atzuche.order.commons.vo.res.*;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailReqVO;
import com.atzuche.order.coreapi.service.OrderCostService;
import com.atzuche.order.coreapi.service.OwnerCostFacadeService;
import com.atzuche.order.coreapi.service.RenterCostFacadeService;
import com.atzuche.order.open.vo.RenterCostShortDetailVO;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
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
	private OwnerOrderService ownerOrderService;

	@Autowired
	private CashierQueryService cashierQueryService;
	@Autowired
	private OwnerCostFacadeService ownerCostFacadeService;
	
	@PostMapping("/order/cost/renter/get")
	public ResponseData<OrderRenterCostResVO> orderCostRenterGet(@Valid @RequestBody OrderCostReqVO req, BindingResult bindingResult) {
		log.info("租客子订单费用详细 orderCostRenterGet params=[{}]", req.toString());
		BindingResultUtil.checkBindingResult(bindingResult);
		OrderRenterCostResVO resVo = orderCostService.orderCostRenterGet(req);
		return ResponseData.success(resVo);
	}

	@GetMapping("/order/renter/cost/fullDetail")
	public ResponseData<RenterCostDetailVO> getRenterCostFullDetail(@RequestParam("orderNo") String orderNo){
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

		RenterCostDetailVO renterBasicCostDetailVO = facadeService.getRenterCostFullDetail(orderNo,renterOrderNo,memNo);

		return ResponseData.success(renterBasicCostDetailVO);
	}
	@GetMapping("/order/owner/cost/fullDetail")
	public ResponseData<OwnerCostDetailVO> getRenterCostFullDetail(@RequestParam("orderNo") String orderNo,
																   @RequestParam("ownerOrderNo") String ownerOrderNo,
																   @RequestParam("ownerMemNo") String ownerMemNo){
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		if(orderEntity==null){
			throw new OrderNotFoundException(orderNo);
		}
		if((ownerOrderNo == null || ownerOrderNo.trim().length()<=0) && (ownerMemNo == null || ownerMemNo.trim().length()<=0)){
			ResponseData responseData = new ResponseData();
			responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
			responseData.setResMsg("车主子订单号或车主会员号必须有一个不为空！");
			return responseData;
		}
		OwnerCostDetailVO ownerCostDetailVO = ownerCostFacadeService.getOwnerCostFullDetail(orderNo,ownerOrderNo,ownerMemNo);
		return ResponseData.success(ownerCostDetailVO);
	}

	public ResponseData getCarOwnerIncomeFullDetail(@RequestParam("orderNo")String orderNo,@RequestParam("ownerNo")String ownerNo){
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		if(orderEntity==null){
			throw new OrderNotFoundException(orderNo);
		}

		OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
		if(ownerOrderEntity==null){
			throw new OwnerOrderNotFoundException(orderNo);
		}

		if(!ownerNo.equals(ownerOrderEntity.getMemNo())){
           log.warn("查到的车主号码:{}和前端请求的车主号码:{}不一致",ownerOrderEntity.getMemNo(),ownerNo);
           throw new OwnerOrderNotFoundException(orderNo);
		}










		//FIXME:
        return null;
	}




	/**
	 * 获取租客的费用简况
	 * @param orderNo
	 * @return
	 */
	@GetMapping("/order/renter/cost/shortDetail")
	public ResponseData<RenterCostShortDetailVO> getRenterCostShortDetail(String orderNo){
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

		AccountRenterDepositEntity depositEntity = cashierQueryService.getTotalToPayDepositAmt(orderNo);
		AccountRenterWzDepositEntity wzDepositEntity = cashierQueryService.getTotalToPayWzDepositAmt(orderNo);

		RenterCostShortDetailVO shortDetail = new RenterCostShortDetailVO();

		shortDetail.setTotalRentCostAmt(-totalRentCostAmtWithoutFine);
		shortDetail.setTotalFineAmt(-totalFineAmt);
		shortDetail.setYingFuDeposit(-depositEntity.getYingfuDepositAmt());
		shortDetail.setShiFuDeposit(depositEntity.getShifuDepositAmt());
		shortDetail.setYingFuWzDeposit(-wzDepositEntity.getYingshouDeposit());
		shortDetail.setShiFuWzDeposit(wzDepositEntity.getShishouDeposit());
		shortDetail.setToPayDeposit(-(depositEntity.getYingfuDepositAmt()+depositEntity.getShifuDepositAmt()));
		shortDetail.setToPayWzDeposit(-(wzDepositEntity.getYingshouDeposit()+wzDepositEntity.getShishouDeposit()));
		shortDetail.setExpReturnDeposit(depositEntity.getShifuDepositAmt());
		shortDetail.setExpReturnWzDeposit(wzDepositEntity.getShishouDeposit());
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
	
	@GetMapping("/order/owner/cost/settle/detail/get")
	public ResponseData<OwnerCostSettleDetailVO> getOwnerCostSettleDetail(@RequestParam("orderNo") String orderNo,@RequestParam("ownerNo") String ownerNo){
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		if(orderEntity==null){
			throw new OrderNotFoundException(orderNo);
		}
		OwnerCostSettleDetailVO ownerCostSettleDetailVO = ownerCostFacadeService.getOwnerCostSettleDetail(orderNo,ownerNo);
		return ResponseData.success(ownerCostSettleDetailVO);
	}
	
	
	@PostMapping("/order/owner/cost/settle/detail/list")
	public ResponseData<List<OwnerCostSettleDetailVO>> listOwnerCostSettleDetail(@RequestBody OwnerCostSettleDetailReqVO req){
		List<OwnerCostSettleDetailVO> listVo = ownerCostFacadeService.listOwnerCostSettleDetail(req);
		return ResponseData.success(listVo);
	}
	

}
