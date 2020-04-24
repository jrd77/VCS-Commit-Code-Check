package com.atzuche.order.sms.service;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.entity.rentCost.RentCarCostDTO;
import com.atzuche.order.commons.entity.rentCost.RenterCostDetailDTO;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.open.service.FeignOrderCostService;
import com.atzuche.order.sms.common.annatation.OrderService;
import com.atzuche.order.sms.common.annatation.SMS;
import com.atzuche.order.sms.interfaces.IOrderRouteKeyMessage;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 胡春林
 * 发送订单车主确认还车成功事件
 */
@OrderService
@Slf4j
public class OwnerReturnCarSuccessService implements IOrderRouteKeyMessage<Map> {

    @Autowired
    FeignOrderCostService feignOrderCostService;

    @Override
    @SMS(renterFlag = "CarRentalEnd2Renter")
    public OrderMessage sendOrderMessageWithNo(OrderMessage orderMessage) { return orderMessage;
    }

    /**
     * 获取特殊参数值
     * @param map
     * @return
     */
    @Override
    public Map hasSMSElseOtherParams(Map map) {
        if (!map.containsKey("orderNo")) {
            log.info("没有对应的订单号：map：[{}]", JSONObject.toJSONString(map));
            return null;
        }
        Map paramsMap = findRenterDetailCost((String) map.get("orderNo"));
        return paramsMap;
    }

    @Override
    public Map hasPushElseOtherParams(Map map) {
        return null;
    }

    /**
     * 获取租客费用数据
     * @param orderNo
     * @return
     */
    public Map findRenterDetailCost(String orderNo) {
        Map map = Maps.newHashMap();
        RenterCostDetailDTO renterCostDetailDTO = feignOrderCostService.renterCostDetail(orderNo).getData();
        if (Objects.isNull(renterCostDetailDTO)) {
            return map;
        }
        RentCarCostDTO rentCarCostDTO = renterCostDetailDTO.getRentCarCostDTO();
        map.put("TotalAmount", rentCarCostDTO.getCostStatisticsDTO().getShouldReceiveAmt());
        map.put("Rent", rentCarCostDTO.getBaseCostDTO().getRenterAmt());
        map.put("Insurance", rentCarCostDTO.getBaseCostDTO().getBasicGuaranteeFee());
        map.put("AbatementAmt", rentCarCostDTO.getBaseCostDTO().getAllGuaranteeFee());
        map.put("ExtraDriverInsure", rentCarCostDTO.getBaseCostDTO().getDriverInsurance());
        map.put("CouponOffset", rentCarCostDTO.getCouponDeductionDTO().getDeliveryCouponRealDeduction() + rentCarCostDTO.getCouponDeductionDTO().getOwnerCouponRealDeduction() + rentCarCostDTO.getCouponDeductionDTO().getPlatFormCouponRealDeduction());
        map.put("WalletPay", rentCarCostDTO.getCouponDeductionDTO().getWalletBalanceRealDeduction());
        map.put("RenterPayPlatformContent", rentCarCostDTO.getRentAmtSubsidy());
        map.put("You2OwnerAdjust", 0);
        map.put("Owner2YouAdjust", 0);
        map.put("SrvGetCost", 0);
        map.put("SrvReturnCost", 0);
        return map;
    }
}
