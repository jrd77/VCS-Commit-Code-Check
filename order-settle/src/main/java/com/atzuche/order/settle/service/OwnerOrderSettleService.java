package com.atzuche.order.settle.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.commons.PlatformProfitStatusEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineType;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.OwnerOrderNotFoundException;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.settle.exception.OwnerCancelSettleException;
import com.atzuche.order.settle.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.settle.service.notservice.AccountDebtNoTService;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * @Author ZhangBin
 * @Date 2020/3/5 14:18
 * @Description: 车主结算
 * 
 **/
@Slf4j
@Service
public class OwnerOrderSettleService {
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OrderSettleNoTService orderSettleNoTService;
    @Autowired
    private CashierSettleService cashierSettleService;
    @Autowired
    private CashierService cashierService;
    @Autowired
    private AccountDebtDetailNoTService accountDebtDetailNoTService;
    @Autowired
    private AccountDebtNoTService accountDebtNoTService;

    /*
     * @Author ZhangBin
     * @Date 2020/3/5 10:20
     * @Description: 订单取消-车主端结算
     * @param orderNo 主订单号
     * @param ownerOrderNo 车主子订单号
     * @param ownerMemNo 车主会员号
     * @return
     **/
    public void settleOwnerOrderCancel(String orderNo,String ownerOrderNo){
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        OwnerOrderEntity ownerOrder = null;
        try{
            ownerOrder = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
            if(ownerOrder == null){
                log.error("车主子订单获取为空 ownerOrderNo={}",ownerOrderNo);
                throw new OwnerOrderNotFoundException(ownerOrderNo);
            }
            //1、获取子订单信息
            SettleOrders settleOrders = new SettleOrders();
            settleOrders.setOrderNo(orderNo);
            settleOrders.setOwnerOrderNo(ownerOrderNo);
            settleOrders.setOwnerMemNo(ownerOrder.getMemNo());
            settleOrders.setOwnerOrder(ownerOrder);

            //2、查询车主罚金明细
            orderSettleNoTService.getCancelOwnerCostSettleDetail(settleOrders);

            //3、获取车主罚金与收益
            SettleCancelOrdersAccount settleCancelOrdersAccount = this.initOwnerSettleCancelOrdersAccount(settleOrders);

            //4、车主、平台罚金与收益处理
            this.platformAndOwnerIncomeFineHandler(settleOrders,settleCancelOrdersAccount);

        }catch (Exception e){
            e.printStackTrace();
            log.error("订单取消-车主端结算异常orderNo={}，ownerOrderNo={}，ownerMemNo={}",orderNo,ownerOrderNo,ownerOrder.getMemNo(),e);
            throw new OwnerCancelSettleException();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/3/6 10:47
     * @Description: 获取平台收益、车主罚金与收益
     * 
     **/
    private SettleCancelOrdersAccount initOwnerSettleCancelOrdersAccount(SettleOrders settleOrders) {
        OwnerCosts ownerCosts = settleOrders.getOwnerCosts();
        SettleCancelOrdersAccount settleCancelOrdersAccount = new SettleCancelOrdersAccount();
        //车主罚金
        int ownerFineAmt = 0;
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getOwnerOrderFineDeatails())){
            int amt = ownerCosts.getOwnerOrderFineDeatails().stream().filter(obj ->{
                //过滤的时候-->只过滤出租客并且是取消订单的违约金
                return SubsidySourceCodeEnum.OWNER.getCode().equals(obj.getFineSubsidySourceCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType());
            }).mapToInt(OwnerOrderFineDeatailEntity::getFineAmount).sum();
            ownerFineAmt = ownerFineAmt +amt;
        }
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getConsoleOwnerOrderFineDeatailEntitys())){
            int amt = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys()
                    .stream()
                    .filter(obj -> SubsidySourceCodeEnum.OWNER.getCode().equals(obj.getFineSubsidySourceCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType()))
                    .mapToInt(ConsoleOwnerOrderFineDeatailEntity::getFineAmount).sum();
            ownerFineAmt = ownerFineAmt +amt;
        }

        //车主收益
        int ownerFineIncomeAmt = 0;
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getOwnerOrderFineDeatails())){
            int amt = ownerCosts.getOwnerOrderFineDeatails().stream().filter(obj ->{
                //过滤的时候-->只过滤出租客并且是取消订单的违约金
                AccountOwnerCostSettleDetailEntity entity = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(obj,entity);
                entity.setCostType(OrderSettleNoTService.getCostTypeEnumBySubsidy(obj.getFineSubsidySourceCode()).getCode());
                entity.setSourceCode(String.valueOf(obj.getFineType()));
                entity.setSourceDetail(obj.getFineTypeDesc());
                entity.setAmt(obj.getFineAmount());
                settleCancelOrdersAccount.getAccountOwnerCostSettleDetails().add(entity);
                return SubsidySourceCodeEnum.OWNER.getCode().equals(obj.getFineSubsidyCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType());
            }).mapToInt(OwnerOrderFineDeatailEntity::getFineAmount).sum();
            ownerFineIncomeAmt = ownerFineIncomeAmt +amt;
        }
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getConsoleOwnerOrderFineDeatailEntitys())){
            int amt = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys().stream().filter(obj ->{
                AccountOwnerCostSettleDetailEntity entity = new AccountOwnerCostSettleDetailEntity();
                BeanUtils.copyProperties(obj,entity);
                entity.setCostType(OrderSettleNoTService.getCostTypeEnumBySubsidy(obj.getFineSubsidySourceCode()).getCode());
                entity.setSourceCode(String.valueOf(obj.getFineType()));
                entity.setSourceDetail(obj.getFineTypeDesc());
                entity.setAmt(obj.getFineAmount());
                settleCancelOrdersAccount.getAccountOwnerCostSettleDetails().add(entity);
                return SubsidySourceCodeEnum.OWNER.getCode().equals(obj.getFineSubsidyCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType());
            }).mapToInt(ConsoleOwnerOrderFineDeatailEntity::getFineAmount).sum();
            ownerFineIncomeAmt = ownerFineIncomeAmt +amt;
        }
        //平台罚金收入
        /*
        1、平台罚金都是收入不会负出
        2、因为平台买了基础保障费，取消订单后，需要收取基础保障费罚金，包含在取消订单的罚金中，找罚金补贴方是平台的即可。
        取消订单，平台罚金收益只会存在下面两张表中：
        `owner_order_fine_deatail`
        `renter_order_fine_deatail`

        3、以下两张表中并不会存平台相关的取消订单罚金费用
         `console_owner_order_fine_deatail`
         `console_renter_order_fine_deatail`
        */
        int platformFineImconeAmt=0;
        if(Objects.nonNull(ownerCosts) && !CollectionUtils.isEmpty(ownerCosts.getOwnerOrderFineDeatails())){
            //过滤的时候-->只过滤出租客并且是取消订单的违约金并且目标方式平台
            int amt = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys()
                    .stream()
                    .filter(obj -> SubsidySourceCodeEnum.PLATFORM.getCode().equals(obj.getFineSubsidyCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType()))
                    .mapToInt(ConsoleOwnerOrderFineDeatailEntity::getFineAmount).sum();
            platformFineImconeAmt = platformFineImconeAmt +amt;
        }

        settleCancelOrdersAccount.setOwnerFineAmt(ownerFineAmt);
        settleCancelOrdersAccount.setOwnerFineIncomeAmt(ownerFineIncomeAmt);
        settleCancelOrdersAccount.setPlatformFineImconeAmt(platformFineImconeAmt);
        settleCancelOrdersAccount.setOwnerFineTotal(ownerFineAmt + ownerFineIncomeAmt);
        log.info("取消订单-结算-车主端结算-获取平台收益、车主罚金与收益settleCancelOrdersAccount={},settleOrders={}", JSON.toJSONString(settleCancelOrdersAccount),JSON.toJSONString(settleOrders));
        return settleCancelOrdersAccount;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/3/6 10:46
     * @Description: 平台收益、车主收益合罚金的处理逻辑
     * 
     **/
    private void platformAndOwnerIncomeFineHandler(SettleOrders settleOrders, SettleCancelOrdersAccount settleCancelOrdersAccount) {
        AccountPlatformProfitEntity accountPlatformProfitEntity = new AccountPlatformProfitEntity();
        accountPlatformProfitEntity.setOrderNo(settleOrders.getOrderNo());
        accountPlatformProfitEntity.setStatus(PlatformProfitStatusEnum.CANCEL_SETTLE.getCode());
        accountPlatformProfitEntity.setOwnerOrderNo(settleOrders.getOwnerOrderNo());

       //车主收益或者罚金的处理
        this.repayHistoryDebtOwner(settleOrders,settleCancelOrdersAccount);

        // 平台收益处理
        if(settleCancelOrdersAccount.getPlatformFineImconeAmt()!=0){
            List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails = new ArrayList<>();
            AccountPlatformProfitDetailEntity entity = new AccountPlatformProfitDetailEntity();
            entity.setOrderNo(settleOrders.getOrderNo());
            entity.setAmt(settleCancelOrdersAccount.getPlatformFineImconeAmt());
            entity.setSourceDesc(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getTxt());
            entity.setSourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_FINE_COST.getCashNo());
            accountPlatformProfitDetails.add(entity);
            //insert 流水记录 account_platform_profit_detail(平台结算收益明细表)
            cashierSettleService.insertAccountPlatformProfitDetails(accountPlatformProfitDetails);
            accountPlatformProfitEntity.setPlatformReceivableAmt(settleCancelOrdersAccount.getPlatformFineImconeAmt());
            accountPlatformProfitEntity.setPlatformReceivedAmt(settleCancelOrdersAccount.getPlatformFineImconeAmt());
        }
        //insert 结算总表 account_platform_profit(平台订单收益结算表)
        cashierSettleService.insertAccountPlatformProfit(accountPlatformProfitEntity);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/3/6 11:00
     * @Description: 车主收益合罚金的处理逻辑
     * 
     **/
    private void repayHistoryDebtOwner(SettleOrders settleOrders, SettleCancelOrdersAccount settleCancelOrdersAccount){
        //当是收益的时候，走历史欠款抵扣
        SettleOrdersAccount settleOrdersAccount = new SettleOrdersAccount();
        if(settleCancelOrdersAccount.getOwnerFineTotal() > 0 ){
            log.info("取消订单-结算-车主端结算-车主收益开始抵扣历史欠款开始settleCancelOrdersAccount={},settleOrders={}",
                    JSON.toJSONString(settleCancelOrdersAccount),JSON.toJSONString(settleOrders));
            settleOrdersAccount.setOwnerCostAmtFinal(settleCancelOrdersAccount.getOwnerFineTotal());
            settleOrdersAccount.setOwnerCostSurplusAmt(settleCancelOrdersAccount.getOwnerFineTotal());
            settleOrdersAccount.setOrderNo(settleOrders.getOrderNo());
            settleOrdersAccount.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
            settleOrdersAccount.setOwnerMemNo(settleOrders.getOwnerMemNo());
            orderSettleNoTService.repayHistoryDebtOwner(settleOrdersAccount);
            log.info("取消订单-结算-车主端结算-车主收益开始抵扣历史欠款结束settleCancelOrdersAccount={},settleOrders={}",
                    JSON.toJSONString(settleCancelOrdersAccount),JSON.toJSONString(settleOrders));
            //历史欠款抵扣完之后还有剩余-->再走收益审核
            if(settleOrdersAccount.getOwnerCostSurplusAmt() > 0){
                log.info("取消订单-结算-车主端结算-车主收益抵抵扣历史欠款后仍有剩余，准备进入车主收益审核settleCancelOrdersAccount={},settleOrders={}",
                        JSON.toJSONString(settleCancelOrdersAccount),JSON.toJSONString(settleOrders));
                AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamine = new AccountOwnerIncomeExamineReqVO();
                accountOwnerIncomeExamine.setAmt(settleOrdersAccount.getOwnerCostSurplusAmt());
                accountOwnerIncomeExamine.setMemNo(settleOrders.getOwnerMemNo());
                accountOwnerIncomeExamine.setOrderNo(settleOrders.getOrderNo());
                accountOwnerIncomeExamine.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
                accountOwnerIncomeExamine.setRemark("罚金收入");
                accountOwnerIncomeExamine.setDetail("罚金收入");
                accountOwnerIncomeExamine.setOwnerOrderNo(settleOrders.getOwnerOrderNo());
                accountOwnerIncomeExamine.setStatus(AccountOwnerIncomeExamineStatus.WAIT_EXAMINE);
                accountOwnerIncomeExamine.setType(AccountOwnerIncomeExamineType.OWNER_INCOME);
                //insert  account_owner_income_examine(车主收益待审核表)
                cashierService.insertOwnerIncomeExamine(accountOwnerIncomeExamine);
                //insert 流水记录 account_owner_cost_settle_detail 租车费用结算明细
                cashierSettleService.insertAccountOwnerCostSettleDetails(settleCancelOrdersAccount.getAccountOwnerCostSettleDetails());
                log.info("取消订单-结算-车主端结算-车主收益抵抵扣历史欠款后仍有剩余，车主收益审核录入完成accountOwnerIncomeExamine={}",JSON.toJSONString(accountOwnerIncomeExamine));
            }else{
                log.info("取消订单-结算-车主端结算-车主收益抵抵扣历史欠款无剩余settleOrdersAccount={}",JSON.toJSONString(settleOrdersAccount));
            }
        }else if(settleCancelOrdersAccount.getOwnerFineTotal() < 0 ){
            log.info("取消订单-结算-车主端结算-车主罚金走历史欠款settleCancelOrdersAccount={}",JSON.toJSONString(settleCancelOrdersAccount));
            //当时罚金的时候，直接走历史欠款
            AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
            BeanUtils.copyProperties(settleOrders,accountInsertDebt);
            accountInsertDebt.setType(DebtTypeEnum.CANCEL.getCode());
            accountInsertDebt.setMemNo(settleOrders.getOwnerMemNo());
            accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            accountInsertDebt.setAmt(settleCancelOrdersAccount.getOwnerFineAmt());
            /*
               有历史欠款就更新欠款总金额，没有历史欠款就新增历史欠款  insert/update  account_debt
               添加历史欠款明细记录 insert  account_debt_detail
             */
            int result = cashierService.createDebt(accountInsertDebt);
            log.info("取消订单-结算-车主端结算-车主罚金走历史欠款result={},accountInsertDebt={}",result,JSON.toJSONString(accountInsertDebt));
        }else{
            log.info("取消订单-结算-车主端结算-获取车主罚金与收益后续处理--->车主无收益，无罚金settleOrders={},settleCancelOrdersAccount={}",JSON.toJSONString(settleOrders),JSON.toJSONString(settleCancelOrdersAccount));
        }

    }
}
