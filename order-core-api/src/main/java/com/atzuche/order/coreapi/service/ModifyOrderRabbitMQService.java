package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyConfirmDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.owner.commodity.service.OwnerCommodityService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderChangeCarMq;
import com.autoyol.event.rabbit.neworder.OrderDelayMq;
import com.autoyol.event.rabbit.neworder.OrderOwnerAgreeModifyMq;
import com.autoyol.event.rabbit.neworder.OrderOwnerRefundModifyMq;
import com.autoyol.event.rabbit.neworder.OrderPlatModifyMq;
import com.autoyol.event.rabbit.neworder.OrderRenterApplyModifyMq;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderRabbitMQService {
	@Autowired
	private BaseProducer baseProducer;
	@Autowired
	private OrderSourceStatService orderSourceStatService;
	@Autowired
	private ModifyOrderRiskService modifyOrderRiskService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private OwnerCommodityService ownerCommodityService;
	
	/**
	 * 租客申请修改订单事件
	 * @param modifyOrderDTO
	 */
	public void sendOrderRenterApplyModifyMq(ModifyOrderDTO modifyOrderDTO) {
		try {
			String orderNo = modifyOrderDTO.getOrderNo();
			// 获取订单来源信息
	        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
	        // 主订单信息
	        OrderEntity oe = modifyOrderDTO.getOrderEntity();
	        // 租客商品信息
	        RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
	        //发送MQ时间
			OrderRenterApplyModifyMq mq = new OrderRenterApplyModifyMq();
			mq.setOrderNo(orderNo);
			mq.setMemNo(Integer.valueOf(modifyOrderDTO.getMemNo()));
			mq.setCategory(oe.getCategory() != null ? String.valueOf(oe.getCategory()):null);
			mq.setBusinessParentType(osse.getBusinessParentType());
			mq.setBusinessChildType(osse.getBusinessChildType());
			mq.setPlatformParentType(osse.getPlatformParentType());
			mq.setPlatformChildType(osse.getPlatformChildType());
			mq.setRentTime(modifyOrderRiskService.localDateTime2Date(modifyOrderDTO.getRentTime()));
			mq.setRevertTime(modifyOrderRiskService.localDateTime2Date(modifyOrderDTO.getRevertTime()));
	        if (renterGoodsDetailDTO != null) {
	        	mq.setCarNo(renterGoodsDetailDTO.getCarNo());
	        }
	        log.info("ModifyOrderRabbitMQService.sendOrderRenterApplyModifyMq mqparam=[{}]", JSON.toJSONString(mq));
			OrderMessage orderMessage = OrderMessage.builder().build();
	        orderMessage.setMessage(mq);
	        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.RENTER_ORDER_MODIFY.exchange,NewOrderMQActionEventEnum.RENTER_ORDER_MODIFY.routingKey,orderMessage);
		} catch (Exception e) {
			log.error("ModifyOrderRabbitMQService.sendOrderRenterApplyModifyMq error:", e);
			Cat.logError("ModifyOrderRabbitMQService.sendOrderRenterApplyModifyMq error", e);
		}
	}
	
	
	/**
	 * 车主同意订单修改事件
	 * @param modifyOrderOwnerDTO
	 */
	public void sendOrderOwnerAgreeModifyMq(ModifyOrderOwnerDTO modifyOrderOwnerDTO) {
		try {
			String orderNo = modifyOrderOwnerDTO.getOrderNo();
			// 获取订单来源信息
	        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
	        // 获取主订单信息
			OrderEntity oe = orderService.getOrderEntity(orderNo);
	        // 租客商品信息
			OwnerGoodsDetailDTO ownerGoodsDetailDTO = modifyOrderOwnerDTO.getOwnerGoodsDetailDTO();
			//发送MQ
			OrderOwnerAgreeModifyMq mq = new OrderOwnerAgreeModifyMq();
			mq.setOrderNo(orderNo);
			mq.setMemNo(Integer.valueOf(oe.getMemNoRenter()));
			mq.setCategory(oe.getCategory() != null ? String.valueOf(oe.getCategory()):null);
			mq.setBusinessParentType(osse.getBusinessParentType());
			mq.setBusinessChildType(osse.getBusinessChildType());
			mq.setPlatformParentType(osse.getPlatformParentType());
			mq.setPlatformChildType(osse.getPlatformChildType());
			mq.setRentTime(modifyOrderRiskService.localDateTime2Date(modifyOrderOwnerDTO.getRentTime()));
			mq.setRevertTime(modifyOrderRiskService.localDateTime2Date(modifyOrderOwnerDTO.getRevertTime()));
	        if (ownerGoodsDetailDTO != null) {
	        	mq.setCarNo(ownerGoodsDetailDTO.getCarNo());
	        }
	        log.info("ModifyOrderRabbitMQService.sendOrderOwnerAgreeModifyMq mqparam=[{}]", JSON.toJSONString(mq));
			OrderMessage orderMessage = OrderMessage.builder().build();
	        orderMessage.setMessage(mq);
	        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.OWNER_ORDER_AGREEMODIFY.exchange,NewOrderMQActionEventEnum.OWNER_ORDER_AGREEMODIFY.routingKey,orderMessage);
		} catch (Exception e) {
			log.error("ModifyOrderRabbitMQService.sendOrderOwnerAgreeModifyMq error:", e);
			Cat.logError("ModifyOrderRabbitMQService.sendOrderOwnerAgreeModifyMq error", e);
		}
	}
	
	
	/**
	 * 车主拒绝订单修改事件
	 * @param modifyConfirmDTO
	 */
	public void sendOrderOwnerRefundModifyMq(ModifyConfirmDTO modifyConfirmDTO) {
		try {
			String orderNo = modifyConfirmDTO.getOrderNo();
			// 获取订单来源信息
	        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
	        // 获取主订单信息
			OrderEntity oe = orderService.getOrderEntity(orderNo);
	        // 租客商品信息
			OwnerGoodsDetailDTO ownerGoodsDetailDTO = ownerCommodityService.getOwnerGoodsDetail(modifyConfirmDTO.getOwnerOrderNo(), false);
			// 获取租客修改申请表中已同意的租客子订单
			RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByRenterOrderNo(modifyConfirmDTO.getRenterOrderNo());
			//发送MQ
			OrderOwnerRefundModifyMq mq = new OrderOwnerRefundModifyMq();
			mq.setOrderNo(orderNo);
			mq.setMemNo(Integer.valueOf(renterOrder.getRenterMemNo()));
			mq.setCategory(oe.getCategory() != null ? String.valueOf(oe.getCategory()):null);
			mq.setBusinessParentType(osse.getBusinessParentType());
			mq.setBusinessChildType(osse.getBusinessChildType());
			mq.setPlatformParentType(osse.getPlatformParentType());
			mq.setPlatformChildType(osse.getPlatformChildType());
			mq.setRentTime(modifyOrderRiskService.localDateTime2Date(renterOrder.getExpRentTime()));
			mq.setRevertTime(modifyOrderRiskService.localDateTime2Date(renterOrder.getExpRevertTime()));
	        if (ownerGoodsDetailDTO != null) {
	        	mq.setCarNo(ownerGoodsDetailDTO.getCarNo());
	        }
	        log.info("ModifyOrderRabbitMQService.sendOrderOwnerRefundModifyMq mqparam=[{}]", JSON.toJSONString(mq));
			OrderMessage orderMessage = OrderMessage.builder().build();
	        orderMessage.setMessage(mq);
	        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.OWNER_ORDER_REFUNDMODIFY.exchange,NewOrderMQActionEventEnum.OWNER_ORDER_REFUNDMODIFY.routingKey,orderMessage);
		} catch (Exception e) {
			log.error("ModifyOrderRabbitMQService.sendOrderOwnerRefundModifyMq error:", e);
			Cat.logError("ModifyOrderRabbitMQService.sendOrderOwnerRefundModifyMq error", e);
		}
	}
	
	
	/**
	 * 管理后台修改订单事件
	 * @param modifyOrderDTO
	 */
	public void sendOrderPlatModifyMq(ModifyOrderDTO modifyOrderDTO) {
		try {
			String orderNo = modifyOrderDTO.getOrderNo();
			// 主订单信息
	        OrderEntity oe = modifyOrderDTO.getOrderEntity();
			// 获取订单来源信息
	        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
	        // 租客商品信息
	        RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
	        //发送MQ
	        OrderPlatModifyMq mq = new OrderPlatModifyMq();
			mq.setOrderNo(orderNo);
			mq.setMemNo(Integer.valueOf(modifyOrderDTO.getMemNo()));
			mq.setCategory(oe.getCategory() != null ? String.valueOf(oe.getCategory()):null);
			mq.setBusinessParentType(osse.getBusinessParentType());
			mq.setBusinessChildType(osse.getBusinessChildType());
			mq.setPlatformParentType(osse.getPlatformParentType());
			mq.setPlatformChildType(osse.getPlatformChildType());
			mq.setRentTime(modifyOrderRiskService.localDateTime2Date(modifyOrderDTO.getRentTime()));
			mq.setRevertTime(modifyOrderRiskService.localDateTime2Date(modifyOrderDTO.getRevertTime()));
	        if (renterGoodsDetailDTO != null) {
	        	mq.setCarNo(renterGoodsDetailDTO.getCarNo());
	        }
	        log.info("ModifyOrderRabbitMQService.sendOrderPlatModifyMq mqparam=[{}]", JSON.toJSONString(mq));
			OrderMessage orderMessage = OrderMessage.builder().build();
	        orderMessage.setMessage(mq);
	        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.PLATFORM_ORDER_MODIFY.exchange,NewOrderMQActionEventEnum.PLATFORM_ORDER_MODIFY.routingKey,orderMessage);
		} catch (Exception e) {
			log.error("ModifyOrderRabbitMQService.sendOrderPlatModifyMq error:", e);
			Cat.logError("ModifyOrderRabbitMQService.sendOrderPlatModifyMq error", e);
		}
	}
	
	
	/**
	 * 订单换车成功事件
	 * @param modifyOrderDTO
	 */
	public void sendOrderChangeCarMq(ModifyOrderDTO modifyOrderDTO) {
		try {
			// 主订单信息
	        OrderEntity oe = modifyOrderDTO.getOrderEntity();
			String orderNo = modifyOrderDTO.getOrderNo();
			// 获取订单来源信息
	        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
	        // 租客商品信息
	        RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
	        //发送MQ
	        OrderChangeCarMq mq = new OrderChangeCarMq();
			mq.setOrderNo(orderNo);
			mq.setMemNo(Integer.valueOf(modifyOrderDTO.getMemNo()));
			mq.setCategory(oe.getCategory() != null ? String.valueOf(oe.getCategory()):null);
			mq.setBusinessParentType(osse.getBusinessParentType());
			mq.setBusinessChildType(osse.getBusinessChildType());
			mq.setPlatformParentType(osse.getPlatformParentType());
			mq.setPlatformChildType(osse.getPlatformChildType());
			mq.setRentTime(modifyOrderRiskService.localDateTime2Date(modifyOrderDTO.getRentTime()));
			mq.setRevertTime(modifyOrderRiskService.localDateTime2Date(modifyOrderDTO.getRevertTime()));
	        if (renterGoodsDetailDTO != null) {
	        	mq.setCarNo(renterGoodsDetailDTO.getCarNo());
	        }
	        log.info("ModifyOrderRabbitMQService.sendOrderChangeCarMq mqparam=[{}]", JSON.toJSONString(mq));
			OrderMessage orderMessage = OrderMessage.builder().build();
	        orderMessage.setMessage(mq);
	        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_CHANGECAR.exchange,NewOrderMQActionEventEnum.ORDER_CHANGECAR.routingKey,orderMessage);
		} catch (Exception e) {
			log.error("ModifyOrderRabbitMQService.sendOrderChangeCarMq error:", e);
			Cat.logError("ModifyOrderRabbitMQService.sendOrderChangeCarMq error", e);
		}
	}
	
	
	/**
	 * 订单延时成功事件
	 * @param modifyOrderDTO
	 */
	public void sendOrderDelayMq(ModifyOrderDTO modifyOrderDTO) {
		try {
			// 主订单信息
	        OrderEntity oe = modifyOrderDTO.getOrderEntity();
			LocalDateTime initRevertTime = oe.getExpRevertTime();
			LocalDateTime updRevertTime = modifyOrderDTO.getRevertTime();
			if (!(initRevertTime != null && updRevertTime != null && updRevertTime.isAfter(initRevertTime))) {
	        	// 非订单延时
				return;
	        }
			String orderNo = modifyOrderDTO.getOrderNo();
			// 获取订单来源信息
	        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
	        // 租客商品信息
	        RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
	        //发送MQ
	        OrderDelayMq mq = new OrderDelayMq();
			mq.setOrderNo(orderNo);
			mq.setMemNo(Integer.valueOf(modifyOrderDTO.getMemNo()));
			mq.setCategory(oe.getCategory() != null ? String.valueOf(oe.getCategory()):null);
			mq.setBusinessParentType(osse.getBusinessParentType());
			mq.setBusinessChildType(osse.getBusinessChildType());
			mq.setPlatformParentType(osse.getPlatformParentType());
			mq.setPlatformChildType(osse.getPlatformChildType());
			mq.setRentTime(modifyOrderRiskService.localDateTime2Date(modifyOrderDTO.getRentTime()));
			mq.setRevertTime(modifyOrderRiskService.localDateTime2Date(modifyOrderDTO.getRevertTime()));
	        if (renterGoodsDetailDTO != null) {
	        	mq.setCarNo(renterGoodsDetailDTO.getCarNo());
	        }
	        log.info("ModifyOrderRabbitMQService.sendOrderDelayMq mqparam=[{}]", JSON.toJSONString(mq));
			OrderMessage orderMessage = OrderMessage.builder().build();
	        orderMessage.setMessage(mq);
	        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_DELAY_SUCCESS.exchange,NewOrderMQActionEventEnum.ORDER_DELAY_SUCCESS.routingKey,orderMessage);
		} catch (Exception e) {
			log.error("ModifyOrderRabbitMQService.sendOrderDelayMq error:", e);
			Cat.logError("ModifyOrderRabbitMQService.sendOrderDelayMq error", e);
		}
	}
	
	/**
	 * 车主同意订单修改事件
	 * @param modifyOrderOwnerDTO
	 */
	public void sendOrderDelayOwnerMq(ModifyOrderOwnerDTO modifyOrderOwnerDTO) {
		try {
			String orderNo = modifyOrderOwnerDTO.getOrderNo();
			// 获取订单来源信息
	        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
	        // 获取主订单信息
			OrderEntity oe = orderService.getOrderEntity(orderNo);
			LocalDateTime initRevertTime = oe.getExpRevertTime();
			LocalDateTime updRevertTime = modifyOrderOwnerDTO.getRevertTime();
			if (!(initRevertTime != null && updRevertTime != null && updRevertTime.isAfter(initRevertTime))) {
	        	// 非订单延时
				return;
	        }
	        // 租客商品信息
			OwnerGoodsDetailDTO ownerGoodsDetailDTO = modifyOrderOwnerDTO.getOwnerGoodsDetailDTO();
			//发送MQ
			OrderDelayMq mq = new OrderDelayMq();
			mq.setOrderNo(orderNo);
			mq.setMemNo(Integer.valueOf(oe.getMemNoRenter()));
			mq.setCategory(oe.getCategory() != null ? String.valueOf(oe.getCategory()):null);
			mq.setBusinessParentType(osse.getBusinessParentType());
			mq.setBusinessChildType(osse.getBusinessChildType());
			mq.setPlatformParentType(osse.getPlatformParentType());
			mq.setPlatformChildType(osse.getPlatformChildType());
			mq.setRentTime(modifyOrderRiskService.localDateTime2Date(modifyOrderOwnerDTO.getRentTime()));
			mq.setRevertTime(modifyOrderRiskService.localDateTime2Date(modifyOrderOwnerDTO.getRevertTime()));
	        if (ownerGoodsDetailDTO != null) {
	        	mq.setCarNo(ownerGoodsDetailDTO.getCarNo());
	        }
	        log.info("ModifyOrderRabbitMQService.sendOrderDelayOwnerMq mqparam=[{}]", JSON.toJSONString(mq));
			OrderMessage orderMessage = OrderMessage.builder().build();
	        orderMessage.setMessage(mq);
	        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_DELAY_SUCCESS.exchange,NewOrderMQActionEventEnum.ORDER_DELAY_SUCCESS.routingKey,orderMessage);
		} catch (Exception e) {
			log.error("ModifyOrderRabbitMQService.sendOrderDelayOwnerMq error:", e);
			Cat.logError("ModifyOrderRabbitMQService.sendOrderDelayOwnerMq error", e);
		}
	}
}
