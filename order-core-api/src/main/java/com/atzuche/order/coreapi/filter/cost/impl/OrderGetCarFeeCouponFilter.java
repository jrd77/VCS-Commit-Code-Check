package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostGetCarFeeCouponReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderGetCarFeeCouponResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.service.RenterOrderCalCostService;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 计算订单取送车服务券抵扣金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:57
 */
@Service
@Slf4j
public class OrderGetCarFeeCouponFilter implements OrderCostFilter{

    @Resource
    private RenterOrderCalCostService renterOrderCalCostService;
    @Resource
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostGetCarFeeCouponReqDTO orderCostGetCarFeeCouponReqDTO =
                context.getReqContext().getOrderCostGetCarFeeCouponReqDTO();

        if (Objects.isNull(baseReqDTO) || Objects.isNull(orderCostGetCarFeeCouponReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算订单取送车服务券抵扣金额参数为空!");
        }

        if(StringUtils.isBlank(orderCostGetCarFeeCouponReqDTO.getGetCarFreeCouponId())) {
            log.info("计算订单取送车服务券抵扣金额: getCarFreeCouponId is empty.");
            return ;
        }

        MemAvailCouponRequestVO memAvailCouponRequestVO = new MemAvailCouponRequestVO();
        BeanUtils.copyProperties(orderCostGetCarFeeCouponReqDTO, memAvailCouponRequestVO);
        memAvailCouponRequestVO.setDisCouponId(orderCostGetCarFeeCouponReqDTO.getGetCarFreeCouponId());
        OrderCouponDTO getCarFeeCoupon =
                renterOrderCalCostService.calGetAndReturnSrvCouponDeductInfo(memAvailCouponRequestVO);
        log.info("计算订单取送车服务券抵扣金额-->优惠券信息.getCarFeeCoupon:[{}]", JSON.toJSONString(getCarFeeCoupon));
        if (null != getCarFeeCoupon) {
            OrderCostDetailContext orderCostDetailContext = context.getCostDetailContext();
            orderCostDetailContext.setSurplusSrvGetCost(OrderConstant.ZERO);
            orderCostDetailContext.setSurplusSrvReturnCost(OrderConstant.ZERO);

            //记录送取服务券
            getCarFeeCoupon.setOrderNo(baseReqDTO.getOrderNo());
            getCarFeeCoupon.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
            orderCostDetailContext.getCoupons().add(getCarFeeCoupon);
            //补贴明细
            RenterOrderSubsidyDetailDTO getCarFeeCouponSubsidyInfo =
                    renterOrderSubsidyDetailService.calGetCarFeeCouponSubsidyInfo(Integer.valueOf(baseReqDTO.getMemNo())
                            , getCarFeeCoupon);
            OrderGetCarFeeCouponResDTO orderGetCarFeeCouponResDTO = new OrderGetCarFeeCouponResDTO();
            if(null != getCarFeeCouponSubsidyInfo) {
                getCarFeeCouponSubsidyInfo.setOrderNo(baseReqDTO.getOrderNo());
                getCarFeeCouponSubsidyInfo.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
                orderGetCarFeeCouponResDTO.setSubsidyAmt(getCarFeeCouponSubsidyInfo.getSubsidyAmount());
                orderGetCarFeeCouponResDTO.setGetCarFeeCoupon(getCarFeeCoupon);
                orderGetCarFeeCouponResDTO.setSubsidyDetail(getCarFeeCouponSubsidyInfo);
                context.getResContext().setOrderGetCarFeeCouponResDTO(orderGetCarFeeCouponResDTO);
            }
            log.info("计算订单取送车服务券抵扣金额.result is, orderGetCarFeeCouponResDTO:[{}]",
                    JSON.toJSONString(orderGetCarFeeCouponResDTO));
            orderCostDetailContext.getSubsidyDetails().add(getCarFeeCouponSubsidyInfo);
        }

    }
}
