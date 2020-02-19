package com.atzuche.order.settle.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Map;

import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayReqVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderSettlementMq;
import com.autoyol.event.rabbit.neworder.OrderWzSettlementMq;
import com.autoyol.platformcost.model.FeeResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitDetailNotService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformSubsidyDetailNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OrderSettleNewService {
    @Autowired CashierNoTService cashierNoTService;
    @Autowired AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;
    @Autowired AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired AccountPlatformSubsidyDetailNoTService accountPlatformSubsidyDetailNoTService;
    @Autowired AccountPlatformProfitDetailNotService accountPlatformProfitDetailNotService;
    @Autowired private OrderSettleNoTService orderSettleNoTService;
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private CashierPayService cashierPayService;
    @Autowired private BaseProducer baseProducer;



    /**
     * 结算逻辑
     */
    @Transactional(rollbackFor=Exception.class)
    public void settleOrder(SettleOrders settleOrders, SettleOrdersDefinition settleOrdersDefinition,OrderPayCallBack callBack) {
        //7.1 租车费用  总费用 信息落库 并返回最新租车费用 实付
        AccountRenterCostSettleEntity accountRenterCostSettle = cashierSettleService.updateRentSettleCost(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), settleOrdersDefinition.getAccountRenterCostSettleDetails());
        log.info("OrderSettleService updateRentSettleCost [{}]",GsonUtils.toJson(accountRenterCostSettle));
        Cat.logEvent("updateRentSettleCost",GsonUtils.toJson(accountRenterCostSettle));

        //7.2 车主 费用 落库表
        cashierSettleService.insertAccountOwnerCostSettle(settleOrders.getOrderNo(),settleOrders.getOwnerOrderNo(),settleOrders.getOwnerMemNo(),settleOrdersDefinition.getAccountOwnerCostSettleDetails());
        //8 获取租客 实付 车辆押金
        int depositAmt = cashierSettleService.getRentDeposit(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        int depositAmtRealPay = cashierSettleService.getRentDepositRealPay(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        SettleOrdersAccount settleOrdersAccount = new SettleOrdersAccount();
        BeanUtils.copyProperties(settleOrders,settleOrdersAccount);
        //查询订单剩余应付
        settleOrdersAccount.setRentCostAmtFinal(accountRenterCostSettle.getRentAmt());
        settleOrdersAccount.setRentCostPayAmt(accountRenterCostSettle.getShifuAmt());
        settleOrdersAccount.setDepositAmt(depositAmtRealPay);
        settleOrdersAccount.setDepositSurplusAmt(depositAmt);
        settleOrdersAccount.setOwnerCostAmtFinal(settleOrdersDefinition.getOwnerCostAmtFinal());
        settleOrdersAccount.setOwnerCostSurplusAmt(settleOrdersDefinition.getOwnerCostAmtFinal());
        //>0 表示 实付 大于应退 差值为应退（实付>0  应付小于0 getRentAmt 为所有租车费用明细含补贴 相加之和）
        int rentCostSurplusAmt = (accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt())>0
                ?(accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt())
                :0;
        settleOrdersAccount.setRentCostSurplusAmt(rentCostSurplusAmt);
        log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount one [{}]", GsonUtils.toJson(settleOrdersAccount));
        Cat.logEvent("settleOrdersAccount",GsonUtils.toJson(settleOrdersAccount));

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setSettleTime(LocalDateTime.now());
        orderStatusDTO.setOrderNo(settleOrders.getOrderNo());
        orderStatusDTO.setStatus(OrderStatusEnum.TO_WZ_SETTLE.getStatus());
        orderStatusDTO.setSettleStatus(SettleStatusEnum.SETTLED.getCode());
        //9 租客费用 结余处理
        orderSettleNoTService.rentCostSettle(settleOrders,settleOrdersAccount,callBack);
        //10租客车辆押金/租客剩余租车费用 结余历史欠款
        orderSettleNoTService.repayHistoryDebtRent(settleOrdersAccount);
        //11 租客费用 退还
        orderSettleNoTService.refundRentCost(settleOrdersAccount,settleOrdersDefinition.getAccountRenterCostSettleDetails(),orderStatusDTO,settleOrders);
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
        //16 退优惠卷 凹凸币
        orderSettleNoTService.settleUndoCoupon(settleOrders.getOrderNo(),settleOrders.getRentCosts().getRenterOrderSubsidyDetails());
        log.info("OrderSettleService settleUndoCoupon settleUndoCoupon one [{}]", GsonUtils.toJson(settleOrdersAccount));
        Cat.logEvent("settleUndoCoupon",GsonUtils.toJson(settleOrdersAccount));
    }

    /**
     * 通过收银台查取  待支付租车费用金额
     * @param orderNo memNo
     * @return
     */
    public int getRentCostAmtFinal(String orderNo,String memNo) {
        OrderPayReqVO orderPayReqVO = new OrderPayReqVO();
        orderPayReqVO.setOrderNo(orderNo);
        orderPayReqVO.setMenNo(memNo);
        orderPayReqVO.setIsUseWallet(YesNoEnum.NO.getCode());
        List<String> payKind = ImmutableList.of(
                DataPayKindConstant.RENT_AMOUNT
        );
        orderPayReqVO.setPayKind(payKind);
        OrderPayableAmountResVO vo = cashierPayService.getOrderPayableAmount(orderPayReqVO);
        return vo.getAmt();
    }

    /**
     * 车辆结算  校验费用落库等无实物操作
     */
    public SettleOrdersDefinition settleOrderFirst(SettleOrders settleOrders){
        //1 查询所有租客费用明细
        orderSettleNoTService.getRenterCostSettleDetail(settleOrders);
        log.info("OrderSettleService getRenterCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));
        Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));

        //3 查询所有车主费用明细 TODO 暂不支持 多个车主
        orderSettleNoTService.getOwnerCostSettleDetail(settleOrders);
        Cat.logEvent("getOwnerCostSettleDetail",GsonUtils.toJson(settleOrders));
        log.info("OrderSettleService getOwnerCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));

        //4 计算费用统计  资金统计
        SettleOrdersDefinition settleOrdersDefinition = orderSettleNoTService.settleOrdersDefinition(settleOrders);
        log.info("OrderSettleService settleOrdersDefinition settleOrdersDefinition [{}]", GsonUtils.toJson(settleOrdersDefinition));
        Cat.logEvent("SettleOrdersDefinition",GsonUtils.toJson(settleOrdersDefinition));

        //5 费用明细先落库
        orderSettleNoTService.insertSettleOrders(settleOrdersDefinition);

        //6 费用平账 平台收入 + 平台补贴 + 车主费用 + 车主补贴 + 租客费用 + 租客补贴 = 0
        int totleAmt = settleOrdersDefinition.getPlatformProfitAmt() + settleOrdersDefinition.getPlatformSubsidyAmt()
                + settleOrdersDefinition.getOwnerCostAmt() + settleOrdersDefinition.getOwnerSubsidyAmt()
                + settleOrdersDefinition.getRentCostAmt() + settleOrdersDefinition.getRentSubsidyAmt();
        if(totleAmt != 0){
            Cat.logEvent("pingzhang","平账失败");
            //TODO 走Cat告警
            throw new OrderSettleFlatAccountException();
        }
        return settleOrdersDefinition;
    }


    /**
     * 租车费用 对应补贴冲账 记录
     * @param renterOrderCostDetail
     * @param settleOrdersDefinition
     */
    public void addRentCostToPlatformAndOwner(RenterOrderCostDetailEntity renterOrderCostDetail, SettleOrdersDefinition settleOrdersDefinition) {
        //1 提前还车违约金 费用收益方 平台   平台端记录冲账流水
        if(RenterCashCodeEnum.FINE_AMT.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.FINE_AMT.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.FINE_AMT.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        //2 提前还车服务费 费用收益方 平台   平台端记录冲账流水
        if(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.SRV_RETURN_COST.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        //2 提前取车车服务费 费用收益方 平台   平台端记录冲账流水
        if(RenterCashCodeEnum.SRV_GET_COST.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.SRV_GET_COST.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.SRV_GET_COST.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        //3 平台保障费 费用收益方 平台   平台端记录冲账流水
        if(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        //4 全面保障费 费用收益方 平台   平台端记录冲账流水
        if(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.ABATEMENT_INSURE.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        //5 附加驾驶人险 费用收益方 平台   平台端记录冲账流水
        if(RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        //6 手续费 平台   平台端记录冲账流水
        if(RenterCashCodeEnum.FEE.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.FEE.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.FEE.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }

        //7 平台卷  平台   平台端记录冲账流水
        if(RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformSubsidyDetailEntity accountPlatformSubsidyDetail = new AccountPlatformSubsidyDetailEntity();
            accountPlatformSubsidyDetail.setAmt(-totalAmount);
            accountPlatformSubsidyDetail.setSourceCode(RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo());
            accountPlatformSubsidyDetail.setSourceDesc(RenterCashCodeEnum.REAL_COUPON_OFFSET.getTxt());
            accountPlatformSubsidyDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformSubsidyDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformSubsidy(accountPlatformSubsidyDetail);
        }
        //8 取还车卷  平台   平台端记录冲账流水
        if(RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformSubsidyDetailEntity accountPlatformSubsidyDetail = new AccountPlatformSubsidyDetailEntity();
            accountPlatformSubsidyDetail.setAmt(-totalAmount);
            accountPlatformSubsidyDetail.setSourceCode(RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getCashNo());
            accountPlatformSubsidyDetail.setSourceDesc(RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getTxt());
            accountPlatformSubsidyDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformSubsidyDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformSubsidy(accountPlatformSubsidyDetail);
        }
        //9 凹凸币  平台   平台端记录冲账流水
        if(RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformSubsidyDetailEntity accountPlatformSubsidyDetail = new AccountPlatformSubsidyDetailEntity();
            accountPlatformSubsidyDetail.setAmt(-totalAmount);
            accountPlatformSubsidyDetail.setSourceCode(RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo());
            accountPlatformSubsidyDetail.setSourceDesc(RenterCashCodeEnum.AUTO_COIN_DEDUCT.getTxt());
            accountPlatformSubsidyDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformSubsidyDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformSubsidy(accountPlatformSubsidyDetail);
        }
    }




    /**
     * 车主端代管车服务费车主端代管车服务费 费用平台端冲账
     * @param proxyExpense
     * @param settleOrdersDefinition
     */
    public void addProxyExpenseAmtToPlatform(OwnerOrderPurchaseDetailEntity proxyExpense, SettleOrdersDefinition settleOrdersDefinition) {
        int totalAmount = proxyExpense.getTotalAmount();
        AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
        accountPlatformProfitDetail.setAmt(-totalAmount);
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_PROXY_EXPENSE_COST.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_OWNER_PROXY_EXPENSE_COST.getTxt());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(proxyExpense.getId()));
        accountPlatformProfitDetail.setOrderNo(proxyExpense.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);

    }

    /**
     * 车主端平台服务费 费用平台端冲账
     * @param serviceExpense
     * @param settleOrdersDefinition
     */
    public void addServiceExpenseAmtToPlatform(OwnerOrderPurchaseDetailEntity serviceExpense, SettleOrdersDefinition settleOrdersDefinition) {
        int totalAmount = serviceExpense.getTotalAmount();
        AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
        accountPlatformProfitDetail.setAmt(-totalAmount);
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_SERVICE_EXPENSE_COST.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_OWNER_SERVICE_EXPENSE_COST.getTxt());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(serviceExpense.getId()));
        accountPlatformProfitDetail.setOrderNo(serviceExpense.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
    }

    /**
     * 获取车主增值服务费用列表 费用平台端冲账
     * @param renterOrderCostDetail
     * @param settleOrdersDefinition
     */
    public void addOwnerOrderIncrementAmtToPlatform(OwnerOrderIncrementDetailEntity renterOrderCostDetail, SettleOrdersDefinition settleOrdersDefinition) {
        int totalAmount = renterOrderCostDetail.getTotalAmount();
        AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
        accountPlatformProfitDetail.setAmt(-totalAmount);
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getTxt());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
        accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
    }

    /**
     * 获取gps服务费 费用平台端冲账
     * @param renterOrderCostDetail
     * @param settleOrdersDefinition
     */
    public void addGpsCostAmtToPlatform(OwnerOrderPurchaseDetailEntity renterOrderCostDetail, SettleOrdersDefinition settleOrdersDefinition) {
        int totalAmount = renterOrderCostDetail.getTotalAmount();
        AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
        accountPlatformProfitDetail.setAmt(totalAmount);
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getTxt());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
        accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
    }


    /**
     *  先查询  发现 有结算数据停止结算 手动处理
     * @param orderNo
     */
    public void checkIsSettle(String orderNo) {
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetailEntitys = accountRenterCostSettleDetailNoTService.getAccountRenterCostSettleDetail(orderNo);
        List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetailEntitys = accountPlatformProfitDetailNotService.getPlatformProfitDetails(orderNo);
        List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetailEntitys = accountPlatformSubsidyDetailNoTService.getPlatformSubsidyDetails(orderNo);
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetailEntitys = accountOwnerCostSettleDetailNoTService.getAccountOwnerCostSettleDetails(orderNo);

        if(!CollectionUtils.isEmpty(accountRenterCostSettleDetailEntitys)){
            throw new RuntimeException("有结算数据停止结算");
        }
        if(!CollectionUtils.isEmpty(accountPlatformProfitDetailEntitys)){
            throw new RuntimeException("有结算数据停止结算");
        }
        if(!CollectionUtils.isEmpty(accountPlatformSubsidyDetailEntitys)){
            throw new RuntimeException("有结算数据停止结算");
        }
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetailEntitys)){
            throw new RuntimeException("有结算数据停止结算");
        }
    }


    public void addRenterGetAndReturnCarAmtToPlatform(AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail, SettleOrdersDefinition settleOrdersDefinition) {
        int totalAmount = accountRenterCostSettleDetail.getAmt();
        AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
        accountPlatformProfitDetail.setAmt(-totalAmount);
        accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getTxt());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(accountRenterCostSettleDetail.getId()));
        accountPlatformProfitDetail.setOrderNo(accountRenterCostSettleDetail.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
    }

    public void addPlatFormAmt(SettleOrdersDefinition settleOrdersDefinition,SettleOrders settleOrders) {
        //租客交接车油费
        int rentOilAmt = settleOrdersDefinition.getAccountRenterCostSettleDetails().stream().filter(obj ->{
            return RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_OIL_COST.getCashNo().equals(obj.getCostCode());
        }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
        //
        //车主交接车油费
        int ownerOilAmt = settleOrdersDefinition.getAccountOwnerCostSettleDetails().stream().filter(obj ->{
            return OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getCashNo().equals(obj.getSourceCode());
        }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
        //平台补贴油费
        int platFormAmt =0;
        if(!CollectionUtils.isEmpty(settleOrdersDefinition.getAccountPlatformSubsidyDetails())){
            platFormAmt = settleOrdersDefinition.getAccountPlatformSubsidyDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.SUBSIDY_OIL.getCashNo().equals(obj.getSourceCode());
            }).mapToInt(AccountPlatformSubsidyDetailEntity::getAmt).sum();
        }

        int oilAmt = rentOilAmt + ownerOilAmt + platFormAmt;
        if(oilAmt!=0){
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-oilAmt);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.SUBSIDY_OIL.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.SUBSIDY_OIL.getTxt());
            accountPlatformProfitDetail.setOrderNo(settleOrders.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
    }

    /**
     * 订单车辆结算成功事件
     * @param orderNo
     */
    public void sendOrderSettleSuccessMq(String orderNo,String renterMemNo,RentCosts rentCosts) {
        AccountRenterCostSettleEntity entity=cashierSettleService.getAccountRenterCostSettleEntity(orderNo,renterMemNo);
        OrderSettlementMq orderSettlementMq = new OrderSettlementMq();
        if(Objects.nonNull(entity) && Objects.nonNull(entity)){

            String insureTotalPrices = Objects.nonNull(entity.getBasicEnsureAmount())?String.valueOf(entity.getBasicEnsureAmount()):"0";
            orderSettlementMq.setInsureTotalPrices(insureTotalPrices);
            String abatementInsure = Objects.nonNull(entity.getComprehensiveEnsureAmount())?String.valueOf(entity.getComprehensiveEnsureAmount()):"0";
            orderSettlementMq.setAbatementInsure(abatementInsure);
        }
        int subsidyPlamtAmt=0;
        int subsidyOwnerAmt=0;
        if(!CollectionUtils.isEmpty(rentCosts.getOrderConsoleSubsidyDetails())){
            subsidyPlamtAmt = subsidyPlamtAmt + rentCosts.getOrderConsoleSubsidyDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo().equals(obj.getSubsidyTypeCode());
            }).mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
             subsidyOwnerAmt = subsidyOwnerAmt + rentCosts.getOrderConsoleSubsidyDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo().equals(obj.getSubsidyTypeCode());
            }).mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        if(!CollectionUtils.isEmpty(rentCosts.getRenterOrderSubsidyDetails())){
            subsidyPlamtAmt = subsidyPlamtAmt + rentCosts.getRenterOrderSubsidyDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo().equals(obj.getSubsidyTypeCode());
            }).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
            subsidyOwnerAmt = subsidyOwnerAmt + rentCosts.getRenterOrderSubsidyDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo().equals(obj.getSubsidyTypeCode());
            }).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        orderSettlementMq.setPlatformCouponDeductionAmount(String.valueOf(subsidyPlamtAmt));
        orderSettlementMq.setOwnerCouponDeductionAmount(String.valueOf(subsidyOwnerAmt));

        //查询租车费用  过滤租金 取 日均价 多个的话 按id倒叙  取第一个
        if(!CollectionUtils.isEmpty(rentCosts.getRenterOrderCostDetails())){
           int price = rentCosts.getRenterOrderCostDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getCostCode());
            }).sorted(Comparator.comparing(RenterOrderCostDetailEntity::getId).reversed())
                    .limit(1).mapToInt(RenterOrderCostDetailEntity::getUnitPrice).sum();

            int rentAmt = rentCosts.getRenterOrderCostDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getCostCode());
            }).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
            orderSettlementMq.setHolidayAverage(String.valueOf(price));
            orderSettlementMq.setRentAmt(String.valueOf(rentAmt));
        }
        orderSettlementMq.setStatus(0);
        orderSettlementMq.setOrderNo(orderNo);
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderSettlementMq);
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_SETTLEMENT_SUCCESS.exchange,NewOrderMQActionEventEnum.ORDER_SETTLEMENT_SUCCESS.routingKey,orderMessage);
    }
    /**
     * 订单违章结算成功事件
     * @param orderNo
     */
    public void sendOrderWzSettleSuccessMq(String orderNo) {
        OrderWzSettlementMq orderSettlementMq = new OrderWzSettlementMq();
        orderSettlementMq.setStatus(0);
        orderSettlementMq.setOrderNo(orderNo);
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderSettlementMq);
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_SUCCESS.exchange,NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_SUCCESS.routingKey,orderMessage);
    }

    /**
     * 订单结算失败事件
     * @param orderNo
     */
    public void sendOrderSettleFailMq(String orderNo) {
        OrderSettlementMq orderSettlementMq = new OrderSettlementMq();
        orderSettlementMq.setStatus(1);
        orderSettlementMq.setOrderNo(orderNo);
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderSettlementMq);
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_SETTLEMENT_FAIL.exchange,NewOrderMQActionEventEnum.ORDER_SETTLEMENT_FAIL.routingKey,orderMessage);
    }
    /**
     * 订单结算失败事件
     * @param orderNo
     */
    public void sendOrderWzSettleFailMq(String orderNo) {
        OrderWzSettlementMq orderSettlementMq = new OrderWzSettlementMq();
        orderSettlementMq.setStatus(1);
        orderSettlementMq.setOrderNo(orderNo);
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderSettlementMq);
        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_FAIL.exchange,NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_FAIL.routingKey,orderMessage);
    }

    /**
     * 查询 租客 应付金额
     * @param rentCosts
     * @return
     */
    public int getYingfuRenterCost(RentCosts rentCosts) {
        int renterCost=0;
        // 租车费用
        if(!CollectionUtils.isEmpty(rentCosts.getRenterOrderCostDetails())){
            renterCost = renterCost +  rentCosts.getRenterOrderCostDetails().stream().mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
        }
        //交接车油费
        if(Objects.nonNull(rentCosts.getOilAmt())){
            String oilDifferenceCrash = rentCosts.getOilAmt().getOilDifferenceCrash();
            if(!StringUtil.isBlank(oilDifferenceCrash)){
                renterCost = renterCost + Integer.valueOf(oilDifferenceCrash);
            }
        }
        //交接 超历程
        if(Objects.nonNull(rentCosts.getMileageAmt())){
            Integer totalFee = rentCosts.getMileageAmt().getTotalFee();
            if(Objects.nonNull(totalFee)){
                renterCost = renterCost + totalFee;
            }
        }
        // 补贴
        if(!CollectionUtils.isEmpty(rentCosts.getRenterOrderSubsidyDetails())){
            renterCost = renterCost +  rentCosts.getRenterOrderSubsidyDetails().stream().mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        //租客罚金
        if(!CollectionUtils.isEmpty(rentCosts.getRenterOrderFineDeatails())){
            renterCost = renterCost +  rentCosts.getRenterOrderFineDeatails().stream().mapToInt(RenterOrderFineDeatailEntity::getFineAmount).sum();
        }
        //管理后台补贴
        if(!CollectionUtils.isEmpty(rentCosts.getOrderConsoleSubsidyDetails())){
            renterCost = renterCost +  rentCosts.getOrderConsoleSubsidyDetails().stream().mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        //获取全局的租客订单罚金明细
        if(!CollectionUtils.isEmpty(rentCosts.getConsoleRenterOrderFineDeatails())){
            renterCost = renterCost +  rentCosts.getConsoleRenterOrderFineDeatails().stream().mapToInt(ConsoleRenterOrderFineDeatailEntity::getFineAmount).sum();
        }
        //后台管理操作费用表（无条件补贴）
        if(!CollectionUtils.isEmpty(rentCosts.getOrderConsoleCostDetailEntity())){
            renterCost = renterCost +  rentCosts.getOrderConsoleCostDetailEntity().stream().mapToInt(OrderConsoleCostDetailEntity::getSubsidyAmount).sum();
        }
        return renterCost;
    }
}
