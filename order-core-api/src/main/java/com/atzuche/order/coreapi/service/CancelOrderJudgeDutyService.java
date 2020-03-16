package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.vo.res.order.OrderJudgeDutyVO;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.coreapi.entity.dto.JudgeDutyResDTO;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineApplyEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderFineApplyService;
import com.atzuche.order.ownercost.service.OwnerOrderFineDeatailService;
import com.atzuche.order.parentorder.entity.OrderCancelAppealEntity;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderRefundRecordEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelAppealService;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderRefundRecordService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private OrderCancelReasonService orderCancelReasonService;
    @Autowired
    private OrderCancelAppealService orderCancelAppealService;
    @Autowired
    private OrderRefundRecordService orderRefundRecordService;

    @Transactional(rollbackFor = Exception.class)
    public JudgeDutyResDTO judgeDuty(Integer wrongdoer, Boolean isDispatch, Boolean isSubsidyFineAmt,
                                     LocalDateTime cancelReqTime, CancelOrderReqContext reqContext) {
        CancelOrderReqDTO cancelOrderReqDTO = reqContext.getCancelOrderReqDTO();
        OrderStatusEntity orderStatusEntity = reqContext.getOrderStatusEntity();
        RenterOrderEntity renterOrderEntity = reqContext.getRenterOrderEntity();
        OwnerOrderEntity ownerOrderEntity = reqContext.getOwnerOrderEntity();
        RenterOrderCostEntity renterOrderCostEntity = reqContext.getRenterOrderCostEntity();
        int fineAmt = 0;
        int insuranceFineAmt = 0;
        boolean isNoticeSettle = true;
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

                fineAmt = penalty;
                //罚租客补贴给车主
                renterOrderFineDetailEntityOne = renterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty, FineSubsidyCodeEnum.OWNER,
                        FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);

                //罚租客补贴给平台(保险费)
                if (!cancelReqTime.isBefore(renterOrderEntity.getExpRentTime())) {
                    insuranceFineAmt = Math.abs(renterOrderCostEntity.getBasicEnsureAmount());
                    RenterOrderFineDeatailEntity renterOrderFineDetailEntityTwo =
                            renterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), Math.abs(renterOrderCostEntity.getBasicEnsureAmount()), FineSubsidyCodeEnum.PLATFORM,
                                    FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);
                    renterOrderFineDeatailService.saveRenterOrderFineDeatail(renterOrderFineDetailEntityTwo);
                }

                //车主收益(来自租客罚金)
                if (isSubsidyFineAmt) {
                    consoleOwnerOrderFineDeatailEntity =
                            consoleOwnerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty, FineSubsidyCodeEnum.OWNER,
                                    FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);
                    //延时退款，等待车主同意
                    if (null != consoleOwnerOrderFineDeatailEntity && !cancelOrderReqDTO.getConsoleInvoke()) {
                        OrderRefundRecordEntity orderRefundRecordEntity =
                                orderRefundRecordService.orderRefundDataConvert(cancelOrderReqDTO.getOrderNo(),
                                        cancelOrderReqDTO.getRenterOrderNo(), cancelOrderReqDTO.getOwnerOrderNo(), penalty);
                        orderRefundRecordEntity.setCreateOp(OrderConstant.SYSTEM_OPERATOR);
                        orderRefundRecordEntity.setUpdateOp(OrderConstant.SYSTEM_OPERATOR);
                        orderRefundRecordService.saveOrderRefundRecord(orderRefundRecordEntity);
                        isNoticeSettle = false;
                    }
                } else {
                    if (null != renterOrderFineDetailEntityOne) {
                        renterOrderFineDetailEntityOne.setFineSubsidyCode(FineSubsidySourceCodeEnum.PLATFORM.getFineSubsidySourceCode());
                        renterOrderFineDetailEntityOne.setFineSubsidyDesc(FineSubsidySourceCodeEnum.PLATFORM.getFineSubsidySourceDesc());
                    }
                }
            }
            renterOrderFineDeatailService.saveRenterOrderFineDeatail(renterOrderFineDetailEntityOne);
            consoleOwnerOrderFineDeatailService.addFineRecord(consoleOwnerOrderFineDeatailEntity);
        } else if (CancelOrderDutyEnum.CANCEL_ORDER_DUTY_OWNER.getCode() == wrongdoer) {
            //车主责任
            OwnerGoodsDetailDTO goodsDetail = reqContext.getOwnerGoodsDetailDTO();

            CancelFineAmtDTO cancelFineAmt = buildCancelFineAmtDTO(renterOrderEntity,
                    renterOrderCostEntity, goodsDetail.getCarOwnerType());
            cancelFineAmt.setCancelTime(cancelReqTime);
            int penalty = orderStatusEntity.getRentCarPayStatus() == OrderConstant.YES ? renterOrderFineDeatailService.calCancelFine(cancelFineAmt) : 0;
            fineAmt = penalty;
            //罚车主补贴给平台(保险费)
            if (!cancelReqTime.isBefore(renterOrderEntity.getExpRentTime())) {
                insuranceFineAmt = Math.abs(renterOrderCostEntity.getBasicEnsureAmount());
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

                isNoticeSettle = false;
            } else {
                //罚车主补贴给租客
                OwnerOrderFineDeatailEntity ownerOrderFineDetailEntityOne =
                        ownerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty,
                                FineSubsidyCodeEnum.RENTER, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
                if (null != ownerOrderFineDetailEntityOne) {
                    ownerOrderFineDetailEntityOne.setOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo());
                }
                //租客收益处理
                if (isSubsidyFineAmt) {
                    ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity =
                            consoleRenterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty,
                                    FineSubsidyCodeEnum.RENTER, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
                    consoleRenterOrderFineDeatailService.saveConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);
                } else {
                    if (null != ownerOrderFineDetailEntityOne) {
                        ownerOrderFineDetailEntityOne.setFineSubsidyCode(FineSubsidySourceCodeEnum.PLATFORM.getFineSubsidySourceCode());
                        ownerOrderFineDetailEntityOne.setFineSubsidyDesc(FineSubsidySourceCodeEnum.PLATFORM.getFineSubsidySourceDesc());
                    }
                }

                ownerOrderFineDeatailService.addOwnerOrderFineRecord(ownerOrderFineDetailEntityOne);
            }

        }
