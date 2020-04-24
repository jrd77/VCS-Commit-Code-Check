package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostGetReturnCarCostReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.*;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.coreapi.utils.OrderCostDetailCalculationUtil;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.GetReturnCostDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
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
        log.info("订单费用计算-->上门送取服务费.param is,baseReqDTO:[{}],getReturnCarCostReqDTO:[{}]", JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(getReturnCarCostReqDTO));

        if (Objects.isNull(baseReqDTO) || Objects.isNull(getReturnCarCostReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算上门送取服务费参数为空!");
        }
        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        BeanUtils.copyProperties(baseReqDTO, costBaseDTO);

        //取还车费用计算相关信息
        GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
        BeanUtils.copyProperties(getReturnCarCostReqDTO, getReturnCarCostReqDto);
        getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);

        List<RenterOrderCostDetailEntity> costDetails = context.getCostDetailContext().getCostDetails();
        List<RenterOrderSubsidyDetailDTO> subsidyDetails = context.getCostDetailContext().getSubsidyDetails();
        int rentAmt = OrderCostDetailCalculationUtil.getOrderRentAmt(costDetails, subsidyDetails);
        int insurAmt = OrderCostDetailCalculationUtil.getInsuranceAmt(costDetails, subsidyDetails);
        int serviceAmount = OrderCostDetailCalculationUtil.getFeeAmt(costDetails, subsidyDetails);
        int comprehensiveEnsureAmount = OrderCostDetailCalculationUtil.getAbatementAmt(costDetails, subsidyDetails);
        log.info("订单费用计算-->上门送取服务费.sumJudgeFreeFee = rentAmt:[{}] + insurAmt:[{}] + serviceAmount:[{}] + " +
                "comprehensiveEnsureAmount:[{}]", rentAmt, insurAmt, serviceAmount, comprehensiveEnsureAmount);
        getReturnCarCostReqDto.setSumJudgeFreeFee(Math.abs(rentAmt + insurAmt + serviceAmount + comprehensiveEnsureAmount));
        GetReturnCostDTO getReturnCostDTO = renterOrderCostCombineService.getReturnCarCost(getReturnCarCostReqDto);
        log.info("订单费用计算-->上门送取服务费.getReturnCostDTO:[{}]", JSON.toJSONString(getReturnCostDTO));

        OrderGetAndReturnCarCostResDTO orderGetAndReturnCarCostResDTO = new OrderGetAndReturnCarCostResDTO();
        if (Objects.nonNull(getReturnCostDTO) && CollectionUtils.isNotEmpty(getReturnCostDTO.getRenterOrderCostDetailEntityList())) {
            orderGetAndReturnCarCostResDTO.setGetCarCost(getReturnCostDTO.getGetReturnResponseVO().getGetFee());
            orderGetAndReturnCarCostResDTO.setReturnCarCost(getReturnCostDTO.getGetReturnResponseVO().getReturnFee());
            orderGetAndReturnCarCostResDTO.setDetails(getReturnCostDTO.getRenterOrderCostDetailEntityList());
            orderGetAndReturnCarCostResDTO.setSubsidyDetails(getReturnCostDTO.getRenterOrderSubsidyDetailDTOList());

            //赋值OrderCostDetailContext
            OrderCostDetailContext costDetailContext = context.getCostDetailContext();
            costDetailContext.getCostDetails().addAll(getReturnCostDTO.getRenterOrderCostDetailEntityList());
            costDetailContext.getSubsidyDetails().addAll(getReturnCostDTO.getRenterOrderSubsidyDetailDTOList());

            costDetailContext.setSrvGetCost(orderGetAndReturnCarCostResDTO.getGetCarCost());
            costDetailContext.setSrvReturnCost(orderGetAndReturnCarCostResDTO.getReturnCarCost());
            costDetailContext.setSurplusSrvGetCost(orderGetAndReturnCarCostResDTO.getGetCarCost());
            costDetailContext.setSurplusSrvReturnCost(orderGetAndReturnCarCostResDTO.getReturnCarCost());
        }

        log.info("订单费用计算-->上门送取服务费.result is,orderGetAndReturnCarCostResDTO = [{}]", JSON.toJSONString(orderGetAndReturnCarCostResDTO));
        context.getResContext().setOrderGetAndReturnCarCostResDTO(orderGetAndReturnCarCostResDTO);
    }
}
