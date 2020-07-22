package com.atzuche.order.settle.service;

import com.aliyun.mns.client.AsyncCallback;
import com.aliyun.mns.model.Message;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.commons.JsonUtil;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterwz.aliyunmq.AliyunMnsService;
import com.atzuche.order.renterwz.entity.MqSendFeelbackLogEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.enums.WzCostEnums;
import com.atzuche.order.renterwz.service.MqSendFeelbackLogService;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.renterwz.service.RenyunSendIllegalInfoLogService;
import com.atzuche.order.settle.dto.mq.SendWzSettleSuccessMqToRenyunDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author pengcheng.fu
 * @date 2020/7/22 17:31
 */

@Service
@Slf4j
public class OrderWzSettleSendMqService {

    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired
    private AccountRenterWzDepositCostNoTService accountRenterWzDepositCostNoTService;
    @Autowired
    private RenyunSendIllegalInfoLogService renyunSendIllegalInfoLogService;
    @Autowired
    private RenterOrderWzCostDetailService renterOrderWzCostDetailService;
    @Autowired
    private MqSendFeelbackLogService mqSendFeelbackLogService;

    @Autowired
    private AliyunMnsService aliyunMnsService;
    @Value("${com.autoyol.mns.queue.auto-order-settle-info-queue}")
    private String queueNameTransSettel;



    /**
     * 违章结算成功后通知仁云流程系统
     *
     * @param orderNo 订单号
     */
    public void sendOrderWzSettleSuccessToRenYunMq(String orderNo, String renterMemNo) {
        SendWzSettleSuccessMqToRenyunDTO renyunMq = new SendWzSettleSuccessMqToRenyunDTO();
        renyunMq.setOrderno(orderNo);
        renyunMq.setWzcode(renyunSendIllegalInfoLogService.getWzCodeByOrderNo(orderNo));

        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        renyunMq.setWzdstate(Objects.nonNull(orderStatusEntity) && orderStatusEntity.getWzRefundStatus() == OrderConstant.YES ? "已退款" : "待退款");

        AccountRenterWzDepositEntity depositEntity = accountRenterWzDepositNoTService.getAccountRenterWZDeposit(orderNo, renterMemNo);
        if(Objects.nonNull(depositEntity)) {
            if(Objects.nonNull(depositEntity.getIsAuthorize())) {
                if(depositEntity.getIsAuthorize() == OrderConstant.ONE || depositEntity.getIsAuthorize() == OrderConstant.TWO) {
                    renyunMq.setWzdeposit(String.valueOf(depositEntity.getAuthorizeDepositAmt() + depositEntity.getCreditPayAmt()));
                } else {
                    renyunMq.setWzdeposit(String.valueOf(depositEntity.getShishouDeposit()));
                }
            } else {
                renyunMq.setWzdeposit(String.valueOf(depositEntity.getShishouDeposit()));
            }
        } else {
            renyunMq.setWzdeposit("0");
        }
        AccountRenterWzDepositCostEntity entity = accountRenterWzDepositCostNoTService.queryWzDeposit(orderNo,renterMemNo);
        if(Objects.nonNull(entity) && Integer.parseInt(renyunMq.getWzdeposit()) > 0) {
            int syWzdeposit = Integer.parseInt(renyunMq.getWzdeposit()) + entity.getYingkouAmt();
            renyunMq.setSywzdeposit(String.valueOf(syWzdeposit));
        } else {
            renyunMq.setSywzdeposit("0");
        }

        List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetails = renterOrderWzCostDetailService.queryInfosByOrderNo(orderNo);
        if(CollectionUtils.isEmpty(renterOrderWzCostDetails)) {
            renyunMq.setClaim("0");
            renyunMq.setOther("0");
        } else {
            int claim = renterOrderWzCostDetails.stream().filter(wzd -> StringUtils.equals(wzd.getCostCode(),
                    WzCostEnums.INSURANCE_CLAIM.getCode())).mapToInt(RenterOrderWzCostDetailEntity::getAmount).sum();

            int other = renterOrderWzCostDetails.stream().filter(wzd -> StringUtils.equals(wzd.getCostCode(),
                    WzCostEnums.WZ_OTHER_FINE.getCode())).mapToInt(RenterOrderWzCostDetailEntity::getAmount).sum();

            renyunMq.setClaim(String.valueOf(claim));
            renyunMq.setOther(String.valueOf(other));
        }
        // 发送mq
        aliyunMnsService.asyncSend̨MessageToQueue(JsonUtil.toJson(renyunMq), queueNameTransSettel, true,new AsyncCallback<Message>() {
            @Override
            public void onSuccess(Message result) {
                String messageId=result.getMessageId();
                MqSendFeelbackLogEntity log=new MqSendFeelbackLogEntity();
                log.setMsgId(messageId);
                log.setMsg(JsonUtil.toJson(renyunMq));
                log.setCreateTime(new Date());
                log.setQueueName(queueNameTransSettel);
                log.setStatus("01");
                log.setSendTime(new Date());
                mqSendFeelbackLogService.saveMqSendFeelbackLog(log);
            }
            @Override
            public void onFail(Exception ex) {
                log.error("Send mq to renyun err.", ex);
            }
        });
    }
}
