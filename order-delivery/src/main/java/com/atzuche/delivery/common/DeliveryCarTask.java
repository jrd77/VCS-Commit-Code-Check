package com.atzuche.delivery.common;

import com.atzuche.delivery.service.delivery.RenYunDeliveryCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.atzuche.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.delivery.vo.delivery.RenYunFlowOrderDTO;

/**
 * @author 胡春林
 * 执行流程
 */
@Component
public class DeliveryCarTask {

    @Autowired
    RenYunDeliveryCarService renyunDeliveryCarService;

    /**
     * 添加订单到仁云流程系统
     */
    @Async
    public void addRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderVO) {
        renyunDeliveryCarService.addRenYunFlowOrderInfo(renYunFlowOrderVO);
    }

    /**
     * 更新订单到仁云流程系统
     */
    @Async
    public void updateRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderVO) {
        renyunDeliveryCarService.updateRenYunFlowOrderInfo(renYunFlowOrderVO);
    }

    /**
     * 取消订单到仁云流程系统
     */
    @Async
    public void cancelRenYunFlowOrderInfo(CancelFlowOrderDTO cancelFlowOrderVO) {
        renyunDeliveryCarService.cancelRenYunFlowOrderInfo(cancelFlowOrderVO);
    }
}
