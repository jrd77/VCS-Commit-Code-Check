package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderInsurAmtDeductionResDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderInsurAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.renterorder.service.InsurAbamentDiscountService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.platformcost.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 计算订单基础保险减免金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 11:12
 */

@Service
@Slf4j
public class OrderInsurAmtDeductionFilter implements OrderCostFilter {

    @Autowired
    private InsurAbamentDiscountService insurAbamentDiscountService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        log.info("订单费用计算-->基础保障费折扣.param is,baseReqDTO:[{}]", JSON.toJSONString(baseReqDTO));

        if (Objects.isNull(baseReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算基础保障费减免金额参数为空!");
        }

        OrderInsurAmtResDTO orderInsurAmtResDTO = context.getResContext().getOrderInsurAmtResDTO();
        if (null == orderInsurAmtResDTO || null == orderInsurAmtResDTO.getDetail()) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算基础保障费减免金额基础保障费信息为空!");
        }

        // 获取保险的折扣
        Integer inmsrpGuidePrice = Objects.isNull(context.getReqContext().getAbatementAmtReqDTO().getInmsrp()) ?
                context.getReqContext().getAbatementAmtReqDTO().getGuidPrice() :
                context.getReqContext().getAbatementAmtReqDTO().getInmsrp();
        double ratio = CommonUtils.getInsureDiscount(baseReqDTO.getStartTime(), baseReqDTO.getEndTime(),
                inmsrpGuidePrice);
        log.info("订单费用计算-->基础保障费折扣.orderNo=[{}],ratio=[{}]", baseReqDTO.getOrderNo(), ratio);
        if (ratio >= OrderConstant.D_ONE) {
            log.info("订单费用计算-->基础保障费折扣.order no relief!");
            return;
        }

        RenterOrderCostDetailEntity detailEntity = orderInsurAmtResDTO.getDetail();
        // 折扣后的单价
        int discountAfterUnitPrice = (int) Math.ceil(detailEntity.getUnitPrice() * ratio);
        // 折扣后的基础保障费
        int discountAfterInsurAmt = (int) Math.ceil(discountAfterUnitPrice * detailEntity.getCount());

        int insurAmt = null == detailEntity.getTotalAmount() ? OrderConstant.ZERO : detailEntity.getTotalAmount();
        if (Math.abs(insurAmt) > Math.abs(discountAfterInsurAmt)) {
            int subsidyAmount = Math.abs(insurAmt) - Math.abs(discountAfterInsurAmt);
            // 基础信息
            CostBaseDTO costBaseDTO = new CostBaseDTO();
            BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

            RenterOrderSubsidyDetailDTO subsidyDetail =
                    insurAbamentDiscountService.convertToRenterOrderSubsidyDetailDTO(costBaseDTO, subsidyAmount, SubsidyTypeCodeEnum.INSURE_AMT,
                            SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER,
                            RenterCashCodeEnum.INSURE_TOTAL_PRICES, "基础保障费折扣");

            OrderInsurAmtDeductionResDTO orderInsurAmtDeductionResDTO = new OrderInsurAmtDeductionResDTO();
            orderInsurAmtDeductionResDTO.setSubsidyAmt(subsidyAmount);
            orderInsurAmtDeductionResDTO.setSubsidyDetail(subsidyDetail);

            //赋值OrderCostDetailContext
            OrderCostDetailContext costDetailContext = context.getCostDetailContext();
            costDetailContext.getSubsidyDetails().add(subsidyDetail);

            log.info("订单费用计算-->基础保障费折扣.result is,orderInsurAmtDeductionResDTO:[{}]", JSON.toJSONString(orderInsurAmtDeductionResDTO));
            context.getResContext().setOrderInsurAmtDeductionResDTO(orderInsurAmtDeductionResDTO);
        } else {
            log.info("订单费用计算-->基础保障费折扣.result is,insurAmt:[{}], discountAfterInsurAmt:[{}], discountAfterUnitPrice:[{}]",
                    insurAmt, discountAfterInsurAmt, discountAfterUnitPrice);
        }
    }
}
