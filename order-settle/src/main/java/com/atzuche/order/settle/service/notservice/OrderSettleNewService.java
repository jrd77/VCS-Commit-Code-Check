package com.atzuche.order.settle.service.notservice;

import java.util.List;

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
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
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


    /**
     * 结算逻辑
     */
    @Transactional(rollbackFor=Exception.class)
    public void settleOrder(SettleOrders settleOrders,SettleOrdersDefinition settleOrdersDefinition) {
        //7.1 租车费用  总费用 信息落库 并返回最新租车费用 实付
        AccountRenterCostSettleEntity accountRenterCostSettle = cashierSettleService.updateRentSettleCost(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), settleOrdersDefinition.getAccountRenterCostSettleDetails());
        log.info("OrderSettleService updateRentSettleCost [{}]",GsonUtils.toJson(accountRenterCostSettle));
        Cat.logEvent("updateRentSettleCost",GsonUtils.toJson(accountRenterCostSettle));

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
        Cat.logEvent("settleOrdersAccount",GsonUtils.toJson(settleOrdersAccount));

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
        //16 退优惠卷
        orderSettleNoTService.settleUndoCoupon(settleOrders.getOrderNo(),settleOrders.getRentCosts().getRenterOrderSubsidyDetails());
        log.info("OrderSettleService settleUndoCoupon settleUndoCoupon one [{}]", GsonUtils.toJson(settleOrdersAccount));
        Cat.logEvent("settleUndoCoupon",GsonUtils.toJson(settleOrdersAccount));
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

        //6 费用平账 平台收入 + 平台补贴 +  + 车主补贴 + 租客费用 + 租客补贴 = 0
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
        //4 平台保障费 费用收益方 平台   平台端记录冲账流水
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
        //5 平台保障费 费用收益方 平台   平台端记录冲账流水
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
        //6 附加驾驶人险 费用收益方 平台   平台端记录冲账流水
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
        //7 手续费 平台   平台端记录冲账流水
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
        //8 车主卷抵扣  车主  车主端记录冲账流水
        if(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetailEntity = new AccountOwnerCostSettleDetailEntity();
            accountOwnerCostSettleDetailEntity.setAmt(-totalAmount);
            accountOwnerCostSettleDetailEntity.setSourceDetail(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getTxt());
            accountOwnerCostSettleDetailEntity.setSourceCode(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo());
            accountOwnerCostSettleDetailEntity.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountOwnerCostSettleDetailEntity.setOrderNo(renterOrderCostDetail.getOrderNo());
            accountOwnerCostSettleDetailEntity.setMemNo(renterOrderCostDetail.getMemNo());
            settleOrdersDefinition.addOwnerCosts(accountOwnerCostSettleDetailEntity);
        }
        //9 限时红包 TODO
        //10 平台卷  平台   平台端记录冲账流水
        if(RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.REAL_COUPON_OFFSET.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        //11 取还车卷  平台   平台端记录冲账流水
        if(RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        //12 凹凸币  平台   平台端记录冲账流水
        if(RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.AUTO_COIN_DEDUCT.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
    }


    /**
     * 交接车超历程费用
     * @param mileageAmt
     * @param settleOrdersDefinition
     */
    public void addMileageAmtToPlatformAndOwner(RenterOrderCostDetailEntity mileageAmt, SettleOrdersDefinition settleOrdersDefinition) {
        if(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getCashNo().equals(mileageAmt.getCostCode())){
            int totalAmount = mileageAmt.getTotalAmount();
            AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetailEntity = new AccountOwnerCostSettleDetailEntity();
            accountOwnerCostSettleDetailEntity.setAmt(-totalAmount);
            accountOwnerCostSettleDetailEntity.setSourceDetail(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getTxt());
            accountOwnerCostSettleDetailEntity.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_MILEAGE_COST.getCashNo());
            accountOwnerCostSettleDetailEntity.setUniqueNo(String.valueOf(mileageAmt.getId()));
            accountOwnerCostSettleDetailEntity.setOrderNo(mileageAmt.getOrderNo());
            accountOwnerCostSettleDetailEntity.setMemNo(mileageAmt.getMemNo());
            settleOrdersDefinition.addOwnerCosts(accountOwnerCostSettleDetailEntity);
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
        accountPlatformProfitDetail.setAmt(-totalAmount);
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


}