//        else if (CancelOrderDutyEnum.CANCEL_ORDER_DUTY_PLATFORM.getCode() == wrongdoer) {
//            //双方无责、平台承担保险
//        }
        //更新申述信息
        OrderCancelAppealEntity orderCancelAppealEntity =
                orderCancelAppealService.selectByOrderNo(cancelOrderReqDTO.getOrderNo());
        if (null != orderCancelAppealEntity) {
            OrderCancelAppealEntity record = new OrderCancelAppealEntity();
            record.setId(orderCancelAppealEntity.getId());
            record.setIsWrongdoer(OrderConstant.YES);
            orderCancelAppealService.updateOrderCancelAppeal(record);
        }
        //更新取消订单责任方
        OrderCancelReasonEntity orderCancelReasonEntity =
                orderCancelReasonService.selectByOrderNo(cancelOrderReqDTO.getOrderNo(),
                        renterOrderEntity.getRenterOrderNo(), ownerOrderEntity.getOwnerOrderNo());
        if (null != orderCancelReasonEntity) {
            OrderCancelReasonEntity record = new OrderCancelReasonEntity();
            record.setId(orderCancelReasonEntity.getId());
            record.setDutySource(wrongdoer);
            record.setFineAmt(fineAmt);
            record.setInsuranceFineAmt(insuranceFineAmt);
            record.setUpdateOp(cancelOrderReqDTO.getOperatorName());
            orderCancelReasonService.updateOrderCancelReasonRecord(record);
        }

        JudgeDutyResDTO judgeDutyRes = new JudgeDutyResDTO();
        judgeDutyRes.setIsNoticeSettle(isNoticeSettle);

        return judgeDutyRes;
    }


    /**
     * 根据订单号查询订单取消/申述记录
     *
     * @param orderNo 订单号
     * @return List<OrderJudgeDutyVO>
     */
    public List<OrderJudgeDutyVO> queryOrderJudgeDutysByOrderNo(String orderNo) {

        List<OrderCancelReasonEntity> records = orderCancelReasonService.selectListByOrderNo(orderNo);
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }

        List<OrderJudgeDutyVO> list = new ArrayList<>();
        records.stream().filter(reocrd -> StringUtils.endsWithIgnoreCase(reocrd.getOperateType(), "1")).forEach(record -> {
            OrderJudgeDutyVO orderJudgeDutyVO = new OrderJudgeDutyVO();
            orderJudgeDutyVO.setId(record.getId());
            orderJudgeDutyVO.setOrderNo(record.getOrderNo());
            orderJudgeDutyVO.setRenterOrderNo(record.getRenterOrderNo());
            orderJudgeDutyVO.setOwnerOrderNo(record.getOwnerOrderNo());
            orderJudgeDutyVO.setOperateName(getOptName(record));
            orderJudgeDutyVO.setOptSource(getOptSource(record));
            orderJudgeDutyVO.setOptReason(record.getCancelReason());
            orderJudgeDutyVO.setOptTime(DateUtils.formate(record.getCreateTime(), DateUtils.DATE_DEFAUTE1));
            orderJudgeDutyVO.setIsManualCondemn("1");
            if (null != record.getDutySource()) {
                orderJudgeDutyVO.setDutyource(CancelOrderDutyEnum.from(record.getDutySource()).getName());
                orderJudgeDutyVO.setPenaltyAmt(String.valueOf(record.getFineAmt()));
                orderJudgeDutyVO.setInsuranceAmt(String.valueOf(record.getInsuranceFineAmt()));

                orderJudgeDutyVO.setJudgeDutyjOperator(record.getUpdateOp());
                orderJudgeDutyVO.setJudgeDutyjOptTime(DateUtils.formate(record.getUpdateTime(), DateUtils.DATE_DEFAUTE1));
                orderJudgeDutyVO.setIsManualCondemn("0");
            }
        });
        return list;
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

    /**
     * 获取操作名称
     *
     * @param record 取消记录
     * @return String
     */
    private String getOptName(OrderCancelReasonEntity record) {
        if (record.getAppealFlag() == OrderConstant.YES) {
            return "申诉";
        }

        if (record.getCancelSource().intValue() == CancelSourceEnum.PLATFORM.getCode().intValue()) {
            return "平台取消";
        }

        if (record.getCancelSource().intValue() == CancelSourceEnum.INSTEAD_OF_OWNER.getCode().intValue() ||
                record.getCancelSource().intValue() == CancelSourceEnum.INSTEAD_OF_RENTER.getCode().intValue()) {
            return CancelSourceEnum.from(record.getCancelSource()).getMsg();
        }
        return "取消订单";

    }

    /**
     * 获取操作方
     *
     * @param record 取消记录
     * @return String
     */
    private String getOptSource(OrderCancelReasonEntity record) {

        if (record.getCancelSource().intValue() == CancelSourceEnum.INSTEAD_OF_OWNER.getCode().intValue() ||
                record.getCancelSource().intValue() == CancelSourceEnum.INSTEAD_OF_RENTER.getCode().intValue() ||
                record.getCancelSource().intValue() == CancelSourceEnum.PLATFORM.getCode().intValue()) {
            return record.getCreateOp();
        }
        return CancelSourceEnum.from(record.getCancelSource()).getMsg();

    }
}
