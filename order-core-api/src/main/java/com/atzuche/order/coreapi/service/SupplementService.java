package com.atzuche.order.coreapi.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.OrderSupplementDetailVO;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SupplementOpTypeEnum;
import com.atzuche.order.commons.enums.SupplementTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParentOrderNotFindException;
import com.atzuche.order.coreapi.modifyorder.exception.SupplementAmtException;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SupplementService {

	@Autowired
	private OrderSupplementDetailService orderSupplementDetailService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;
	@Autowired
	private CashierNoTService cashierNoTService;
	@Autowired
	private AccountRenterCostSettleService accountRenterCostSettleService;
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private CashierService cashierService;
	@Autowired
	private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
	@Autowired
	private OrderStatusService orderStatusService;
	
	/**
	 * 保存补付记录
	 * @param orderSupplementDetailDTO
	 */
	public void saveSupplement(OrderSupplementDetailDTO orderSupplementDetailDTO) {
		if (orderSupplementDetailDTO == null) {
			return;
		}
		// 获取订单结算状态
		OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(orderSupplementDetailDTO.getOrderNo());
		if (orderStatus != null) {
			if (orderStatus.getStatus() != null && orderStatus.getStatus().intValue() == OrderStatusEnum.CLOSED.getStatus()) {
				orderSupplementDetailDTO.setCashType(2);
			} else if (orderStatus.getWzSettleStatus() != null && orderStatus.getWzSettleStatus() != 0) {
				orderSupplementDetailDTO.setCashType(2);
			} else if (orderStatus.getSettleStatus() != null && orderStatus.getSettleStatus() != 0) {
				orderSupplementDetailDTO.setCashType(1);
			} else {
				//throw new SupplementCanNotSupportException();
				orderSupplementDetailDTO.setCashType(1);
			}
		} else {
			// 订单状态异常
			log.error("order/supplement/add 订单状态异常");
		}
		/*
		 * if (orderSupplementDetailDTO.getAmt() >= 0 &&
		 * orderSupplementDetailDTO.getCashType() != null &&
		 * orderSupplementDetailDTO.getCashType() == 2) { throw new
		 * SupplementAmtException(); }
		 */
		// 校验手动补付金额
		checkSupplement(orderSupplementDetailDTO.getOrderNo(), orderStatus, orderSupplementDetailDTO.getAmt());
		OrderSupplementDetailEntity supplementEntity = new OrderSupplementDetailEntity();
		BeanUtils.copyProperties(orderSupplementDetailDTO, supplementEntity);
		// 根据订单号获取主订单信息
		OrderEntity orderEntity = orderService.getOrderEntity(orderSupplementDetailDTO.getOrderNo());
		if (orderEntity == null) {
			Cat.logError("SupplementService.saveSupplement保存补付记录", new ModifyOrderParentOrderNotFindException());
			throw new OrderNotFoundException(orderSupplementDetailDTO.getOrderNo());
		}
		supplementEntity.setMemNo(orderEntity.getMemNoRenter());
		supplementEntity.setSupplementType(SupplementTypeEnum.MANUAL_CREATE.getCode());
		supplementEntity.setOpType(SupplementOpTypeEnum.MANUAL_CREATE.getCode());
		if (orderSupplementDetailDTO.getAmt() >= 0) {
			supplementEntity.setPayFlag(0);
		}
		orderSupplementDetailService.saveOrderSupplementDetail(supplementEntity);
	}
	
	
	/**
	 * 校验手动补付金额
	 * @param orderNo
	 * @param orderStatus
	 * @param amt
	 */
	private void checkSupplement(String orderNo, OrderStatusEntity orderStatus, Integer amt) {
		int curAmt = amt == null ? 0:amt;
		if (curAmt == 0) {
			throw new SupplementAmtException("手动添加补付不能为0");
		}
		// 获取补付列表
		List<OrderSupplementDetailEntity> list = this.listOrderSupplementDetailEntityByOrderNo(orderNo,orderStatus);
		if ((list == null || list.isEmpty()) && curAmt > 0) {
			throw new SupplementAmtException("负数金额超过可添加的限额");
		}
		int totalSupAmt = 0;
		for (OrderSupplementDetailEntity supp:list) {
			int payFlag = supp.getPayFlag() == null ? -1:supp.getPayFlag();
			if (payFlag == 0 || payFlag == 1 || payFlag == 4 || payFlag == 5) {
				int cursupAmt = supp.getAmt() == null ? 0:supp.getAmt();
				totalSupAmt += cursupAmt;
			}
		}
		if ((curAmt + totalSupAmt) > 0) {
			throw new SupplementAmtException("负数金额超过可添加的限额");
		}
	}
	
	
	/**
	 * 根据订单号获取补付记录
	 * @param orderNo
	 * @return List<OrderSupplementDetailEntity>
	 */
	public List<OrderSupplementDetailEntity> listOrderSupplementDetailEntityByOrderNo(String orderNo, OrderStatusEntity orderStatus) {
		List<OrderSupplementDetailEntity> list = orderSupplementDetailService.listOrderSupplementDetailByOrderNo(orderNo);
        // 订单状态
		int status = orderStatus == null || orderStatus.getStatus() == null ? -1:orderStatus.getStatus();
        if (status > OrderStatusEnum.TO_SETTLE.getStatus() || status == OrderStatusEnum.CLOSED.getStatus()) {
        	return list;
        }
		// 获取修改前有效租客子订单信息
		RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndChildStatus(orderNo);
		if (renterOrder == null) {
			return list;
		}
//		List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableGlobalVO(orderNo,renterOrder.getRenterOrderNo(),renterOrder.getRenterMemNo());
		List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableIncrementVO(orderNo,renterOrder.getRenterOrderNo(),renterOrder.getRenterMemNo());
		
        //应付租车费用（已经求和）
        int rentAmtAfter = cashierNoTService.sumRentOrderCost(payableVOs);
        // 平台给租客的补贴总额
		int platformToRenterAmt = orderConsoleSubsidyDetailService.getPlatformToRenterSubsidyAmt(orderNo, renterOrder.getRenterMemNo());
		rentAmtAfter = rentAmtAfter + platformToRenterAmt;
        //已付租车费用(shifu  租车费用的实付)
        int rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderNo,renterOrder.getRenterMemNo());
        
        if(!CollectionUtils.isEmpty(payableVOs) && rentAmtAfter+rentAmtPayed < 0){   // 
        	RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER;	                    
            //数据封装
            OrderSupplementDetailEntity entity = orderSupplementDetailService.handleConsoleData(rentAmtAfter+rentAmtPayed, type, renterOrder.getRenterMemNo(), orderNo);
            if(entity != null) {
            	list = list == null ? new ArrayList<OrderSupplementDetailEntity>():list;
            	list.add(entity);
        	}
        }
        return list;
	}

	public List<OrderSupplementDetailVO> listOrderSupplementDetailVOByOrderNo(String orderNo){
		List<OrderSupplementDetailEntity>  entityList = orderSupplementDetailService.listOrderSupplementDetailByOrderNo(orderNo);
		List<OrderSupplementDetailVO> voList = new ArrayList<>();
		for(OrderSupplementDetailEntity entity:entityList){
			if (entity.getPayFlag() != null && entity.getPayFlag() == 3) {
				// 已支付的补付记录
				OrderSupplementDetailVO vo = new OrderSupplementDetailVO();
				BeanUtils.copyProperties(entity,vo);
				voList.add(vo);
			}
		}
		// 获取收银记录
		List<CashierEntity> afterList = cashierService.getCashierRentCostsByOrderNo(orderNo);
		if (afterList == null || afterList.isEmpty()) {
			return voList;
		}
		int suppRentAmt = afterList.stream().filter(cash -> {return "12".equals(cash.getPayKind());}).mapToInt(CashierEntity::getPayAmt).sum();
		int suppDebtAmt = afterList.stream().filter(cash -> {return "07".equals(cash.getPayKind());}).mapToInt(CashierEntity::getPayAmt).sum();
		if (suppRentAmt > 0) {
			OrderSupplementDetailEntity entity = orderSupplementDetailService.handleConsoleData(-suppRentAmt, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER, afterList.get(0).getMemNo(), orderNo);
			OrderSupplementDetailVO vo = new OrderSupplementDetailVO();
			BeanUtils.copyProperties(entity,vo);
			voList.add(vo);
		}
		if (suppDebtAmt > 0) {
			OrderSupplementDetailEntity entity = orderSupplementDetailService.handleConsoleData(-suppDebtAmt, RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN, afterList.get(0).getMemNo(), orderNo);
			OrderSupplementDetailVO vo = new OrderSupplementDetailVO();
			BeanUtils.copyProperties(entity,vo);
			voList.add(vo);
		}
		return voList;
	}
	
	/**
	 * 根据id删除补付记录
	 * @param id
	 */
	public void deleteSupplement(Integer id) {
		orderSupplementDetailService.updateDeleteById(id);
	}
}
