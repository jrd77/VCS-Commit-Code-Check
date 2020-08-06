package com.atzuche.order.delivery.common;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.delivery.config.DeliveryRenYunConfig;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.service.MailSendService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryModeService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.service.delivery.RenYunDeliveryCarService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.CodeUtils;
import com.atzuche.order.delivery.utils.EmailConstants;
import com.atzuche.order.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeliveryAsyncProxy {
    @Autowired
    RenYunDeliveryCarService renyunDeliveryCarService;
    @Autowired
    MailSendService mailSendService;
    @Autowired
    HandoverCarService handoverCarService;
    @Autowired
    CodeUtils codeUtils;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    DeliveryRenYunConfig deliveryRenYunConfig;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private RenterOrderDeliveryModeService renterOrderDeliveryModeService;
    @Autowired
    private RenterOrderCostService renterOrderCostService;
    @Autowired
    private DeliveryCarInfoPriceService deliveryCarInfoPriceService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;

    @Async
    public void addRenYunFlowOrderInfoproxy(RenYunFlowOrderDTO renYunFlowOrderDTO){
        String result = renyunDeliveryCarService.addRenYunFlowOrderInfo(renYunFlowOrderDTO);
        log.info("添加订单到仁云流程系统addRenYunFlowOrderInfo,renYunFlowOrderDTO={},result={}", JSON.toJSONString(renYunFlowOrderDTO),result);
        if (StringUtils.isBlank(result)) {
            sendMailByType(renYunFlowOrderDTO.getServicetype(), DeliveryConstants.ADD_TYPE, deliveryRenYunConfig.ADD_FLOW_ORDER, renYunFlowOrderDTO.getOrdernumber());
        }
    }

    @Async
    public void updateRenYunFlowOrderInfoProxy(UpdateFlowOrderDTO updateFlowOrderDTO) {
        String result = renyunDeliveryCarService.updateRenYunFlowOrderInfo(updateFlowOrderDTO);
        log.info("更新仁云流程系统updateRenYunFlowOrderInfo，updateFlowOrderDTO={},result={}",JSON.toJSONString(updateFlowOrderDTO),result);
        if (StringUtils.isBlank(result)) {
            sendMailByType(updateFlowOrderDTO.getServicetype(), DeliveryConstants.CHANGE_TYPE, deliveryRenYunConfig.CHANGE_FLOW_ORDER, updateFlowOrderDTO.getOrdernumber());
        }
    }

    /**
     * 发送email
     */
    public void sendMailByType(String serviceType, String actionType, String url, String orderNumber) {
        try {
            String typeName = ServiceTypeEnum.TAKE_TYPE.equals(serviceType) ? DeliveryConstants.SERVICE_TAKE_TEXT : ServiceTypeEnum.BACK_TYPE.equals(serviceType) ? DeliveryConstants.SERVICE_BACK_TEXT : serviceType;
            String interfaceName = "";
            switch (actionType) {
                case DeliveryConstants.ADD_TYPE:
                    interfaceName = DeliveryConstants.ADD_INTERFACE_NAME;
                    break;
                case DeliveryConstants.CHANGE_TYPE:
                    interfaceName = DeliveryConstants.CANCEL_INTERFACE_NAME;
                    break;
                case DeliveryConstants.CANCEL_TYPE:
                    interfaceName = DeliveryConstants.CHANGE_INTERFACE_NAME;
                    break;
                default:
                    break;
            }
            if (mailSendService != null) {
                String[] toEmails = DeliveryConstants.EMAIL_PARAMS.split(",");
                String content = String.format(EmailConstants.PROCESS_SYSTEM_NOTICE_CONTENT, orderNumber, interfaceName, url, typeName);
                mailSendService.sendSimpleEmail(toEmails, EmailConstants.PROCESS_SYSTEM_NOTICE_SUBJECT, content);
            }
        } catch (Exception e) {
            log.info("发送邮件失败---->>>>{}:", e);
        }
    }
}
