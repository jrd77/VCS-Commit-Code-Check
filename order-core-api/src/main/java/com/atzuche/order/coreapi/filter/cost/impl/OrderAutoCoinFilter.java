package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coin.service.AccountRenterCostCoinService;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostAutoCoinReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderAutoCoinResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.autoyol.auto.coin.service.vo.res.AutoCoinResponseVO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 计算订单凹凸币抵扣金额
 *
 * @author pengcheng.fu
 * @date 2020/3/31 10:59
 */
@Service
@Slf4j
public class OrderAutoCoinFilter implements OrderCostFilter {

    @Resource
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

    @Resource
    private AccountRenterCostCoinService accountRenterCostCoinService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostAutoCoinReqDTO orderCostAutoCoinReqDTO = context.getReqContext().getAutoCoinReqDTO();
        if (Objects.isNull(baseReqDTO) || Objects.isNull(orderCostAutoCoinReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算订单凹凸币抵扣金额参数为空!");
        }

        if (null == orderCostAutoCoinReqDTO.getUseAutoCoin() || OrderConstant.NO == orderCostAutoCoinReqDTO.getUseAutoCoin()) {
            log.info("计算订单凹凸币抵扣金额: useAutoCoin is:[{}].", orderCostAutoCoinReqDTO.getUseAutoCoin());
            return;
        }

        int pointValue = accountRenterCostCoinService.getUserCoinTotalAmt(baseReqDTO.getMemNo());
        AutoCoinResponseVO crmCustPoint = new AutoCoinResponseVO();
        crmCustPoint.setMemNo(Integer.valueOf(baseReqDTO.getMemNo()));
        crmCustPoint.setPointValue(pointValue);
        RenterOrderSubsidyDetailDTO autoCoinSubsidyInfo = renterOrderSubsidyDetailService.calAutoCoinSubsidyInfo(crmCustPoint,
                context.getCostDetailContext().getOriginalRentAmt(),
                context.getCostDetailContext().getSurplusRentAmt(), OrderConstant.YES);
        log.info("计算订单凹凸币抵扣金额-->补贴信息A.result is, autoCoinSubsidyInfo:[{}]", JSON.toJSONString(autoCoinSubsidyInfo));
        if (null != autoCoinSubsidyInfo) {
            //重置剩余租金
            int autoCoinSubsidyAmt = null == autoCoinSubsidyInfo.getSubsidyAmount() ? OrderConstant.ZERO :
                    autoCoinSubsidyInfo.getSubsidyAmount();
            context.getCostDetailContext().setSurplusRentAmt(context.getCostDetailContext().getSurplusRentAmt() - autoCoinSubsidyAmt);

            autoCoinSubsidyInfo.setOrderNo(baseReqDTO.getOrderNo());
            autoCoinSubsidyInfo.setRenterOrderNo(baseReqDTO.getRenterOrderNo());
            context.getCostDetailContext().getSubsidyDetails().add(autoCoinSubsidyInfo);

            OrderAutoCoinResDTO orderAutoCoinResDTO = new OrderAutoCoinResDTO();
            orderAutoCoinResDTO.setSubsidyAmt(autoCoinSubsidyAmt);
            orderAutoCoinResDTO.setSubsidyDetail(autoCoinSubsidyInfo);
            log.info("计算订单凹凸币抵扣金额-->补贴信息B.result is, orderAutoCoinResDTO:[{}]", JSON.toJSONString(orderAutoCoinResDTO));
        }

    }
}
