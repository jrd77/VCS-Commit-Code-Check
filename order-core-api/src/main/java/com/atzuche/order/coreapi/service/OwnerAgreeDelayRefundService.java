package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.CancelOrderDutyEnum;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.cashcode.FineTypeCashCodeEnum;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.coreapi.submit.exception.CancelOrderCheckException;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderRefundRecordEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderRefundRecordService;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 车主同意取消订单违约罚金
 *
 * @author pengcheng.fu
 * @date 2020/3/16 16:47
 */
@Service
public class OwnerAgreeDelayRefundService {

    private static Logger logger = LoggerFactory.getLogger(OwnerAgreeDelayRefundService.class);


    @Autowired
    private OrderRefundRecordService orderRefundRecordService;
    @Autowired
    private OrderCancelReasonService orderCancelReasonService;
    @Autowired
    private RenterOrderFineDeatailService renterOrderFineDeatailService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;


    /**
     * 车主同意订单取消
     *
     * @param reqContext 请求参数
     * @return boolean 是否通知更新会员节假日取消次数:true,是 false,否
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean agreeDelayRefund(CancelOrderReqContext reqContext) {
        CancelOrderReqDTO cancelOrderReqDTO = reqContext.getCancelOrderReqDTO();

        OrderRefundRecordEntity record = new OrderRefundRecordEntity();
        record.setId(reqContext.getOrderRefundRecordEntity().getId());
        boolean isNotice = false;
        if (StringUtils.equals(cancelOrderReqDTO.getTakePenalty(), String.valueOf(OrderConstant.NO))) {
            //不同意收取违约金
            record.setType(OrderConstant.TWO);
            //更新orderCancelReason信息
            OrderCancelReasonEntity reasonEntity =
                    orderCancelReasonService.selectByOrderNo(cancelOrderReqDTO.getOrderNo(),
                            reqContext.getRenterOrderEntity().getRenterOrderNo(),
                            reqContext.getOwnerOrderEntity().getOwnerOrderNo());

            OrderCancelReasonEntity orderCancelReasonEntity = new OrderCancelReasonEntity();
            orderCancelReasonEntity.setId(reasonEntity.getId());
            orderCancelReasonEntity.setDutySource(CancelOrderDutyEnum.CANCEL_ORDER_DUTY_PLATFORM.getCode());
            orderCancelReasonEntity.setFineAmt(OrderConstant.ZERO);
            orderCancelReasonService.updateOrderCancelReasonRecord(orderCancelReasonEntity);
            //对冲renter_order_fine_deatail信息
            RenterOrderFineDeatailEntity entity =
                    renterOrderFineDeatailService.selectByCondition(reqContext.getRenterOrderEntity().getRenterOrderNo(),
                            FineTypeCashCodeEnum.CANCEL_FINE, FineSubsidyCodeEnum.OWNER, FineSubsidySourceCodeEnum.RENTER);
            if (null != entity) {
                entity.setId(null);
                entity.setFineAmount(-entity.getFineAmount());
                renterOrderFineDeatailService.saveRenterOrderFineDeatail(entity);
            }
            //对冲console_owner_order_fine_deatail信息
            ConsoleOwnerOrderFineDeatailEntity consoleOwnerOrderFineDeatailEntity =
                    consoleOwnerOrderFineDeatailService.selectByCondition(cancelOrderReqDTO.getOrderNo(),
                            FineTypeCashCodeEnum.CANCEL_FINE, FineSubsidyCodeEnum.OWNER, FineSubsidySourceCodeEnum.RENTER);
            if (null != consoleOwnerOrderFineDeatailEntity) {
                consoleOwnerOrderFineDeatailEntity.setId(null);
                consoleOwnerOrderFineDeatailEntity.setFineAmount(-consoleOwnerOrderFineDeatailEntity.getFineAmount());
                consoleOwnerOrderFineDeatailService.addFineRecord(consoleOwnerOrderFineDeatailEntity);
            }
            //通知撤销节假日取消次数
            isNotice = true;
        } else if (StringUtils.equals(cancelOrderReqDTO.getTakePenalty(), String.valueOf(OrderConstant.YES))) {
            //同意收取违约金
            record.setType(OrderConstant.ONE);
        } else {
            logger.error("操作无效.orderNo:[{}],takePenalty:[{}]", cancelOrderReqDTO.getOrderNo(),
                    cancelOrderReqDTO.getTakePenalty());
            throw new CancelOrderCheckException(ErrorCode.PARAMETER_ERROR.getCode(), "是否收取违约罚金标识无效,请确认！");
        }

        record.setStatus(cancelOrderReqDTO.getRefundRecordStatus());
        record.setUpdateOp(OrderConstant.SYSTEM_OPERATOR);
        int result = orderRefundRecordService.updateByPrimaryKeySelective(record);
        logger.info("Update order refund record. result:[{}]", result);
        return isNotice;
    }

}
