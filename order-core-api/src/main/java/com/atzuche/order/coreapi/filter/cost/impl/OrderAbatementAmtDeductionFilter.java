package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderAbatementAmtDeductionResDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderAbatementAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.renterorder.service.InsurAbamentDiscountService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.platformcost.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 计算订单全面保障服务减免金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 11:13
 */
@Service
@Slf4j
public class OrderAbatementAmtDeductionFilter implements OrderCostFilter {

    @Autowired
    private InsurAbamentDiscountService insurAbamentDiscountService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        log.info("订单费用计算-->全面保障服务折扣.param is,baseReqDTO:[{}]", JSON.toJSONString(baseReqDTO));

        if (Objects.isNull(baseReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算全面保障服务折扣金额参数为空!");
        }

        OrderAbatementAmtResDTO orderAbatementAmtResDTO = context.getResContext().getOrderAbatementAmtResDTO();
        if (null == orderAbatementAmtResDTO || CollectionUtils.isEmpty(orderAbatementAmtResDTO.getDetails())) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算全面保障服务减免金额全面保障服务信息为空!");
        }

        // 获取不计免赔的折扣
        double ratio = CommonUtils.getInsureDiscount(baseReqDTO.getStartTime(), baseReqDTO.getEndTime());
        log.info("订单费用计算-->全面保障服务折扣.orderNo=[{}],ratio=[{}]", baseReqDTO.getOrderNo(), ratio);
        if (ratio >= OrderConstant.D_ONE) {
            log.info("订单费用计算-->全面保障服务折扣.order no relief!");
            return;
        }

        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

        List<RenterOrderCostDetailEntity> details = orderAbatementAmtResDTO.getDetails();
        List<RenterOrderSubsidyDetailDTO> abatementSubsidyList = null;
        if (CollectionUtils.isNotEmpty(details)) {
            abatementSubsidyList =
                    details.stream().map(fr -> insurAbamentDiscountService.getAbatementSubsidy(costBaseDTO, fr, ratio
                            , "全面保障服务折扣")).collect(Collectors.toList());
        }

        OrderAbatementAmtDeductionResDTO orderAbatementAmtDeductionResDTO = new OrderAbatementAmtDeductionResDTO();
        if (CollectionUtils.isNotEmpty(abatementSubsidyList)) {
            int subsidyAmount =
                    abatementSubsidyList.stream().filter(x -> null != x.getSubsidyAmount()).mapToInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount).sum();
            orderAbatementAmtDeductionResDTO.setSubsidyAmt(subsidyAmount);
            orderAbatementAmtDeductionResDTO.setSubsidyDetails(abatementSubsidyList);

            //赋值OrderCostDetailContext
            OrderCostDetailContext costDetailContext = context.getCostDetailContext();
            costDetailContext.getSubsidyDetails().addAll(abatementSubsidyList);
        }

        log.info("订单费用计算-->全面保障服务折扣.result is,orderAbatementAmtDeductionResDTO:[{}]", JSON.toJSONString(orderAbatementAmtDeductionResDTO));
        context.getResContext().setOrderAbatementAmtDeductionResDTO(orderAbatementAmtDeductionResDTO);
    }
}
