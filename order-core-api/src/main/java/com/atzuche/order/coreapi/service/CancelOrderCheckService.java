package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.commons.enums.MemRoleEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.coreapi.submitOrder.exception.CancelOrderCheckException;
import com.atzuche.order.coreapi.submitOrder.exception.RefuseOrderCheckException;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderRefundRecordEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * CancelOrderController 相关业务校验
 *
 * @author pengcheng.fu
 * @date 2020/2/3 14:30
 */

@Service
public class CancelOrderCheckService {

    private static Logger logger = LoggerFactory.getLogger(CancelOrderCheckService.class);

    /**
     * 车主/租客取消订单校验
     *
     * @param reqContext 取消订单请求参数
     */
    public void checkCancelOrder(CancelOrderReqContext reqContext) {
        CancelOrderReqDTO cancelOrderReqDTO = reqContext.getCancelOrderReqDTO();
        RenterOrderEntity renterOrderEntity = reqContext.getRenterOrderEntity();
        if(null == renterOrderEntity) {
            logger.error("No valid renter order found. orderNo:[{}]", cancelOrderReqDTO.getOrderNo());
            throw new CancelOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }
        if(StringUtils.equals(MemRoleEnum.RENTER.getCode(), cancelOrderReqDTO.getMemRole())
                && !renterOrderEntity.getRenterMemNo().equals(cancelOrderReqDTO.getMemNo())
                && !cancelOrderReqDTO.getConsoleInvoke()){
            logger.error("Renter order and memNo 不一致,[orderNo={},memNo={}]",cancelOrderReqDTO.getOrderNo(),
                    cancelOrderReqDTO.getMemNo());
            throw new CancelOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }

        OwnerOrderEntity ownerOrderEntity = reqContext.getOwnerOrderEntity();
        if(null == ownerOrderEntity) {
            logger.error("No valid owner order found. orderNo:[{}]", cancelOrderReqDTO.getOrderNo());
            throw new CancelOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }

        if(StringUtils.equals(MemRoleEnum.OWNER.getCode(), cancelOrderReqDTO.getMemRole())
                && !ownerOrderEntity.getMemNo().equals(cancelOrderReqDTO.getMemNo())
                && !cancelOrderReqDTO.getConsoleInvoke()){
            logger.error("Owner order and memNo 不一致,[orderNo={},memNo={}]",cancelOrderReqDTO.getOrderNo(),
                    cancelOrderReqDTO.getMemNo());
            throw new CancelOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }

        OrderStatusEntity orderStatusEntity = reqContext.getOrderStatusEntity();
        if(null != orderStatusEntity) {
            //已结束的订单不能取消
            if(null != orderStatusEntity.getStatus() && OrderStatusEnum.CLOSED.getStatus() == orderStatusEntity.getStatus()) {
                throw new CancelOrderCheckException(ErrorCode.TRANS_CANCEL_DUPLICATE);
            }

            if(null != orderStatusEntity.getStatus() && OrderStatusEnum.TO_SETTLE.getStatus() <= orderStatusEntity.getStatus()) {
                throw new CancelOrderCheckException(ErrorCode.TRANS_CANCEL_DUPLICATE);
            }

            if(!cancelOrderReqDTO.getConsoleInvoke()) {
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


    /**
     * 车主取消校验
     * @param reqContext 取消订单请求参数
     */
    public void checkOwnerCancelOrder(CancelOrderReqContext reqContext) {
        OrderStatusEntity orderStatusEntity = reqContext.getOrderStatusEntity();
        if(null != orderStatusEntity) {
            if (null != orderStatusEntity.getStatus()) {
                //待确认的订单不能车主取消
                if(OrderStatusEnum.TO_CONFIRM.getStatus() == orderStatusEntity.getStatus()) {
                    throw new CancelOrderCheckException(ErrorCode.ORDER_STATUS_NOT_ALLOWED);
                }
                //待调度的订单不能车主取消
                if(OrderStatusEnum.TO_DISPATCH.getStatus() == orderStatusEntity.getStatus()) {
                    throw new CancelOrderCheckException(ErrorCode.DISPATCHING_ORDER_STATUS_NOT_ALLOWED);
                }
            }
        } else {
            throw new CancelOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }

        CancelOrderReqDTO orderReqDTO = reqContext.getCancelOrderReqDTO();
        if(!orderReqDTO.getConsoleInvoke()) {
            OwnerGoodsDetailDTO ownerGoodsDetailDTO = reqContext.getOwnerGoodsDetailDTO();
            if(null != ownerGoodsDetailDTO.getCarOwnerType()) {
                logger.error("Car owner type is :[{}]", ownerGoodsDetailDTO.getCarOwnerType());
                if(ownerGoodsDetailDTO.getCarOwnerType() == CarOwnerTypeEnum.F.getCode()) {
                    throw new RefuseOrderCheckException(ErrorCode.MANAGED_CAR_CAN_NOT_OPT_TRANS);
                } else if (ownerGoodsDetailDTO.getCarOwnerType() == CarOwnerTypeEnum.G.getCode()) {
                    throw new RefuseOrderCheckException(ErrorCode.PROXY_CAR_CAN_NOT_OPT_TRANS);
                }
            } else {
                logger.warn("Car owner type is empty. ");
            }
        }


    }


    public void checkOrderCancelJudgeDuty(CancelOrderReqContext reqContext) {
        OrderStatusEntity orderStatusEntity = reqContext.getOrderStatusEntity();
        if(null != orderStatusEntity) {
            if (null != orderStatusEntity.getStatus()) {
                if(orderStatusEntity.getStatus() != OrderStatusEnum.CLOSED.getStatus() && orderStatusEntity.getStatus() != OrderStatusEnum.TO_DISPATCH.getStatus() ) {
                    throw new RefuseOrderCheckException(ErrorCode.ORDER_STATUS_NOT_ALLOWED);
                }
            }

            if(null != orderStatusEntity.getRentCarPayStatus() && orderStatusEntity.getRentCarPayStatus() != OrderConstant.YES) {
                throw new RefuseOrderCheckException(ErrorCode.ORDER_PAY_NOT_SUCC);
            }
        } else {
            throw new RefuseOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }

        OrderCancelReasonEntity orderCancelReasonEntity = reqContext.getOrderCancelReasonEntity();
        if(null != orderCancelReasonEntity && null != orderCancelReasonEntity.getDutySource()) {
            throw new RefuseOrderCheckException(ErrorCode.CANNOT_REPEAT_OPERATION);
        }

    }


    public void delayRefundCheck(CancelOrderReqContext reqContext) {
        OrderRefundRecordEntity orderRefundRecordEntity = reqContext.getOrderRefundRecordEntity();
        if (null == orderRefundRecordEntity) {
            throw new RefuseOrderCheckException(ErrorCode.CURRENT_NOT_ALLOW_MODIFY_ADDRESS);
        }
        if (OrderConstant.ONE == orderRefundRecordEntity.getStatus()
                || OrderConstant.TWO == orderRefundRecordEntity.getStatus()
                || orderRefundRecordEntity.getCreateTime().plusHours(OrderConstant.ONE).isBefore(LocalDateTime.now())) {
            throw new RefuseOrderCheckException(ErrorCode.CURRENT_NOT_ALLOW_MODIFY_ADDRESS.getCode(), "您已超时，订单状态已发生改变，请刷新后再试。");

        }
    }

}
