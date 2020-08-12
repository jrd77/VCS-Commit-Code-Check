package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostGetReturnCarCostReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderServiceChargeResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 计算取还车精准达服务费
 */
@Service
@Slf4j
public class AccurateSrvCostFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
    	OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostGetReturnCarCostReqDTO getReturnCarCostReqDTO = context.getReqContext().getGetReturnCarCostReqDTO();
        log.info("订单费用计算-->取还车精准达服务费.param is,baseReqDTO:[{}],getReturnCarCostReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(getReturnCarCostReqDTO));

        if (Objects.isNull(baseReqDTO) || Objects.isNull(getReturnCarCostReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算取还车精准达服务费参数为空!");
        }
        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

        //取还车费用计算相关信息
        GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
        BeanUtils.copyProperties(getReturnCarCostReqDTO, getReturnCarCostReqDto);
        getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);
        List<RenterOrderCostDetailEntity> accurateList = renterOrderCostCombineService.getAccurateSrvFee(getReturnCarCostReqDto);
        log.info("订单费用计算-->取还车精准达服务费.accurateList:[{}]", JSON.toJSONString(accurateList));

        OrderServiceChargeResDTO orderServiceChargeResDTO = new OrderServiceChargeResDTO();
        if (accurateList != null && !accurateList.isEmpty()) {
            //赋值OrderCostDetailContext
            OrderCostDetailContext costDetailContext = context.getCostDetailContext();
            costDetailContext.getCostDetails().addAll(accurateList);
        }
        log.info("订单费用计算-->取还车精准达服务费.result is,orderServiceChargeResDTO:[{}]", JSON.toJSONString(orderServiceChargeResDTO));
        context.getResContext().setOrderServiceChargeResDTO(orderServiceChargeResDTO);
    }
}
