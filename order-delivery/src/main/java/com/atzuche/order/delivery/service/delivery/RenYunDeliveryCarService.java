package com.atzuche.order.delivery.service.delivery;

import com.atzuche.order.delivery.common.DeliveryConstants;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.enums.DeliveryTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Iterator;
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

    /**
     * 添加订单到仁云流程系统
     */
    public String addRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderVO) {

        String flowOrderMap = getFlowOrderMap(renYunFlowOrderVO);
        String result = retryDeliveryCarService.sendHttpToRenYun(DeliveryConstants.ADD_FLOW_ORDER, flowOrderMap, DeliveryTypeEnum.ADD_TYPE.getValue().intValue());
        return result;
    }

    /**
     * 更新订单到仁云流程系统
     */
    public String updateRenYunFlowOrderInfo(UpdateFlowOrderDTO updateFlowOrderDTO) {
        String flowOrderMap = getFlowOrderMap(updateFlowOrderDTO);
        String result = retryDeliveryCarService.sendHttpToRenYun(DeliveryConstants.CHANGE_FLOW_ORDER, flowOrderMap, DeliveryTypeEnum.UPDATE_TYPE.getValue().intValue());
        return result;
    }

    /**
     * 取消订单到仁云流程系统
     */
    public String cancelRenYunFlowOrderInfo(CancelFlowOrderDTO cancelFlowOrderVO) {
        String flowOrderMap = getFlowOrderMap(cancelFlowOrderVO);
        String result = retryDeliveryCarService.sendHttpToRenYun(DeliveryConstants.CANCEL_FLOW_ORDER, flowOrderMap, DeliveryTypeEnum.CANCEL_TYPE.getValue().intValue());
        return result;
    }

    /**
     * 获取参数Map
     *
     * @param object
     * @return
     */
    public String getFlowOrderMap(Serializable object) {
        Map<String, Object> flowOrderMap = CommonUtil.javaBeanToMap(object);
        if (null == flowOrderMap) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "新增配送订单转化成map失败");
        }
        Iterator<Map.Entry<String, Object>> params = flowOrderMap.entrySet().iterator();
        while (params.hasNext()) {
            Map.Entry<String, Object> entry = params.next();
            if (null == entry.getValue() || entry.getKey().equals("class") || entry.getKey().equals("sign")) {
                params.remove();
            }
        }
        String sign = CommonUtil.getSign(flowOrderMap);
        if (StringUtils.isBlank(sign)) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "获取sign失败");
        }
        flowOrderMap.put("sign", sign);
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        if (params != null) {
            for (Map.Entry<String, Object> e : flowOrderMap.entrySet()) {
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
            sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

}
