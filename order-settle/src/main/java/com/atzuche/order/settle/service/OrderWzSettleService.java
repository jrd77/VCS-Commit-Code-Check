package com.atzuche.order.settle.service;

import com.atzuche.order.cashieraccount.service.MemberSecondSettleService;
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
    @Autowired
    private OrderWzSettleSendMqService orderWzSettleSendMqService;
    @Autowired
    private MemberSecondSettleService memberSecondSettleService;


    public void settleWzOrder(String orderNo) {
        log.info("OrderWzSettleService settleOrder orderNo [{}]", orderNo);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "违章结算服务");
        Cat.logEvent("settleOrder", orderNo);
        SettleOrdersWz settleOrders = new SettleOrdersWz();
        try {
            //1 初始化操作
            settleOrders = orderWzSettleNewService.initSettleOrders(orderNo);
            log.info("OrderSettleService settleOrders init data settleOrders [{}]", GsonUtils.toJson(settleOrders));
            Cat.logEvent("settleOrders", GsonUtils.toJson(settleOrders));

            //2 校验订单
            orderWzSettleNewService.check(settleOrders.getRenterOrder());

            //3 获取订单违章费用等信息
            log.info("OrderSettleService settleOrderFirst start [{}]", GsonUtils.toJson(settleOrders));
            orderWzSettleNewService.settleOrderFirst(settleOrders);
            log.info("OrderSettleService settleOrderFirst end [{}]", GsonUtils.toJson(settleOrders));

            //4 订单违章计算处理
            orderWzSettleNewService.settleOrderAfter(settleOrders);
            log.info("OrderSettleService settleOrderAfter over [{}]", GsonUtils.toJson(settleOrders));
            Cat.logEvent("settleOrders", GsonUtils.toJson(settleOrders));

            //5 调远程抵扣老系统历史欠款
            remoteOldSysDebtService.deductBalance(settleOrders.getRenterMemNo(), settleOrders.getTotalWzDebtAmt());

            //6 发送违章计算成功MQ
            orderWzSettleNewService.sendOrderWzSettleSuccessMq(orderNo, settleOrders.getRenterMemNo(), settleOrders.getOwnerMemNo(), settleOrders.getRenterOrder());

            //7 违章结算成功通知仁云
            orderWzSettleSendMqService.sendOrderWzSettleSuccessToRenYunMq(orderNo, settleOrders.getRenterMemNo());

            //8 记录分流标识(已车主的会员号为准。) 200616
            memberSecondSettleService.initDepositWzMemberSecondSettle(orderNo, Integer.valueOf(settleOrders.getRenterMemNo()));
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderWzSettleService settleOrder,orderNo={},", orderNo, e);
            //结算失败，更新结算标识字段。 违章押金结算失败
            OrderStatusEntity entity = orderStatusService.getByOrderNo(orderNo);
            OrderStatusEntity record = new OrderStatusEntity();
            record.setId(entity.getId());
            record.setWzSettleStatus(SettleStatusEnum.SETTL_FAIL.getCode());
            record.setWzSettleTime(LocalDateTime.now());
            //记录结算消息，错误码
            record.setSettleMsg(record.getSettleMsg() + "wz:" + e.getMessage());
            orderStatusService.updateByPrimaryKeySelective(record);
            orderWzSettleNewService.sendOrderWzSettleFailMq(orderNo, settleOrders.getRenterMemNo(), settleOrders.getOwnerMemNo());
            t.setStatus(e);
            Cat.logError("结算失败.orderNo=" + orderNo, e);
            throw new RuntimeException("违章结算失败 ,不能结算");
        } finally {
            t.complete();
        }
        log.info("settleWzOrder finish end ");
    }

}
