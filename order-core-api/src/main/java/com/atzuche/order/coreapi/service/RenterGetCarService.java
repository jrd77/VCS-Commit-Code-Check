package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.vo.req.GetCarReqVO;
import com.atzuche.order.coreapi.submitOrder.exception.RefuseOrderCheckException;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.autoyol.commons.web.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租客取车操作
 *
 * @author pengcheng.fu
 * @date 2020/2/12 11:11
 */
@Service
public class RenterGetCarService {

    private static Logger logger = LoggerFactory.getLogger(RenterGetCarService.class);

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderFlowService orderFlowService;




    @Transactional(rollbackFor = Exception.class)
    public void pickUpCar(GetCarReqVO reqVO){
        //校验
        Integer status = orderStatusService.getStatusByOrderNo(reqVO.getOrderNo());
        if(null == status) {
            logger.error("Order status is empty. orderNo:[{}]", reqVO.getOrderNo());
            throw new RefuseOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }

        if(status != OrderStatusEnum.TO_GET_CAR.getStatus()) {
            logger.error("PickUpCar operation is not allowed for order . status:[{}],orderNo:[{}]", status, reqVO.getOrderNo());
            throw new RefuseOrderCheckException(ErrorCode.TRANS_STATUS_CHANGE_CAN_NOT_CANCEL);
        }

        //订单状态变更
        OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
        orderStatusEntity.setOrderNo(reqVO.getOrderNo());
        orderStatusEntity.setStatus(OrderStatusEnum.TO_RETURN_CAR.getStatus());
        orderStatusEntity.setUpdateOp(reqVO.getOperatorName());
        int result = orderStatusService.updateRenterOrderByOrderNo(orderStatusEntity);
        logger.info("Update order status.result:[{}], orderNo:[{}]", result, reqVO.getOrderNo());
        //添加trans_flow
        orderFlowService.inserOrderStatusChangeProcessInfo(reqVO.getOrderNo(), OrderStatusEnum.TO_RETURN_CAR);
    }


}
