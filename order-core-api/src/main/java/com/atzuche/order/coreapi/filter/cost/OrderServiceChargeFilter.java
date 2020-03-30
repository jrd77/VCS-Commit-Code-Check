package com.atzuche.order.coreapi.filter.cost;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderServiceChargeResDTO;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 计算手续费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:09
 */
@Service
@Slf4j
public class OrderServiceChargeFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        log.info("计算订单手续费.param is,baseReqDTO:[{}]", JSON.toJSONString(baseReqDTO));
        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);
        RenterOrderCostDetailEntity serviceChargeFeeEntity = renterOrderCostCombineService.getServiceChargeFeeEntity(costBaseDTO);

        OrderServiceChargeResDTO orderServiceChargeResDTO = new OrderServiceChargeResDTO();
        orderServiceChargeResDTO.setServiceCharge(serviceChargeFeeEntity.getTotalAmount());
        orderServiceChargeResDTO.setDetail(serviceChargeFeeEntity);
        log.info("计算订单手续费.result is,orderServiceChargeResDTO = [{}]", JSON.toJSONString(orderServiceChargeResDTO));
        context.getResContext().setOrderServiceChargeResDTO(orderServiceChargeResDTO);
    }
}
