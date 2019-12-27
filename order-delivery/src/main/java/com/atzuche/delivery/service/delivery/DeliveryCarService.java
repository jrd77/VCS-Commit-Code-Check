package com.atzuche.delivery.service.delivery;

import com.atzuche.delivery.common.DeliveryCarTask;
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
    public void addRenYunFlowOrderInfo() {
        deliveryCarTask.addRenYunFlowOrderInfo(null);
    }

    /**
     * 更新订单到仁云流程系统
     */
    public void updateRenYunFlowOrderInfo() {
        deliveryCarTask.updateRenYunFlowOrderInfo(null);
    }

    /**
     * 取消订单到仁云流程系统
     */
    public void cancelRenYunFlowOrderInfo() {
        deliveryCarTask.cancelRenYunFlowOrderInfo(null);
    }


}
