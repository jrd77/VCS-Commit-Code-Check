package com.atzuche.order.coreapi.filter.cost;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostGetReturnCarCostReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.*;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.dto.GetReturnCostDTO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 计算取还车服务费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:11
 */
@Service
@Slf4j
public class OrderGetAndReturnCarCostFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostGetReturnCarCostReqDTO getReturnCarCostReqDTO = context.getReqContext().getGetReturnCarCostReqDTO();
        log.info("计算订单取还车服务费.param is,baseReqDTO:[{}],getReturnCarCostReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(getReturnCarCostReqDTO));

        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

        //取还车费用计算相关信息
        GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
        BeanUtils.copyProperties(getReturnCarCostReqDTO, getReturnCarCostReqDto);
        getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);

        int rentAmt = 0;
        OrderRentAmtResDTO orderRentAmtResDTO = context.getResContext().getOrderRentAmtResDTO();
        if (null != orderRentAmtResDTO && null != orderRentAmtResDTO.getRentAmt()) {
            rentAmt = orderRentAmtResDTO.getRentAmt();
        }
        OrderInsurAmtResDTO orderInsurAmtResDTO = context.getResContext().getOrderInsurAmtResDTO();
        int insurAmt = 0;
        if (null != orderInsurAmtResDTO && null != orderInsurAmtResDTO.getInsurAmt()) {
            insurAmt = orderInsurAmtResDTO.getInsurAmt();
        }
        OrderServiceChargeResDTO orderServiceChargeResDTO = context.getResContext().getOrderServiceChargeResDTO();
        int serviceAmount = 0;
        if (null != orderServiceChargeResDTO && null != orderServiceChargeResDTO.getServiceCharge()) {
            serviceAmount = orderServiceChargeResDTO.getServiceCharge();
        }
        OrderAbatementAmtResDTO orderAbatementAmtResDTO = context.getResContext().getOrderAbatementAmtResDTO();
        int comprehensiveEnsureAmount = 0;
        if (null != orderAbatementAmtResDTO && null != orderAbatementAmtResDTO.getAbatementAmt()) {
            comprehensiveEnsureAmount = orderAbatementAmtResDTO.getAbatementAmt();
        }
        log.info("sumJudgeFreeFee = rentAmt:[{}] + insurAmt:[{}] + serviceAmount:[{}] + " +
                "comprehensiveEnsureAmount:[{}]", rentAmt, insurAmt, serviceAmount, comprehensiveEnsureAmount);
        getReturnCarCostReqDto.setSumJudgeFreeFee(Math.abs(rentAmt + insurAmt + serviceAmount + comprehensiveEnsureAmount));
        GetReturnCostDTO returnCarCost = renterOrderCostCombineService.getReturnCarCost(getReturnCarCostReqDto);

        OrderGetAndReturnCarCostResDTO orderGetAndReturnCarCostResDTO = new OrderGetAndReturnCarCostResDTO();
        orderGetAndReturnCarCostResDTO.setGetCarCost(returnCarCost.getGetReturnResponseVO().getGetFee());
        orderGetAndReturnCarCostResDTO.setReturnCarCost(returnCarCost.getGetReturnResponseVO().getReturnFee());
        orderGetAndReturnCarCostResDTO.setDetails(returnCarCost.getRenterOrderCostDetailEntityList());
        orderGetAndReturnCarCostResDTO.setSubsidyDetails(returnCarCost.getRenterOrderSubsidyDetailDTOList());

        log.info("计算订单取还车服务费.result is,orderGetAndReturnCarCostResDTO = [{}]", JSON.toJSONString(orderGetAndReturnCarCostResDTO));
        context.getResContext().setOrderGetAndReturnCarCostResDTO(orderGetAndReturnCarCostResDTO);
    }
}
