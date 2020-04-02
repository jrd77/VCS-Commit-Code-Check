package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.ExtraDriverDTO;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostExtraDriverReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderExtraDriverInsureAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 计算附加驾驶人保险费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:07
 */
@Service
@Slf4j
public class OrderExtraDriverInsureAmtFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostExtraDriverReqDTO extraDriverReqDTO = context.getReqContext().getExtraDriverReqDTO();
        log.info("计算订单附加驾驶人保险费.param is,baseReqDTO:[{}],extraDriverReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(extraDriverReqDTO));

        if (Objects.isNull(baseReqDTO) || Objects.isNull(extraDriverReqDTO)) {
            log.info("param is empty.");
            return;
        }
        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

        //附加驾驶人险计算相关信息
        ExtraDriverDTO extraDriverDTO = new ExtraDriverDTO();
        extraDriverDTO.setCostBaseDTO(costBaseDTO);
        extraDriverDTO.setDriverIds(extraDriverReqDTO.getDriverIds());
        RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(extraDriverDTO);

        OrderExtraDriverInsureAmtResDTO extraDriverInsureAmtResDTO = new OrderExtraDriverInsureAmtResDTO();
        extraDriverInsureAmtResDTO.setExtraDriverInsureAmt(extraDriverInsureAmtEntity.getTotalAmount());
        extraDriverInsureAmtResDTO.setDetail(extraDriverInsureAmtEntity);
        log.info("计算订单附加驾驶人保险费.result is,extraDriverInsureAmtResDTO = [{}]", JSON.toJSONString(extraDriverInsureAmtResDTO));
        context.getResContext().setOrderExtraDriverInsureAmtResDTO(extraDriverInsureAmtResDTO);
    }
}
