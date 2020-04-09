package com.atzuche.order.coreapi.common.conver;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.ListUtil;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.order.CostItemVO;
import com.atzuche.order.commons.vo.res.order.DepositAmtVO;
import com.atzuche.order.commons.vo.res.order.IllegalDepositVO;
import com.atzuche.order.commons.vo.res.order.TotalCostVO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.renterorder.entity.dto.DeductContextDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.vo.RenterOrderCarDepositResVO;
import com.atzuche.order.renterorder.vo.RenterOrderIllegalResVO;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.atzuche.order.settle.vo.req.CancelOrderReqDTO;
import com.autoyol.platformcost.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
     * @param carRentTimeRangeResVO 提前延后信息
     * @return RenterOrderReqVO 租客订单请求参数
     */
    public RenterOrderReqVO buildRenterOrderReqVO(String orderNo, String renterOrderNo, OrderReqContext reqContext,
                                                  CarRentTimeRangeResVO carRentTimeRangeResVO) {

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
        renterOrderReqVO.setGetCarBeforeTime(null == carRentTimeRangeResVO || null == carRentTimeRangeResVO.getGetMinutes() ? 0 : carRentTimeRangeResVO.getGetMinutes());
        renterOrderReqVO.setReturnCarAfterTime(null == carRentTimeRangeResVO || null == carRentTimeRangeResVO.getReturnMinutes() ? 0 :
                carRentTimeRangeResVO.getReturnMinutes());

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
     * @return List<CostItemVO>
     */
    public List<CostItemVO> buildCostItemList(RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO) {
        logger.info("Build costItem list.param is,renterOrderCostRespDTO:[{}]", JSON.toJSONString(renterOrderCostRespDTO));
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

        // 获取保险和不计免赔的折扣
        double insureDiscount = CommonUtils.getInsureDiscount(renterOrderReqVO.getRentTime(), renterOrderReqVO.getRevertTime());
        // 平台保障费用项
        costItemList.add(getInsurCostItemVO(renterOrderCostRespDTO, insureDiscount));
        // 全面保障费用项
        costItemList.add(getAbatementCostItemVO(renterOrderCostRespDTO, insureDiscount));
        logger.info("Build costItem list.result is,costItemList:[{}]", JSON.toJSONString(costItemList));
        return costItemList;
    }


    /**
     * 获取平台保障费费用项
     *
     * @param renterOrderCostRespDTO
     * @param insureDiscount
     * @return
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
     * @param renterOrderCostRespDTO
     * @param insureDiscount
     * @return
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
        int totalFee = costItems.stream().mapToInt(CostItemVO::getTotalAmount).sum();
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
        logger.info("下单前费用计算--初始context数据.deductContext:[{}]", JSON.toJSONString(deductContext));
        return deductContext;
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



}
