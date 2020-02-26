package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineApplyEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderFineApplyService;
import com.atzuche.order.ownercost.service.OwnerOrderFineDeatailService;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.settle.service.OrderSettleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 取消订单责任判定
 *
 * @author pengcheng.fu
 * @date 2020/2/25 15:45
 */

@Service
public class CancelOrderJudgeDutyService {

    private static Logger logger = LoggerFactory.getLogger(CancelOrderJudgeDutyService.class);

    @Autowired
    private RenterOrderFineDeatailService renterOrderFineDeatailService;
    @Autowired
    private OwnerOrderFineDeatailService ownerOrderFineDeatailService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
    @Autowired
    private OwnerOrderFineApplyService ownerOrderFineApplyService;
    @Autowired
    private OrderSettleService orderSettleService;
    @Autowired
    private OrderCancelReasonService orderCancelReasonService;

    @Transactional(rollbackFor = Exception.class)
    public void judgeDuty(Integer wrongdoer , Boolean isDispatch,
                          LocalDateTime cancelReqTime, CancelOrderReqContext reqContext) {
        CancelOrderReqDTO cancelOrderReqDTO = reqContext.getCancelOrderReqDTO();
        OrderStatusEntity orderStatusEntity = reqContext.getOrderStatusEntity();
        RenterOrderEntity renterOrderEntity = reqContext.getRenterOrderEntity();
        RenterOrderCostEntity renterOrderCostEntity = reqContext.getRenterOrderCostEntity();
        if (CancelOrderDutyEnum.CANCEL_ORDER_DUTY_RENTER.getCode() == wrongdoer) {
            //租客责任
            RenterGoodsDetailDTO goodsDetail = reqContext.getRenterGoodsDetailDTO();
            RenterOrderFineDeatailEntity renterOrderFineDetailEntityOne = null;
            ConsoleOwnerOrderFineDeatailEntity consoleOwnerOrderFineDeatailEntity = null;
            if (orderStatusEntity.getRentCarPayStatus() == OrderConstant.YES) {
                CancelFineAmtDTO cancelFineAmt = buildCancelFineAmtDTO(renterOrderEntity,
                        renterOrderCostEntity, goodsDetail.getCarOwnerType());
                cancelFineAmt.setCancelTime(cancelReqTime);
                int penalty = renterOrderFineDeatailService.calCancelFine(cancelFineAmt);

                //罚租客补贴给车主
                renterOrderFineDetailEntityOne = renterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty, FineSubsidyCodeEnum.OWNER,
                        FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);

                //罚租客补贴给平台(保险费)
                if (!cancelReqTime.isBefore(renterOrderEntity.getExpRentTime())) {
                    RenterOrderFineDeatailEntity renterOrderFineDetailEntityTwo =
                            renterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), Math.abs(renterOrderCostEntity.getBasicEnsureAmount()), FineSubsidyCodeEnum.PLATFORM,
                                    FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);
                    renterOrderFineDeatailService.saveRenterOrderFineDeatail(renterOrderFineDetailEntityTwo);
                }

