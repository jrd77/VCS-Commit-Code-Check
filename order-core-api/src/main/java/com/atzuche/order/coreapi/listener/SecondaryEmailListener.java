/**
 * 
 */
package com.atzuche.order.coreapi.listener;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.cashieraccount.service.remote.AutoSecondOpenRemoteService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.coreapi.entity.OrderSecondOpenEntity;
import com.atzuche.order.coreapi.service.OrderSecondOpenService;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.event.rabbit.neworder.OrderChangeCarMq;
import com.autoyol.event.rabbit.neworder.OrderCreateMq;
import com.autoyol.event.rabbit.neworder.OrderSettlementMq;
import com.autoyol.event.rabbit.neworder.OrderWzSettlementMq;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

/**
 * @author jing.huang 二清邮件提醒
 */
@Component
public class SecondaryEmailListener {
	private static final Logger logger = LoggerFactory.getLogger(SecondaryEmailListener.class);

	private static final String CREATE_ORDER_QUEUE = "action.order.secondary.settlement.noOpenVir.createOrder.queue";
	private static final String CHANGE_CAR_QUEUE = "action.order.secondary.settlement.noOpenVir.changeCar.queue";
	private static final String SETTLE_QUEUE = "action.order.secondary.settlement.noOpenVir.settle.queue";
	private static final String WZ_SETTLE_QUEUE = "action.order.secondary.settlement.noOpenVir.wzSettle.queue";
	
    @Autowired
	AutoSecondOpenRemoteService autoSecondOpenRemoteService;
    @Autowired
    OrderSecondOpenService orderSecondOpenService;
    @Autowired
    OwnerGoodsService ownerGoodsService;
    @Autowired
    OwnerMemberService ownerMemberService;
	
    //下单，换车，结算+违章结算
//	@RabbitListener(queues = CREATE_ORDER_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = CREATE_ORDER_QUEUE, durable = "true"),
            exchange = @Exchange(value = "auto-order-action", durable = "true", type = "topic"), key = "action.order.create")
    },containerFactory = "orderRabbitListenerContainerFactory")
	public void processCreateOrder(Message message) {
		//接收消息转换为对象
		OrderSecondOpenEntity record = new OrderSecondOpenEntity();
		Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "二清未开户mq");

		try {
			OrderMessage orderMessage = JSONObject.parseObject(message.getBody(), OrderMessage.class);
			JSONObject jsonObject = (JSONObject)orderMessage.getMessage();
	        OrderCreateMq orderCreateMq = JSON.parseObject(jsonObject.toJSONString(),OrderCreateMq.class);
	        if(orderCreateMq == null) {
	        	logger.info("未接收到的参数,队列名[{}]",CREATE_ORDER_QUEUE);
	        	return;
	        }else {
	        	logger.info("接收到的参数[{}]",GsonUtils.toJson(orderCreateMq));
	        }
	        
			Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "SecondaryEmailListener.process");
			Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, GsonUtils.toJson(orderCreateMq));
			
			//数据封装
			Integer carNo = orderCreateMq.getCarNo();
			String orderNo = orderCreateMq.getOrderNo();
			Integer ownerNo = orderCreateMq.getOwnerMemNo();
			Date rentTime = orderCreateMq.getRentTime();
			Date revertTime = orderCreateMq.getRevertTime();
			
			handle(record, rentTime,revertTime, carNo, orderNo, ownerNo,"下单");

			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			logger.error("二清未开户mq 异常,mailJson:[{}] , e :[{}]", GsonUtils.toJson(record), e);
			t.setStatus(e);
			Cat.logError("二清未开户mq 异常, e {}", e);

		} finally {
			t.complete();
		}
		logger.info("secondaryEmailListener process end ");
	}
	
//	@RabbitListener(queues = CHANGE_CAR_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = CHANGE_CAR_QUEUE, durable = "true"),
            exchange = @Exchange(value = "auto-order-action", durable = "true", type = "topic"), key = "action.order.changeCar")
    },containerFactory = "orderRabbitListenerContainerFactory")
	public void processChangeCar(Message message) {
		//接收消息转换为对象
		OrderSecondOpenEntity record = new OrderSecondOpenEntity();
		Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "二清未开户mq");

		try {
			OrderMessage orderMessage = JSONObject.parseObject(message.getBody(), OrderMessage.class);
			JSONObject jsonObject = (JSONObject)orderMessage.getMessage();
			OrderChangeCarMq orderCreateMq = JSON.parseObject(jsonObject.toJSONString(),OrderChangeCarMq.class);
			if(orderCreateMq == null) {
				logger.info("未接收到的参数,队列名[{}]",CHANGE_CAR_QUEUE);
	        	return;
	        }else {
	        	logger.info("接收到的参数[{}]",GsonUtils.toJson(orderCreateMq));
	        }
			
			Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "SecondaryEmailListener.process");
			Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, GsonUtils.toJson(orderCreateMq));
			
			//数据封装
			Integer carNo = orderCreateMq.getCarNo();
			String orderNo = orderCreateMq.getOrderNo();
			Integer ownerNo = orderCreateMq.getOwnerMemNo();
			Date rentTime = orderCreateMq.getRentTime();
			Date revertTime = orderCreateMq.getRevertTime();
			
			handle(record, rentTime,revertTime, carNo, orderNo, ownerNo,"换车");

			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			logger.error("二清未开户mq 异常,mailJson:[{}] , e :[{}]", GsonUtils.toJson(record), e);
			t.setStatus(e);
			Cat.logError("二清未开户mq 异常, e {}", e);

		} finally {
			t.complete();
		}
		logger.info("secondaryEmailListener process end ");
	}
	
