package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostPlatformCouponReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderPlatformCouponResDTO;
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

        if (Objects.isNull(baseReqDTO) || Objects.isNull(orderCostPlatformCouponReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算订单平台优惠券抵扣金额参数为空!");
        }

        if (StringUtils.isBlank(orderCostPlatformCouponReqDTO.getDisCouponId())) {
            log.info("计算订单平台优惠券抵扣金额: disCouponId is empty.");
            return;
        }

        MemAvailCouponRequestVO memAvailCouponRequestVO = new MemAvailCouponRequestVO();
        BeanUtils.copyProperties(orderCostPlatformCouponReqDTO, memAvailCouponRequestVO);

        memAvailCouponRequestVO.setOrderNo(baseReqDTO.getOrderNo());
        memAvailCouponRequestVO.setMemNo(Integer.valueOf(baseReqDTO.getMemNo()));
        memAvailCouponRequestVO.setRentTime(DateUtils.formateLong(baseReqDTO.getStartTime(), DateUtils.DATE_DEFAUTE));
        memAvailCouponRequestVO.setRevertTime(DateUtils.formateLong(baseReqDTO.getEndTime(), DateUtils.DATE_DEFAUTE));
        memAvailCouponRequestVO.setRentAmt(context.getCostDetailContext().getSurplusRentAmt());
        memAvailCouponRequestVO.setOriginalRentAmt(context.getCostDetailContext().getOriginalRentAmt());
        memAvailCouponRequestVO.setSrvGetCost(context.getCostDetailContext().getSurplusSrvGetCost());
        memAvailCouponRequestVO.setSrvReturnCost(context.getCostDetailContext().getSurplusSrvReturnCost());
        memAvailCouponRequestVO.setInsureTotalPrices(0);
        memAvailCouponRequestVO.setAbatement(0);
        memAvailCouponRequestVO.setCounterFee(20);
        memAvailCouponRequestVO.setHolidayAverage(0);

        OrderCouponDTO platfromCoupon = renterOrderCalCostService.calPlatformCouponDeductInfo(memAvailCouponRequestVO);
        log.info("平台优惠券处理-->优惠券信息.result is, platfromCoupon:[{}]", JSON.toJSONString(platfromCoupon));


        if (null != platfromCoupon) {
            //重置剩余租金
            int disAmt = null == platfromCoupon.getAmount() ? 0 : platfromCoupon.getAmount();
            context.getCostDetailContext().setSurplusRentAmt(context.getCostDetailContext().getSurplusRentAmt() - disAmt);
            //记录平台券
            platfromCoupon.setOrderNo(baseReqDTO.getOrderNo());
            platfromCoupon.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
            context.getCostDetailContext().getCoupons().add(platfromCoupon);
            //补贴明细
            RenterOrderSubsidyDetailDTO platformCouponSubsidyInfo =
                    renterOrderSubsidyDetailService.calPlatformCouponSubsidyInfo(Integer.valueOf(baseReqDTO.getMemNo()), platfromCoupon);
            platformCouponSubsidyInfo.setOrderNo(baseReqDTO.getOrderNo());
            platformCouponSubsidyInfo.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
            context.getCostDetailContext().getSubsidyDetails().add(platformCouponSubsidyInfo);

            OrderPlatformCouponResDTO orderPlatformCouponResDTO = new OrderPlatformCouponResDTO();
            orderPlatformCouponResDTO.setSubsidyAmt(disAmt);
            orderPlatformCouponResDTO.setPlatformCoupon(platfromCoupon);
            orderPlatformCouponResDTO.setSubsidyDetail(platformCouponSubsidyInfo);

            context.getResContext().setOrderPlatformCouponResDTO(orderPlatformCouponResDTO);
            log.info("平台优惠券处理-->优惠券补贴信息.result is, orderPlatformCouponResDTO:[{}]",
                    JSON.toJSONString(orderPlatformCouponResDTO));
        }
    }
}
