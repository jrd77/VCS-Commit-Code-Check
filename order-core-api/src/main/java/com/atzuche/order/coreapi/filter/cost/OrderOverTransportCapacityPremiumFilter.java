package com.atzuche.order.coreapi.filter.cost;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostExtraDriverReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostGetReturnCarOverCostReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderInsurAmtResDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderOverTransportCapacityPremiumResDTO;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.GetReturnOverCostDTO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        log.info("计算超运能溢价金额.param is,baseReqDTO:[{}],orderCostGetReturnCarOverCostReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(orderCostGetReturnCarOverCostReqDTO));

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
        List<RenterOrderCostDetailEntity> list = getReturnOverCost.getRenterOrderCostDetailEntityList();

        OrderOverTransportCapacityPremiumResDTO orderOverTransportCapacityPremiumResDTO =
                new OrderOverTransportCapacityPremiumResDTO();
        if (CollectionUtils.isNotEmpty(list)) {
            int getOverCost = list.stream()
                    .filter(x -> RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo().equals(x.getCostCode())).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();

            int returnOverCost = list.stream()
                    .filter(x -> RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo().equals(x.getCostCode())).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();

            orderOverTransportCapacityPremiumResDTO.setDetails(list);
            orderOverTransportCapacityPremiumResDTO.setGetCarOverCost(getOverCost);
            orderOverTransportCapacityPremiumResDTO.setReturnCarOverCost(returnOverCost);
        }
        log.info("计算超运能溢价金额.result is,orderOverTransportCapacityPremiumResDTO = [{}]", JSON.toJSONString(orderOverTransportCapacityPremiumResDTO));
        context.getResContext().setOrderOverTransportCapacityPremiumResDTO(orderOverTransportCapacityPremiumResDTO);
    }
}
