package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.entity.orderDetailDto.ProcessRespDTO;
import com.atzuche.order.coreapi.listener.push.OrderSendMessageFactory;
import com.atzuche.order.coreapi.listener.sms.SMSOrderBaseEventService;
import com.atzuche.order.mq.enums.PushMessageTypeEnum;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 胡春林
 */
@Service
public class RemindPayIllegalCrashService {

    private Logger logger = LoggerFactory.getLogger(RemindPayIllegalCrashService.class);
    @Autowired
    SMSOrderBaseEventService smsOrderBaseEventService;
    @Autowired
    OrderSendMessageFactory orderSendMessageFactory;
    @Autowired
    FeignOrderDetailService feignOrderDetailService;

    public List<OrderDTO> findProcessOrderInfo() {

        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "每天定时查询当前进行中的订单");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDetailService.queryInProcess");
            ResponseData<ProcessRespDTO> orderResponseData = feignOrderDetailService.queryInProcess();
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            if (Objects.nonNull(orderResponseData) && orderResponseData.getResCode() != null
                    && ErrorCode.SUCCESS.getCode().equals(orderResponseData.getResCode())
                    && orderResponseData.getData() != null) {
                List<OrderDTO> orderList  = orderResponseData.getData().getOrderDTOs();
                if (CollectionUtils.isEmpty(orderList)) {
                    return new ArrayList<>();
                } else {
                    return orderList;
                }
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("执行 每天定时查询当前进行中的订单 异常", e);
            Cat.logError("执行 每天定时查询当前进行中的订单 异常", e);
        } finally {
            t.complete();
        }
        return new ArrayList<>();
    }

    public void sendShortMessageData(boolean condition, String orderNo) {
        if (condition) {
            Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.REMIND_PAY_ILLEGAL_DEPOSIT_2_RENTER.getValue(), null, null);
            smsOrderBaseEventService.sendShortMessage(map);
        }
    }

    /**
     * 未支付租车押金或违章押金
     * @param orderNo
     */
    public void sendNoPayShortMessageData(String orderNo,String typeName) {
        Map paramsMap = Maps.newHashMap();
        paramsMap.put("PayType", typeName);
        Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.CANCEL_ORDER_2_RENTER.getValue(), null, paramsMap);
        smsOrderBaseEventService.sendShortMessage(map);
        //发送push
        Map pushMap = SmsParamsMapUtil.getParamsMap(orderNo, PushMessageTypeEnum.RENTER_NO_PAY_ILLEGAL_CANCEL.getValue(), PushMessageTypeEnum.RENTER_NO_PAY_ILLEGAL_2_OWNER.getValue(), null);
        orderSendMessageFactory.sendPushMessage(pushMap);
    }

    /**
     * 距离支付结束时间只有30分钟时，如还未支付租车费用
     * @param orderNo
     */
    public void sendNoPayCarShortMessageData(String orderNo) {
        Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.CANCLE_ORDER_WARNINGF_OR_FREEDEPOSIT.getValue(), null, null);
        smsOrderBaseEventService.sendShortMessage(map);
    }

    /**
     * 车主同意后-未支付租车押金-每15分钟提醒一次
     * @param orderNo
     */
    public void sendNoPayCarCostShortMessageData(String orderNo,Map paramsMap) {
        Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.NO_EXEMPT_PREORDER_REMIND_PAYRENT.getValue(), null, paramsMap);
        smsOrderBaseEventService.sendShortMessage(map);
    }

}
