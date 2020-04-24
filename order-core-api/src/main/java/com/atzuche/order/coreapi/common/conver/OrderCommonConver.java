package com.atzuche.order.coreapi.common.conver;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.ListUtil;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CarRentTimeRangeDTO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.RenterChildStatusEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.order.CostItemVO;
import com.atzuche.order.commons.vo.res.order.DepositAmtVO;
import com.atzuche.order.commons.vo.res.order.IllegalDepositVO;
import com.atzuche.order.commons.vo.res.order.TotalCostVO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostReqContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostResContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.*;
import com.atzuche.order.coreapi.utils.OrderCostDetailCalculationUtil;
import com.atzuche.order.coreapi.utils.RenterOrderSubsidyDetailCalculationUtil;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.renterorder.entity.RenterAdditionalDriverEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.DeductContextDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.entity.dto.cost.CreateRenterOrderDataReqDTO;
import com.atzuche.order.renterorder.vo.RenterOrderCarDepositResVO;
import com.atzuche.order.renterorder.vo.RenterOrderIllegalResVO;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.atzuche.order.settle.vo.req.CancelOrderReqDTO;
import com.autoyol.platformcost.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author pengcheng.fu
 * @date 2020/1/13 14:35
 */

@Component
public class OrderCommonConver {

    private static Logger logger = LoggerFactory.getLogger(OrderCommonConver.class);

    /**
     * 租客订单请求参数封装
     *
     * @param orderNo               主订单号
     * @param renterOrderNo         租客子订单号
     * @param reqContext            下单请求参数
     * @return RenterOrderReqVO 租客订单请求参数
     */
    public RenterOrderReqVO buildRenterOrderReqVO(String orderNo, String renterOrderNo, OrderReqContext reqContext) {

        RenterOrderReqVO renterOrderReqVO = new RenterOrderReqVO();
        renterOrderReqVO.setOrderNo(orderNo);
        renterOrderReqVO.setRenterOrderNo(renterOrderNo);

        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, RenterOrderReqVO.class, false);
        beanCopier.copy(reqContext.getOrderReqVO(), renterOrderReqVO, null);

        OrderReqVO orderReqVO = reqContext.getOrderReqVO();
        renterOrderReqVO.setEntryCode(orderReqVO.getSceneCode());
        renterOrderReqVO.setSource(orderReqVO.getSource());
        String driverIds = orderReqVO.getDriverIds();
        renterOrderReqVO.setDriverIds(ListUtil.parseString(driverIds, ","));
        renterOrderReqVO.setGetCarBeforeTime(null == reqContext.getCarRentTimeRangeDTO() || null == reqContext.getCarRentTimeRangeDTO().getGetMinutes() ?
                OrderConstant.ZERO : reqContext.getCarRentTimeRangeDTO().getGetMinutes());
        renterOrderReqVO.setReturnCarAfterTime(null == reqContext.getCarRentTimeRangeDTO() || null == reqContext.getCarRentTimeRangeDTO().getReturnMinutes() ? OrderConstant.ZERO :
                reqContext.getCarRentTimeRangeDTO().getReturnMinutes());

        RenterGoodsDetailDTO goodsDetail = reqContext.getRenterGoodsDetailDto();
        renterOrderReqVO.setGuidPrice(goodsDetail.getCarGuidePrice());
        renterOrderReqVO.setCarSurplusPrice(goodsDetail.getCarSurplusPrice());
        renterOrderReqVO.setInmsrp(goodsDetail.getCarInmsrp());
        renterOrderReqVO.setBrandId(goodsDetail.getBrand());
        renterOrderReqVO.setTypeId(goodsDetail.getType());
        renterOrderReqVO.setLicenseDay(goodsDetail.getLicenseDay());
        renterOrderReqVO.setLabelIds(goodsDetail.getLabelIds());
        renterOrderReqVO.setRenterGoodsPriceDetailDTOList(goodsDetail.getRenterGoodsPriceDetailDTOList());
        renterOrderReqVO.setPlateNum(goodsDetail.getCarPlateNum());
        renterOrderReqVO.setAbatement(orderReqVO.getAbatement());
        renterOrderReqVO.setCarShowLat(goodsDetail.getCarShowLat());
        renterOrderReqVO.setCarShowLon(goodsDetail.getCarShowLon());
        renterOrderReqVO.setCarRealLat(goodsDetail.getCarRealLat());
        renterOrderReqVO.setCarRealLon(goodsDetail.getCarRealLon());

