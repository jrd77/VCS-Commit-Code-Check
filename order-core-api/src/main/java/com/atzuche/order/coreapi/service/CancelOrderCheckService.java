package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.coreapi.submitOrder.exception.CancelOrderCheckException;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * CancelOrderController 相关业务校验
 *
 * @author pengcheng.fu
 * @date 2020/2/3 14:30
 */

@Service
public class CancelOrderCheckService {

    private static Logger logger = LoggerFactory.getLogger(CancelOrderCheckService.class);

    @Resource
    private RenterOrderService renterOrderService;

    @Resource
    private OrderStatusService orderStatusService;


    /**
     * 车主/租客取消订单校验
     *
     * @param orderNo 主订单号
     * @param isConsoleInvoke  是否是管理后台请求操作:true,是 false,否
     */
    public void checkCancelOrder(String orderNo, boolean isConsoleInvoke) {

        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(null == renterOrderEntity) {
            logger.error("No valid renter order found. orderNo:[{}]", orderNo);
            throw new CancelOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }

        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if(null != orderStatusEntity) {
            //已结束的订单不能取消
            if(null != orderStatusEntity.getStatus() && OrderStatusEnum.CLOSED.getStatus() == orderStatusEntity.getStatus()) {
                throw new CancelOrderCheckException(ErrorCode.TRANS_CANCEL_DUPLICATE);
            }

            if(!isConsoleInvoke) {
                //进行中的订单不能取消
                if(orderStatusEntity.getStatus() >= OrderStatusEnum.TO_RETURN_CAR.getStatus()
                        && null != orderStatusEntity.getIsDispatch()
                        && orderStatusEntity.getIsDispatch() == OrderConstant.NO) {
                    throw new CancelOrderCheckException(ErrorCode.TRANS_FINISHED_CAN_NOT_CANCEL);
                }

                //车主确认且租期已开始不能取消
                if(orderStatusEntity.getStatus() > OrderStatusEnum.TO_CONFIRM.getStatus()
                        && !LocalDateTime.now().isBefore(renterOrderEntity.getExpRentTime())) {
                    throw new CancelOrderCheckException(ErrorCode.TRANS_IN_PROCESSING_CAN_NOT_CACEL);
                }
            }
        }
    }


    public void checkOwnerCancelOrder(OrderStatusEntity orderStatusEntity) {

        if(null != orderStatusEntity) {
            //待调度的订单不能车主取消
            if (null != orderStatusEntity.getStatus() && OrderStatusEnum.TO_DISPATCH.getStatus() == orderStatusEntity.getStatus()) {
                throw new CancelOrderCheckException(ErrorCode.DISPATCHING_ORDER_STATUS_NOT_ALLOWED);
            }
        } else {
            throw new CancelOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }
    }

}
