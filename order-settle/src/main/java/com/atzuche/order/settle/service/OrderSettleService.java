package com.atzuche.order.settle.service;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.SettleCancelOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.utils.GsonUtils;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 车辆结算
 * @author haibao.yan
 */
@Service
@Slf4j
public class OrderSettleService{
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private OrderSettleNoTService orderSettleNoTService;


    /**
     * 车辆押金结算
     */
    @Transactional(rollbackFor=Exception.class)
    public void settleOrder(String orderNo) {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "车俩结算服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderSettleService.settleOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);

            log.info("OrderSettleService settleOrder start param [{}]",orderNo);
            //3.3 初始化结算对象
            SettleOrders settleOrders =  orderSettleNoTService.initSettleOrders(orderNo);
            log.info("OrderSettleService initSettleOrders settleOrders [{}]", GsonUtils.toJson(settleOrders));
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));

            //3.4 查询所有租客费用明细
            orderSettleNoTService.getRenterCostSettleDetail(settleOrders);
            log.info("OrderSettleService getRenterCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));


            //3.5 查询所有车主费用明细 TODO 暂不支持 多个车主
            orderSettleNoTService.getOwnerCostSettleDetail(settleOrders);
            log.info("OrderSettleService getOwnerCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));

            //4 计算费用统计  资金统计
            SettleOrdersDefinition settleOrdersDefinition = orderSettleNoTService.settleOrdersDefinition(settleOrders);
            log.info("OrderSettleService settleOrdersDefinition settleOrdersDefinition [{}]", GsonUtils.toJson(settleOrdersDefinition));
            Cat.logEvent("SettleOrdersDefinition",GsonUtils.toJson(settleOrdersDefinition));

            //5 费用明细先落库
            orderSettleNoTService.insertSettleOrders(settleOrdersDefinition);

            //6 费用平账 平台收入 + 平台补贴 + 车主收益 + 车主补贴 + 租客费用 + 租客补贴 = 0
            int totleAmt = settleOrdersDefinition.getPlatformProfitAmt() + settleOrdersDefinition.getPlatformSubsidyAmt()
                         + settleOrdersDefinition.getOwnerCostAmt() + settleOrdersDefinition.getOwnerSubsidyAmt()
                         + settleOrdersDefinition.getRentCostAmt() + settleOrdersDefinition.getRentSubsidyAmt();
            if(totleAmt != 0){
                //TODO 走告警
                throw new OrderSettleFlatAccountException();
            }
            //开启事务
            //7.1 租车费用  总费用 信息落库 并返回最新租车费用 实付
            AccountRenterCostSettleEntity accountRenterCostSettle = cashierSettleService.updateRentSettleCost(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), settleOrdersDefinition.getAccountRenterCostSettleDetails());
            //7.2 车主 费用 落库表
            cashierSettleService.insertAccountOwnerCostSettle(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo(),settleOrders.getOwnerMemNo(),settleOrdersDefinition.getAccountOwnerCostSettleDetails());
            //8 获取租客 实付 车辆押金
            int depositAmt = cashierSettleService.getRentDeposit(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
            SettleOrdersAccount settleOrdersAccount = new SettleOrdersAccount();
            BeanUtils.copyProperties(settleOrders,settleOrdersAccount);
            settleOrdersAccount.setRentCostAmtFinal(accountRenterCostSettle.getRentAmt());
            settleOrdersAccount.setRentCostPayAmt(accountRenterCostSettle.getShifuAmt());
            settleOrdersAccount.setDepositAmt(depositAmt);
            settleOrdersAccount.setDepositSurplusAmt(depositAmt);
            settleOrdersAccount.setOwnerCostAmtFinal(settleOrdersDefinition.getOwnerCostAmtFinal());
            settleOrdersAccount.setOwnerCostSurplusAmt(settleOrdersDefinition.getOwnerCostAmtFinal());
            int rentCostSurplusAmt = (accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt())<=0?0:(accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt());
            settleOrdersAccount.setRentCostSurplusAmt(rentCostSurplusAmt);
            log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount one [{}]", GsonUtils.toJson(settleOrdersAccount));
            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
            orderStatusDTO.setOrderNo(settleOrders.getOrderNo());
            //9 租客费用 结余处理
            orderSettleNoTService.rentCostSettle(settleOrders,settleOrdersAccount);
            //10租客车辆押金/租客剩余租车费用 结余历史欠款
            orderSettleNoTService.repayHistoryDebtRent(settleOrdersAccount);
            //11 租客费用 退还
            orderSettleNoTService.refundRentCost(settleOrdersAccount,settleOrdersDefinition.getAccountRenterCostSettleDetails(),orderStatusDTO);
            //12 租客押金 退还
            orderSettleNoTService.refundDepositAmt(settleOrdersAccount,orderStatusDTO);
            //13车主收益 结余处理 历史欠款
            orderSettleNoTService.repayHistoryDebtOwner(settleOrdersAccount);
            //14 车主待审核收益落库
            orderSettleNoTService.insertOwnerIncomeExamine(settleOrdersAccount);
            //15 更新订单状态
            settleOrdersAccount.setOrderStatusDTO(orderStatusDTO);
            orderSettleNoTService.saveOrderStatusInfo(settleOrdersAccount);
            log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount two [{}]", GsonUtils.toJson(settleOrdersAccount));
            //16 发消息 TODO
            // TODO 退优惠卷
            t.setStatus(Transaction.SUCCESS);

        } catch (Exception e) {
            log.error("OrderSettleService settleOrder,e={},",e);
            t.setStatus(e);
            Cat.logError("结算失败  :{}",e);
            throw new RuntimeException("结算失败 ,不能结算");
        } finally {
            t.complete();
        }
        log.info("OrderPayCallBack payCallBack end " );
    }

    /**
     * 取消订单结算
     * @param orderNo
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean settleOrderCancel(String orderNo) {
//        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "取消订单结算服务");
//        try {
//            Cat.logEvent(CatConstants.FEIGN_METHOD, "OrderSettleService.settleOrderCancel");
//            Cat.logEvent(CatConstants.FEIGN_PARAM, orderNo);
//            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
//            orderStatusDTO.setOrderNo(orderNo);
//            // 1 取消订单初始化
//            SettleOrders settleOrders =  orderSettleNoTService.initCancelSettleOrders(orderNo);
//            //2 查询所有租客罚金明细  及 凹凸币补贴
//            orderSettleNoTService.getCancelRenterCostSettleDetail(settleOrders);
//            //3 查询所有车主罚金明细
//            orderSettleNoTService.getCancelOwnerCostSettleDetail(settleOrders);
//            //4 查询 租客实际 付款金额（包含 租车费用，车俩押金，违章押金，钱包）
//            SettleCancelOrdersAccount settleCancelOrdersAccount = orderSettleNoTService.initSettleCancelOrdersAccount(settleOrders);
//            //5 车主罚金处理
//            orderSettleNoTService.handleOwnerFine(settleOrders,settleCancelOrdersAccount);
//            //6 租客罚金处理
//            orderSettleNoTService.handleRentFine(settleOrders,settleCancelOrdersAccount);
//            //7 租客还历史欠款
//            orderSettleNoTService.repayHistoryDebtRentCancel(settleOrders,settleCancelOrdersAccount);
//            //8 租客金额 退还 包含 凹凸币，钱包 租车费用 押金 违章押金 退还 （优惠卷退还 TODO）
//            orderSettleNoTService.refundCancelCost(settleOrders,settleCancelOrdersAccount,orderStatusDTO);
//
//            log.info("OrderSettleService initSettleOrders settleOrders [{}]", GsonUtils.toJson(settleOrders));
//            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));
//        } catch (Exception e) {
//            log.error("OrderSettleService settleOrderCancel,e={},",e);
//            t.setStatus(e);
//            Cat.logError("结算失败  :{}",e);
//            throw new RuntimeException("结算失败 ,不能结算");
//        } finally {
//            t.complete();
//        }
        return true;
    }
}
