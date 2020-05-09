package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostGetReturnCarOverCostReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderOverTransportCapacityPremiumResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.GetReturnOverCostDTO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 计算超运能溢价金额
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:14
 */
@Service
@Slf4j
public class OrderOverTransportCapacityPremiumFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostGetReturnCarOverCostReqDTO orderCostGetReturnCarOverCostReqDTO =
                context.getReqContext().getGetReturnCarOverCostReqDTO();
        log.info("订单费用计算-->超运能溢价.param is,baseReqDTO:[{}],orderCostGetReturnCarOverCostReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(orderCostGetReturnCarOverCostReqDTO));

        if (Objects.isNull(baseReqDTO) || Objects.isNull(orderCostGetReturnCarOverCostReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算超运能溢价金额参数为空!");
        }

        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);
        //超运能溢价计算相关信息
        GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
        getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
        getReturnCarOverCostReqDto.setCityCode(orderCostGetReturnCarOverCostReqDTO.getCityCode());
        getReturnCarOverCostReqDto.setIsGetCarCost(orderCostGetReturnCarOverCostReqDTO.getIsGetCarCost());
        getReturnCarOverCostReqDto.setIsReturnCarCost(orderCostGetReturnCarOverCostReqDTO.getIsReturnCarCost());
        if (null != orderCostGetReturnCarOverCostReqDTO.getOrderCategory()) {
            getReturnCarOverCostReqDto.setOrderType(orderCostGetReturnCarOverCostReqDTO.getOrderCategory());
        }
        GetReturnOverCostDTO getReturnOverCost = renterOrderCostCombineService.getGetReturnOverCost(getReturnCarOverCostReqDto);
        log.info("订单费用计算-->超运能溢价.getReturnOverCost:[{}]", JSON.toJSONString(getReturnOverCost));

        OrderOverTransportCapacityPremiumResDTO orderOverTransportCapacityPremiumResDTO =
                new OrderOverTransportCapacityPremiumResDTO();
        if (Objects.nonNull(getReturnOverCost) && CollectionUtils.isNotEmpty(getReturnOverCost.getRenterOrderCostDetailEntityList())) {
            List<RenterOrderCostDetailEntity> list = getReturnOverCost.getRenterOrderCostDetailEntityList();
            int getOverCost = list.stream()
                    .filter(x -> RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo().equals(x.getCostCode()) && null != x.getTotalAmount())
                    .mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
            int returnOverCost = list.stream()
                    .filter(x -> RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo().equals(x.getCostCode()) && null != x.getTotalAmount())
                    .mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
            orderOverTransportCapacityPremiumResDTO.setDetails(list);
            orderOverTransportCapacityPremiumResDTO.setGetCarOverCost(getOverCost);
            orderOverTransportCapacityPremiumResDTO.setReturnCarOverCost(returnOverCost);

            //赋值OrderCostDetailContext
            OrderCostDetailContext costDetailContext = context.getCostDetailContext();
            costDetailContext.getCostDetails().addAll(list);

            costDetailContext.setGetBlockedRaiseAmt(getOverCost);
            costDetailContext.setReturnBlockedRaiseAmt(returnOverCost);
            costDetailContext.setSurplusGetBlockedRaiseAmt(getOverCost);
            costDetailContext.setSurplusReturnBlockedRaiseAmt(returnOverCost);
        }
        log.info("订单费用计算-->超运能溢价.result is,orderOverTransportCapacityPremiumResDTO:[{}]",
                JSON.toJSONString(orderOverTransportCapacityPremiumResDTO));
        context.getResContext().setOrderOverTransportCapacityPremiumResDTO(orderOverTransportCapacityPremiumResDTO);
    }
}
