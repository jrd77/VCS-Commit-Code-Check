package com.atzuche.order.settle.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitDetailNotService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformSubsidyDetailNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostSettleDetailNoTService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.service.notservice.OrderWzSettleNoTService;
import com.atzuche.order.settle.vo.req.RentCostsWz;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.atzuche.order.settle.vo.req.SettleOrdersWz;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OrderWzSettleNewService {
    @Autowired CashierNoTService cashierNoTService;
    @Autowired AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;
    @Autowired AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired AccountPlatformSubsidyDetailNoTService accountPlatformSubsidyDetailNoTService;
    @Autowired AccountPlatformProfitDetailNotService accountPlatformProfitDetailNotService;
    @Autowired private OrderSettleNoTService orderSettleNoTService;
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired
    private AccountRenterWzDepositCostSettleDetailNoTService accountRenterWzDepositCostSettleDetailNoTService;
    @Autowired
    OrderWzSettleNoTService orderWzSettleNoTService;
    
    /**
     *  先查询  发现 有结算数据停止结算 手动处理
     * @param orderNo
     */
    public void checkIsSettle(String orderNo) {
    	//违章
    	/**
    	 * 租车费用结算明细表
			account_renter_cost_settle_detail
    	 */
//        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetailEntitys = accountRenterCostSettleDetailNoTService.getAccountRenterCostSettleDetail(orderNo);
        /**
         * 违章费用结算明细表
	account_renter_wz_deposit_cost_settle_detail
         */
    	List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetailEntitys = accountRenterWzDepositCostSettleDetailNoTService.getAccountRenterWzDepositCostSettleDetail(orderNo);
    	
    	/**
    	 * todo 应该根据违章押金的部分的补贴和收益来处理，代收代付？？  先注释掉。
    	 */
    	//平台
//        List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetailEntitys = accountPlatformProfitDetailNotService.getPlatformProfitDetails(orderNo);
//        List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetailEntitys = accountPlatformSubsidyDetailNoTService.getPlatformSubsidyDetails(orderNo);
//        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetailEntitys = accountOwnerCostSettleDetailNoTService.getAccountOwnerCostSettleDetails(orderNo);

        if(!CollectionUtils.isEmpty(accountRenterWzDepositCostSettleDetailEntitys)){
            throw new RuntimeException("有违章结算数据停止结算");
        }
//        if(!CollectionUtils.isEmpty(accountPlatformProfitDetailEntitys)){
//            throw new RuntimeException("有平台收入结算数据停止结算");
//        }
//        if(!CollectionUtils.isEmpty(accountPlatformSubsidyDetailEntitys)){
//            throw new RuntimeException("有平台补贴结算数据停止结算");
//        }
        
//        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetailEntitys)){
//            throw new RuntimeException("有结算数据停止结算");
//        }
    }
    
    
    // ----------------------------------------------------------------------------------------------------------------------------------
    

    /**
     * 结算逻辑
     */
    @Transactional(rollbackFor=Exception.class)
    public void settleOrder(SettleOrdersWz settleOrders) {
        //7.1 违章费用  总费用 信息落库 并返回最新租车费用 实付
    	/**
    	 * 违章费用总表及其结算总表
		account_renter_wz_deposit_cost
    	 */
        AccountRenterCostSettleEntity accountRenterCostSettle = cashierSettleService.updateRentSettleCost(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), null);
        log.info("OrderSettleService updateRentSettleCost [{}]",GsonUtils.toJson(accountRenterCostSettle));
        Cat.logEvent("updateRentSettleCost",GsonUtils.toJson(accountRenterCostSettle));
        
        //8 获取租客 实付 违章押金
        int wzDepositAmt = cashierSettleService.getSurplusWZDepositCostAmt(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        
        SettleOrdersAccount settleOrdersAccount = new SettleOrdersAccount();
        BeanUtils.copyProperties(settleOrders,settleOrdersAccount);
        settleOrdersAccount.setRentCostAmtFinal(accountRenterCostSettle.getRentAmt());
        settleOrdersAccount.setRentCostPayAmt(accountRenterCostSettle.getShifuAmt());
        settleOrdersAccount.setDepositAmt(wzDepositAmt);
        settleOrdersAccount.setDepositSurplusAmt(wzDepositAmt);
        
        int rentCostSurplusAmt = (accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt())<=0?0:(accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt());
        settleOrdersAccount.setRentCostSurplusAmt(rentCostSurplusAmt);
        log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount one [{}]", GsonUtils.toJson(settleOrdersAccount));
        Cat.logEvent("settleOrdersAccount",GsonUtils.toJson(settleOrdersAccount));

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(settleOrders.getOrderNo());
        //10租客车辆押金/租客剩余租车费用 结余历史欠款
        orderWzSettleNoTService.repayWzHistoryDebtRent(settleOrdersAccount);
        //12 租客押金 退还
        orderWzSettleNoTService.refundWzDepositAmt(settleOrdersAccount,orderStatusDTO);
        //15 更新订单状态
        settleOrdersAccount.setOrderStatusDTO(orderStatusDTO);
        orderWzSettleNoTService.saveOrderStatusInfo(settleOrdersAccount);
        log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount two [{}]", GsonUtils.toJson(settleOrdersAccount));
    }

    /**
     * 车辆结算  校验费用落库等无实物操作
     */
    public void settleOrderFirst(SettleOrdersWz settleOrders){
        //1 查询所有租客费用明细
    	orderWzSettleNoTService.getRenterCostSettleDetail(settleOrders);
        log.info("wz OrderSettleService getRenterCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));
        Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));
 
        RentCostsWz rentCosts = settleOrders.getRentCostsWz();
        if(Objects.nonNull(rentCosts)){
            //1.1 查询租车费用
      
            List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetails = rentCosts.getRenterOrderWzCostDetails();
            if(!CollectionUtils.isEmpty(renterOrderWzCostDetails)){
            	
            	List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetails = new ArrayList<AccountRenterWzDepositCostSettleDetailEntity>();
            	
                for(int i=0; i<renterOrderWzCostDetails.size();i++){
                	RenterOrderWzCostDetailEntity renterOrderWzCostDetail = renterOrderWzCostDetails.get(i);
                    AccountRenterWzDepositCostSettleDetailEntity accountRenterWzDepositCostSettleDetail = new AccountRenterWzDepositCostSettleDetailEntity();
                    //赋值
                    accountRenterWzDepositCostSettleDetail.setOrderNo(renterOrderWzCostDetail.getOrderNo());
                    accountRenterWzDepositCostSettleDetail.setMemNo(String.valueOf(renterOrderWzCostDetail.getMemNo()));
                    accountRenterWzDepositCostSettleDetail.setUniqueNo(String.valueOf(renterOrderWzCostDetail.getId()));
                    accountRenterWzDepositCostSettleDetail.setPrice(renterOrderWzCostDetail.getAmount());
                    accountRenterWzDepositCostSettleDetail.setWzAmt(renterOrderWzCostDetail.getAmount());
                    accountRenterWzDepositCostSettleDetail.setUnit(1); //默认1
                    
                    accountRenterWzDepositCostSettleDetails.add(accountRenterWzDepositCostSettleDetail);
                }
                
                
                if(accountRenterWzDepositCostSettleDetails.size() > 0) {
                	//落库
                	cashierSettleService.insertAccountRenterWzDepoistCostSettleDetails(accountRenterWzDepositCostSettleDetails);
                	
                }
            }
        }
        
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
        accountPlatformProfitDetail.setAmt(-totalAmount);
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getTxt());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
        accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
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
        int ownerOilAmt = settleOrdersDefinition.getAccountRenterCostSettleDetails().stream().filter(obj ->{
            return OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getCashNo().equals(obj.getCostCode());
        }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
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
}
