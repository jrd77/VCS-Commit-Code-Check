package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostOwnerCouponReqDTO;
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
        if (Objects.isNull(baseReqDTO) || Objects.isNull(orderCostOwnerCouponReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算订单车主券抵扣金额参数为空!");
        }

        if (StringUtils.isBlank(orderCostOwnerCouponReqDTO.getCouponNo())) {
            log.info("计算订单车主券抵扣金额: couponCode is empty.");
            return;
        }

        OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO = new OwnerCouponGetAndValidReqVO();
        BeanUtils.copyProperties(orderCostOwnerCouponReqDTO, ownerCouponGetAndValidReqVO);
        ownerCouponGetAndValidReqVO.setOrderNo(baseReqDTO.getOrderNo());
        ownerCouponGetAndValidReqVO.setRentAmt(context.getCostDetailContext().getSurplusRentAmt());
        OrderCouponDTO ownerCoupon = renterOrderCalCostService.calOwnerCouponDeductInfo(ownerCouponGetAndValidReqVO);

        log.info("计算订单车主券抵扣金额-->优惠券信息.result is, ownerCoupon:[{}]", JSON.toJSONString(ownerCoupon));
        if (null != ownerCoupon) {
            renterOrderResVO.setOwnerCoupon(ownerCoupon);
            //重置剩余租金
            int disAmt = null == ownerCoupon.getAmount() ? 0 : ownerCoupon.getAmount();
            context.setSurplusRentAmt(context.getSurplusRentAmt() - disAmt);
            //记录车主券
            ownerCoupon.setOrderNo(context.getOrderNo());
            ownerCoupon.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderCouponList().add(ownerCoupon);
            //补贴明细
            RenterOrderSubsidyDetailDTO ownerCouponSubsidyInfo =
                    renterOrderSubsidyDetailService.calOwnerCouponSubsidyInfo(Integer.valueOf(context.getMemNo()),
                            ownerCoupon);
            ownerCouponSubsidyInfo.setOrderNo(context.getOrderNo());
            ownerCouponSubsidyInfo.setRenterOrderNo(context.getRenterOrderNo());
            context.getOrderSubsidyDetailList().add(ownerCouponSubsidyInfo);

            LOGGER.info("车主券处理-->优惠券补贴信息.result is, ownerCouponSubsidyInfo:[{}]",
                    JSON.toJSONString(ownerCouponSubsidyInfo));
            return true;
        }

    }
}
