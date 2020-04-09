package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostOwnerCouponReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderOwnerCouponResDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderRentAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.service.RenterOrderCalCostService;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidReqVO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 计算订单车主券抵扣金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:56
 */
@Service
@Slf4j
public class OrderOwnerCouponFilter implements OrderCostFilter {

    @Resource
    private RenterOrderCalCostService renterOrderCalCostService;
    @Resource
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostOwnerCouponReqDTO orderCostOwnerCouponReqDTO = context.getReqContext().getOwnerCouponReqDTO();
        log.info("订单费用计算-->车主券.param is,baseReqDTO:[{}],orderCostOwnerCouponReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(orderCostOwnerCouponReqDTO));

        if (Objects.isNull(baseReqDTO) || Objects.isNull(orderCostOwnerCouponReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算车主券抵扣金额参数为空!");
        }

        OrderRentAmtResDTO orderRentAmtResDTO = context.getResContext().getOrderRentAmtResDTO();
        if (Objects.isNull(orderRentAmtResDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算车主券抵扣金额租金信息为空!");
        }

        if (StringUtils.isBlank(orderCostOwnerCouponReqDTO.getCouponNo())) {
            log.info("订单费用计算-->车主券.couponCode is empty!");
            return;
        }

        OrderCostDetailContext costDetailContext = context.getCostDetailContext();

        OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO = new OwnerCouponGetAndValidReqVO();
        BeanUtils.copyProperties(orderCostOwnerCouponReqDTO, ownerCouponGetAndValidReqVO);
        ownerCouponGetAndValidReqVO.setOrderNo(baseReqDTO.getOrderNo());
        ownerCouponGetAndValidReqVO.setRentAmt(costDetailContext.getSurplusRentAmt());
        OrderCouponDTO ownerCoupon = renterOrderCalCostService.calOwnerCouponDeductInfo(ownerCouponGetAndValidReqVO);
        log.info("订单费用计算-->车主券.ownerCoupon:[{}]", JSON.toJSONString(ownerCoupon));

        if (null != ownerCoupon) {
            //记录车主券
            ownerCoupon.setOrderNo(baseReqDTO.getOrderNo());
            ownerCoupon.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
            //补贴明细
            RenterOrderSubsidyDetailDTO ownerCouponSubsidyInfo =
                    renterOrderSubsidyDetailService.calOwnerCouponSubsidyInfo(Integer.valueOf(baseReqDTO.getMemNo()),
                            ownerCoupon);
            ownerCouponSubsidyInfo.setOrderNo(baseReqDTO.getOrderNo());
            ownerCouponSubsidyInfo.setRenterOrderNo(baseReqDTO.getRenterOrderNo());

            OrderOwnerCouponResDTO orderOwnerCouponResDTO = new OrderOwnerCouponResDTO();
            orderOwnerCouponResDTO.setGetCarFeeCoupon(ownerCoupon);
            orderOwnerCouponResDTO.setSubsidyAmt(ownerCouponSubsidyInfo.getSubsidyAmount());
            orderOwnerCouponResDTO.setSubsidyDetail(ownerCouponSubsidyInfo);


            //赋值OrderCostDetailContext
            int disAmt = null == ownerCoupon.getAmount() ? 0 : ownerCoupon.getAmount();
            costDetailContext.setSurplusRentAmt(costDetailContext.getSurplusRentAmt() - disAmt);
            costDetailContext.getCoupons().add(ownerCoupon);
            costDetailContext.getSubsidyDetails().add(ownerCouponSubsidyInfo);

            log.info("订单费用计算-->车主券.result is, orderOwnerCouponResDTO:[{}]",
                    JSON.toJSONString(orderOwnerCouponResDTO));
            context.getResContext().setOrderOwnerCouponResDTO(orderOwnerCouponResDTO);
        }

    }
}
