package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.LongOrderOwnerCouponReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.LongOrderOwnerCouponResDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderRentAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.service.CouponAndCoinHandleService;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.entity.vo.HolidayAverageDateTimeVO;
import com.atzuche.order.rentercost.entity.vo.HolidayAverageResultVO;
import com.atzuche.order.rentercost.entity.vo.OwnerCouponLongVO;
import com.atzuche.order.renterorder.entity.OwnerCouponLongEntity;
import com.atzuche.order.renterorder.service.RenterOrderCostHandleService;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponLongReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponLongResVO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 计算长租订单租金折扣信息
 * <p><font color = red>注:只抵扣租金且优先抵扣</font></p>
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:56
 */
@Service
@Slf4j
public class LongOrderOwnerCouponFilter implements OrderCostFilter {

    @Autowired
    private CouponAndCoinHandleService couponAndCoinHandleService;
    @Autowired
    private RenterOrderCostHandleService renterOrderCostHandleService;


    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        LongOrderOwnerCouponReqDTO longOrderOwnerCouponReqDTO = context.getReqContext().getLongOrderOwnerCouponReqDTO();
        log.info("订单费用计算-->长租订单租金折扣.param is,baseReqDTO:[{}],longOrderOwnerCouponReqDTO:[{}]",
                JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(longOrderOwnerCouponReqDTO));

