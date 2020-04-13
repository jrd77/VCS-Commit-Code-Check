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
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
        log.info("订单费用计算-->长租订单取还车服务费补贴.param is,baseReqDTO:[{}]", JSON.toJSONString(baseReqDTO));

        if (Objects.isNull(baseReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算长租订单取还车服务费补贴参数为空!");
        }

        OrderCostDetailContext orderCostDetailContext = context.getCostDetailContext();
        int srvGetCost = null != orderCostDetailContext.getSurplusSrvGetCost() ? orderCostDetailContext.getSurplusSrvGetCost() :
                OrderConstant.ZERO;
        //取车费用补贴
        RenterOrderSubsidyDetailDTO getCarSubsidy = buildRenterOrderSubsidyDetailDTO(baseReqDTO, srvGetCost,
                SubsidyTypeCodeEnum.GET_CAR, RenterCashCodeEnum.SRV_GET_COST);
        log.info("订单费用计算-->长租订单取还车服务费补贴.getCarSubsidy:[{}]", JSON.toJSONString(getCarSubsidy));

        int srvReturnCost = null != orderCostDetailContext.getSurplusSrvReturnCost() ?
                orderCostDetailContext.getSurplusSrvReturnCost() : OrderConstant.ZERO;
        //还车费用补贴
        RenterOrderSubsidyDetailDTO returnCarSubsidy = buildRenterOrderSubsidyDetailDTO(baseReqDTO, srvReturnCost,
                SubsidyTypeCodeEnum.RETURN_CAR, RenterCashCodeEnum.SRV_RETURN_COST);
        log.info("订单费用计算-->长租订单取还车服务费补贴.returnCarSubsidy:[{}]", JSON.toJSONString(returnCarSubsidy));

        //取车超运能溢价
        int getBlockedRaiseAmt = null == orderCostDetailContext.getSurplusGetBlockedRaiseAmt() ? OrderConstant.ZERO : orderCostDetailContext.getSurplusGetBlockedRaiseAmt();
        RenterOrderSubsidyDetailDTO getBlockedSubsidy = buildRenterOrderSubsidyDetailDTO(baseReqDTO, getBlockedRaiseAmt,
                SubsidyTypeCodeEnum.GET_BLOCKED, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT);
        log.info("订单费用计算-->长租订单取还车服务费补贴.getBlockedSubsidy:[{}]", JSON.toJSONString(getBlockedSubsidy));

        //还车超运能溢价
        int returnBlockedRaiseAmt = null == orderCostDetailContext.getSurplusReturnBlockedRaiseAmt() ?
                OrderConstant.ZERO :
                orderCostDetailContext.getSurplusReturnBlockedRaiseAmt();
        RenterOrderSubsidyDetailDTO returnBlockedSubsidy = buildRenterOrderSubsidyDetailDTO(baseReqDTO, returnBlockedRaiseAmt,
                SubsidyTypeCodeEnum.RETURN_BLOCKED, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT);
        log.info("订单费用计算-->长租订单取还车服务费补贴.returnBlockedSubsidy:[{}]", JSON.toJSONString(returnBlockedSubsidy));


        LongOrderGetAndReturnCarCostSubsidyResDTO longOrderGetAndReturnCarCostSubsidyResDTO =
                new LongOrderGetAndReturnCarCostSubsidyResDTO();

        List<RenterOrderSubsidyDetailDTO> subsidyDetails = new ArrayList<>();
        if (null != getCarSubsidy) {
            longOrderGetAndReturnCarCostSubsidyResDTO.setGetCarSubsidyAmt(getCarSubsidy.getSubsidyAmount());
            orderCostDetailContext.setSurplusSrvGetCost(OrderConstant.ZERO);
            subsidyDetails.add(getCarSubsidy);
        }
        if (null != returnCarSubsidy) {
            longOrderGetAndReturnCarCostSubsidyResDTO.setReturnCarSubsidyAmt(returnCarSubsidy.getSubsidyAmount());
            orderCostDetailContext.setSurplusSrvReturnCost(OrderConstant.ZERO);
            subsidyDetails.add(returnCarSubsidy);
        }
        if (null != getBlockedSubsidy) {
            longOrderGetAndReturnCarCostSubsidyResDTO.setGetBlockedSubsidyAmt(getBlockedSubsidy.getSubsidyAmount());
            orderCostDetailContext.setSurplusGetBlockedRaiseAmt(OrderConstant.ZERO);
            subsidyDetails.add(getBlockedSubsidy);
        }
        if (null != returnBlockedSubsidy) {
            longOrderGetAndReturnCarCostSubsidyResDTO.setReturnBlockedSubsidyAmt(returnBlockedSubsidy.getSubsidyAmount());
            orderCostDetailContext.setSurplusReturnBlockedRaiseAmt(OrderConstant.ZERO);
            subsidyDetails.add(returnBlockedSubsidy);
        }

        if (CollectionUtils.isNotEmpty(subsidyDetails)) {
            longOrderGetAndReturnCarCostSubsidyResDTO.setSubsidyDetails(subsidyDetails);

            //赋值OrderCostDetailContext
            orderCostDetailContext.getSubsidyDetails().addAll(subsidyDetails);
        }

        log.info("订单费用计算-->长租订单取还车服务费补贴.result is,longOrderGetAndReturnCarCostSubsidyResDTO:[{}]", JSON.toJSONString(longOrderGetAndReturnCarCostSubsidyResDTO));
        context.getResContext().setLongOrderGetAndReturnCarCostSubsidyResDTO(longOrderGetAndReturnCarCostSubsidyResDTO);
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
        if (null == subsidyAmount || OrderConstant.ZERO == subsidyAmount) {
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
