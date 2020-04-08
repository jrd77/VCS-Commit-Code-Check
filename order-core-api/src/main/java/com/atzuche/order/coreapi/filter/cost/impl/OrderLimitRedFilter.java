package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostLimitRedReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderLimitRedResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 计算订单限时红包抵扣金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:58
 */
@Service
@Slf4j
public class OrderLimitRedFilter implements OrderCostFilter {

    @Resource
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostLimitRedReqDTO orderCostLimitRedReqDTO = context.getReqContext().getLimitRedReqDTO();
        if (Objects.isNull(baseReqDTO) || Objects.isNull(orderCostLimitRedReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算订单限时红包抵扣金额参数为空!");
        }

        if (null == orderCostLimitRedReqDTO.getReductiAmt()) {
            log.info("计算订单限时红包抵扣金额: reduct amt is empty.");
            return;
        }

        RenterOrderSubsidyDetailDTO limitRedSubsidyInfo =
                renterOrderSubsidyDetailService.calLimitRedSubsidyInfo(Integer.valueOf(baseReqDTO.getMemNo()),
                        context.getCostDetailContext().getSurplusRentAmt(), orderCostLimitRedReqDTO.getReductiAmt());
        log.info("计算订单限时红包抵扣金额-->补贴信息A.result is, limitRedSubsidyInfo:[{}]", JSON.toJSONString(limitRedSubsidyInfo));
        if (null != limitRedSubsidyInfo) {
            //重置剩余租金
            int realLimitReductiAmt = null == limitRedSubsidyInfo.getSubsidyAmount() ? OrderConstant.ZERO :
                    limitRedSubsidyInfo.getSubsidyAmount();
            context.getCostDetailContext().setSurplusRentAmt(context.getCostDetailContext().getSurplusRentAmt() - realLimitReductiAmt);
            //补贴明细
            limitRedSubsidyInfo.setOrderNo(baseReqDTO.getOrderNo());
            limitRedSubsidyInfo.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
            context.getCostDetailContext().getSubsidyDetails().add(limitRedSubsidyInfo);

            OrderLimitRedResDTO orderLimitRedResDTO = new OrderLimitRedResDTO();
            orderLimitRedResDTO.setSubsidyAmt(realLimitReductiAmt);
            orderLimitRedResDTO.setSubsidyDetail(limitRedSubsidyInfo);

            log.info("计算订单限时红包抵扣金额-->补贴信息B.result is, orderLimitRedResDTO:[{}]",
                    JSON.toJSONString(orderLimitRedResDTO));

        }

    }
}