//	@RabbitListener(queues = SETTLE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = SETTLE_QUEUE, durable = "true"),
            exchange = @Exchange(value = "auto-order-action", durable = "true", type = "topic"), key = "action.order.settlement.success")
    },containerFactory = "orderRabbitListenerContainerFactory")
	public void processSettle(Message message) {
		//接收消息转换为对象
		OrderSecondOpenEntity record = new OrderSecondOpenEntity();
		Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "二清未开户mq");

		try {
			OrderMessage orderMessage = JSONObject.parseObject(message.getBody(), OrderMessage.class);
			JSONObject jsonObject = (JSONObject)orderMessage.getMessage();
			OrderSettlementMq orderCreateMq = JSON.parseObject(jsonObject.toJSONString(),OrderSettlementMq.class);
			if(orderCreateMq == null) {
				logger.info("未接收到的参数,队列名[{}]",SETTLE_QUEUE);
	        	return;
	        }else {
	        	logger.info("接收到的参数[{}]",GsonUtils.toJson(orderCreateMq));
	        }
			
			Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "SecondaryEmailListener.process");
			Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, GsonUtils.toJson(orderCreateMq));
			
			//数据封装
			Integer carNo = orderCreateMq.getCarNo();
			String orderNo = orderCreateMq.getOrderNo();
			Integer ownerNo = orderCreateMq.getOwnerMemNo();
			Date rentTime = orderCreateMq.getRentTime();
			Date revertTime = orderCreateMq.getRevertTime();
			
			handle(record, rentTime,revertTime, carNo, orderNo, ownerNo,"租车结算");

			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			logger.error("二清未开户mq 异常,mailJson:[{}] , e :[{}]", GsonUtils.toJson(record), e);
			t.setStatus(e);
			Cat.logError("二清未开户mq 异常, e {}", e);

		} finally {
			t.complete();
		}
		logger.info("secondaryEmailListener process end ");
	}
	
	
//	@RabbitListener(queues = WZ_SETTLE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = WZ_SETTLE_QUEUE, durable = "true"),
            exchange = @Exchange(value = "auto-order-action", durable = "true", type = "topic"), key = "action.order.wz.settlement.success")
    },containerFactory = "orderRabbitListenerContainerFactory")
	public void process(Message message) {
		//接收消息转换为对象
		OrderSecondOpenEntity record = new OrderSecondOpenEntity();
		Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "二清未开户mq");

		try {
			OrderMessage orderMessage = JSONObject.parseObject(message.getBody(), OrderMessage.class);
			JSONObject jsonObject = (JSONObject)orderMessage.getMessage();
			OrderWzSettlementMq orderCreateMq = JSON.parseObject(jsonObject.toJSONString(),OrderWzSettlementMq.class);
			if(orderCreateMq == null) {
				logger.info("未接收到的参数,队列名[{}]",WZ_SETTLE_QUEUE);
	        	return;
	        }else {
	        	logger.info("接收到的参数[{}]",GsonUtils.toJson(orderCreateMq));
	        }
			
			Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "SecondaryEmailListener.process");
			Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, GsonUtils.toJson(orderCreateMq));
			
			//数据封装
			Integer carNo = orderCreateMq.getCarNo();
			String orderNo = orderCreateMq.getOrderNo();
			Integer ownerNo = orderCreateMq.getOwnerMemNo();
			Date rentTime = orderCreateMq.getRentTime();
			Date revertTime = orderCreateMq.getRevertTime();
			
			handle(record, rentTime,revertTime, carNo, orderNo, ownerNo,"违章结算");

			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			logger.error("二清未开户mq 异常,mailJson:[{}] , e :[{}]", GsonUtils.toJson(record), e);
			t.setStatus(e);
			Cat.logError("二清未开户mq 异常, e {}", e);

		} finally {
			t.complete();
		}
		logger.info("secondaryEmailListener process end ");
	}
	
	
	
	/**
	 * 公共处理逻辑
	 * @param record
	 * @param orderCreateMq
	 * @param carNo
	 * @param orderNo
	 * @param ownerNo
	 */
	private void handle(OrderSecondOpenEntity record, Date rentTime,Date revertTime, Integer carNo, String orderNo,Integer ownerNo,String savePoint) {
		record.setCarNo(String.valueOf(carNo));
		record.setOrderNo(orderNo);
		record.setRentTime(LocalDateTimeUtils.dateToStringDefault(rentTime));
		record.setRevertTime(LocalDateTimeUtils.dateToStringDefault(revertTime));
		record.setOwnerNo(String.valueOf(ownerNo));
		record.setSavePoint(savePoint);
		
		OwnerGoodsEntity goods = ownerGoodsService.getOwnerGoodsByCarNoAndOrderNo(carNo, orderNo);
		if(goods != null) {
			String carPlateNum = goods.getCarPlateNum();
			String carOwnerType = CarOwnerTypeEnum.getNameByCode(goods.getCarOwnerType());
			record.setPlateNum(carPlateNum);
			record.setCarOwnerType(carOwnerType);
		}
		
		OwnerMemberEntity members = ownerMemberService.queryOwnerMemberEntityByOrderNoAndOwnerNo(orderNo, String.valueOf(ownerNo));
		if(members != null) {
			String ownerPhone = members.getPhone();
			record.setOwnerPhone(ownerPhone);
		}
		
		//对数据的处理。
//		boolean isOpenVir = autoSecondOpenRemoteService.checkOwnerIsOpenVir(record.getOwnerNo());
		
		//仅仅测试
		boolean isOpenVir = false;
		int isOpenVirInt = isOpenVir?1:0;
		record.setIsOpenVir(isOpenVirInt);
		orderSecondOpenService.insert(record);
	}

}
