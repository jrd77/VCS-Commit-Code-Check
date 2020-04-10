package com.atzuche.order.coreapi.task;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.coreapi.entity.SmsMsgSendLogEntity;
import com.atzuche.order.coreapi.enums.ShortMessageCodeEnum;
import com.atzuche.order.coreapi.listener.push.OrderSendMessageFactory;
import com.atzuche.order.coreapi.mapper.SmsMsgSendLogMapper;
import com.atzuche.order.coreapi.service.RemindPayIllegalCrashService;
import com.atzuche.order.coreapi.utils.SMSTaskDateTimeUtils;
import com.atzuche.order.mq.enums.PushMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.utils.DateUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Maps;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 胡春林
 */
@Component
@JobHandler("remindPushPayDepositTask")
public class RemindPushPayDepositTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(RemindPushPayDepositTask.class);
    @Autowired
    OrderStatusService orderStatusService;
    @Resource
    private RemindPayIllegalCrashService remindPayIllegalCrashService;
    @Autowired
    OrderSendMessageFactory orderSendMessageFactory;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    @Resource
    SmsMsgSendLogMapper smsMsgSendLogMapper;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"RemindPushPayDepositTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消  定时器");
            XxlJobLogger.log("开始执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 定时器");
            List<OrderDTO> orderNos = remindPayIllegalCrashService.findProcessOrderInfo();
            if (CollectionUtils.isEmpty(orderNos)) {
                return SUCCESS;
            }
            for (OrderDTO violateBO : orderNos) {
                OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(violateBO.getOrderNo());
                if (orderStatusEntity.getStatus().intValue() != 0 && orderStatusEntity.getWzPayStatus().intValue() == 0 && orderStatusEntity.getStatus().intValue() < 8) {
                    SmsMsgSendLogEntity smsMsgSendLogEntity = smsMsgSendLogMapper.selectByOrderNoAndMemNo(violateBO.getOrderNo(), violateBO.getMemNoRenter(), ShortMessageCodeEnum.PUSH_DEPOSETID_COST_4_HOURS.getValue().intValue());
                    if (Objects.isNull(smsMsgSendLogEntity) || smsMsgSendLogEntity.getStatus().intValue() == 0) {
                        //没有支付违章押金 是否是凹凸自營車輛
                        boolean result = CarOwnerTypeEnum.isAuToByCode(getCarOwnerType(violateBO.getOrderNo()));
                        Map paraMap = Maps.newHashMap();
                        if (result) {
                            if (!violateBO.getExpRentTime().isAfter(LocalDateTime.now()) || !LocalDateTime.now().plusHours(2).isAfter(violateBO.getExpRentTime())) {
                                return SUCCESS;
                            }
                            paraMap.put("leftHours", 2);
                        } else {
                            if (!violateBO.getExpRentTime().isAfter(LocalDateTime.now()) || !LocalDateTime.now().plusHours(4).isAfter(violateBO.getExpRentTime())) {
                                return SUCCESS;
                            }
                            paraMap.put("leftHours", 4);
                        }
                        Map pushMap = SmsParamsMapUtil.getParamsMap(violateBO.getOrderNo(), PushMessageTypeEnum.RENTER_NO_PAY_ILLEGAL.getValue(), null, paraMap);
                        orderSendMessageFactory.sendPushMessage(pushMap);
                        SmsMsgSendLogEntity smsMsgSendLog = new SmsMsgSendLogEntity();
                        smsMsgSendLog.setMemNo(violateBO.getMemNoRenter());
                        smsMsgSendLog.setOrderNo(violateBO.getOrderNo());
                        smsMsgSendLog.setMsgType(ShortMessageCodeEnum.PRE_CAR_COST_4_HOURS.getValue().intValue());
                        smsMsgSendLog.setSendParams(JSONObject.toJSONString(paraMap));
                        smsMsgSendLog.setSendTime(LocalDateTime.now());
                        smsMsgSendLog.setStatus(1);
                        int count = smsMsgSendLog.getSendTotal() == null ? 0 : smsMsgSendLog.getSendTotal();
                        smsMsgSendLog.setSendTotal(count + 1);
                        smsMsgSendLogMapper.insertSelective(smsMsgSendLog);
                    }
                }
            }
            logger.info("结束执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 ");
            XxlJobLogger.log("结束执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 ");
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 异常:" + e);
            logger.error("执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 异常",e);
            Cat.logError("执行 您还未支付预定车辆的押金，请在X小时内完成支付。否则该订单将被取消 异常",e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }

    /**
     * 获取车主车辆类型
     * @param orderNo
     * @return
     */
    public Integer getCarOwnerType(String orderNo) {
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        RenterGoodsDetailDTO ownerGoodsDetailDTO = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo(), false);
        return ownerGoodsDetailDTO.getCarOwnerType();
    }


}