        if (Objects.isNull(longOrderOwnerCouponReqDTO) || StringUtils.isBlank(longOrderOwnerCouponReqDTO.getCouponCode())) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算长租订单租金折扣信息参数为空!");
        }

        OrderRentAmtResDTO orderRentAmtResDTO = context.getResContext().getOrderRentAmtResDTO();
        if (Objects.isNull(orderRentAmtResDTO) || CollectionUtils.isEmpty(orderRentAmtResDTO.getDetails())) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算长租订单租金折扣信息租金信息为空!");
        }
        //获取折扣信息
        List<RenterOrderSubsidyDetailDTO> subsidyDetails = new ArrayList<>();
        List<RenterOrderCostDetailEntity> details = orderRentAmtResDTO.getDetails();
        OwnerCouponLongReqVO reqVO = buildOwnerCouponLongReqVO(baseReqDTO, longOrderOwnerCouponReqDTO);
        List<HolidayAverageDateTimeVO> ownerUnitPriceVOS = new ArrayList<HolidayAverageDateTimeVO>();
        details.forEach(d -> {
            HolidayAverageDateTimeVO ownerUnitPrice = new HolidayAverageDateTimeVO();
            ownerUnitPrice.setRentOriginalUnitPriceAmt(d.getUnitPrice());
            ownerUnitPrice.setRevertTime(reqVO.getRevertTime());
            ownerUnitPriceVOS.add(ownerUnitPrice);
        });
        reqVO.setOwnerUnitPriceVOS(ownerUnitPriceVOS);
        OwnerCouponLongVO resVO = couponAndCoinHandleService.getLongOwnerCoupon(reqVO);

        log.info("订单费用计算-->长租订单租金折扣.resVO:[{}]", JSON.toJSONString(resVO));
        OwnerCouponLongEntity ownerCouponLongEntity = null;
        if (Objects.nonNull(resVO) && CollectionUtils.isNotEmpty(resVO.getOwnerUnitPriceRespVOS())) {
            List<HolidayAverageResultVO> holidayAverageResultList = resVO.getOwnerUnitPriceRespVOS();

            ownerCouponLongEntity = new OwnerCouponLongEntity();
            ownerCouponLongEntity.setOrderNo(baseReqDTO.getOrderNo());
            ownerCouponLongEntity.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
            ownerCouponLongEntity.setRenterMemNo(baseReqDTO.getMemNo());
            ownerCouponLongEntity.setCouponCode(longOrderOwnerCouponReqDTO.getCouponCode());
            ownerCouponLongEntity.setOriginalUnitPrice(holidayAverageResultList.get(0).getRentOriginalUnitPriceAmt());
            ownerCouponLongEntity.setActUnitPrice(holidayAverageResultList.get(0).getActRentUnitPriceAmt());
            ownerCouponLongEntity.setDiscounRatio(holidayAverageResultList.get(0).getDiscounRatio());
            ownerCouponLongEntity.setReductionPrice(holidayAverageResultList.get(0).getReductionAmt());
            ownerCouponLongEntity.setDiscountDesc(resVO.getDiscountDesc());

            for (int i = 0; i < details.size(); i++) {
                RenterOrderCostDetailEntity detailEntity = details.get(i);
                RenterOrderSubsidyDetailDTO subsidyDetailDTO =
                        renterOrderCostHandleService.handleLongOwnerCoupon(holidayAverageResultList.get(i).getActRentUnitPriceAmt(),
                                detailEntity.getCount(), detailEntity.getTotalAmount());
                if (null != subsidyDetailDTO) {
                    subsidyDetailDTO.setOrderNo(baseReqDTO.getOrderNo());
                    subsidyDetailDTO.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
                    subsidyDetailDTO.setMemNo(baseReqDTO.getMemNo());
                    subsidyDetails.add(subsidyDetailDTO);
                }
            }
        }

        log.info("订单费用计算-->长租订单租金折扣.subsidyDetails:[{}]", JSON.toJSONString(subsidyDetails));
        LongOrderOwnerCouponResDTO longOrderOwnerCouponResDTO = new LongOrderOwnerCouponResDTO();
        if (CollectionUtils.isNotEmpty(subsidyDetails)) {
            int subsidyAmt =
                    subsidyDetails.stream().filter(x -> null != x.getSubsidyAmount()).mapToInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount).sum();
            longOrderOwnerCouponResDTO.setSubsidyDetails(subsidyDetails);
            longOrderOwnerCouponResDTO.setSubsidyAmt(subsidyAmt);

            //赋值OrderCostDetailContext
            OrderCostDetailContext orderCostDetailContext = context.getCostDetailContext();
            orderCostDetailContext.setSurplusRentAmt(orderCostDetailContext.getSurplusRentAmt() - subsidyAmt);
            orderCostDetailContext.setOriginalRentAmt(orderCostDetailContext.getOriginalRentAmt() - subsidyAmt);
            orderCostDetailContext.getSubsidyDetails().addAll(subsidyDetails);
        }

        if (Objects.nonNull(ownerCouponLongEntity)) {
            int actUnitPrice = ownerCouponLongEntity.getActUnitPrice();
            //更新租金单价
            context.getCostDetailContext().getCostDetails().stream()
                    .filter(cost -> StringUtils.equals(RenterCashCodeEnum.RENT_AMT.getCashNo(), cost.getCostCode()))
                    .forEach(cost ->  cost.setShowUnitPrice(actUnitPrice));

            orderRentAmtResDTO.getDetails().forEach(cost ->  cost.setShowUnitPrice(actUnitPrice));
            longOrderOwnerCouponResDTO.setOwnerCouponLongEntity(ownerCouponLongEntity);
        }
        log.info("订单费用计算-->长租订单租金折扣.result is,longOrderOwnerCouponResDTO:[{}]",
                JSON.toJSONString(longOrderOwnerCouponResDTO));
        context.getResContext().setLongOrderOwnerCouponResDTO(longOrderOwnerCouponResDTO);

    }

    /**
     * 封装OwnerCouponLongReq
     *
     * @param baseReqDTO                 基础参数
     * @param longOrderOwnerCouponReqDTO 长租订单请求参数
     * @return OwnerCouponLongReqVO
     */
    private OwnerCouponLongReqVO buildOwnerCouponLongReqVO(OrderCostBaseReqDTO baseReqDTO,
                                                           LongOrderOwnerCouponReqDTO longOrderOwnerCouponReqDTO) {
        OwnerCouponLongReqVO reqVO = new OwnerCouponLongReqVO();
        reqVO.setOrderNo(baseReqDTO.getOrderNo());
        reqVO.setRenterMemNo(baseReqDTO.getMemNo());
        reqVO.setOwnerMemNo(baseReqDTO.getOwnerMemNo());
        reqVO.setCarNo(longOrderOwnerCouponReqDTO.getCarNo());
        reqVO.setRentTime(LocalDateTimeUtils.formatDateTime(baseReqDTO.getStartTime()));
        reqVO.setRevertTime(LocalDateTimeUtils.formatDateTime(baseReqDTO.getEndTime()));
        reqVO.setCouponCode(longOrderOwnerCouponReqDTO.getCouponCode());
        return reqVO;
    }

}
