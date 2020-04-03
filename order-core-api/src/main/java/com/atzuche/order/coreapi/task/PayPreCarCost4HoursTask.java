package com.atzuche.order.coreapi.task;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.mapper.CashierMapper;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.coreapi.entity.SmsMsgSendLogEntity;
import com.atzuche.order.coreapi.enums.ShortMessageCodeEnum;
import com.atzuche.order.coreapi.listener.push.OrderSendMessageFactory;
import com.atzuche.order.coreapi.mapper.SmsMsgSendLogMapper;
import com.atzuche.order.coreapi.service.RemindPayIllegalCrashService;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
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
@JobHandler("payPreCarCost4HoursTask")
public class PayPreCarCost4HoursTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(PayPreCarCost4HoursTask.class);
    @Resource
    private RemindPayIllegalCrashService remindPayIllegalCrashService;
    @Resource
    CashierMapper cashierMapper;
    @Autowired
    OrderSendMessageFactory orderSendMessageFactory;
    @Resource
    SmsMsgSendLogMapper smsMsgSendLogMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "预支付4小时内未完成订单退还租车押金");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD, "payPreCarCost4HoursTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM, null);
            logger.info("开始执行 预支付4小时内未完成订单退还租车押金  定时器");
            XxlJobLogger.log("开始执行 预支付4小时内未完成订单退还租车押金");
            List<OrderDTO> orderNos = remindPayIllegalCrashService.findProcessOrderInfo();
            if (CollectionUtils.isEmpty(orderNos)) {
                return SUCCESS;
            }
            for (OrderDTO violateBO : orderNos) {
                CashierEntity cashierEntity = cashierMapper.getPayDeposit(violateBO.getOrderNo(), violateBO.getMemNoRenter(), "01", "02");
                if (Objects.isNull(cashierEntity)) {
                    return SUCCESS;
                }
                LocalDateTime localDateTime = DateUtils.parseLocalDateTime(cashierEntity.getPayTime(), DateUtils.DATE_DEFAUTE);
                if (localDateTime.isBefore(LocalDateTime.now()) && localDateTime.plusHours(4).isAfter(localDateTime)) {
                    SmsMsgSendLogEntity smsMsgSendLogEntity = smsMsgSendLogMapper.selectByOrderNoAndMemNo(violateBO.getOrderNo(), violateBO.getMemNoRenter(), ShortMessageCodeEnum.PRE_CAR_COST_4_HOURS.getValue().intValue());
                    if(Objects.isNull(smsMsgSendLogEntity) || smsMsgSendLogEntity.getStatus().intValue() == 0)
                    {
                        Map paramsMap = Maps.newHashMap();
                        paramsMap.put("RentCarDeposit", cashierEntity.getPayAmt());
                        Map map = SmsParamsMapUtil.getParamsMap(violateBO.getOrderNo(), ShortMessageTypeEnum.REFUND_COST_SUCCESS_2_RENTER.getValue(), null, paramsMap);
                        orderSendMessageFactory.sendShortMessage(map);
                        SmsMsgSendLogEntity smsMsgSendLog = new SmsMsgSendLogEntity();
                        smsMsgSendLog.setMemNo(violateBO.getMemNoRenter());
                        smsMsgSendLog.setOrderNo(violateBO.getOrderNo());
                        smsMsgSendLog.setMsgType(ShortMessageCodeEnum.PRE_CAR_COST_4_HOURS.getValue().intValue());
                        smsMsgSendLog.setSendParams(JSONObject.toJSONString(map));
                        smsMsgSendLog.setSendTime(LocalDateTime.now());
                        smsMsgSendLog.setStatus(1);
                        int count = smsMsgSendLog.getSendTotal() == null ? 0 : smsMsgSendLog.getSendTotal();
                        smsMsgSendLog.setSendTotal(count+1);
                        smsMsgSendLogMapper.insertSelective(smsMsgSendLog);
                    }
                }
            }
            logger.info("结束执行 预支付4小时内未完成订单退还租车押金 ");
            XxlJobLogger.log("结束执行 预支付4小时内未完成订单退还租车押金 ");
            t.setStatus(Transaction.SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 预支付4小时内未完成订单退还租车押金 异常:" + e);
            logger.error("执行 预支付4小时内未完成订单退还租车押金 异常", e);
            Cat.logError("执行 预支付4小时内未完成订单退还租车押金 异常", e);
            t.setStatus(e);
            return new ReturnT(FAIL.getCode(), e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
