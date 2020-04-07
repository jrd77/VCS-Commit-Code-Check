package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDetailContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.LongOrderGetAndReturnCarCostSubsidyResDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderGetAndReturnCarCostResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 计算长租订单取还车服务费补贴金额
 *
 * @author pengcheng.fu
 * @date 2020/4/1 14:30
 */
@Service
@Slf4j
public class LongOrderGetAndReturnCarCostSubsidyFilter implements OrderCostFilter {

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        log.info("计算长租订单取还车服务费补贴金额.param is,baseReqDTO:[{}]", JSON.toJSONString(baseReqDTO));

        if (Objects.isNull(baseReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算长租订单取还车服务费补贴金额参数为空!");
        }

        OrderGetAndReturnCarCostResDTO orderGetAndReturnCarCostResDTO =
                context.getResContext().getOrderGetAndReturnCarCostResDTO();
        if (Objects.isNull(orderGetAndReturnCarCostResDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算长租订单取还车服务费补贴金额取还车费用信息为空!");
        }

        OrderCostDetailContext orderCostDetailContext = context.getCostDetailContext();

        int srvGetCost = null != orderCostDetailContext.getSurplusSrvGetCost() ? orderCostDetailContext.getSurplusSrvGetCost() :
                OrderConstant.ZERO;
        //取车费用补贴
        RenterOrderSubsidyDetailDTO getCarSubsidy = buildRenterOrderSubsidyDetailDTO(baseReqDTO, srvGetCost,
                SubsidyTypeCodeEnum.GET_CAR, RenterCashCodeEnum.LONG_GET_CAR_OFFSET_COST);

        int srvReturnCost = null != orderCostDetailContext.getSurplusSrvReturnCost() ?
                orderCostDetailContext.getSurplusSrvReturnCost() : OrderConstant.ZERO;
        //还车费用补贴
        RenterOrderSubsidyDetailDTO returnCarSubsidy = buildRenterOrderSubsidyDetailDTO(baseReqDTO, srvReturnCost,
                SubsidyTypeCodeEnum.RETURN_CAR, RenterCashCodeEnum.LONG_RETURN_CAR_OFFSET_COST);

        LongOrderGetAndReturnCarCostSubsidyResDTO longOrderGetAndReturnCarCostSubsidyResDTO =
                new LongOrderGetAndReturnCarCostSubsidyResDTO();

        List<RenterOrderSubsidyDetailDTO> subsidyDetails = new ArrayList<>();
        if (null != getCarSubsidy) {
            longOrderGetAndReturnCarCostSubsidyResDTO.setGetCarSubsidyAmt(getCarSubsidy.getSubsidyAmount());
            subsidyDetails.add(getCarSubsidy);
            orderCostDetailContext.setSurplusSrvGetCost(OrderConstant.ZERO);
        }
        if (null != returnCarSubsidy) {
            longOrderGetAndReturnCarCostSubsidyResDTO.setReturnCarSubsidyAmt(returnCarSubsidy.getSubsidyAmount());
            subsidyDetails.add(returnCarSubsidy);
            orderCostDetailContext.setSurplusSrvReturnCost(OrderConstant.ZERO);
        }
        longOrderGetAndReturnCarCostSubsidyResDTO.setSubsidyDetails(subsidyDetails);
        context.getResContext().setLongOrderGetAndReturnCarCostSubsidyResDTO(longOrderGetAndReturnCarCostSubsidyResDTO);
        log.info("计算长租订单取还车服务费补贴金额.result is,longOrderGetAndReturnCarCostSubsidyResDTO:[{}]", JSON.toJSONString(longOrderGetAndReturnCarCostSubsidyResDTO));

        context.getCostDetailContext().setSubsidyDetails(subsidyDetails);
    }


    /**
     * 长租订单取还车补贴明细
     *
     * @param baseReqDTO          请求参数
     * @param subsidyAmount       补贴金额
     * @param subsidyTypeCodeEnum 补贴类型
     * @param renterCashCodeEnum  费用编码
     * @return RenterOrderSubsidyDetailDTO 补贴明细
     */
    public RenterOrderSubsidyDetailDTO buildRenterOrderSubsidyDetailDTO(OrderCostBaseReqDTO baseReqDTO,
                                                                        Integer subsidyAmount,
                                                                        SubsidyTypeCodeEnum subsidyTypeCodeEnum,
                                                                        RenterCashCodeEnum renterCashCodeEnum) {
        if (null == subsidyAmount || OrderConstant.ZERO != subsidyAmount) {
            return null;
        }
        RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();
        renterOrderSubsidyDetailDTO.setOrderNo(baseReqDTO.getOrderNo());
        renterOrderSubsidyDetailDTO.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
        renterOrderSubsidyDetailDTO.setMemNo(baseReqDTO.getMemNo());

        renterOrderSubsidyDetailDTO.setSubsidyAmount(Math.abs(subsidyAmount));
        renterOrderSubsidyDetailDTO.setSubsidyTypeCode(subsidyTypeCodeEnum.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTypeName(subsidyTypeCodeEnum.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
        renterOrderSubsidyDetailDTO.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());

        renterOrderSubsidyDetailDTO.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidyCostCode(renterCashCodeEnum.getCashNo());
        renterOrderSubsidyDetailDTO.setSubsidyCostName(renterCashCodeEnum.getTxt());
        renterOrderSubsidyDetailDTO.setSubsidyDesc("长租订单取还车费用抵扣(包含对应的运能溢价金额)");
        return renterOrderSubsidyDetailDTO;
    }
}
