package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.HolidaySettingSDK;
import com.atzuche.config.common.entity.HolidaySettingEntity;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.CancelOrderDutyEnum;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.vo.res.order.OrderJudgeDutyVO;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineApplyEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderFineApplyService;
import com.atzuche.order.ownercost.service.OwnerOrderFineDeatailService;
import com.atzuche.order.parentorder.entity.OrderCancelAppealEntity;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelAppealService;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(rollbackFor = Exception.class)
    public void judgeDuty(Integer wrongdoer , Boolean isDispatch, Boolean isSubsidyFineAmt,
                          LocalDateTime cancelReqTime, CancelOrderReqContext reqContext) {
        CancelOrderReqDTO cancelOrderReqDTO = reqContext.getCancelOrderReqDTO();
        OrderStatusEntity orderStatusEntity = reqContext.getOrderStatusEntity();
        RenterOrderEntity renterOrderEntity = reqContext.getRenterOrderEntity();
        OwnerOrderEntity ownerOrderEntity = reqContext.getOwnerOrderEntity();
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
                if(isSubsidyFineAmt) {
                    consoleOwnerOrderFineDeatailEntity =
                            consoleOwnerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty, FineSubsidyCodeEnum.OWNER,
                                    FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);
                } else {
                    if(null != renterOrderFineDetailEntityOne) {
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
                //租客收益处理
                if(isSubsidyFineAmt) {
                    ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity =
                            consoleRenterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty,
                                    FineSubsidyCodeEnum.RENTER, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
                    consoleRenterOrderFineDeatailService.saveConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);
                } else {
                    if(null != ownerOrderFineDetailEntityOne) {
                        ownerOrderFineDetailEntityOne.setFineSubsidyCode(FineSubsidySourceCodeEnum.PLATFORM.getFineSubsidySourceCode());
                        ownerOrderFineDetailEntityOne.setFineSubsidyDesc(FineSubsidySourceCodeEnum.PLATFORM.getFineSubsidySourceDesc());
                    }
                }

                ownerOrderFineDeatailService.addOwnerOrderFineRecord(ownerOrderFineDetailEntityOne);
            }

        } else if (CancelOrderDutyEnum.CANCEL_ORDER_DUTY_PLATFORM.getCode() == wrongdoer) {
            //双方无责、平台承担保险


        }
        //更新申述信息
        OrderCancelAppealEntity orderCancelAppealEntity =
                orderCancelAppealService.selectByOrderNo(cancelOrderReqDTO.getOrderNo());
        int appealFlag = OrderConstant.NO;
        if(null != orderCancelAppealEntity) {
            appealFlag = OrderConstant.YES;
            OrderCancelAppealEntity record = new OrderCancelAppealEntity();
            record.setId(orderCancelAppealEntity.getId());
            record.setIsWrongdoer(OrderConstant.YES);
            orderCancelAppealService.updateOrderCancelAppeal(record);
        }
        //更新取消订单责任方
        OrderCancelReasonEntity orderCancelReasonEntity =
                orderCancelReasonService.selectByOrderNo(cancelOrderReqDTO.getOrderNo(),
                        renterOrderEntity.getRenterOrderNo(), ownerOrderEntity.getOwnerOrderNo());
        if(null != orderCancelReasonEntity) {
            OrderCancelReasonEntity record = new OrderCancelReasonEntity();
            record.setId(orderCancelReasonEntity.getId());
            record.setDutySource(wrongdoer);
            record.setAppealFlag(appealFlag);
            record.setUpdateOp(cancelOrderReqDTO.getOperatorName());
            orderCancelReasonService.updateOrderCancelReasonRecord(record);
        }

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
        records.forEach(record -> {
            OrderJudgeDutyVO orderJudgeDutyVO = new OrderJudgeDutyVO();


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

}
