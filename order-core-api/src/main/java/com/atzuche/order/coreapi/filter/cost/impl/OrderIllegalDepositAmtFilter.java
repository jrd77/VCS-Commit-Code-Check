package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.IllegalDepositAmtDTO;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostViolationDepositAmtReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderIllegalDepositAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentermem.service.RenterMemberRightService;
import com.atzuche.order.renterorder.vo.RenterOrderIllegalResVO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 计算订单违章押金
 *
 * @author pengcheng.fu
 * @date 2020/3/31 11:01
 */
@Service
@Slf4j
public class OrderIllegalDepositAmtFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired
    private RenterMemberRightService renterMemberRightService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostViolationDepositAmtReqDTO violationDepositAmtReqDTO =
                context.getReqContext().getViolationDepositAmtReqDTO();
        log.info("订单费用计算-->违章押金.param is,baseReqDTO:[{}],violationDepositAmtReqDTO:[{}]",
                JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(violationDepositAmtReqDTO));

        if (Objects.isNull(baseReqDTO) && Objects.isNull(violationDepositAmtReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算违章押金参数为空!");
        }


        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

        IllegalDepositAmtDTO illDTO = new IllegalDepositAmtDTO();
        illDTO.setCostBaseDTO(costBaseDTO);
        illDTO.setCarPlateNum(violationDepositAmtReqDTO.getCarPlateNum());
        illDTO.setCityCode(violationDepositAmtReqDTO.getCityCode());

        Integer illegalDepositAmt = renterOrderCostCombineService.getIllegalDepositAmt(illDTO);
        log.info("订单费用计算-->违章押金.illegalDepositAmt:[{}]", illegalDepositAmt);

        int realIllegalDepositAmt =
                renterMemberRightService.wzDepositAmt(violationDepositAmtReqDTO.getRenterMemberRightDTOList(),
                        illegalDepositAmt);
        log.info("订单费用计算-->违章押金.realIllegalDepositAmt:[{}]", realIllegalDepositAmt);

        RenterOrderIllegalResVO renterOrderIllegalResVO = new RenterOrderIllegalResVO();
        renterOrderIllegalResVO.setOrderNo(baseReqDTO.getOrderNo());
        renterOrderIllegalResVO.setMemNo(baseReqDTO.getMemNo());
        renterOrderIllegalResVO.setFreeDepositType(Objects.nonNull(violationDepositAmtReqDTO.getFreeDoubleTypeId()) ?
                FreeDepositTypeEnum.getFreeDepositTypeEnumByCode(violationDepositAmtReqDTO.getFreeDoubleTypeId()) : null);
        renterOrderIllegalResVO.setYingfuDepositAmt(-Math.abs(realIllegalDepositAmt));

        OrderIllegalDepositAmtResDTO orderIllegalDepositAmtResDTO = new OrderIllegalDepositAmtResDTO();
        orderIllegalDepositAmtResDTO.setIllegalDepositAmt(renterOrderIllegalResVO.getYingfuDepositAmt());
        orderIllegalDepositAmtResDTO.setIllegalDeposit(renterOrderIllegalResVO);
        log.info("订单费用计算-->违章押金.result is, orderIllegalDepositAmtResDTO:[{}]", orderIllegalDepositAmtResDTO);
        context.getResContext().setOrderIllegalDepositAmtResDTO(orderIllegalDepositAmtResDTO);
    }
}
