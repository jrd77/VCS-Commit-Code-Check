package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.AbatementAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostAbatementAmtReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderAbatementAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 计算全面保障服务费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:05
 */
@Service
@Slf4j
public class OrderAbatementAmtFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostAbatementAmtReqDTO abatementAmtReqDTO = context.getReqContext().getAbatementAmtReqDTO();
        log.info("计算订单全面保障服务费.param is,baseReqDTO:[{}],abatementAmtReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(abatementAmtReqDTO));

        if (Objects.isNull(baseReqDTO) || Objects.isNull(abatementAmtReqDTO)) {
            log.info("param is empty.");
            return;
        }
        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

        //全面保障服务费计算相关信息
        AbatementAmtDTO abatementAmtDTO = new AbatementAmtDTO();
        abatementAmtDTO.setCostBaseDTO(costBaseDTO);
        abatementAmtDTO.setCarLabelIds(abatementAmtReqDTO.getCarLabelIds());
        abatementAmtDTO.setCertificationTime(abatementAmtReqDTO.getCertificationTime());
        abatementAmtDTO.setGetCarBeforeTime(abatementAmtReqDTO.getGetCarBeforeTime());
        abatementAmtDTO.setReturnCarAfterTime(abatementAmtReqDTO.getReturnCarAfterTime());
        abatementAmtDTO.setInmsrp(abatementAmtReqDTO.getInmsrp());
        abatementAmtDTO.setGuidPrice(abatementAmtReqDTO.getGuidPrice());
        abatementAmtDTO.setIsAbatement(abatementAmtReqDTO.getIsAbatement());

        List<RenterOrderCostDetailEntity> list =
                renterOrderCostCombineService.listAbatementAmtEntity(abatementAmtDTO);

        OrderAbatementAmtResDTO orderAbatementAmtResDTO = new OrderAbatementAmtResDTO();
        if (CollectionUtils.isNotEmpty(list)) {
            int abatementAmt = list.stream().mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
            orderAbatementAmtResDTO.setAbatementAmt(abatementAmt);
            orderAbatementAmtResDTO.setDetails(list);
        }

        log.info("计算订单全面保障服务费.result is,orderAbatementAmtResDTO = [{}]", JSON.toJSONString(orderAbatementAmtResDTO));
        context.getResContext().setOrderAbatementAmtResDTO(orderAbatementAmtResDTO);

    }
}