        RenterMemberDTO renterMember = reqContext.getRenterMemberDto();
        renterOrderReqVO.setCertificationTime(renterMember.getCertificationTime());
        renterOrderReqVO.setIsNew(null == renterMember.getIsNew() || renterMember.getIsNew() == 0);
        renterOrderReqVO.setRenterMemberRightDTOList(renterMember.getRenterMemberRightDTOList());
        renterOrderReqVO.setCommUseDriverList(renterMember.getCommUseDriverList());
        renterOrderReqVO.setReplyFlag(goodsDetail.getReplyFlag());
        logger.info("Build renter order reqVO,result is ,renterOrderReqVO:[{}]",
                JSON.toJSONString(renterOrderReqVO));
        return renterOrderReqVO;
    }

    /**
     * 获取商品明细请求参数
     *
     * @param orderReqVO 下单请求参数
     * @return CarProxyService.CarDetailReqVO
     */
    public CarProxyService.CarDetailReqVO buildCarDetailReqVO(OrderReqVO orderReqVO) {
        CarProxyService.CarDetailReqVO carDetailReqVO = new CarProxyService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(StringUtils.isBlank(orderReqVO.getCarAddrIndex()) ? 0 : Integer.parseInt(orderReqVO.getCarAddrIndex()));
        carDetailReqVO.setCarNo(orderReqVO.getCarNo());
        carDetailReqVO.setRentTime(orderReqVO.getRentTime());
        carDetailReqVO.setRevertTime(orderReqVO.getRevertTime());
        carDetailReqVO.setUseSpecialPrice(StringUtils.equals("1",
                orderReqVO.getUseSpecialPrice()));
        return carDetailReqVO;
    }

    /**
     * 下单前费用计算--租车费用列表
     *
     * @param renterOrderCostRespDTO 订单租车费用信息
     * @param renterOrderReqVO 请求信息
     * @return List<CostItemVO>
     */
    public List<CostItemVO> buildCostItemList(RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO) {
        logger.info("Build costItem list A.param is,renterOrderCostRespDTO:[{}]",
                JSON.toJSONString(renterOrderCostRespDTO));
        if (null == renterOrderCostRespDTO || CollectionUtils.isEmpty(renterOrderCostRespDTO.getRenterOrderCostDetailDTOList())) {
            return null;
        }

        List<CostItemVO> costItemList = new ArrayList<>();
        renterOrderCostRespDTO.getRenterOrderCostDetailDTOList().forEach(cost -> {
            if (RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getCostCode()) ||
                    RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getCostCode())) {
                return;
            }
            CostItemVO vo = new CostItemVO();
            vo.setCostCode(cost.getCostCode());
            vo.setCostDesc(cost.getCostDesc());
            vo.setCount(cost.getCount());
            if (StringUtils.equals(RenterCashCodeEnum.SRV_GET_COST.getCashNo(), cost.getCostCode())) {
                vo.setUnitPrice(renterOrderCostRespDTO.getGetRealAmt());
                vo.setTotalAmount(-renterOrderCostRespDTO.getGetRealAmt());
            } else if (StringUtils.equals(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo(), cost.getCostCode())) {
                vo.setUnitPrice(renterOrderCostRespDTO.getReturnRealAmt());
                vo.setTotalAmount(-renterOrderCostRespDTO.getReturnRealAmt());
            } else {
                vo.setUnitPrice(cost.getUnitPrice());
                vo.setTotalAmount(cost.getTotalAmount());
            }
            costItemList.add(vo);
        });
        Integer inmsrpGuidePrice = renterOrderReqVO.getInmsrp() == null ? renterOrderReqVO.getGuidPrice():renterOrderReqVO.getInmsrp();
        // 获取保险和不计免赔的折扣
        double insureDiscount = CommonUtils.getInsureDiscount(renterOrderReqVO.getRentTime(), renterOrderReqVO.getRevertTime(), inmsrpGuidePrice);
        // 平台保障费用项
        costItemList.add(getInsurCostItemVO(renterOrderCostRespDTO, insureDiscount));
        // 全面保障费用项
        costItemList.add(getAbatementCostItemVO(renterOrderCostRespDTO, insureDiscount));
        logger.info("Build costItem list A.result is,costItemList:[{}]", JSON.toJSONString(costItemList));
        return costItemList;
    }


    /**
     * 下单前费用计算--租车费用列表
     *
     * @param orderCostContext 订单租车费用信息
     * @return List<CostItemVO>
     */
    public List<CostItemVO> buildCostItemList(OrderCostContext orderCostContext) {
        logger.info("Build costItem list B.param is,orderCostContext:[{}]",
                JSON.toJSONString(orderCostContext));
        if (null == orderCostContext || CollectionUtils.isEmpty(orderCostContext.getCostDetailContext().getCostDetails())) {
            return null;
        }
        List<CostItemVO> costItemList = new ArrayList<>();

        orderCostContext.getCostDetailContext().getCostDetails().forEach(cost -> {
            if (StringUtils.equals(RenterCashCodeEnum.SRV_GET_COST.getCashNo(), cost.getCostCode()) ||
                    StringUtils.equals(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo(), cost.getCostCode()) ||
                    StringUtils.equals(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo(), cost.getCostCode()) ||
                    StringUtils.equals(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo(), cost.getCostCode())
            ) {
                return;
            }
            CostItemVO vo = new CostItemVO();
            vo.setCostCode(cost.getCostCode());
            vo.setCostDesc(cost.getCostDesc());
            vo.setCount(cost.getCount());
            int subsidyAmt =
                    RenterOrderSubsidyDetailCalculationUtil.getOrderSubsidyCostByCashCode(orderCostContext.getCostDetailContext().getSubsidyDetails(), RenterCashCodeEnum.from(cost.getCostCode())).getAmt();
            vo.setTotalAmount(cost.getTotalAmount() + subsidyAmt);
            vo.setUnitPrice(cost.getShowUnitPrice());
            costItemList.add(vo);
        });

        //取车服务费用(取车服务费 + 取车超运能溢价)
        int srvGetCost = OrderCostDetailCalculationUtil.getSrvGetCostAmt(orderCostContext.getCostDetailContext().getCostDetails(),
                orderCostContext.getCostDetailContext().getSubsidyDetails());
        int getBlockedRaiseAmt = OrderCostDetailCalculationUtil.getGetBlockedRaiseAmt(orderCostContext.getCostDetailContext().getCostDetails(),
                orderCostContext.getCostDetailContext().getSubsidyDetails());
        CostItemVO getCarCostVo = new CostItemVO();
        getCarCostVo.setCostCode(RenterCashCodeEnum.SRV_GET_COST.getCashNo());
        getCarCostVo.setCostDesc(RenterCashCodeEnum.SRV_GET_COST.getTxt());
        getCarCostVo.setCount(OrderConstant.D_ONE);
        getCarCostVo.setTotalAmount(srvGetCost + getBlockedRaiseAmt);
        getCarCostVo.setUnitPrice(Math.abs(getCarCostVo.getTotalAmount()));
        costItemList.add(getCarCostVo);

        //取车服务费用(还车服务费 + 还车超运能溢价)
        int srvReturnCost = OrderCostDetailCalculationUtil.getSrvReturnCostAmt(orderCostContext.getCostDetailContext().getCostDetails(),
                orderCostContext.getCostDetailContext().getSubsidyDetails());
        int returnBlockedRaiseAmt = OrderCostDetailCalculationUtil.getReturnBlockedRaiseAmt(orderCostContext.getCostDetailContext().getCostDetails(),
                orderCostContext.getCostDetailContext().getSubsidyDetails());
        CostItemVO returnCarCostVo = new CostItemVO();
        returnCarCostVo.setCostCode(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo());
        returnCarCostVo.setCostDesc(RenterCashCodeEnum.SRV_RETURN_COST.getTxt());
        returnCarCostVo.setCount(OrderConstant.D_ONE);
        returnCarCostVo.setTotalAmount(srvReturnCost + returnBlockedRaiseAmt);
        returnCarCostVo.setUnitPrice(Math.abs(returnCarCostVo.getTotalAmount()));
        costItemList.add(returnCarCostVo);

        logger.info("Build costItem list B.result is,costItemList:[{}]", JSON.toJSONString(costItemList));
        return costItemList;
    }


    /**
     * 获取平台保障费费用项
     *
     * @param renterOrderCostRespDTO XX
     * @param insureDiscount         XX
     * @return CostItemVO
     */
    private CostItemVO getInsurCostItemVO(RenterOrderCostRespDTO renterOrderCostRespDTO, Double insureDiscount) {
        if (renterOrderCostRespDTO == null) {
            return null;
        }
        List<RenterOrderCostDetailEntity> costDetailList = renterOrderCostRespDTO.getRenterOrderCostDetailDTOList();
        if (costDetailList == null || costDetailList.isEmpty()) {
            return null;
        }
        int unitPrice = 0;
        double count = 0.0;
        for (RenterOrderCostDetailEntity cost : costDetailList) {
            if (RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getCostCode())) {
                unitPrice = cost.getUnitPrice();
                count = cost.getCount();
                break;
            }
        }
        Integer basicEnsureAmount = renterOrderCostRespDTO.getBasicEnsureAmount();
        CostItemVO vo = new CostItemVO();
        vo.setCostCode(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo());
        vo.setCostDesc(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getTxt());
        vo.setCount(count);
        vo.setTotalAmount(basicEnsureAmount);
        vo.setUnitPrice(unitPrice);
        vo.setDiscount(insureDiscount);
        return vo;
    }


    /**
     * 获取全面保障费费用项
     *
     * @param renterOrderCostRespDTO XX
     * @param insureDiscount         XX
     * @return CostItemVO
     */
    private CostItemVO getAbatementCostItemVO(RenterOrderCostRespDTO renterOrderCostRespDTO, Double insureDiscount) {
        if (renterOrderCostRespDTO == null) {
            return null;
        }
        List<RenterOrderCostDetailEntity> costDetailList = renterOrderCostRespDTO.getRenterOrderCostDetailDTOList();
        if (costDetailList == null || costDetailList.isEmpty()) {
            return null;
        }
        int unitPrice = 0;
        double count = 0.0;
        int totalCost = 0;
        for (RenterOrderCostDetailEntity cost : costDetailList) {
            if (RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getCostCode())) {
                count += cost.getCount();
                totalCost += cost.getTotalAmount();
            }
        }
        if (totalCost != 0 && count != 0.0) {
            unitPrice = (int) Math.ceil(Math.abs(totalCost) / count);
        }
        Integer comprehensiveEnsureAmount = renterOrderCostRespDTO.getComprehensiveEnsureAmount();
        CostItemVO vo = new CostItemVO();
        vo.setCostCode(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo());
        vo.setCostDesc(RenterCashCodeEnum.ABATEMENT_INSURE.getTxt());
        vo.setCount(count);
        vo.setTotalAmount(comprehensiveEnsureAmount);
        vo.setUnitPrice(unitPrice);
        vo.setDiscount(insureDiscount);
        return vo;
    }


    /**
     * 下单前费用计算--租车费用总计
     *
     * @param costItems 租车费用列表
     * @return TotalCostVO 租车费用总计信息
     */
    public TotalCostVO buildTotalCostVO(List<CostItemVO> costItems) {

        if (CollectionUtils.isEmpty(costItems)) {
            return null;
        }
        int totalFee = costItems.stream().filter(cost -> Objects.nonNull(cost.getTotalAmount())).mapToInt(CostItemVO::getTotalAmount).sum();
        TotalCostVO totalCost = new TotalCostVO();
        totalCost.setTotalFee(totalFee);
        logger.info("Build TotalCostVO.result is,totalCost:[{}]", JSON.toJSONString(totalCost));
        return totalCost;
    }

    /**
     * 下单前费用计算--车辆押金信息
     *
     * @param renterOrderCarDepositResVO 车辆押金信息
     * @return DepositAmtVO
     */
    public DepositAmtVO buildDepositAmtVO(RenterOrderCarDepositResVO renterOrderCarDepositResVO) {
        if (null == renterOrderCarDepositResVO) {
            return null;
        }

        DepositAmtVO depositAmtVO = new DepositAmtVO();
        depositAmtVO.setDepositAmt(renterOrderCarDepositResVO.getYingfuDepositAmt());
        depositAmtVO.setReductionAmt(renterOrderCarDepositResVO.getReductionAmt());
        depositAmtVO.setReductionRate(renterOrderCarDepositResVO.getReductionRate());
        return depositAmtVO;
    }

    /**
     * 下单前费用计算--违章押金信息
     *
     * @param renterOrderIllegalResVO 违章押金信息
     * @return IllegalDepositVO
     */
    public IllegalDepositVO buildIllegalDepositVO(RenterOrderIllegalResVO renterOrderIllegalResVO) {

        if (null == renterOrderIllegalResVO) {
            return null;
        }

        IllegalDepositVO illegalDepositVO = new IllegalDepositVO();
        illegalDepositVO.setIllegalDepositAmt(renterOrderIllegalResVO.getYingfuDepositAmt());
        return illegalDepositVO;
    }


    /**
     * 依据租车费用初始化抵扣信息
     *
     * @param orderCostContext 租车费用明细
     * @return DeductContextDTO 抵扣信息公共参数
     */
    public DeductContextDTO initDeductContext(OrderCostContext orderCostContext) {

        if (null == orderCostContext || CollectionUtils.isEmpty(orderCostContext.getCostDetailContext().getCostDetails())) {
            return null;
        }

        DeductContextDTO deductContext = new DeductContextDTO();
        deductContext.setOriginalRentAmt(null == orderCostContext.getCostDetailContext().getOriginalRentAmt() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getOriginalRentAmt()));
        deductContext.setSurplusRentAmt(null == orderCostContext.getCostDetailContext().getSurplusRentAmt() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusRentAmt()));

        int srvGetCost = null == orderCostContext.getCostDetailContext().getSurplusSrvGetCost() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusSrvGetCost());

        int srvReturnCost = null == orderCostContext.getCostDetailContext().getSurplusSrvReturnCost() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusSrvReturnCost());

        int overGetCost = null == orderCostContext.getCostDetailContext().getSurplusGetBlockedRaiseAmt() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusGetBlockedRaiseAmt());
        int overReturnCost = null == orderCostContext.getCostDetailContext().getSurplusReturnBlockedRaiseAmt() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusReturnBlockedRaiseAmt());

        deductContext.setSrvGetCost(srvGetCost + overGetCost);
        deductContext.setSrvReturnCost(srvReturnCost + overReturnCost);
        logger.info("下单前费用计算--初始context数据 B.deductContext:[{}]", JSON.toJSONString(deductContext));
        return deductContext;
    }


    /**
     * 依据租车费用初始化抵扣信息
     *
     * @param renterOrderCostRespDTO 租车费用明细
     * @return DeductContextDTO 抵扣信息公共参数
     */
    public DeductContextDTO initDeductContext(RenterOrderCostRespDTO renterOrderCostRespDTO) {

        if (null == renterOrderCostRespDTO) {
            return null;
        }

        DeductContextDTO deductContext = new DeductContextDTO();
        deductContext.setOriginalRentAmt(null == renterOrderCostRespDTO.getRentAmount() ? 1 : Math.abs(renterOrderCostRespDTO.getRentAmount()));
        deductContext.setSurplusRentAmt(null == renterOrderCostRespDTO.getRentAmount() ? 1 : Math.abs(renterOrderCostRespDTO.getRentAmount()));

        int srvGetCost = null == renterOrderCostRespDTO.getGetRealAmt() ? 0 :
                Math.abs(renterOrderCostRespDTO.getGetRealAmt());

        int srvReturnCost = null == renterOrderCostRespDTO.getReturnRealAmt() ? 0 :
                Math.abs(renterOrderCostRespDTO.getReturnRealAmt());

        int overGetCost = null == renterOrderCostRespDTO.getGetOverAmt() ? 0 : Math.abs(renterOrderCostRespDTO.getGetOverAmt());
        int overReturnCost = null == renterOrderCostRespDTO.getReturnOverAmt() ? 0 :
                Math.abs(renterOrderCostRespDTO.getReturnOverAmt());

        deductContext.setSrvGetCost(srvGetCost + overGetCost);
        deductContext.setSrvReturnCost(srvReturnCost + overReturnCost);
        logger.info("下单前费用计算--初始context数据 A.deductContext:[{}]", JSON.toJSONString(deductContext));
        return deductContext;
    }

    /**
     * 封装优惠券请求参数
     *
     * @param context          租车费用相关信息
     * @param orderCostContext 租客订单请求信息
     * @return MemAvailCouponRequestVO 优惠券请求信息
     */
    public MemAvailCouponRequestVO buildMemAvailCouponRequestVO(OrderReqContext context, OrderCostContext orderCostContext) {
        MemAvailCouponRequestVO memAvailCouponRequestVO = new MemAvailCouponRequestVO();
        memAvailCouponRequestVO.setOrderNo(orderCostContext.getReqContext().getBaseReqDTO().getOrderNo());
        memAvailCouponRequestVO.setMemNo(Integer.valueOf(context.getOrderReqVO().getMemNo()));
        memAvailCouponRequestVO.setCarNo(Integer.valueOf(context.getOrderReqVO().getCarNo()));
        memAvailCouponRequestVO.setCityCode(Integer.valueOf(context.getOrderReqVO().getCityCode()));
        memAvailCouponRequestVO.setIsNew(null != context.getRenterMemberDto().getIsNew() && OrderConstant.YES == context.getRenterMemberDto().getIsNew());
        memAvailCouponRequestVO.setRentAmt(null == orderCostContext.getCostDetailContext().getSurplusRentAmt() ?
                OrderConstant.ZERO : Math.abs(orderCostContext.getCostDetailContext().getSurplusRentAmt()));
        memAvailCouponRequestVO.setInsureTotalPrices(Math.abs(OrderCostDetailCalculationUtil.getInsuranceAmt(orderCostContext.getCostDetailContext().getCostDetails(), orderCostContext.getCostDetailContext().getSubsidyDetails())));
        memAvailCouponRequestVO.setAbatement(Math.abs(OrderCostDetailCalculationUtil.getAbatementAmt(orderCostContext.getCostDetailContext().getCostDetails(), orderCostContext.getCostDetailContext().getSubsidyDetails())));

        int srvGetCost = null == orderCostContext.getCostDetailContext().getSurplusSrvGetCost() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusSrvGetCost());
        int srvReturnCost = null == orderCostContext.getCostDetailContext().getSurplusSrvReturnCost() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusSrvReturnCost());
        int overGetCost = null == orderCostContext.getCostDetailContext().getSurplusGetBlockedRaiseAmt() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusGetBlockedRaiseAmt());
        int overReturnCost = null == orderCostContext.getCostDetailContext().getSurplusReturnBlockedRaiseAmt() ? OrderConstant.ZERO :
                Math.abs(orderCostContext.getCostDetailContext().getSurplusReturnBlockedRaiseAmt());

        memAvailCouponRequestVO.setSrvGetCost(srvGetCost + overGetCost);
        memAvailCouponRequestVO.setSrvReturnCost(srvReturnCost + overReturnCost);


        int holidayAverage =
                Objects.isNull(orderCostContext.getResContext().getOrderRentAmtResDTO().getHolidayAverage()) ?
                        OrderConstant.ZERO : orderCostContext.getResContext().getOrderRentAmtResDTO().getHolidayAverage();
        memAvailCouponRequestVO.setHolidayAverage(Math.abs(holidayAverage));
        memAvailCouponRequestVO.setLabelIds(context.getRenterGoodsDetailDto().getLabelIds());
        memAvailCouponRequestVO.setRentTime(DateUtils.formateLong(context.getOrderReqVO().getRentTime(), DateUtils.DATE_DEFAUTE));
        memAvailCouponRequestVO.setRevertTime(DateUtils.formateLong(context.getOrderReqVO().getRevertTime(), DateUtils.DATE_DEFAUTE));

        memAvailCouponRequestVO.setCounterFee(Math.abs(OrderCostDetailCalculationUtil.getFeeAmt(orderCostContext.getCostDetailContext().getCostDetails(), orderCostContext.getCostDetailContext().getSubsidyDetails())));
        memAvailCouponRequestVO.setOriginalRentAmt(null == orderCostContext.getCostDetailContext().getOriginalRentAmt() ?
                OrderConstant.ZERO : Math.abs(orderCostContext.getCostDetailContext().getOriginalRentAmt()));
        logger.info("下单前费用计算--优惠券请求参数 memAvailCouponRequestVO:[{}]", JSON.toJSONString(memAvailCouponRequestVO));
        return memAvailCouponRequestVO;
    }

    /**
     * 仁云流程系统请求信息处理
     *
     * @param orderNo 主订单号
     * @param res     取消订单返回信息
     * @return CancelOrderDeliveryVO 仁云流程系统请求信息
     */
    public CancelOrderDeliveryVO buildCancelOrderDeliveryVO(String orderNo, CancelOrderResDTO res) {
        if (!res.getSrvGetFlag() && !res.getSrvReturnFlag()) {
            return null;
        }
        String servicetype = "";
        if (res.getSrvGetFlag() && res.getSrvReturnFlag()) {
            servicetype = "all";
        } else if (res.getSrvGetFlag()) {
            servicetype = "take";
        } else if (res.getSrvReturnFlag()) {
            servicetype = "back";
        }
        CancelOrderDeliveryVO cancelOrderDeliveryVO = new CancelOrderDeliveryVO();

        CancelFlowOrderDTO cancelFlowOrderDTO = new CancelFlowOrderDTO();
        cancelFlowOrderDTO.setOrdernumber(orderNo);
        cancelFlowOrderDTO.setServicetype(servicetype);

        cancelOrderDeliveryVO.setRenterOrderNo(res.getRenterOrderNo());
        cancelOrderDeliveryVO.setCancelFlowOrderDTO(cancelFlowOrderDTO);
        return cancelOrderDeliveryVO;
    }

    /**
     * 取消订单结算请求参数封装
     *
     * @param orderNo         订单号
     * @param renterOrderNo   租客订单号
     * @param ownerOrderNo    车主订单号
     * @param settleOwnerFlg  车主订单结算标识
     * @param settleRenterFlg 租客订单结算标识
     * @return CancelOrderReqDTO
     */
    public CancelOrderReqDTO buildCancelOrderReqDTO(String orderNo, String renterOrderNo, String ownerOrderNo,
                                                    Boolean settleOwnerFlg, Boolean settleRenterFlg) {

        CancelOrderReqDTO reqDTO = new CancelOrderReqDTO();
        reqDTO.setSettleRenterFlg(settleRenterFlg);
        reqDTO.setSettleOwnerFlg(settleOwnerFlg);
        reqDTO.setOrderNo(orderNo);
        reqDTO.setRenterOrderNo(renterOrderNo);
        reqDTO.setOwnerOrderNo(ownerOrderNo);
        return reqDTO;
    }


    /**
     * 初始化订单费用计算参数
     *
     * @param orderNo       订单号
     * @param renterOrderNo 租客订单号
     * @param ownerOrderNo  车主订单号
     * @param context       请求参数
     * @return OrderCostContext
     */
    public OrderCostContext initOrderCostContext(String orderNo, String renterOrderNo, String ownerOrderNo,
                                                 OrderReqContext context) {
        //请求参数
        OrderCostReqContext reqContext = new OrderCostReqContext();
        //基础参数
        reqContext.setBaseReqDTO(initOrderCostBaseReqDTO(orderNo, renterOrderNo, ownerOrderNo, context));
        //租金参数
        reqContext.setRentAmtReqDTO(initOrderCostRentAmtReqDTO(context));
        //基础保障服务费参数
        reqContext.setInsurAmtReqDTO(initOrderCostInsurAmtReqDTO(context));
        //全面保障服务参数
        reqContext.setAbatementAmtReqDTO(initOrderCostAbatementAmtReqDTO(context));
        //附加驾驶人参数
        reqContext.setExtraDriverReqDTO(initOrderCostExtraDriverReqDTO(context));
        //取还车服务费参数
        reqContext.setGetReturnCarCostReqDTO(initOrderCostGetReturnCarCostReqDTO(context));
        //取还车超运能溢价参数
        reqContext.setGetReturnCarOverCostReqDTO(initOrderCostGetReturnCarOverCostReqDTO(context));
        //长租订单租金折扣参数
        reqContext.setLongOrderOwnerCouponReqDTO(initLongOrderOwnerCouponReqDTO(context));
        //送取服务券参数
        reqContext.setOrderCostGetCarFeeCouponReqDTO(initOrderCostGetCarFeeCouponReqDTO(context));
        //车主券参数
        reqContext.setOwnerCouponReqDTO(initOrderCostOwnerCouponReqDTO(context));
        //平台优惠券参数
        reqContext.setCostCouponReqDTO(initOrderCostPlatformCouponReqDTO(context));
        //限时红包参数
        reqContext.setLimitRedReqDTO(initOrderCostLimitRedReqDTO(context));
        //凹凸币参数
        reqContext.setAutoCoinReqDTO(initOrderCostAutoCoinReqDTO(context));
        //车辆押金
        reqContext.setCostCarDepositAmtReqDTO(initOrderCostCarDepositAmtReqDTO(context));
        //违章押金
        reqContext.setViolationDepositAmtReqDTO(initOrderCostViolationDepositAmtReqDTO(context));
        //费用清单
        OrderCostResContext resContext = new OrderCostResContext();
        //公共参数(用于费用迭代变更)
        OrderCostDetailContext orderCostDetailContext = new OrderCostDetailContext();
        return new OrderCostContext(reqContext, resContext, orderCostDetailContext);
    }

    /**
     * 初始化基础参数
     *
     * @param orderNo       订单号
     * @param renterOrderNo 租客订单号
     * @param ownerOrderNo  车主订单号
     * @param context       请求参数
     * @return OrderCostBaseReqDTO
     */
    public OrderCostBaseReqDTO initOrderCostBaseReqDTO(String orderNo, String renterOrderNo,
                                                       String ownerOrderNo, OrderReqContext context) {
        OrderCostBaseReqDTO baseReqDTO = new OrderCostBaseReqDTO();
        baseReqDTO.setOrderNo(orderNo);
        baseReqDTO.setRenterOrderNo(renterOrderNo);
        baseReqDTO.setOwnerOrderNo(ownerOrderNo);
        baseReqDTO.setMemNo(context.getRenterMemberDto().getMemNo());
        baseReqDTO.setOwnerMemNo(context.getOwnerMemberDto().getMemNo());
        baseReqDTO.setStartTime(context.getOrderReqVO().getRentTime());
        baseReqDTO.setEndTime(context.getOrderReqVO().getRevertTime());
        logger.info("Init OrderCostBaseReqDTO.result is,baseReqDTO:[{}]", JSON.toJSONString(baseReqDTO));
        return baseReqDTO;
    }


    /**
     * 初始化计算租金参数
     *
     * @param context 请求参数
     * @return OrderCostRentAmtReqDTO
     */
    public OrderCostRentAmtReqDTO initOrderCostRentAmtReqDTO(OrderReqContext context) {
        OrderCostRentAmtReqDTO orderCostRentAmtReqDTO = new OrderCostRentAmtReqDTO();
        orderCostRentAmtReqDTO.setRenterGoodsPriceDetailDTOList(context.getRenterGoodsDetailDto().getRenterGoodsPriceDetailDTOList());
        logger.info("Init OrderCostRentAmtReqDTO.result is,orderCostRentAmtReqDTO:[{}]", JSON.toJSONString(orderCostRentAmtReqDTO));
        return orderCostRentAmtReqDTO;
    }

    /**
     * 初始化计算基础保险费参数
     *
     * @param context 请求参数
     * @return OrderCostInsurAmtReqDTO
     */
    public OrderCostInsurAmtReqDTO initOrderCostInsurAmtReqDTO(OrderReqContext context) {

        OrderCostInsurAmtReqDTO orderCostInsurAmtReqDTO = new OrderCostInsurAmtReqDTO();
        orderCostInsurAmtReqDTO.setCertificationTime(context.getRenterMemberDto().getCertificationTime());
        orderCostInsurAmtReqDTO.setGetCarBeforeTime(null == context.getCarRentTimeRangeDTO() || null == context.getCarRentTimeRangeDTO().getGetMinutes() ? OrderConstant.ZERO :
                context.getCarRentTimeRangeDTO().getGetMinutes());
        orderCostInsurAmtReqDTO.setReturnCarAfterTime(null == context.getCarRentTimeRangeDTO() || null == context.getCarRentTimeRangeDTO().getReturnMinutes() ? OrderConstant.ZERO :
                context.getCarRentTimeRangeDTO().getReturnMinutes());
        orderCostInsurAmtReqDTO.setCarLabelIds(context.getRenterGoodsDetailDto().getLabelIds());
        orderCostInsurAmtReqDTO.setGuidPrice(context.getRenterGoodsDetailDto().getCarGuidePrice());
        orderCostInsurAmtReqDTO.setInmsrp(context.getRenterGoodsDetailDto().getCarInmsrp());
        logger.info("Init OrderCostInsurAmtReqDTO.result is,orderCostInsurAmtReqDTO:[{}]", JSON.toJSONString(orderCostInsurAmtReqDTO));
        return orderCostInsurAmtReqDTO;
    }

    /**
     * 初始化计算全面保障服务参数
     *
     * @param context 请求参数
     * @return OrderCostAbatementAmtReqDTO
     */
    public OrderCostAbatementAmtReqDTO initOrderCostAbatementAmtReqDTO(OrderReqContext context) {
        OrderCostAbatementAmtReqDTO orderCostAbatementAmtReqDTO = new OrderCostAbatementAmtReqDTO();
        orderCostAbatementAmtReqDTO.setCertificationTime(context.getRenterMemberDto().getCertificationTime());
        orderCostAbatementAmtReqDTO.setGetCarBeforeTime(null == context.getCarRentTimeRangeDTO() || null == context.getCarRentTimeRangeDTO().getGetMinutes() ? OrderConstant.ZERO :
                context.getCarRentTimeRangeDTO().getGetMinutes());
        orderCostAbatementAmtReqDTO.setReturnCarAfterTime(null == context.getCarRentTimeRangeDTO() || null == context.getCarRentTimeRangeDTO().getReturnMinutes() ? OrderConstant.ZERO :
                context.getCarRentTimeRangeDTO().getReturnMinutes());
        orderCostAbatementAmtReqDTO.setCarLabelIds(context.getRenterGoodsDetailDto().getLabelIds());
        orderCostAbatementAmtReqDTO.setGuidPrice(context.getRenterGoodsDetailDto().getCarGuidePrice());
        orderCostAbatementAmtReqDTO.setInmsrp(context.getRenterGoodsDetailDto().getCarInmsrp());
        orderCostAbatementAmtReqDTO.setIsAbatement(null != context.getOrderReqVO().getAbatement() && context.getOrderReqVO().getAbatement() == OrderConstant.YES);
        logger.info("Init OrderCostAbatementAmtReqDTO.result is,orderCostAbatementAmtReqDTO:[{}]", JSON.toJSONString(orderCostAbatementAmtReqDTO));
        return orderCostAbatementAmtReqDTO;
    }

    /**
     * 初始化计算附加驾驶人保险参数
     *
     * @param context 请求参数
     * @return OrderCostExtraDriverReqDTO
     */
    public OrderCostExtraDriverReqDTO initOrderCostExtraDriverReqDTO(OrderReqContext context) {
        OrderCostExtraDriverReqDTO orderCostExtraDriverReqDTO = new OrderCostExtraDriverReqDTO();
        if (StringUtils.isNotBlank(context.getOrderReqVO().getDriverIds())) {
            orderCostExtraDriverReqDTO.setDriverIds(ListUtil.parseString(context.getOrderReqVO().getDriverIds(), ","));
        }
        logger.info("Init OrderCostExtraDriverReqDTO.result is,orderCostExtraDriverReqDTO:[{}]", JSON.toJSONString(orderCostExtraDriverReqDTO));
        return orderCostExtraDriverReqDTO;
    }

    /**
     * 初始化计算取还车服务费参数
     *
     * @param context 请求参数
     * @return OrderCostGetReturnCarCostReqDTO
     */
    public OrderCostGetReturnCarCostReqDTO initOrderCostGetReturnCarCostReqDTO(OrderReqContext context) {
        OrderCostGetReturnCarCostReqDTO orderCostGetReturnCarCostReqDTO = new OrderCostGetReturnCarCostReqDTO();
        orderCostGetReturnCarCostReqDTO.setCarShowLat(context.getRenterGoodsDetailDto().getCarShowLat());
        orderCostGetReturnCarCostReqDTO.setCarShowLon(context.getRenterGoodsDetailDto().getCarShowLon());
        orderCostGetReturnCarCostReqDTO.setCarRealLat(context.getRenterGoodsDetailDto().getCarRealLat());
        orderCostGetReturnCarCostReqDTO.setCarRealLon(context.getRenterGoodsDetailDto().getCarRealLon());
        orderCostGetReturnCarCostReqDTO.setCityCode(Integer.valueOf(context.getOrderReqVO().getCityCode()));
        orderCostGetReturnCarCostReqDTO.setEntryCode(context.getOrderReqVO().getSceneCode());
        orderCostGetReturnCarCostReqDTO.setSource(Integer.valueOf(context.getOrderReqVO().getSource()));
        orderCostGetReturnCarCostReqDTO.setSrvGetLat(context.getOrderReqVO().getSrvGetLat());
        orderCostGetReturnCarCostReqDTO.setSrvGetLon(context.getOrderReqVO().getSrvGetLon());
        orderCostGetReturnCarCostReqDTO.setSrvReturnLon(context.getOrderReqVO().getSrvReturnLon());
        orderCostGetReturnCarCostReqDTO.setSrvReturnLat(context.getOrderReqVO().getSrvReturnLat());
        orderCostGetReturnCarCostReqDTO.setIsPackageOrder(StringUtils.equals("2", context.getOrderReqVO().getOrderCategory()));
        orderCostGetReturnCarCostReqDTO.setIsGetCarCost(null != context.getOrderReqVO().getSrvGetFlag() && OrderConstant.YES == context.getOrderReqVO().getSrvGetFlag());
        orderCostGetReturnCarCostReqDTO.setIsReturnCarCost(null != context.getOrderReqVO().getSrvReturnFlag() && OrderConstant.YES == context.getOrderReqVO().getSrvReturnFlag());
        logger.info("Init OrderCostGetReturnCarCostReqDTO.result is,orderCostGetReturnCarCostReqDTO:[{}]", JSON.toJSONString(orderCostGetReturnCarCostReqDTO));
        return orderCostGetReturnCarCostReqDTO;
    }


    /**
     * 初始化计算取还车超运能溢价参数
     *
     * @param context 请求参数
     * @return OrderCostGetReturnCarOverCostReqDTO
     */
    public OrderCostGetReturnCarOverCostReqDTO initOrderCostGetReturnCarOverCostReqDTO(OrderReqContext context) {
        OrderCostGetReturnCarOverCostReqDTO orderCostGetReturnCarOverCostReqDTO =
                new OrderCostGetReturnCarOverCostReqDTO();

        orderCostGetReturnCarOverCostReqDTO.setCityCode(Integer.valueOf(context.getOrderReqVO().getCityCode()));
        orderCostGetReturnCarOverCostReqDTO.setIsGetCarCost(null != context.getOrderReqVO().getSrvGetFlag() && OrderConstant.YES == context.getOrderReqVO().getSrvGetFlag());
        orderCostGetReturnCarOverCostReqDTO.setIsReturnCarCost(null != context.getOrderReqVO().getSrvReturnFlag() && OrderConstant.YES == context.getOrderReqVO().getSrvReturnFlag());
        if (StringUtils.isNotBlank(context.getOrderReqVO().getOrderCategory())) {
            orderCostGetReturnCarOverCostReqDTO.setOrderCategory(Integer.valueOf(context.getOrderReqVO().getOrderCategory()));
        }
        logger.info("Init OrderCostGetReturnCarOverCostReqDTO.result is,orderCostGetReturnCarOverCostReqDTO:[{}]", JSON.toJSONString(orderCostGetReturnCarOverCostReqDTO));
        return orderCostGetReturnCarOverCostReqDTO;
    }

    /**
     * 初始化计算长租租金折扣参数
     *
     * @param context 请求参数
     * @return LongOrderOwnerCouponReqDTO
     */
    public LongOrderOwnerCouponReqDTO initLongOrderOwnerCouponReqDTO(OrderReqContext context) {
        LongOrderOwnerCouponReqDTO longOrderOwnerCouponReqDTO = new LongOrderOwnerCouponReqDTO();
        longOrderOwnerCouponReqDTO.setCarNo(context.getOrderReqVO().getCarNo());
        longOrderOwnerCouponReqDTO.setCouponCode(context.getOrderReqVO().getLongOwnerCouponNo());
        logger.info("Init LongOrderOwnerCouponReqDTO.result is,longOrderOwnerCouponReqDTO:[{}]", JSON.toJSONString(longOrderOwnerCouponReqDTO));
        return longOrderOwnerCouponReqDTO;
    }

    /**
     * 初始化计算取送服务券参数
     *
     * @param context 请求参数
     * @return OrderCostGetCarFeeCouponReqDTO
     */
    public OrderCostGetCarFeeCouponReqDTO initOrderCostGetCarFeeCouponReqDTO(OrderReqContext context) {
        OrderCostGetCarFeeCouponReqDTO orderCostGetCarFeeCouponReqDTO = new OrderCostGetCarFeeCouponReqDTO();
        orderCostGetCarFeeCouponReqDTO.setCarNo(Integer.valueOf(context.getOrderReqVO().getCarNo()));
        orderCostGetCarFeeCouponReqDTO.setCityCode(Integer.valueOf(context.getOrderReqVO().getCityCode()));
        orderCostGetCarFeeCouponReqDTO.setGetCarFreeCouponId(context.getOrderReqVO().getGetCarFreeCouponId());
        orderCostGetCarFeeCouponReqDTO.setIsNew(null == context.getRenterMemberDto().getIsNew() || context.getRenterMemberDto().getIsNew() == OrderConstant.NO);
        orderCostGetCarFeeCouponReqDTO.setLabelIds(context.getRenterGoodsDetailDto().getLabelIds());
        logger.info("Init OrderCostGetCarFeeCouponReqDTO.result is,orderCostGetCarFeeCouponReqDTO:[{}]", JSON.toJSONString(orderCostGetCarFeeCouponReqDTO));
        return orderCostGetCarFeeCouponReqDTO;
    }

    /**
     * 初始化计算车主券参数
     *
     * @param context 请求参数
     * @return OrderCostOwnerCouponReqDTO
     */
    public OrderCostOwnerCouponReqDTO initOrderCostOwnerCouponReqDTO(OrderReqContext context) {
        OrderCostOwnerCouponReqDTO orderCostOwnerCouponReqDTO = new OrderCostOwnerCouponReqDTO();
        orderCostOwnerCouponReqDTO.setCarNo(Integer.valueOf(context.getOrderReqVO().getCarNo()));
        orderCostOwnerCouponReqDTO.setCouponNo(context.getOrderReqVO().getCarOwnerCouponNo());
        orderCostOwnerCouponReqDTO.setMark(OrderConstant.ONE);

        logger.info("Init OrderCostOwnerCouponReqDTO.result is,orderCostOwnerCouponReqDTO:[{}]", JSON.toJSONString(orderCostOwnerCouponReqDTO));
        return orderCostOwnerCouponReqDTO;
    }

    /**
     * 初始化计算平台优惠券参数
     *
     * @param context 请求参数
     * @return OrderCostPlatformCouponReqDTO
     */
    public OrderCostPlatformCouponReqDTO initOrderCostPlatformCouponReqDTO(OrderReqContext context) {
        OrderCostPlatformCouponReqDTO orderCostPlatformCouponReqDTO = new OrderCostPlatformCouponReqDTO();
        orderCostPlatformCouponReqDTO.setCarNo(Integer.valueOf(context.getOrderReqVO().getCarNo()));
        orderCostPlatformCouponReqDTO.setCityCode(Integer.valueOf(context.getOrderReqVO().getCityCode()));
        orderCostPlatformCouponReqDTO.setDisCouponId(context.getOrderReqVO().getDisCouponIds());
        orderCostPlatformCouponReqDTO.setIsNew(null == context.getRenterMemberDto().getIsNew() || context.getRenterMemberDto().getIsNew() == OrderConstant.NO);
        orderCostPlatformCouponReqDTO.setLabelIds(context.getRenterGoodsDetailDto().getLabelIds());
        logger.info("Init OrderCostPlatformCouponReqDTO.result is,orderCostPlatformCouponReqDTO:[{}]", JSON.toJSONString(orderCostPlatformCouponReqDTO));
        return orderCostPlatformCouponReqDTO;
    }

    /**
     * 初始化计算限时红包参数
     *
     * @param context 请求参数
     * @return OrderCostLimitRedReqDTO
     */
    public OrderCostLimitRedReqDTO initOrderCostLimitRedReqDTO(OrderReqContext context) {
        OrderCostLimitRedReqDTO orderCostLimitRedReqDTO = new OrderCostLimitRedReqDTO();
        if (StringUtils.isNotBlank(context.getOrderReqVO().getReductiAmt())) {
            orderCostLimitRedReqDTO.setReductiAmt(Integer.valueOf(context.getOrderReqVO().getReductiAmt()));
        }
        logger.info("Init OrderCostLimitRedReqDTO.result is,orderCostLimitRedReqDTO:[{}]", JSON.toJSONString(orderCostLimitRedReqDTO));
        return orderCostLimitRedReqDTO;
    }

    /**
     * 初始化计算凹凸币参数
     *
     * @param context 请求参数
     * @return OrderCostAutoCoinReqDTO
     */
    public OrderCostAutoCoinReqDTO initOrderCostAutoCoinReqDTO(OrderReqContext context) {
        OrderCostAutoCoinReqDTO orderCostAutoCoinReqDTO = new OrderCostAutoCoinReqDTO();
        orderCostAutoCoinReqDTO.setUseAutoCoin(context.getOrderReqVO().getUseAutoCoin());
        logger.info("Init OrderCostAutoCoinReqDTO.result is,orderCostAutoCoinReqDTO:[{}]", JSON.toJSONString(orderCostAutoCoinReqDTO));
        return orderCostAutoCoinReqDTO;
    }

    /**
     * 初始化计算车辆押金参数
     *
     * @param context 请求参数
     * @return OrderCostCarDepositAmtReqDTO
     */
    public OrderCostCarDepositAmtReqDTO initOrderCostCarDepositAmtReqDTO(OrderReqContext context) {
        OrderCostCarDepositAmtReqDTO orderCostCarDepositAmtReqDTO = new OrderCostCarDepositAmtReqDTO();
        orderCostCarDepositAmtReqDTO.setCityCode(Integer.valueOf(context.getOrderReqVO().getCityCode()));
        orderCostCarDepositAmtReqDTO.setFreeDoubleTypeId(StringUtils.isBlank(context.getOrderReqVO().getFreeDoubleTypeId()) ? null : Integer.valueOf(context.getOrderReqVO().getFreeDoubleTypeId()));
        orderCostCarDepositAmtReqDTO.setGuidPrice(context.getRenterGoodsDetailDto().getCarGuidePrice());
        orderCostCarDepositAmtReqDTO.setSurplusPrice(context.getRenterGoodsDetailDto().getCarSurplusPrice());
        orderCostCarDepositAmtReqDTO.setBrand(context.getRenterGoodsDetailDto().getBrand());
        orderCostCarDepositAmtReqDTO.setType(context.getRenterGoodsDetailDto().getType());
        orderCostCarDepositAmtReqDTO.setLicenseDay(context.getRenterGoodsDetailDto().getLicenseDay());
        orderCostCarDepositAmtReqDTO.setRenterMemberRightDTOList(context.getRenterMemberDto().getRenterMemberRightDTOList());
        logger.info("Init OrderCostCarDepositAmtReqDTO.result is,orderCostCarDepositAmtReqDTO:[{}]", JSON.toJSONString(orderCostCarDepositAmtReqDTO));
        return orderCostCarDepositAmtReqDTO;
    }

    /**
     * 初始化计算违章押金参数
     *
     * @param context 请求参数
     * @return OrderCostViolationDepositAmtReqDTO
     */
    public OrderCostViolationDepositAmtReqDTO initOrderCostViolationDepositAmtReqDTO(OrderReqContext context) {
        OrderCostViolationDepositAmtReqDTO violationDepositAmtReqDTO = new OrderCostViolationDepositAmtReqDTO();

        violationDepositAmtReqDTO.setCityCode(Integer.valueOf(context.getOrderReqVO().getCityCode()));
        violationDepositAmtReqDTO.setFreeDoubleTypeId(StringUtils.isBlank(context.getOrderReqVO().getFreeDoubleTypeId()) ? null : Integer.valueOf(context.getOrderReqVO().getFreeDoubleTypeId()));
        violationDepositAmtReqDTO.setCarPlateNum(context.getRenterGoodsDetailDto().getCarPlateNum());
        violationDepositAmtReqDTO.setRenterMemberRightDTOList(context.getRenterMemberDto().getRenterMemberRightDTOList());
        logger.info("Init OrderCostViolationDepositAmtReqDTO.result is,violationDepositAmtReqDTO:[{}]", JSON.toJSONString(violationDepositAmtReqDTO));
        return violationDepositAmtReqDTO;
    }



    /**
     * 构建下单落库数据
     *
     * @param context     请求参数
     * @param costContext 费用明细
     * @return CreateRenterOrderDataReqDTO
     */
    public CreateRenterOrderDataReqDTO buildCreateRenterOrderDataReqDTO(OrderReqContext context, OrderCostContext costContext) {

        CreateRenterOrderDataReqDTO createRenterOrderDataReqDTO = new CreateRenterOrderDataReqDTO();
        //租客订单信息
        createRenterOrderDataReqDTO.setRenterOrderEntity(buildRenterOrderEntity(costContext.getReqContext().getBaseReqDTO().getOrderNo(), costContext.getReqContext().getBaseReqDTO().getRenterOrderNo(), context));
        //优惠券列表
        createRenterOrderDataReqDTO.setOrderCouponList(costContext.getCostDetailContext().getCoupons());
        //长租订单折扣信息
        if(Objects.nonNull(costContext.getResContext().getLongOrderOwnerCouponResDTO())) {
            createRenterOrderDataReqDTO.setOwnerCouponLongEntity(costContext.getResContext().getLongOrderOwnerCouponResDTO().getOwnerCouponLongEntity());
        }
        //车辆押金详情
        if(Objects.nonNull(costContext.getResContext().getOrderCarDepositAmtResDTO())) {
            createRenterOrderDataReqDTO.setRenterDepositDetailEntity(costContext.getResContext().getOrderCarDepositAmtResDTO().getDepositDetailEntity());
        }
        //附加驾驶人信息
        if (StringUtils.isNotBlank(context.getOrderReqVO().getDriverIds())) {
            List<String> driverIds = ListUtil.parseString(context.getOrderReqVO().getDriverIds(), ",");
            createRenterOrderDataReqDTO.setRenterAdditionalDriverEntities(
                    buildRenterAdditionalDriverEntitys(
                            costContext.getReqContext().getBaseReqDTO().getOrderNo(),
                            costContext.getReqContext().getBaseReqDTO().getRenterOrderNo(),
                            context.getRenterMemberDto().getCommUseDriverList(), driverIds));
        }
        //订单费用明细
        createRenterOrderDataReqDTO.setRenterOrderCostRespDTO(buildRenterOrderCostRespDTO(costContext));
        return createRenterOrderDataReqDTO;
    }


    /**
     * 构建租客订单信息
     *
     * @param orderNo       订单号
     * @param renterOrderNo 租客订单号
     * @param context       请求参数
     * @return RenterOrderEntity 租客订单信息
     */
    public RenterOrderEntity buildRenterOrderEntity(String orderNo, String renterOrderNo, OrderReqContext context) {

        OrderReqVO orderReqVO = context.getOrderReqVO();

        RenterOrderEntity record = new RenterOrderEntity();
        record.setOrderNo(orderNo);
        record.setRenterOrderNo(renterOrderNo);
        record.setExpRentTime(orderReqVO.getRentTime());
        record.setExpRevertTime(orderReqVO.getRevertTime());
        record.setGoodsCode(orderReqVO.getCarNo());
        record.setGoodsType(String.valueOf(OrderConstant.ONE));
        record.setAgreeFlag(null == context.getRenterGoodsDetailDto().getReplyFlag() ? OrderConstant.NO :
                context.getRenterGoodsDetailDto().getReplyFlag());
        record.setReqAcceptTime(record.getAgreeFlag() == OrderConstant.NO ? null : LocalDateTime.now());
        record.setIsUseCoin(orderReqVO.getUseAutoCoin());
        record.setIsUseWallet(orderReqVO.getUseBal());
        if (StringUtils.isNotBlank(orderReqVO.getDriverIds())) {
            String[] driverIds = orderReqVO.getDriverIds().split(",");
            record.setAddDriver(driverIds.length);
        } else {
            record.setAddDriver(OrderConstant.ZERO);
        }
        record.setIsUseCoupon(StringUtils.isNotBlank(orderReqVO.getCarOwnerCouponNo())
                || StringUtils.isNotBlank(orderReqVO.getDisCouponIds())
                || StringUtils.isNotBlank(orderReqVO.getGetCarFreeCouponId()) ? OrderConstant.YES : OrderConstant.NO);
        record.setIsGetCar(orderReqVO.getSrvGetFlag());
        record.setIsReturnCar(orderReqVO.getSrvReturnFlag());
        record.setIsAbatement(Objects.isNull(orderReqVO.getAbatement()) ? OrderConstant.NO : orderReqVO.getAbatement());
        record.setIsUseSpecialPrice(Objects.isNull(orderReqVO.getUseSpecialPrice()) ? OrderConstant.NO :
                Integer.valueOf(orderReqVO.getUseSpecialPrice()));
        record.setChildStatus(RenterChildStatusEnum.PROCESS_ING.getCode());
        record.setRenterMemNo(orderReqVO.getMemNo());
        record.setCreateOp(orderReqVO.getOperator());
        record.setChangeSource(orderReqVO.getChangeSource());
        logger.info("Build RenterOrderEntity.result is,record:[{}]",
                JSON.toJSONString(record));
        return record;
    }


    /**
     * 构建订单附加驾驶人列表
     *
     * @param orderNo           订单号
     * @param renterOrderNo     租客订单号
     * @param commUseDriverList 租客附加驾驶人列表
     * @param driverIds         订单附加驾驶人ID列表
     * @return List<RenterAdditionalDriverEntity> 附加驾驶人列表
     */
    public List<RenterAdditionalDriverEntity> buildRenterAdditionalDriverEntitys(String orderNo,
                                                                                 String renterOrderNo,
                                                                                 List<CommUseDriverInfoDTO> commUseDriverList,
                                                                                 List<String> driverIds) {
        List<RenterAdditionalDriverEntity> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(driverIds) && !CollectionUtils.isEmpty(commUseDriverList)) {
            for (CommUseDriverInfoDTO commUseDriverInfo : commUseDriverList) {
                if (null != commUseDriverInfo.getId() && driverIds.contains(commUseDriverInfo.getId().toString())) {
                    RenterAdditionalDriverEntity record = new RenterAdditionalDriverEntity();
                    record.setOrderNo(orderNo);
                    record.setRenterOrderNo(renterOrderNo);
                    record.setDriverId(String.valueOf(commUseDriverInfo.getId()));
                    record.setRealName(commUseDriverInfo.getRealName());
                    record.setPhone(String.valueOf(commUseDriverInfo.getMobile()));
                    record.setIdCard(commUseDriverInfo.getIdCard());
                    record.setDriLicAllowCar(commUseDriverInfo.getDriLicAllowCar());
                    record.setValidityStartDate(commUseDriverInfo.getValidityStartDate());
                    record.setValidityEndDate(commUseDriverInfo.getValidityEndDate());

                    //添加操作人
                    record.setCreateOp(commUseDriverInfo.getConsoleOperatorName());
                    record.setUpdateOp(commUseDriverInfo.getConsoleOperatorName());

                    list.add(record);
                }
            }
        }
        logger.info("Build RenterAdditionalDriverEntity.result is,list:[{}]",
                JSON.toJSONString(list));
        return list;
    }

    /**
     * 构建租客订单费用明细
     *
     * @param costContext 订单费用明细
     * @return RenterOrderCostRespDTO
     */
    public RenterOrderCostRespDTO buildRenterOrderCostRespDTO(OrderCostContext costContext) {
        RenterOrderCostRespDTO respDTO = new RenterOrderCostRespDTO();
        respDTO.setOrderNo(costContext.getReqContext().getBaseReqDTO().getOrderNo());
        respDTO.setRenterOrderNo(costContext.getReqContext().getBaseReqDTO().getRenterOrderNo());
        respDTO.setMemNo(costContext.getReqContext().getBaseReqDTO().getMemNo());

        //费用明细列表
        List<RenterOrderCostDetailEntity> costDetails = costContext.getCostDetailContext().getCostDetails();
        //补贴明细列表
        List<RenterOrderSubsidyDetailDTO> subsidyDetails = costContext.getCostDetailContext().getSubsidyDetails();
        //租金
        respDTO.setRentAmount(OrderCostDetailCalculationUtil.getOrderRentAmt(costDetails, subsidyDetails));
        //基础保障费
        respDTO.setBasicEnsureAmount(OrderCostDetailCalculationUtil.getInsuranceAmt(costDetails, subsidyDetails));
        //全面保障费
        respDTO.setComprehensiveEnsureAmount(OrderCostDetailCalculationUtil.getAbatementAmt(costDetails, subsidyDetails));
        //手续费
        respDTO.setCommissionAmount(OrderCostDetailCalculationUtil.getFeeAmt(costDetails).getAmt());
        //附加驾驶人保障费用
        respDTO.setAdditionalDrivingEnsureAmount(OrderCostDetailCalculationUtil.getExtraDriverInsureAmt(costDetails).getAmt());
        //取车费用
        respDTO.setGetRealAmt(OrderCostDetailCalculationUtil.getSrvGetCostAmt(costDetails, subsidyDetails));
        //还车费用
        respDTO.setReturnRealAmt(OrderCostDetailCalculationUtil.getSrvReturnCostAmt(costDetails, subsidyDetails));
        //取车超运能溢价
        respDTO.setGetOverAmt(OrderCostDetailCalculationUtil.getGetBlockedRaiseAmt(costDetails, subsidyDetails));
        //还车超运能溢价
        respDTO.setReturnOverAmt(OrderCostDetailCalculationUtil.getReturnBlockedRaiseAmt(costDetails, subsidyDetails));
        //租车费用 = 租金+平台保障费+全面保障费+取还车费用+取还车超运能费用+附加驾驶员费用+手续费；
        int rentCarAmount =
                respDTO.getRentAmount() +
                        respDTO.getBasicEnsureAmount() +
                        respDTO.getComprehensiveEnsureAmount() +
                        respDTO.getGetRealAmt() + respDTO.getGetOverAmt() +
                        respDTO.getReturnRealAmt() + respDTO.getReturnOverAmt() +
                        respDTO.getAdditionalDrivingEnsureAmount() + respDTO.getCommissionAmount();
        respDTO.setRentCarAmount(rentCarAmount);

        respDTO.setRenterOrderCostDetailDTOList(costDetails);
        respDTO.setRenterOrderSubsidyDetailDTOList(subsidyDetails);

        logger.info("Build RenterOrderCostRespDTO.result is,respDTO:[{}]",
                JSON.toJSONString(respDTO));
        return respDTO;
    }

}
