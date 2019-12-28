package com.atzuche.delivery.service.delivery;

import com.atzuche.delivery.common.DeliveryCarTask;
import com.atzuche.delivery.common.DeliveryErrorCode;
import com.atzuche.delivery.exception.DeliveryBusinessException;
import com.atzuche.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.delivery.vo.delivery.OrderDeliveryVO;
import com.atzuche.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.atzuche.delivery.vo.delivery.UpdateFlowOrderDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 胡春林
 * 配送服务
 */
@Service
public class DeliveryCarService {

    @Autowired
    DeliveryCarTask deliveryCarTask;

    /**
     * 添加订单到仁云流程系统
     */
    public void addRenYunFlowOrderInfo(OrderDeliveryVO orderDeliveryVO) {

        if(null == orderDeliveryVO || orderDeliveryVO.getOrderDeliveryDTO() == null)
        {
            throw new DeliveryBusinessException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        if(orderDeliveryVO.getOrderDeliveryDTO().getIsNotifyRenyun().intValue() == 0)
        {
            return;
        }
        RenYunFlowOrderDTO renYunFlowOrder = orderDeliveryVO.getRenYunFlowOrderDTO();
        deliveryCarTask.addRenYunFlowOrderInfo(renYunFlowOrder);
    }

    /**
     * 更新订单到仁云流程系统
     */
    public void updateRenYunFlowOrderInfo(UpdateFlowOrderDTO updateFlowOrderDTO) {
        deliveryCarTask.updateRenYunFlowOrderInfo(updateFlowOrderDTO);
    }

    /**
     * 取消订单到仁云流程系统
     */
    public void cancelRenYunFlowOrderInfo(CancelFlowOrderDTO cancelFlowOrderDTO) {
        deliveryCarTask.cancelRenYunFlowOrderInfo(cancelFlowOrderDTO);
    }


}
