/**
 * 
 */
package com.atzuche.order.settle.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineType;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountOldDebtReqVO;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.atzuche.order.settle.vo.res.AccountOldDebtResVO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jhuang
 *
 */
@Service
@Slf4j
public class OrderOwnerSettleNewService {
	@Autowired
	private CashierService cashierService;
	@Autowired
	private AccountDebtService accountDebtService;
	@Autowired 
	private CashierSettleService cashierSettleService;
	
	/**
     * 车主费用结余 处理 （如果结算产生欠款，需要记录。200526）
     * @param settleOrdersAccount
     */
    public void rentCostSettleOwner(SettleOrders settleOrders , SettleOrdersAccount settleOrdersAccount,OrderPayCallBack callBack) {
        //2如果 步骤1 结算 小于0  此订单产生历史欠款
    	if(settleOrdersAccount.getOwnerCostSurplusAmt() < 0){
    		int amt = settleOrdersAccount.getOwnerCostSurplusAmt();
    		log.info("rentCostSettleOwner do create debt,params orderNo=[{}],ownerNo=[{}],amt=[{}]",settleOrders.getOrderNo(),settleOrders.getOwnerMemNo(),amt);
    		
            //2.1 记录历史欠款
            AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
            BeanUtils.copyProperties(settleOrders,accountInsertDebt);
            accountInsertDebt.setMemNo(settleOrders.getOwnerMemNo());
            accountInsertDebt.setType(DebtTypeEnum.SETTLE.getCode());
            accountInsertDebt.setAmt(amt);
            accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            int debtDetailId = cashierService.createDebt(accountInsertDebt);
            
            log.info("rentCostSettleOwner create debt,params orderNo=[{}],accountInsertDebt=[{}],debtDetailId=[{}]",settleOrders.getOrderNo(),GsonUtils.toJson(accountInsertDebt),debtDetailId);
        	
            //2.2记录结算费用状态
            AccountOwnerCostSettleDetailEntity entity = new AccountOwnerCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrders,entity);
            entity.setMemNo(settleOrders.getOwnerMemNo());
            entity.setAmt(-amt);
            entity.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            entity.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(debtDetailId));
            //数据封装
            List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = new ArrayList<AccountOwnerCostSettleDetailEntity>();
            accountOwnerCostSettleDetails.add(entity);
            cashierSettleService.insertAccountOwnerCostSettleDetails(accountOwnerCostSettleDetails);
            // 实付费用加上 历史欠款转移部分，存在欠款 1走历史欠款，2当前订单 账户拉平
            log.info("rentCostSettleOwner create debt,params orderNo=[{}],accountOwnerCostSettleDetailEntity=[{}],debtDetailId=[{}]",settleOrders.getOrderNo(),GsonUtils.toJson(entity),debtDetailId);
            
