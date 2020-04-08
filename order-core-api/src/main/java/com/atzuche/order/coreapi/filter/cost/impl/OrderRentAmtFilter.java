package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RentAmtDTO;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostRentAmtReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderRentAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
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
 * 计算租金
 *
 * @author pengcheng.fu
 * @date 2020/310:59
 */
@Service
@Slf4j
public class OrderRentAmtFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostRentAmtReqDTO rentAmtReqDTO = context.getReqContext().getRentAmtReqDTO();
        log.info("订单费用计算-->租金.param is,baseReqDTO:[{}],rentAmtReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(rentAmtReqDTO));

        if (Objects.isNull(baseReqDTO) || Objects.isNull(rentAmtReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算租金参数为空!");
        }

        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);
        //计算租金
        RentAmtDTO rentAmtDTO = new RentAmtDTO();
        rentAmtDTO.setCostBaseDTO(costBaseDTO);
        rentAmtDTO.setRenterGoodsPriceDetailDTOList(rentAmtReqDTO.getRenterGoodsPriceDetailDTOList());

        List<RenterOrderCostDetailEntity> list = renterOrderCostCombineService.listRentAmtEntity(rentAmtDTO);
        log.info("订单费用计算-->租金.list:[{}]", JSON.toJSONString(list));

        OrderRentAmtResDTO orderRentAmtResDTO = new OrderRentAmtResDTO();
        if (CollectionUtils.isNotEmpty(list)) {
            int rentAmt =
                    list.stream().filter(c -> Objects.nonNull(c.getTotalAmount())).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
            orderRentAmtResDTO.setHolidayAverage(list.get(0).getUnitPrice());
            orderRentAmtResDTO.setRentAmt(rentAmt);
            orderRentAmtResDTO.setDetails(list);

            //赋值OrderCostDetailContext
            OrderCostDetailContext costDetailContext = context.getCostDetailContext();
            costDetailContext.setOriginalRentAmt(rentAmt);
            costDetailContext.setSurplusRentAmt(rentAmt);
            costDetailContext.getCostDetails().addAll(list);
        }
        context.getResContext().setOrderRentAmtResDTO(orderRentAmtResDTO);
        log.info("订单费用计算-->租金.result is,orderRentAmtResDTO:[{}]", JSON.toJSONString(orderRentAmtResDTO));

    }
}
