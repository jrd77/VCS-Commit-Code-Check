package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.InsurAmtDTO;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostInsurAmtReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderInsurAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 计算基础保障费
 *
 * @author pengcheng.fu
 * @date 2020/3/30 11:01
 */
@Service
@Slf4j
public class OrderInsurAmtFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostInsurAmtReqDTO insurAmtReqDTO = context.getReqContext().getInsurAmtReqDTO();
        log.info("订单费用计算-->基础保障费.param is,baseReqDTO:[{}],insurAmtReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(insurAmtReqDTO));

        if (Objects.isNull(baseReqDTO) || Objects.isNull(insurAmtReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算基础保障费参数为空!");
        }
        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

        //计算基础保障费
        InsurAmtDTO insurAmtDTO = new InsurAmtDTO();
        insurAmtDTO.setCostBaseDTO(costBaseDTO);
        insurAmtDTO.setCarLabelIds(insurAmtReqDTO.getCarLabelIds());
        insurAmtDTO.setCertificationTime(insurAmtReqDTO.getCertificationTime());
        insurAmtDTO.setGetCarBeforeTime(insurAmtReqDTO.getGetCarBeforeTime());
        insurAmtDTO.setReturnCarAfterTime(insurAmtReqDTO.getReturnCarAfterTime());
        insurAmtDTO.setInmsrp(insurAmtReqDTO.getInmsrp());
        insurAmtDTO.setGuidPrice(insurAmtReqDTO.getGuidPrice());

        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(insurAmtDTO);
        log.info("订单费用计算-->基础保障费.insurAmtEntity:[{}]", JSON.toJSONString(insurAmtEntity));
        OrderInsurAmtResDTO orderInsurAmtResDTO = new OrderInsurAmtResDTO();
        if (null != insurAmtEntity) {
            orderInsurAmtResDTO.setInsurAmt(insurAmtEntity.getTotalAmount());
            orderInsurAmtResDTO.setDetail(insurAmtEntity);

            //赋值OrderCostDetailContext
            OrderCostDetailContext costDetailContext = context.getCostDetailContext();
            costDetailContext.getCostDetails().add(insurAmtEntity);
        }
        log.info("订单费用计算-->基础保障费.result is,orderInsurAmtResDTO:[{}]", JSON.toJSONString(orderInsurAmtResDTO));
        context.getResContext().setOrderInsurAmtResDTO(orderInsurAmtResDTO);
    }
}
