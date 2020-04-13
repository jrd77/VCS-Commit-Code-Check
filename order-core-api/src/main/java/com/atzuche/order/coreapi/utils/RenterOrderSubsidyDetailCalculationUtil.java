package com.atzuche.order.coreapi.utils;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author pengcheng.fu
 * @date 2020/4/2 15:50
 */
@Slf4j
public class RenterOrderSubsidyDetailCalculationUtil {


    /**
     * 从费用列表中返回指定编码的费用信息
     *
     * @param list         费用列表
     * @param cashCodeEnum 费用编码
     * @return OrderCostDTO 费用项信息
     */
    public static OrderCostDTO getOrderSubsidyCostByCashCode(List<RenterOrderSubsidyDetailDTO> list,
                                                             RenterCashCodeEnum cashCodeEnum) {
        if (CollectionUtils.isEmpty(list)) {
            log.info("The order subsidy cost list is empty.");
            return new OrderCostDTO(false, OrderConstant.ZERO);
        }
        List<RenterOrderSubsidyDetailDTO> subList =
                list.stream().filter(d -> StringUtils.equals(d.getSubsidyCostCode(), cashCodeEnum.getCashNo())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(subList)) {
            log.info("Specify that the order subsidy cost list is empty.cashCodeEnum:[{}]",
                    JSON.toJSONString(cashCodeEnum));
            return new OrderCostDTO(false, OrderConstant.ZERO);
        }
        int subsidyAmt =
                subList.stream().filter(d -> Objects.nonNull(d.getSubsidyAmount())).mapToInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount).sum();
        return new OrderCostDTO(true, subsidyAmt);
    }



}
