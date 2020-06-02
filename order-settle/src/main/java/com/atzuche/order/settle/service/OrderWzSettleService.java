package com.atzuche.order.settle.service;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.settle.vo.req.SettleOrdersWz;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author jing.huang
 *
 */
@Service
@Slf4j
public class OrderWzSettleService {
	@Autowired
	private OrderWzSettleNewService orderWzSettleNewService;
	@Autowired
	private OrderStatusService orderStatusService;
	@Autowired
	private RemoteOldSysDebtService remoteOldSysDebtService;
	
	public void settleWzOrder(String orderNo) {
		log.info("OrderWzSettleService settleOrder orderNo [{}]",orderNo);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "违章结算服务");
        SettleOrdersWz settleOrders = new  SettleOrdersWz();
        try {
            Cat.logEvent("settleOrder",orderNo);
            //1 初始化操作 校验操作
            settleOrders = orderWzSettleNewService.initSettleOrders(orderNo);
            log.info("OrderSettleService settleOrders init data settleOrders [{}]",GsonUtils.toJson(settleOrders));
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));

            // 2 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
            boolean checkFlag = orderWzSettleNewService.check(settleOrders.getRenterOrder());
            if(!checkFlag) {
            	log.info("提前终止结算，当前违章结算状态不符合。orderNo [{}]",orderNo);
    			return;
    		}
            
            //2 无事务操作 查询租客车主费用明细 ，处理费用明细到 结算费用明细  并落库   然后平账校验
            log.info("OrderSettleService settleOrderFirst start [{}]",GsonUtils.toJson(settleOrders));
            orderWzSettleNewService.settleOrderFirst(settleOrders);
            log.info("OrderSettleService settleOrderFirst end [{}]",GsonUtils.toJson(settleOrders));
            

            //3 事务操作结算主逻辑  //开启事务
            orderWzSettleNewService.settleOrderAfter(settleOrders);
            log.info("OrderSettleService settleOrderAfter over [{}]",GsonUtils.toJson(settleOrders));
            
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));
            // 调远程抵扣老系统历史欠款
            remoteOldSysDebtService.deductBalance(settleOrders.getRenterMemNo(), settleOrders.getTotalWzDebtAmt());
            orderWzSettleNewService.sendOrderWzSettleSuccessMq(orderNo,settleOrders.getRenterMemNo(),settleOrders.getOwnerMemNo());
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderWzSettleService settleOrder,orderNo={},",orderNo, e);
            //结算失败，更新结算标识字段。 违章押金结算失败
            OrderStatusEntity entity = orderStatusService.getByOrderNo(orderNo);
            if(null != entity && entity.getIsDetainWz() != OrderConstant.YES) {
                OrderStatusEntity record = new OrderStatusEntity();
                record.setId(entity.getId());
                record.setWzSettleStatus(SettleStatusEnum.SETTL_FAIL.getCode());
                record.setWzSettleTime(LocalDateTime.now());
                orderStatusService.updateByPrimaryKeySelective(record);
            }
            orderWzSettleNewService.sendOrderWzSettleFailMq(orderNo,settleOrders.getRenterMemNo(),settleOrders.getOwnerMemNo());
            t.setStatus(e);
            Cat.logError("结算失败.orderNo="+orderNo,e);
            throw new RuntimeException("违章结算失败 ,不能结算");
        } finally {
            t.complete();
        }
        log.info("settleWzOrder finish end " );
	}

}
