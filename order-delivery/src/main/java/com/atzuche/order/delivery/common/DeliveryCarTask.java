package com.atzuche.order.delivery.common;

import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.service.MailSendService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.RenYunDeliveryCarService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.CodeUtils;
import com.atzuche.order.delivery.utils.EmailConstants;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Future;

/**
 * @author 胡春林
 * 执行流程
 */
@Service
@Slf4j
public class DeliveryCarTask {

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

    /**
     * 添加订单到仁云流程系统
     */
    @Async
    public void addRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderDTO) {
        String result = renyunDeliveryCarService.addRenYunFlowOrderInfo(renYunFlowOrderDTO);
        if (StringUtils.isBlank(result)) {
            sendMailByType(renYunFlowOrderDTO.getServicetype(), DeliveryConstants.ADD_TYPE, DeliveryConstants.ADD_FLOW_ORDER, renYunFlowOrderDTO.getOrdernumber());
        }
    }

    /**
     * 更新订单到仁云流程系统
     */
    @Async
    public void updateRenYunFlowOrderInfo(UpdateFlowOrderDTO updateFlowOrderDTO) {
        String result = renyunDeliveryCarService.updateRenYunFlowOrderInfo(updateFlowOrderDTO);
        if (StringUtils.isBlank(result)) {
            sendMailByType(updateFlowOrderDTO.getServicetype(), DeliveryConstants.CHANGE_TYPE, DeliveryConstants.CHANGE_FLOW_ORDER, updateFlowOrderDTO.getOrdernumber());
        }
    }

    /**
     * 取消订单到仁云流程系统
     */
    @Async
    public Future<Boolean> cancelRenYunFlowOrderInfo(CancelFlowOrderDTO cancelFlowOrderDTO) {

        String result = renyunDeliveryCarService.cancelRenYunFlowOrderInfo(cancelFlowOrderDTO);
        if (StringUtils.isBlank(result)) {
            sendMailByType(cancelFlowOrderDTO.getServicetype(), DeliveryConstants.CANCEL_TYPE, DeliveryConstants.CANCEL_FLOW_ORDER, cancelFlowOrderDTO.getOrdernumber());
        }
        return new AsyncResult(true);
    }

    /**
     * 取消配送订单
     *
     * @param renterOrderNo
     * @param serviceType
     */
    @Transactional(rollbackFor = Exception.class)
    public Future<Boolean> cancelOrderDelivery(String renterOrderNo, Integer serviceType,CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        RenterOrderDeliveryEntity orderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByRenterOrderNo(renterOrderNo, serviceType);

        if (null == orderDeliveryEntity) {
            log.info("没有找到该配送订单信息，renterOrderNo：{}",renterOrderNo);
            return new AsyncResult(false);
        }
        orderDeliveryEntity.setStatus(3);
        orderDeliveryEntity.setRenterGetReturnAddr(orderDeliveryEntity.getOwnerGetReturnAddr());
        orderDeliveryEntity.setRenterGetReturnAddrLat(orderDeliveryEntity.getOwnerGetReturnAddrLat());
        orderDeliveryEntity.setRenterGetReturnAddrLon(orderDeliveryEntity.getOwnerGetReturnAddrLon());
        renterOrderDeliveryService.updateDeliveryByPrimaryKey(orderDeliveryEntity);
       return cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
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