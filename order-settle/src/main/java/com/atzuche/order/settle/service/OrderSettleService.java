package com.atzuche.order.settle.service;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.doc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 车辆结算
 * @author haibao.yan
 */
@Service
@Slf4j
public class OrderSettleService {
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private OrderSettleNoTService orderSettleNoTService;
    @Autowired private RenterOrderService renterOrderService;
    @Autowired private OwnerOrderService ownerOrderService;
    /**
     * 车辆押金结算
     */
    @Async
    @Transactional(rollbackFor=Exception.class)
    public void settleOrder(String orderNo) {
        log.info("OrderSettleService settleOrder start param [{}]",orderNo);
        //1 校验参数
        if(StringUtil.isBlank(orderNo)){
            return;
        }
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())){
            return;
        }
        OwnerOrderEntity ownerOrder = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(ownerOrder) || Objects.isNull(ownerOrder.getOwnerOrderNo())){
            return;
        }

        // 2 TODO 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
        orderSettleNoTService.check(renterOrder);
        // 3 初始化数据

        // 3.1获取租客子订单 和 租客会员号
        String renterOrderNo = renterOrder.getRenterOrderNo();
        String renterMemNo = "";
        //3.2获取车主子订单 和 车主会员号
        String ownerOrderNo = ownerOrder.getOwnerOrderNo();
        String ownerMemNo = ownerOrder.getMemNo();
        //3.3 初始化结算对象
        SettleOrders settleOrders =  orderSettleNoTService.initSettleOrders(orderNo,renterOrderNo,ownerOrderNo,renterMemNo,ownerMemNo);
        log.info("OrderSettleService initSettleOrders settleOrders [{}]", GsonUtils.toJson(settleOrders));

        //3.4 查询所有租客费用明细
        orderSettleNoTService.getRenterCostSettleDetail(settleOrders,renterOrder);
        log.info("OrderSettleService getRenterCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));

        //3.5 查询所有车主费用明细
        orderSettleNoTService.getOwnerCostSettleDetail(settleOrders,ownerOrder);
        log.info("OrderSettleService getOwnerCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));

        //4 计算费用统计  资金统计
        SettleOrdersDefinition settleOrdersDefinition = orderSettleNoTService.settleOrdersDefinition(settleOrders);
        log.info("OrderSettleService settleOrdersDefinition settleOrdersDefinition [{}]", GsonUtils.toJson(settleOrdersDefinition));

        //5 费用明细先落库
        orderSettleNoTService.insertSettleOrders(settleOrdersDefinition);

        //6 费用平账 平台收入 + 平台补贴 + 车主收益 + 车主补贴 + 租客费用 + 租客补贴 = 0
        int totleAmt = settleOrdersDefinition.getPlatformProfitAmt() + settleOrdersDefinition.getPlatformSubsidyAmt()
                     + settleOrdersDefinition.getOwnerCostAmt() + settleOrdersDefinition.getOwnerSubsidyAmt()
                     + settleOrdersDefinition.getRentCostAmt() + settleOrdersDefinition.getRentSubsidyAmt();
        if(totleAmt != 0){
            throw new OrderSettleFlatAccountException();
        }
        //开启事务
        //7 租车费用  总费用 信息落库 并返回最新租车费用 实付
        AccountRenterCostSettleEntity accountRenterCostSettle = cashierSettleService.updateRentSettleCost(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), settleOrdersDefinition.getAccountRenterCostSettleDetails());
        //8 获取租客 实付 车辆押金
        int depositAmt = cashierSettleService.getRentDeposit(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        SettleOrdersAccount settleOrdersAccount = new SettleOrdersAccount();
        BeanUtils.copyProperties(settleOrders,settleOrdersAccount);
        settleOrdersAccount.setRentCostAmtFinal(accountRenterCostSettle.getRentAmt());
        settleOrdersAccount.setRentCostPayAmtFinal(accountRenterCostSettle.getShifuAmt());
        settleOrdersAccount.setDepositAmt(depositAmt);
        settleOrdersAccount.setDepositSurplusAmt(depositAmt);
        settleOrdersAccount.setOwnerCostAmtFinal(settleOrdersDefinition.getOwnerCostAmtFinal());
        int rentCostSurplusAmt = (accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt())<=0?0:(accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt());
        settleOrdersAccount.setRentCostSurplusAmt(rentCostSurplusAmt);
        //9 租客费用结余处理
        orderSettleNoTService.rentCostSettle(settleOrders,settleOrdersAccount);
        //10租客车辆押金结余欠款
        orderSettleNoTService.repayHistoryDebtRent(settleOrdersAccount);
        //11 租客费用 退还
        orderSettleNoTService.refundRentCost(settleOrdersAccount,settleOrdersDefinition.getAccountRenterCostSettleDetails());
        //12 租客押金 退还
        orderSettleNoTService.refundDepositAmt(settleOrdersAccount);
        //12车主收益 结余处理

    }
}
