package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostPlatformCouponReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderPlatformCouponResDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderRentAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.coreapi.utils.OrderCostDetailCalculationUtil;
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
 * 计算订单平台优惠券抵扣金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:58
 */
@Service
@Slf4j
public class OrderPlatformCouponFilter implements OrderCostFilter {

    @Resource
    private RenterOrderCalCostService renterOrderCalCostService;
    @Resource
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostPlatformCouponReqDTO orderCostPlatformCouponReqDTO =
                context.getReqContext().getCostCouponReqDTO();

        log.info("订单费用计算-->平台优惠券.param is,baseReqDTO:[{}],orderCostPlatformCouponReqDTO:[{}]",
                JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(orderCostPlatformCouponReqDTO));
        if (Objects.isNull(baseReqDTO) || Objects.isNull(orderCostPlatformCouponReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算平台优惠券抵扣金额参数为空!");
        }

        OrderRentAmtResDTO orderRentAmtResDTO = context.getResContext().getOrderRentAmtResDTO();
        if (Objects.isNull(orderRentAmtResDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算平台优惠券抵扣金额租金信息为空!");
        }

        if (StringUtils.isBlank(orderCostPlatformCouponReqDTO.getDisCouponId())) {
            log.info("订单费用计算-->平台优惠券.disCouponId is empty!");
            return;
        }

        OrderCostDetailContext costDetailContext = context.getCostDetailContext();
        MemAvailCouponRequestVO memAvailCouponRequestVO = new MemAvailCouponRequestVO();
        BeanUtils.copyProperties(orderCostPlatformCouponReqDTO, memAvailCouponRequestVO);
        memAvailCouponRequestVO.setOrderNo(baseReqDTO.getOrderNo());
        memAvailCouponRequestVO.setMemNo(Integer.valueOf(baseReqDTO.getMemNo()));
        memAvailCouponRequestVO.setRentTime(DateUtils.formateLong(baseReqDTO.getStartTime(), DateUtils.DATE_DEFAUTE));
        memAvailCouponRequestVO.setRevertTime(DateUtils.formateLong(baseReqDTO.getEndTime(), DateUtils.DATE_DEFAUTE));

        memAvailCouponRequestVO.setRentAmt(costDetailContext.getSurplusRentAmt());
        memAvailCouponRequestVO.setOriginalRentAmt(costDetailContext.getOriginalRentAmt());

        int srvGetCost = OrderCostDetailCalculationUtil.getSrvGetCostAmt(costDetailContext.getCostDetails(),
                costDetailContext.getSubsidyDetails());
        int getBlockedRaiseAmt = OrderCostDetailCalculationUtil.getGetBlockedRaiseAmt(costDetailContext.getCostDetails(),
                costDetailContext.getSubsidyDetails());
        memAvailCouponRequestVO.setSrvGetCost(Math.abs(srvGetCost + getBlockedRaiseAmt));

        int srvReturnCost = OrderCostDetailCalculationUtil.getSrvReturnCostAmt(costDetailContext.getCostDetails(),
                costDetailContext.getSubsidyDetails());
        int returnBlockedRaiseAmt = OrderCostDetailCalculationUtil.getReturnBlockedRaiseAmt(costDetailContext.getCostDetails(),
                costDetailContext.getSubsidyDetails());
        memAvailCouponRequestVO.setSrvReturnCost(Math.abs(srvReturnCost + returnBlockedRaiseAmt));

        int insurAmt = OrderCostDetailCalculationUtil.getInsuranceAmt(costDetailContext.getCostDetails(),
                costDetailContext.getSubsidyDetails());
        memAvailCouponRequestVO.setInsureTotalPrices(Math.abs(insurAmt));
        int comprehensiveEnsureAmount = OrderCostDetailCalculationUtil.getAbatementAmt(costDetailContext.getCostDetails(),
                costDetailContext.getSubsidyDetails());
        memAvailCouponRequestVO.setAbatement(Math.abs(comprehensiveEnsureAmount));
        int serviceAmount = OrderCostDetailCalculationUtil.getFeeAmt(costDetailContext.getCostDetails(),
                costDetailContext.getSubsidyDetails());
        memAvailCouponRequestVO.setCounterFee(Math.abs(serviceAmount));
        memAvailCouponRequestVO.setHolidayAverage(orderRentAmtResDTO.getHolidayAverage());

        OrderCouponDTO platfromCoupon = renterOrderCalCostService.calPlatformCouponDeductInfo(memAvailCouponRequestVO);
        log.info("订单费用计算-->平台优惠券.platfromCoupon:[{}]", JSON.toJSONString(platfromCoupon));


        if (null != platfromCoupon) {
            //记录平台券
            platfromCoupon.setOrderNo(baseReqDTO.getOrderNo());
            platfromCoupon.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
            //重置剩余租金
            int disAmt = null == platfromCoupon.getAmount() ? OrderConstant.ZERO : platfromCoupon.getAmount();

            //补贴明细
            RenterOrderSubsidyDetailDTO platformCouponSubsidyInfo =
                    renterOrderSubsidyDetailService.calPlatformCouponSubsidyInfo(Integer.valueOf(baseReqDTO.getMemNo()), platfromCoupon);
            platformCouponSubsidyInfo.setOrderNo(baseReqDTO.getOrderNo());
            platformCouponSubsidyInfo.setRenterOrderNo(baseReqDTO.getRenterOrderNo());

            OrderPlatformCouponResDTO orderPlatformCouponResDTO = new OrderPlatformCouponResDTO();
            orderPlatformCouponResDTO.setSubsidyAmt(disAmt);
            orderPlatformCouponResDTO.setPlatformCoupon(platfromCoupon);
            orderPlatformCouponResDTO.setSubsidyDetail(platformCouponSubsidyInfo);

            costDetailContext.setSurplusRentAmt(costDetailContext.getSurplusRentAmt() - disAmt);
            costDetailContext.getCoupons().add(platfromCoupon);
            costDetailContext.getSubsidyDetails().add(platformCouponSubsidyInfo);

            log.info("订单费用计算-->平台优惠券.result is, orderPlatformCouponResDTO:[{}]",
                    JSON.toJSONString(orderPlatformCouponResDTO));
            context.getResContext().setOrderPlatformCouponResDTO(orderPlatformCouponResDTO);
        }
    }
}