                //车主收益(来自租客罚金)
                consoleOwnerOrderFineDeatailEntity =
                        consoleOwnerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty, FineSubsidyCodeEnum.OWNER,
                                FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);
            }
            renterOrderFineDeatailService.saveRenterOrderFineDeatail(renterOrderFineDetailEntityOne);
            consoleOwnerOrderFineDeatailService.addFineRecord(consoleOwnerOrderFineDeatailEntity);
        } else if (CancelOrderDutyEnum.CANCEL_ORDER_DUTY_OWNER.getCode() == wrongdoer) {
            //车主责任
            OwnerOrderEntity ownerOrderEntity = reqContext.getOwnerOrderEntity();
            OwnerGoodsDetailDTO goodsDetail = reqContext.getOwnerGoodsDetailDTO();

            CancelFineAmtDTO cancelFineAmt = buildCancelFineAmtDTO(renterOrderEntity,
                    renterOrderCostEntity, goodsDetail.getCarOwnerType());
            cancelFineAmt.setCancelTime(cancelReqTime);
            int penalty = orderStatusEntity.getRentCarPayStatus() == OrderConstant.YES ? renterOrderFineDeatailService.calCancelFine(cancelFineAmt) : 0;

            //罚车主补贴给平台(保险费)
            if (!cancelReqTime.isBefore(renterOrderEntity.getExpRentTime())) {
                OwnerOrderFineDeatailEntity ownerOrderFineDeatailEntityTwo =
                        ownerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), Math.abs(renterOrderCostEntity.getBasicEnsureAmount()),
                                FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
                if (null != ownerOrderFineDeatailEntityTwo) {
                    ownerOrderFineDeatailEntityTwo.setOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo());
                }
                ownerOrderFineDeatailService.addOwnerOrderFineRecord(ownerOrderFineDeatailEntityTwo);
            }

            if (isDispatch) {
                //调度后处理
                ownerOrderFineApplyService.addFineApplyRecord(buildOwnerOrderFineApplyEntity(cancelOrderReqDTO.getOrderNo(),
                        ownerOrderEntity.getOwnerOrderNo(), Integer.valueOf(ownerOrderEntity.getMemNo()), penalty));
            } else {
                //罚车主补贴给租客
                OwnerOrderFineDeatailEntity ownerOrderFineDetailEntityOne =
                        ownerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty,
                                FineSubsidyCodeEnum.RENTER, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
                if (null != ownerOrderFineDetailEntityOne) {
                    ownerOrderFineDetailEntityOne.setOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo());
                }
                ownerOrderFineDeatailService.addOwnerOrderFineRecord(ownerOrderFineDetailEntityOne);

                //租客收益处理
                ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity =
                        consoleRenterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty,
                                FineSubsidyCodeEnum.RENTER, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
                consoleRenterOrderFineDeatailService.saveConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);
            }

        } else if (CancelOrderDutyEnum.CANCEL_ORDER_DUTY_PLATFORM.getCode() == wrongdoer) {
            //双方无责、平台承担保险


        }

        //更新取消订单责任方
        OrderCancelReasonEntity orderCancelReasonEntity =
                orderCancelReasonService.selectByOrderNo(cancelOrderReqDTO.getOrderNo());
        if(null != orderCancelReasonEntity) {
            OrderCancelReasonEntity record = new OrderCancelReasonEntity();
            record.setId(orderCancelReasonEntity.getId());
            record.setDutySource(wrongdoer);
            orderCancelReasonService.updateOrderCancelReasonRecord(record);
        }

        if(!isDispatch) {
            //通知结算计算凹凸币和钱包等
            orderSettleService.settleOrderCancel(cancelOrderReqDTO.getOrderNo());
        }

    }


    /**
     * 组装计算取消订单罚金请求参数
     *
     * @param renterOrderEntity     租客订单信息
     * @param renterOrderCostEntity 租客订单费用信息
     * @param carOwnerType          车辆类型
     * @return CancelFineAmtDTO
     */
    private CancelFineAmtDTO buildCancelFineAmtDTO(RenterOrderEntity renterOrderEntity,
                                                   RenterOrderCostEntity renterOrderCostEntity,
                                                   Integer carOwnerType) {
        CancelFineAmtDTO cancelFineAmtDTO = new CancelFineAmtDTO();
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(renterOrderEntity.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        costBaseDTO.setMemNo(renterOrderCostEntity.getMemNo());
        costBaseDTO.setStartTime(renterOrderEntity.getExpRentTime());
        costBaseDTO.setEndTime(renterOrderEntity.getExpRevertTime());

        cancelFineAmtDTO.setCostBaseDTO(costBaseDTO);
        cancelFineAmtDTO.setRentAmt(Math.abs(renterOrderCostEntity.getRentCarAmount()));
        cancelFineAmtDTO.setOwnerType(carOwnerType);
        return cancelFineAmtDTO;
    }


    /**
     * 车主取消罚金调度后续处理信息
     *
     * @param orderNo      订单号
     * @param ownerOrderNo 车主订单号
     * @param memNo        租客会员号
     * @param fineAmt      罚金
     * @return OwnerOrderFineApplyEntity
     */
    private OwnerOrderFineApplyEntity buildOwnerOrderFineApplyEntity(String orderNo, String ownerOrderNo,
                                                                     Integer memNo, Integer fineAmt) {
        if (null == fineAmt || fineAmt == 0) {
            return null;
        }
        OwnerOrderFineApplyEntity applyEntity = new OwnerOrderFineApplyEntity();
        applyEntity.setOrderNo(orderNo);
        applyEntity.setOwnerOrderNo(ownerOrderNo);
        applyEntity.setMemNo(memNo);
        applyEntity.setFineAmount(fineAmt);

        applyEntity.setFineSubsidySourceCode(FineSubsidySourceCodeEnum.OWNER.getFineSubsidySourceCode());
        applyEntity.setFineSubsidySourceDesc(FineSubsidySourceCodeEnum.OWNER.getFineSubsidySourceDesc());
        applyEntity.setFineType(FineTypeEnum.CANCEL_FINE.getFineType());
        applyEntity.setFineTypeDesc(FineTypeEnum.CANCEL_FINE.getFineTypeDesc());

        return applyEntity;
    }

}
