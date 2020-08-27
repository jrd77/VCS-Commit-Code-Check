package com.atzuche.order.cashieraccount.service;

import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayCallBackSuccessVO;
import com.atzuche.order.commons.enums.OrderPayStatusEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.ChangeOrderInfoDTO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.utils.GsonUtils;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderPaySuccessService {
	
	@Autowired
	private OrderStatusService orderStatusService;
	@Autowired
	private OrderFlowService orderFlowService;
	@Autowired
	private DeliveryCarService deliveryCarService;
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private OwnerOrderService ownerOrderService;

	/**
     * 订单流程 数据更新
     * @param vo
     * @param callBack
     */
	@Transactional(rollbackFor=Exception.class)
    public void orderPayCallBack(OrderPayCallBackSuccessVO vo, OrderPayCallBack callBack) {
        //支付成功更新 订单支付状态
        if(Objects.nonNull(vo) && Objects.nonNull(vo.getOrderNo())){
        	//order_supplement_detail  支付欠款的逻辑处理,否则结算后补付会修改订单状态。分离。
        	//rentAmountAfterRenterOrderNo 暂不处理。
        	if( !CollectionUtils.isEmpty(vo.getSupplementIds()) || !CollectionUtils.isEmpty(vo.getDebtIds()) || !CollectionUtils.isEmpty(vo.getRentAmountAfterRenterOrderNos())) {
        		//补付总和  vo.getMemNo(), vo.getIsPayAgain(),vo.getIsGetCar(),
        		//支付欠款，不更新订单状态。
        		//支付补充金额
        		callBack.callBack(vo.getOrderNo(),vo.getRentAmountAfterRenterOrderNos(),vo.getSupplementIds(),vo.getDebtIds());
        	}else {
        		// 加锁
        		OrderStatusEntity orderStatus = orderStatusService.getOrderStatusForUpdate(vo.getOrderNo());
        		if (orderStatus != null && orderStatus.getStatus() != null) {
        			if(OrderStatusEnum.CLOSED.getStatus() == orderStatus.getStatus()) {
        				log.info("当前订单状态=[{}]，无需重复修改订单状态，orderNo=[{}]",orderStatus.getStatus(),vo.getOrderNo());
        				return;
        			}
        		} else {
                    log.warn("Not found order status data.");
        		    return ;
                }


        		if(OrderStatusEnum.TO_GET_CAR.getStatus() > orderStatus.getStatus()) {
	            	// 获取车主子订单状态
	            	OwnerOrderEntity ownerOrder = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(vo.getOrderNo());
	            	int ownerStatus = ownerOrder.getOwnerStatus() == null ? 0:ownerOrder.getOwnerStatus();
		            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
		            BeanUtils.copyProperties(vo,orderStatusDTO);
		            //当支付成功（当车辆押金，违章押金，租车费用都支付成功，更新订单状态 待取车），更新主订单状态待取车
		            if(isChangeOrderStatus(orderStatusDTO)){
		            	int renterStatus = OrderStatusEnum.TO_CONFIRM.getStatus();
		            	if (ownerStatus > OrderStatusEnum.TO_CONFIRM.getStatus()) {
		            		renterStatus = OrderStatusEnum.TO_GET_CAR.getStatus();
		            		orderStatusDTO.setStatus(OrderStatusEnum.TO_GET_CAR.getStatus());
			                vo.setIsGetCar(YesNoEnum.YES);
			                //记录订单流程
			                orderFlowService.inserOrderStatusChangeProcessInfo(orderStatusDTO.getOrderNo(), OrderStatusEnum.TO_GET_CAR);
		            	} else if (ownerStatus == OrderStatusEnum.TO_CONFIRM.getStatus()){
		            		orderStatusDTO.setStatus(OrderStatusEnum.TO_CONFIRM.getStatus());
		            		//记录订单流程
			                orderFlowService.inserOrderStatusChangeProcessInfo(orderStatusDTO.getOrderNo(), OrderStatusEnum.TO_CONFIRM);
		            	}
		            	//更新租客订单车主同意信息
		                RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(vo.getOrderNo());
		                if (renterOrderEntity != null) {
		                	// 更新租客之订单状态
			                renterOrderService.updateRenterStatusByRenterOrderNo(renterOrderEntity.getRenterOrderNo(), renterStatus);
		                }
		            }
		            //更新支付状态（含批量修改，支付租车费用，租车押金，违章押金）
		            orderStatusService.saveOrderStatusInfo(orderStatusDTO);
		            log.info("押金支付发送给任云orderStatusDTO={}",orderStatusDTO);
	                deliveryCarService.changeRenYunFlowOrderInfo(new ChangeOrderInfoDTO().setOrderNo(vo.getOrderNo()));
	                log.info("payOrderCallBackSuccess saveOrderStatusInfo :[{}]", GsonUtils.toJson(orderStatusDTO));
        		}

	            //更新配送 订单补付等信息 只有订单状态为已支付
	            //callback
        		/**
        		 * 16：待取车
        		 * 32：待还车   APP修改订单payKind 03的情况，都会执行回调。 200811
        		 */
	            if(isGetCar(vo)){
	                //异步通知处理类
	            	callBack.callBack(vo.getMemNo(),vo.getOrderNo(),vo.getRenterOrderNo(),vo.getIsPayAgain(),vo.getIsGetCar());
	                
	            }

        	}
        }
    }
    
    
    /**
     * 判断 租车费用 押金 违章押金 是否全部成功支付
     * @param orderStatusDTO
     * @return
     */
    private Boolean isChangeOrderStatus(OrderStatusDTO orderStatusDTO){
        OrderStatusEntity entity = orderStatusService.getByOrderNo(orderStatusDTO.getOrderNo());
        boolean getCar = false;
        if(entity != null) {
        	log.info("支付变更订单状态回调通知OrderStatusEntity result=[{}]",GsonUtils.toJson(entity));
        }
        //如果是调度中的状态，不修改为“待取车”
        if(entity != null && entity.getStatus() != null && OrderStatusEnum.TO_DISPATCH.getStatus() == entity.getStatus().intValue()) {
        	log.info("当前订单状态为调度中的状态，不修改status主订单状态。OrderStatusEntity result=[{}]",GsonUtils.toJson(entity));
        	return getCar;
        }
        
        //以参数的为准。
        Integer rentCarPayStatus = Objects.isNull(orderStatusDTO.getRentCarPayStatus())?entity.getRentCarPayStatus():orderStatusDTO.getRentCarPayStatus();
        Integer depositPayStatus = Objects.isNull(orderStatusDTO.getDepositPayStatus())?entity.getDepositPayStatus():orderStatusDTO.getDepositPayStatus();
        Integer wzPayStatus = Objects.isNull(orderStatusDTO.getWzPayStatus())?entity.getWzPayStatus():orderStatusDTO.getWzPayStatus();
        if(
                (Objects.nonNull(rentCarPayStatus) && OrderPayStatusEnum.PAYED.getStatus() == rentCarPayStatus)&&
                        ( Objects.nonNull(depositPayStatus) && OrderPayStatusEnum.PAYED.getStatus() == depositPayStatus )&&
                        (Objects.nonNull(wzPayStatus)  && OrderPayStatusEnum.PAYED.getStatus() == wzPayStatus)
        ){
            getCar =true;
        }
        return getCar;
    }
    
    
    
    /**
     * 判断 租车费用 是否成功支付
     * @param orderPayCallBackSuccessVO
     * @return
     */
    private boolean isGetCar(OrderPayCallBackSuccessVO orderPayCallBackSuccessVO){
        boolean getCar = false;
        Integer rentCarPayStatus = orderPayCallBackSuccessVO.getRentCarPayStatus();
        if(Objects.nonNull(rentCarPayStatus) && OrderPayStatusEnum.PAYED.getStatus() == rentCarPayStatus){
            getCar =true;
        }
        return getCar;
    }
}
