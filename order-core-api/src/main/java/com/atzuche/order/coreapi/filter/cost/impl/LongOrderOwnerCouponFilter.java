package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
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
import com.atzuche.order.renterorder.service.RenterOrderCostHandleService;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponLongReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponLongResVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 计算长租订单车主券抵扣金额
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
    @Autowired
    private OrderCommonConver orderCommonConver;


    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        LongOrderOwnerCouponReqDTO longOrderOwnerCouponReqDTO = context.getReqContext().getLongOrderOwnerCouponReqDTO();
        log.info("计算长租订单车主券抵扣金额.param is,baseReqDTO:[{}],longOrderOwnerCouponReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(longOrderOwnerCouponReqDTO));

        if (Objects.isNull(longOrderOwnerCouponReqDTO) || StringUtils.isBlank(longOrderOwnerCouponReqDTO.getCouponCode())) {
            log.info("param is empty.");
            return;
        }

        OrderRentAmtResDTO orderRentAmtResDTO = context.getResContext().getOrderRentAmtResDTO();
        if (Objects.isNull(orderRentAmtResDTO)) {
            log.info("Order rentAmt is empty.");
            return;
        }

        List<RenterOrderCostDetailEntity> details = orderRentAmtResDTO.getDetails();
        if (CollectionUtils.isEmpty(details)) {
            log.info("Order rentAmt detail is empty.");
            return;
        }


        OrderCostDetailContext deductAndSubsidyContext = context.getDeductAndSubsidyContext();
        if (Objects.isNull(deductAndSubsidyContext)) {
            deductAndSubsidyContext = orderCommonConver.initOrderCostDeductAndSubsidyContext(context.getResContext());
        }

        List<RenterOrderSubsidyDetailDTO> subsidyDetails = new ArrayList<>();
        details.forEach(d -> {
            OwnerCouponLongReqVO reqVO = buildOwnerCouponLongReqVO(baseReqDTO, longOrderOwnerCouponReqDTO);
            reqVO.setRentOriginalUnitPriceAmt(d.getUnitPrice());
            OwnerCouponLongResVO resVO = couponAndCoinHandleService.getLongOwnerCoupon(reqVO);
            if (!Objects.isNull(resVO)) {
                RenterOrderSubsidyDetailDTO subsidyDetailDTO = renterOrderCostHandleService.handleLongOwnerCoupon(resVO.getActRentUnitPriceAmt(),
                        d.getCount(), d.getTotalAmount());
                if (null != subsidyDetailDTO) {
                    subsidyDetails.add(subsidyDetailDTO);
                }
            }
        });

        int subsidyAmt = subsidyDetails.stream().mapToInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount).sum();
        //更新剩余租金
        deductAndSubsidyContext.setSurplusRentAmt(deductAndSubsidyContext.getSurplusRentAmt() + subsidyAmt);
        log.info("Order surplusRentAmt:[{}]", deductAndSubsidyContext.getSurplusRentAmt());

        LongOrderOwnerCouponResDTO longOrderOwnerCouponResDTO = new LongOrderOwnerCouponResDTO();
        longOrderOwnerCouponResDTO.setSubsidyDetails(subsidyDetails);
        longOrderOwnerCouponResDTO.setSubsidyAmt(subsidyAmt);
        context.getResContext().setLongOrderOwnerCouponResDTO(longOrderOwnerCouponResDTO);
        log.info("计算长租订单车主券抵扣金额.result is,longOrderOwnerCouponResDTO:[{}]", JSON.toJSONString(longOrderOwnerCouponResDTO));
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