            //转入欠款之后，按结算金额0处理。
            settleOrdersAccount.setOwnerCostSurplusAmt(0);
            //更新订单费用处数据
            if(Objects.nonNull(callBack)){
                callBack.callBackSettle(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
            }
        }else {
        	log.info("rentCostSettleOwner donot create debt,params orderNo=[{}],ownerNo=[{}],amt=[{}]",settleOrders.getOrderNo(),settleOrders.getOwnerMemNo(),settleOrdersAccount.getOwnerCostSurplusAmt());
        }
    }
    
    
	/**
     * 车主收益 结余处理 历史欠款
     * @param settleOrdersAccount
     */
    public void repayHistoryDebtOwner(SettleOrdersAccount settleOrdersAccount) {
        //车主收益大于0 先还历史欠款
        if(settleOrdersAccount.getOwnerCostSurplusAmt()>0){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierDeductDebtReq);
            cashierDeductDebtReq.setMemNo(settleOrdersAccount.getOwnerMemNo());
            cashierDeductDebtReq.setAmt(settleOrdersAccount.getOwnerCostSurplusAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_OWNER_INCOME_TO_HISTORY_AMT);
            CashierDeductDebtResVO result = cashierService.deductDebtByOwnerIncome(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 最终车主收益总金额
                settleOrdersAccount.setOwnerCostSurplusAmt(settleOrdersAccount.getOwnerCostSurplusAmt() - deductAmt);
            }
        }
    }
    
    
    /**
     * 车主收益抵扣老系统历史欠款
     * @param settleOrdersAccount
     * @return int saveOwnerIncomeDebt
     */
    public int oldRepayHistoryDebtOwner(SettleOrdersAccount settleOrdersAccount) {
    	List<AccountOldDebtReqVO> oldDebtList = new ArrayList<AccountOldDebtReqVO>();
    	if(settleOrdersAccount.getOwnerCostSurplusAmt() > 0) {
    		AccountOldDebtReqVO accountOldDebtReqVO = new AccountOldDebtReqVO();
    		accountOldDebtReqVO.setOrderNo(settleOrdersAccount.getOrderNo());
    		accountOldDebtReqVO.setOwnerOrderNo(settleOrdersAccount.getOwnerOrderNo());
    		accountOldDebtReqVO.setMemNo(settleOrdersAccount.getOwnerMemNo());
    		accountOldDebtReqVO.setSurplusAmt(settleOrdersAccount.getOwnerCostSurplusAmt());
    		accountOldDebtReqVO.setCahsCodeEnum(RenterCashCodeEnum.SETTLE_OWNER_INCOME_TO_OLD_HISTORY_AMT);
    		oldDebtList.add(accountOldDebtReqVO);
    	}
    	// 抵扣老系统历史欠款
    	List<AccountOldDebtResVO> debtResList = accountDebtService.deductOldDebt(oldDebtList);
    	if (debtResList == null || debtResList.isEmpty()) {
    		return 0;
    	}
    	int totalRealDebtAmt = 0;
    	for (AccountOldDebtResVO debtRes:debtResList) {
    		RenterCashCodeEnum cahsCodeEnum = debtRes.getCahsCodeEnum();
    		totalRealDebtAmt += debtRes.getRealDebtAmt();
    		if (cahsCodeEnum != null && cahsCodeEnum == RenterCashCodeEnum.SETTLE_OWNER_INCOME_TO_OLD_HISTORY_AMT) {
    			// 车主收益
    			cashierService.saveOwnerIncomeDebt(debtRes);
    		}
    	}
    	settleOrdersAccount.setOwnerCostSurplusAmt(settleOrdersAccount.getOwnerCostSurplusAmt() - totalRealDebtAmt);
    	return totalRealDebtAmt;
    } 

    /**
     * 结算 产生 车主待审核记录
     * @param settleOrdersAccount
     */
    public void insertOwnerIncomeExamine(SettleOrdersAccount settleOrdersAccount) {
        if(settleOrdersAccount.getOwnerCostSurplusAmt()>0){
            AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamine = new AccountOwnerIncomeExamineReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,accountOwnerIncomeExamine);
            accountOwnerIncomeExamine.setMemNo(settleOrdersAccount.getOwnerMemNo());
            accountOwnerIncomeExamine.setAmt(settleOrdersAccount.getOwnerCostSurplusAmt());
            accountOwnerIncomeExamine.setMemNo(settleOrdersAccount.getOwnerMemNo());
            accountOwnerIncomeExamine.setRemark("结算收益");
            accountOwnerIncomeExamine.setDetail("结算收益");
            accountOwnerIncomeExamine.setOwnerOrderNo(settleOrdersAccount.getOwnerOrderNo());
            accountOwnerIncomeExamine.setStatus(AccountOwnerIncomeExamineStatus.WAIT_EXAMINE);
            accountOwnerIncomeExamine.setType(AccountOwnerIncomeExamineType.OWNER_INCOME);
            cashierService.insertOwnerIncomeExamine(accountOwnerIncomeExamine);
        }

    }
	
	
	//------------------------------------------------------------------------------------------------------------------------------
	//原方法名：addPlatFormAmt
	public void addPlatFormAmtSeparateOwner(SettleOrdersDefinition settleOrdersDefinition,SettleOrders settleOrders) {
        //租客交接车油费
        int rentOilAmt = settleOrdersDefinition.getAccountRenterCostSettleDetails().stream().filter(obj ->{
            return RenterCashCodeEnum.ACCOUNT_RENTER_DELIVERY_OIL_COST.getCashNo().equals(obj.getCostCode());
        }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
    	
        //需要平账
//    	int rentOilAmt = 0;
//    	RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = settleOrders.getOwnerCosts().getRenterGetAndReturnCarDTO();
//    	if(renterGetAndReturnCarDTO != null) {
//    		String oilDifferenceCrash = renterGetAndReturnCarDTO.getOilDifferenceCrash();
//    		oilDifferenceCrash = StringUtil.isBlank(oilDifferenceCrash)?"0":oilDifferenceCrash;
//    		rentOilAmt = Double.valueOf(oilDifferenceCrash).intValue();
//    	}
    	
        //车主交接车油费
        int ownerOilAmt = settleOrdersDefinition.getAccountOwnerCostSettleDetails().stream().filter(obj ->{
            return OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getCashNo().equals(obj.getSourceCode());
        }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
        
        //平台补贴油费
        int platFormAmt =0;
        //避免重复计算。
//        if(!CollectionUtils.isEmpty(settleOrdersDefinition.getAccountPlatformSubsidyDetails())){
//            platFormAmt = settleOrdersDefinition.getAccountPlatformSubsidyDetails().stream().filter(obj ->{
//                return RenterCashCodeEnum.SUBSIDY_OIL.getCashNo().equals(obj.getSourceCode());
//            }).mapToInt(AccountPlatformSubsidyDetailEntity::getAmt).sum();
//        }

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
     * 获取gps服务费 费用平台端冲账
     * @param renterOrderCostDetail
     * @param settleOrdersDefinition
     */
    public void addGpsCostAmtToPlatform(OwnerOrderPurchaseDetailEntity renterOrderCostDetail, SettleOrdersDefinition settleOrdersDefinition) {
        int totalAmount = renterOrderCostDetail.getTotalAmount();
        AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
        accountPlatformProfitDetail.setAmt(Math.abs(totalAmount));
        accountPlatformProfitDetail.setSourceCode(renterOrderCostDetail.getCostCode());
        accountPlatformProfitDetail.setSourceDesc(renterOrderCostDetail.getCostCodeDesc());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
        accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
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
        //renterOrderCostDetail.getCostCode()  //暂不处理
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getTxt());
        
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
        accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
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
        accountPlatformProfitDetail.setAmt(Math.abs(totalAmount));  //正数
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.SERVICE_CHARGE.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.SERVICE_CHARGE.getTxt());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(serviceExpense.getId()));
        accountPlatformProfitDetail.setOrderNo(serviceExpense.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
    }
    
    /**
     * 车主端代管车服务费车主端代管车服务费 费用平台端冲账
     * @param proxyExpense
     * @param settleOrdersDefinition
     */
    public void addProxyExpenseAmtToPlatform(OwnerOrderPurchaseDetailEntity proxyExpense, SettleOrdersDefinition settleOrdersDefinition) {
        int totalAmount = proxyExpense.getTotalAmount();
        AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
        accountPlatformProfitDetail.setAmt(Math.abs(totalAmount));
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.PROXY_CHARGE.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.PROXY_CHARGE.getTxt());
        accountPlatformProfitDetail.setUniqueNo(String.valueOf(proxyExpense.getId()));
        accountPlatformProfitDetail.setOrderNo(proxyExpense.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);

    }
    
    /**
     * 获取车主gps押金用平台端冲账
     * @param gpsDeposit
     * @param settleOrdersDefinition
     */
    public void addGpsDepositIncrementAmtToPlatform(OwnerOrderIncrementDetailEntity gpsDeposit, SettleOrdersDefinition settleOrdersDefinition) {
        int totalAmount = gpsDeposit.getTotalAmount();
        AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
        accountPlatformProfitDetail.setAmt(-totalAmount);
        //renterOrderCostDetail.getCostCode()  //暂不处理
        accountPlatformProfitDetail.setSourceCode(OwnerCashCodeEnum.HW_DEPOSIT_DEBT.getCashNo());
        accountPlatformProfitDetail.setSourceDesc(OwnerCashCodeEnum.HW_DEPOSIT_DEBT.getTxt());
        
        //accountPlatformProfitDetail.setUniqueNo(String.valueOf(gpsDeposit.getId()));
        accountPlatformProfitDetail.setOrderNo(gpsDeposit.getOrderNo());
        settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
    }
}
