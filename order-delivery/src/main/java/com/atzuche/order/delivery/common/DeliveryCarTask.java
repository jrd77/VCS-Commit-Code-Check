package com.atzuche.order.delivery.common;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.delivery.config.DeliveryRenYunConfig;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.OrderScenesSourceEnum;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.service.MailSendService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.RenYunDeliveryCarService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.CodeUtils;
import com.atzuche.order.delivery.utils.EmailConstants;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.delivery.vo.delivery.ChangeOrderInfoDTO;
import com.atzuche.order.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;

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
    @Autowired
    DeliveryRenYunConfig deliveryRenYunConfig;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private RenterOrderCostService renterOrderCostService;

    /**
     * 添加订单到仁云流程系统
     */
    @Async
    public void addRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderDTO) {
    	//追加咱叔
        appendRenyunrenYunFlowOrderDTOParam(renYunFlowOrderDTO);
        // 追加参数
    	renYunFlowOrderDTO = appendRenYunFlowOrderDTO(renYunFlowOrderDTO);
        String result = renyunDeliveryCarService.addRenYunFlowOrderInfo(renYunFlowOrderDTO);
        log.info("添加订单到仁云流程系统addRenYunFlowOrderInfo,result={}",result);
        if (StringUtils.isBlank(result)) {
            sendMailByType(renYunFlowOrderDTO.getServicetype(), DeliveryConstants.ADD_TYPE, deliveryRenYunConfig.ADD_FLOW_ORDER, renYunFlowOrderDTO.getOrdernumber());
        }
    }

    /**
     * 更新订单到仁云流程系统
     */
    @Async
    public void updateRenYunFlowOrderInfo(UpdateFlowOrderDTO updateFlowOrderDTO) {
        appendRenyunUpdateFlowOrderParam(updateFlowOrderDTO);//添加参数化
        String result = renyunDeliveryCarService.updateRenYunFlowOrderInfo(updateFlowOrderDTO);
        log.info("更新仁云流程系统updateRenYunFlowOrderInfo，result={}",result);
        if (StringUtils.isBlank(result)) {
            sendMailByType(updateFlowOrderDTO.getServicetype(), DeliveryConstants.CHANGE_TYPE, deliveryRenYunConfig.CHANGE_FLOW_ORDER, updateFlowOrderDTO.getOrdernumber());
        }
    }
    /**
     * 实时更新订单信息到流程系统
     */
    @Async
    public void changeRenYunFlowOrderInfo(ChangeOrderInfoDTO changeOrderInfoDTO) {
        renyunDeliveryCarService.changeRenYunFlowOrderInfo(changeOrderInfoDTO);
    }

    /**
     * 取消订单到仁云流程系统
     */
    @Async
    public Future<Boolean> cancelRenYunFlowOrderInfo(CancelFlowOrderDTO cancelFlowOrderDTO) {

        String result = renyunDeliveryCarService.cancelRenYunFlowOrderInfo(cancelFlowOrderDTO);
        if (StringUtils.isBlank(result)) {
            sendMailByType(cancelFlowOrderDTO.getServicetype(), DeliveryConstants.CANCEL_TYPE, deliveryRenYunConfig.CANCEL_FLOW_ORDER, cancelFlowOrderDTO.getOrdernumber());
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
        cancelOtherDeliveryTypeInfo(renterOrderNo,serviceType,cancelOrderDeliveryVO);
        return cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO.getCancelFlowOrderDTO());
    }

    /**
     * 更新另一個配送信息子订单号
     */
    public void cancelOtherDeliveryTypeInfo(String renterOrderNo, Integer serviceType, CancelOrderDeliveryVO cancelOrderDeliveryVO) {
        RenterOrderDeliveryEntity orderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(cancelOrderDeliveryVO.getCancelFlowOrderDTO().getOrdernumber(), serviceType == 1 ? 2 : 1);
        if (null == orderDeliveryEntity) {
            log.info("没有找到该配送订单信息，renterOrderNo：{}", renterOrderNo);
        } else {
            if (!renterOrderNo.equals(orderDeliveryEntity.getRenterOrderNo())) {
                orderDeliveryEntity.setRenterOrderNo(renterOrderNo);
                renterOrderDeliveryService.updateDeliveryByPrimaryKey(orderDeliveryEntity);
            }
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
    
    
    /**
     * 新增仁云追加参数
     * @param renYunFlowOrderDTO
     * @return RenYunFlowOrderDTO
     */
    public RenYunFlowOrderDTO appendRenYunFlowOrderDTO(RenYunFlowOrderDTO renYunFlowOrderDTO) {
    	// 获取有效的租客子订单
    	RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(renYunFlowOrderDTO.getOrdernumber());
    	RenterGoodsDetailDTO renterGoodsDetailDTO = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo(), false);
    	renYunFlowOrderDTO.setSsaRisks(renterOrderEntity.getIsAbatement() == null ? "0":renterOrderEntity.getIsAbatement().toString());
    	if ("8".equals(renYunFlowOrderDTO.getOrderType())) {
    		// 线上长租订单
    		renYunFlowOrderDTO.setBaseInsurFlag("0");
    	} else {
    		renYunFlowOrderDTO.setBaseInsurFlag("1");
    	}
    	Integer addDriver = renterOrderEntity.getAddDriver();
    	renYunFlowOrderDTO.setExtraDriverFlag(addDriver == null || addDriver == 0 ? "0":"1");
    	renYunFlowOrderDTO.setTyreInsurFlag(renterOrderEntity.getTyreInsurFlag() == null ? "0":renterOrderEntity.getTyreInsurFlag().toString());
    	renYunFlowOrderDTO.setDriverInsurFlag(renterOrderEntity.getDriverInsurFlag() == null ? "0":renterOrderEntity.getDriverInsurFlag().toString());
    	renYunFlowOrderDTO.setCarRegNo(renterGoodsDetailDTO.getCarNo() == null ? "":String.valueOf(renterGoodsDetailDTO.getCarNo()));
        renYunFlowOrderDTO.setDayUnitPrice(renterGoodsDetailDTO.getCarGuideDayPrice()==null?"":String.valueOf(renterGoodsDetailDTO.getCarGuideDayPrice()));
    	if (StringUtils.isNotBlank(renYunFlowOrderDTO.getSceneName())) {
            renYunFlowOrderDTO.setSceneName(OrderScenesSourceEnum.getOrderScenesSource(renYunFlowOrderDTO.getSceneName()));
        }
    	return renYunFlowOrderDTO;
    }
    /*
     * @Author ZhangBin
     * @Date 2020/6/24 14:54
     * @Description: 追加参数
     *
     **/
    public void appendRenyunrenYunFlowOrderDTOParam(RenYunFlowOrderDTO renYunFlowOrderDTO){
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(renYunFlowOrderDTO.getOrdernumber());
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(renterOrderEntity.getOrderNo(), renterOrderEntity.getRenterOrderNo());
        renYunFlowOrderDTO.setRentAmt(renterOrderCostEntity.getRentCarAmount()==null?"":String.valueOf(renterOrderCostEntity.getRentCarAmount()));
    }
    /*
     * @Author ZhangBin
     * @Date 2020/6/24 14:54
     * @Description: 追加参数
     *
     **/
    public void appendRenyunUpdateFlowOrderParam(UpdateFlowOrderDTO updateFlowOrderDTO){
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(updateFlowOrderDTO.getOrdernumber());
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(renterOrderEntity.getOrderNo(), renterOrderEntity.getRenterOrderNo());
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo(), false);
        updateFlowOrderDTO.setRentAmt(renterOrderCostEntity.getRentCarAmount()==null?"":String.valueOf(renterOrderCostEntity.getRentCarAmount()));
        updateFlowOrderDTO.setDayUnitPrice(renterGoodsDetail.getCarGuideDayPrice()==null?"":String.valueOf(renterGoodsDetail.getCarGuideDayPrice()));
    }
}