package com.atzuche.order.delivery.service.delivery;

import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.config.DeliveryRenYunConfig;
import com.atzuche.order.delivery.enums.DeliveryTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.ChangeOrderInfoDTO;
import com.atzuche.order.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 胡春林
 * 发送请求到仁云
 */
@Service
@EnableRetry
@Slf4j
public class RenYunDeliveryCarService {

    @Autowired
    RetryDeliveryCarService retryDeliveryCarService;
    @Autowired
    DeliveryRenYunConfig deliveryRenYunConfig;

    /**
     * 添加订单到仁云流程系统
     */
    public String addRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderVO) {
        try {
            String flowOrderMap = getFlowOrderMap(renYunFlowOrderVO);
            return retryDeliveryCarService.sendHttpToRenYun(deliveryRenYunConfig.ADD_FLOW_ORDER, flowOrderMap, DeliveryTypeEnum.ADD_TYPE.getValue());
        } catch (Exception e) {
            log.error("添加订单到仁云流程系统请求仁云失败，失败原因：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 更新订单到仁云流程系统
     */
    public String updateRenYunFlowOrderInfo(UpdateFlowOrderDTO updateFlowOrderDTO) {
        try {
            String flowOrderMap = getFlowOrderMap(updateFlowOrderDTO);
            return retryDeliveryCarService.sendHttpToRenYun(deliveryRenYunConfig.CHANGE_FLOW_ORDER, flowOrderMap, DeliveryTypeEnum.UPDATE_TYPE.getValue());
        } catch (Exception e) {
            log.error("更新订单到仁云流程系统请求仁云失败，失败原因：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 取消订单到仁云流程系统
     */
    public String cancelRenYunFlowOrderInfo(CancelFlowOrderDTO cancelFlowOrderVO) {
        try {
            String flowOrderMap = getFlowOrderMap(cancelFlowOrderVO);
            return retryDeliveryCarService.sendHttpToRenYun(deliveryRenYunConfig.CANCEL_FLOW_ORDER, flowOrderMap, DeliveryTypeEnum.CANCEL_TYPE.getValue());
        } catch (Exception e) {
            log.error("取消订单到仁云流程系统请求仁云失败，失败原因：{}", e.getMessage());
            return null;
        }
    }


    /**
     * 实时更新订单信息到流程系统
     */
    public String changeRenYunFlowOrderInfo(ChangeOrderInfoDTO changeOrderInfoDTO) {
        try {
            String flowOrderMap = getFlowOrderMap(changeOrderInfoDTO);
            log.info("changeRenYunFlowOrderInfo-flowOrderMap={}", flowOrderMap);
            String result = retryDeliveryCarService.sendHttpToRenYun(deliveryRenYunConfig.OTHER_CHANGE_FLOW_ORDER, flowOrderMap, DeliveryTypeEnum.CHANGE_TYPE.getValue());
            log.info("changeRenYunFlowOrderInfo-url={}", deliveryRenYunConfig.OTHER_CHANGE_FLOW_ORDER);
            return result;
        } catch (Exception e) {
            log.error("changeRenYunFlowOrderInfo实时更新订单信息到流程系统失败，changeOrderInfoDTO={},失败原因：{}", changeOrderInfoDTO, e.getMessage());
            return null;
        }
    }


    /**
     * 获取参数Map
     *
     * @param object 序列化對象
     * @return String
     */
    public String getFlowOrderMap(Serializable object) {
        Map<String, Object> flowOrderMap = CommonUtil.javaBeanToMap(object);
        if (null == flowOrderMap) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "新增配送订单转化成map失败");
        }
        flowOrderMap.entrySet().removeIf(entry -> null == entry.getValue() || entry.getKey().equals("class") || entry.getKey().equals("sign"));
        String sign = CommonUtil.getSign(flowOrderMap);
        if (StringUtils.isBlank(sign)) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "获取sign失败");
        }
        flowOrderMap.put("sign", sign);
        // 构建请求参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : flowOrderMap.entrySet()) {
            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());
            sb.append("&");
        }
        sb.substring(0, sb.length() - 1);
        return sb.toString();
    }

}
