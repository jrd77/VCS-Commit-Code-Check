package com.atzuche.order.coreapi.utils;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostDTO;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单费用聚合
 *
 * @author pengcheng.fu
 * @date 2020/4/2 15:50
 */
@Slf4j
public class OrderCostDetailCalculationUtil {


    /**
     * 从费用列表中返回指定编码的费用信息
     *
     * @param list         费用列表
     * @param cashCodeEnum 费用编码
     * @return OrderCostDTO 费用项信息
     */
    public static OrderCostDTO getOrderCostByCashCode(List<RenterOrderCostDetailEntity> list,
                                                      RenterCashCodeEnum cashCodeEnum) {
        if (CollectionUtils.isEmpty(list)) {
            log.info("The order cost list is empty.");
            return new OrderCostDTO(false, OrderConstant.ZERO);
        }
        List<RenterOrderCostDetailEntity> subList =
                list.stream().filter(d -> StringUtils.equals(d.getCostCode(), cashCodeEnum.getCashNo())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(subList)) {
            log.info("Specify that the order cost list is empty.cashCodeEnum:[{}]", JSON.toJSONString(cashCodeEnum));
            return new OrderCostDTO(false, OrderConstant.ZERO);
        }
        int amt =
                subList.stream().filter(d -> Objects.nonNull(d.getTotalAmount())).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
        return new OrderCostDTO(true, amt);
    }

    /**
     * 从费用列表中返回指定编码的费用(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getOrderCostByCashCode(List<RenterOrderCostDetailEntity> orderCostList,
                                             List<RenterOrderSubsidyDetailDTO> subsidyList, RenterCashCodeEnum cashCodeEnum) {
        int totalAmt = 0;
        OrderCostDTO amt = getOrderCostByCashCode(orderCostList, cashCodeEnum);
        log.info("Order cost detail.amt:[{}]", JSON.toJSONString(amt));
        if (amt.getIsExist() && null != amt.getAmt()) {
            totalAmt = totalAmt + amt.getAmt();
        }
        OrderCostDTO subsidyAmt = RenterOrderSubsidyDetailCalculationUtil.getOrderSubsidyCostByCashCode(subsidyList,
                cashCodeEnum);
        log.info("Order subsidy cost detail.subsidyAmt:[{}]", JSON.toJSONString(subsidyAmt));
        if (subsidyAmt.getIsExist() && null != subsidyAmt.getAmt()) {
            totalAmt = totalAmt + subsidyAmt.getAmt();
        }
        return totalAmt;
    }

    /**
     * 从费用列表中获得租金信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getOrderRentAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.RENT_AMT);
    }

    /**
     * 从费用列表中获得租金(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getOrderRentAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                      List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.RENT_AMT);
    }

    /**
     * 从费用列表中获得基础保障费信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getInsuranceAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.INSURE_TOTAL_PRICES);
    }

    /**
     * 从费用列表中获得基础保障费(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getInsuranceAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                      List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.INSURE_TOTAL_PRICES);
    }

    /**
     * 从费用列表中获得全面保障费服务信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getAbatementAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.ABATEMENT_INSURE);
    }

    /**
     * 从费用列表中获得全面保障服务(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getAbatementAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                      List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.ABATEMENT_INSURE);
    }

    /**
     * 从费用列表中获得手续费信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getFeeAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.FEE);
    }

    /**
     * 从费用列表中获得手续费(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getFeeAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.FEE);
    }

    /**
     * 从费用列表中获得取车服务费用信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getSrvGetCostAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.SRV_GET_COST);

    }

    /**
     * 从费用列表中获得取车服务费用(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getSrvGetCostAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                       List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.SRV_GET_COST);
    }

    /**
     * 从费用列表中获得还车服务费用信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getSrvReturnCostAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.SRV_RETURN_COST);
    }

    /**
     * 从费用列表中获得还车服务费用(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getSrvReturnCostAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                          List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.SRV_RETURN_COST);
    }

    /**
     * 从费用列表中获得附加驾驶人保险费用信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getExtraDriverInsureAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.EXTRA_DRIVER_INSURE);
    }

    /**
     * 从费用列表中获得附加驾驶人保险费(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getExtraDriverInsureAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                              List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.EXTRA_DRIVER_INSURE);
    }

    /**
     * 从费用列表中获取取车超运能溢价信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getGetBlockedRaiseAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT);
    }

    /**
     * 从费用列表中获得取车超运能溢价(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getGetBlockedRaiseAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                            List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT);
    }

    /**
     * 从费用列表中获取还车超运能溢价信息
     *
     * @param list 费用列表
     * @return OrderCostDTO
     */
    public static OrderCostDTO getReturnBlockedRaiseAmt(List<RenterOrderCostDetailEntity> list) {
        return getOrderCostByCashCode(list, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT);
    }

    /**
     * 从费用列表中获得还车超运能溢价(包含补贴)
     *
     * @param orderCostList 费用列表
     * @param subsidyList   补贴列表
     * @return int
     */
    public static int getReturnBlockedRaiseAmt(List<RenterOrderCostDetailEntity> orderCostList,
                                               List<RenterOrderSubsidyDetailDTO> subsidyList) {
        return getOrderCostByCashCode(orderCostList, subsidyList, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT);
    }

}
