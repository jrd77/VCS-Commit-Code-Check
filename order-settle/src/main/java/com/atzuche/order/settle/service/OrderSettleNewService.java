package com.atzuche.order.settle.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.req.DeductDepositToRentCostReqVO;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.coin.service.AccountRenterCostCoinService;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.settle.service.notservice.OrderSettleProxyService;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountOldDebtReqVO;
import com.atzuche.order.settle.vo.req.RefundApplyVO;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.atzuche.order.settle.vo.res.AccountOldDebtResVO;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderSettlementMq;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OrderSettleNewService {
    @Autowired CashierNoTService cashierNoTService;
    @Autowired AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;
    @Autowired AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired AccountPlatformSubsidyDetailNoTService accountPlatformSubsidyDetailNoTService;
    @Autowired AccountPlatformProfitDetailNotService accountPlatformProfitDetailNotService;
//    @Autowired private OrderSettleNoTService orderSettleNoTService;  //相互
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private CashierPayService cashierPayService;
    @Autowired private BaseProducer baseProducer;
    @Autowired private CashierService cashierService;
    @Autowired private AccountRenterCostCoinService accountRenterCostCoinService;
    @Autowired private WalletProxyService walletProxyService;
    @Autowired
    private OrderSettleProxyService orderSettleProxyService;
    @Autowired
    private AccountDebtService accountDebtService;

    /**
     *  先查询  发现 有结算数据停止结算 手动处理
     * @param orderNo
     */
    public void checkIsSettle(String orderNo,SettleOrders settleOrders) {
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetailEntitys = accountRenterCostSettleDetailNoTService.getAccountRenterCostSettleDetail(orderNo);
        List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetailEntitys = accountPlatformProfitDetailNotService.getPlatformProfitDetails(orderNo);
        List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetailEntitys = accountPlatformSubsidyDetailNoTService.getPlatformSubsidyDetails(orderNo);
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetailEntitys = accountOwnerCostSettleDetailNoTService.getAccountOwnerCostSettleDetails(orderNo,settleOrders.getOwnerMemNo());

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
    
    /**
     * 租客费用结余 处理 （如果应付 大于实付，这个订单存在未支付信息，优先 押金抵扣，未支付信息）
     * @param settleOrdersAccount
     */
    public void rentCostSettle(SettleOrders settleOrders , SettleOrdersAccount settleOrdersAccount,OrderPayCallBack callBack) {
        //1 如果租车费用计算应付总额大于 实际支付 车辆押金抵扣租车费用欠款
        if(settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmt()<0){
            //1.1押金 抵扣 租车费用欠款
            if(settleOrdersAccount.getDepositAmt()>0){
                DeductDepositToRentCostReqVO vo = new DeductDepositToRentCostReqVO();
                BeanUtils.copyProperties(settleOrders,vo);
                vo.setMemNo(settleOrders.getRenterMemNo());
                int debtAmt = settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmt();
                //真实抵扣金额
                int amt = debtAmt+settleOrdersAccount.getDepositSurplusAmt()>=0?debtAmt:-settleOrdersAccount.getDepositSurplusAmt();
                vo.setAmt(amt);
                //车俩押金抵扣 租车费用金额 返回 已抵扣部分
                cashierSettleService.deductDepositToRentCost(vo);
                //计算剩余车俩押金
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() + amt);
                // 实付费用加上 押金已抵扣部分
                settleOrdersAccount.setRentCostPayAmt(settleOrdersAccount.getRentCostPayAmt() + Math.abs(amt));
                //更新订单费用处数据
                if(Objects.nonNull(callBack)){
                    callBack.callBackSettle(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
                }

            }
        }
        //2如果 步骤1 结算 应付还是大于实付  此订单产生历史欠款
        if(settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmt()<0){
            //2.1 记录历史欠款
            int amt = settleOrdersAccount.getRentCostAmtFinal() + settleOrdersAccount.getRentCostPayAmt();
            AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
            BeanUtils.copyProperties(settleOrders,accountInsertDebt);
            accountInsertDebt.setMemNo(settleOrders.getRenterMemNo());
            accountInsertDebt.setType(DebtTypeEnum.SETTLE.getCode());
            accountInsertDebt.setAmt(amt);
            accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            int debtDetailId = cashierService.createDebt(accountInsertDebt);
            //2.2记录结算费用状态
            AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrders,entity);
            entity.setMemNo(settleOrders.getRenterMemNo());
            entity.setAmt(-amt);
            entity.setCostCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            entity.setCostDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(debtDetailId));
            cashierSettleService.insertAccountRenterCostSettleDetail(entity);
            // 实付费用加上 历史欠款转移部分，存在欠款 1走历史欠款，2当前订单 账户拉平
            settleOrdersAccount.setRentCostPayAmt(settleOrdersAccount.getRentCostPayAmt() + Math.abs(amt));
            //更新订单费用处数据
            if(Objects.nonNull(callBack)){
                callBack.callBackSettle(settleOrders.getOrderNo(),settleOrders.getRenterOrderNo());
            }
        }
    }

    /**
     * 结算租客 还历史欠款
     * @param settleOrdersAccount
     */
    public void repayHistoryDebtRent(SettleOrdersAccount settleOrdersAccount) {
    	boolean rentCostVirtualPayFlag = settleOrdersAccount.getRentCostVirtualPayFlag() == null ? false:settleOrdersAccount.getRentCostVirtualPayFlag();
    	boolean rentDepositVirtualPayFlag = settleOrdersAccount.getRentDepositVirtualPayFlag() == null ? false:settleOrdersAccount.getRentDepositVirtualPayFlag();
        // 1 存在 实付大于应付 先抵扣 历史欠款
        if(settleOrdersAccount.getRentCostSurplusAmt()>0 && !rentCostVirtualPayFlag){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierDeductDebtReq);
            cashierDeductDebtReq.setAmt(settleOrdersAccount.getRentCostSurplusAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_COST_TO_HISTORY_AMT);
            cashierDeductDebtReq.setMemNo(settleOrdersAccount.getRenterMemNo());
            CashierDeductDebtResVO result = cashierService.deductDebtByRentCost(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余租车费用
                settleOrdersAccount.setRentCostSurplusAmt(settleOrdersAccount.getRentCostSurplusAmt() - deductAmt);
            }
        }
        //车辆押金存在 且 租车费用没有抵扣完 ，使用车辆押金抵扣 历史欠款
        if(settleOrdersAccount.getDepositSurplusAmt()>0 && !rentDepositVirtualPayFlag){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierDeductDebtReq);
            cashierDeductDebtReq.setMemNo(settleOrdersAccount.getRenterMemNo());
            cashierDeductDebtReq.setAmt(settleOrdersAccount.getDepositSurplusAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT);
            CashierDeductDebtResVO result = cashierService.deductDebt(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余车俩押金
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() - deductAmt);
            }
        }
    }
    
    
    /**
     * 抵扣老系统欠款
     * @param settleOrdersAccount
     * @return int
     */
    public int oldRepayHistoryDebtRent(SettleOrdersAccount settleOrdersAccount) {
    	boolean rentCostVirtualPayFlag = settleOrdersAccount.getRentCostVirtualPayFlag() == null ? false:settleOrdersAccount.getRentCostVirtualPayFlag();
    	boolean rentDepositVirtualPayFlag = settleOrdersAccount.getRentDepositVirtualPayFlag() == null ? false:settleOrdersAccount.getRentDepositVirtualPayFlag();
    	List<AccountOldDebtReqVO> oldDebtList = new ArrayList<AccountOldDebtReqVO>();
    	// 租车费用抵扣
    	if(settleOrdersAccount.getRentCostSurplusAmt() > 0 && !rentCostVirtualPayFlag) {
    		AccountOldDebtReqVO accountOldDebtReqVO = new AccountOldDebtReqVO();
    		accountOldDebtReqVO.setOrderNo(settleOrdersAccount.getOrderNo());
    		accountOldDebtReqVO.setRenterOrderNo(settleOrdersAccount.getRenterOrderNo());
    		accountOldDebtReqVO.setMemNo(settleOrdersAccount.getRenterMemNo());
    		accountOldDebtReqVO.setSurplusAmt(settleOrdersAccount.getRentCostSurplusAmt());
    		accountOldDebtReqVO.setCahsCodeEnum(RenterCashCodeEnum.SETTLE_RENT_COST_TO_OLD_HISTORY_AMT);
    		oldDebtList.add(accountOldDebtReqVO);
    	}
    	// 车辆押金抵扣
    	if (settleOrdersAccount.getDepositSurplusAmt() > 0 && !rentDepositVirtualPayFlag) {
    		AccountOldDebtReqVO accountOldDebtReqVO = new AccountOldDebtReqVO();
    		accountOldDebtReqVO.setOrderNo(settleOrdersAccount.getOrderNo());
    		accountOldDebtReqVO.setRenterOrderNo(settleOrdersAccount.getRenterOrderNo());
    		accountOldDebtReqVO.setMemNo(settleOrdersAccount.getRenterMemNo());
    		accountOldDebtReqVO.setSurplusAmt(settleOrdersAccount.getDepositSurplusAmt());
    		accountOldDebtReqVO.setCahsCodeEnum(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_OLD_HISTORY_AMT);
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
    		if (cahsCodeEnum != null && cahsCodeEnum == RenterCashCodeEnum.SETTLE_RENT_COST_TO_OLD_HISTORY_AMT) {
    			// 租车费用
    			cashierService.saveRentCostDebt(debtRes);
    			settleOrdersAccount.setRentCostSurplusAmt(settleOrdersAccount.getRentCostSurplusAmt() - debtRes.getRealDebtAmt());
    		} else if (cahsCodeEnum != null && cahsCodeEnum == RenterCashCodeEnum.SETTLE_DEPOSIT_TO_OLD_HISTORY_AMT) {
    			// 车辆押金
    			cashierService.saveDepositDebt(debtRes);
    			settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() - debtRes.getRealDebtAmt());
    		}
    	}
    	return totalRealDebtAmt;
    }
    

    /**
     * 退还多余费用
     * 退还优先级 钱包<消费
     *
     * @param settleOrdersAccount
     */
    public void refundRentCost(SettleOrdersAccount settleOrdersAccount,List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails,OrderStatusDTO orderStatusDTO,SettleOrders settleOrders) {
        List<AccountRenterCostSettleDetailEntity> renterCostSettleDetails = new ArrayList<>();
        //应退结余 租车费用 （包含 租车支付金额 和钱包抵扣）
        int rentCostSurplusAmt = settleOrdersAccount.getRentCostSurplusAmt();
        if(rentCostSurplusAmt>0){
            //退还租车费用
            if(rentCostSurplusAmt>0){
                //退还剩余 租车费用
                RefundApplyVO refundApplyVO = new RefundApplyVO(settleOrders,-rentCostSurplusAmt,RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_REFUND,"结算订单退还");
                List<CashierRefundApplyReqVO> cashierRefundApplyReqs = orderSettleProxyService.getCashierRefundApply(refundApplyVO);
                if(!CollectionUtils.isEmpty(cashierRefundApplyReqs)){
                    for(int i=0;i<cashierRefundApplyReqs.size();i++){
                        CashierRefundApplyReqVO cashierRefundApplyReq = cashierRefundApplyReqs.get(i);
                        int id =cashierService.refundRentCost(cashierRefundApplyReq);
                        AccountRenterCostSettleDetailEntity entity = getAccountRenterCostSettleDetailEntityForRentCost(settleOrdersAccount,cashierRefundApplyReq.getAmt(),id);
                        renterCostSettleDetails.add(entity);
                    }
                }
                //计算 实际支付 金额 退钱总额
                int returnAmt = cashierRefundApplyReqs.stream().mapToInt(CashierRefundApplyReqVO::getAmt).sum();
                // 计算剩余待退
                rentCostSurplusAmt = rentCostSurplusAmt+returnAmt;
                orderStatusDTO.setRentCarRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());
            }

            //2 查询钱包 比较
            if(rentCostSurplusAmt>0){
                int walletAmt = cashierSettleService.getRentCostPayByWallet(settleOrdersAccount.getOrderNo(),settleOrdersAccount.getRenterMemNo());
                //计算应退钱包金额 并退还
                int returnWalletAmt = rentCostSurplusAmt>walletAmt?walletAmt:rentCostSurplusAmt;
                walletProxyService.returnOrChargeWallet(settleOrdersAccount.getRenterMemNo(),settleOrdersAccount.getOrderNo(),Math.abs(returnWalletAmt));
                AccountRenterCostSettleDetailEntity entity = getAccountRenterCostSettleDetailEntityForWallet(settleOrdersAccount,-returnWalletAmt);
                renterCostSettleDetails.add(entity);

            }
            //记录 凹凸币/钱包退还 流水
            cashierSettleService.insertAccountRenterCostSettleDetails(renterCostSettleDetails);
        }
        //1 退还凹凸币 coinAmt为订单真实使用的凹凸币
        int coinAmt = accountRenterCostSettleDetails.stream().filter(obj ->{
            return RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(obj.getCostCode());
        }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
        if(coinAmt>0){
            //退还多余凹凸币
            accountRenterCostCoinService.settleAutoCoin(settleOrdersAccount.getRenterMemNo(),settleOrdersAccount.getOrderNo(),coinAmt);
        }

    }
    
    /**
     * 退还剩余的 车辆押金
     * @param settleOrdersAccount
     */
    public void refundDepositAmt(SettleOrdersAccount settleOrdersAccount,OrderStatusDTO orderStatusDTO) {
        if(settleOrdersAccount.getDepositSurplusAmt()>0){
            //1退还租车押金
            CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierRefundApply);
//            cashierRefundApply.setMemNo(settleOrdersAccount.getRenterMemNo());
//            cashierRefundApply.setAmt(-settleOrdersAccount.getDepositSurplusAmt());
//            cashierRefundApply.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT);
//            cashierRefundApply.setRemake(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT.getTxt());
//            cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getCashNo());
//            cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
//            int id =cashierService.refundDeposit(cashierRefundApply);
            
          
          //方法重构
          CashierEntity cashierEntity = cashierNoTService.getCashierEntity(settleOrdersAccount.getOrderNo(),settleOrdersAccount.getRenterMemNo(), DataPayKindConstant.RENT);
          BeanUtils.copyProperties(cashierEntity,cashierRefundApply);
          
          //预授权处理
          int id = 0;
          if(cashierEntity != null && DataPayTypeConstant.PAY_PRE.equals(cashierEntity.getPayType())) {
          	id = cashierService.refundDepositPreAuthAll(settleOrdersAccount.getDepositSurplusAmt(), cashierEntity, cashierRefundApply, RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT);
          }else {
          	//消费
          	//退货
          	id = cashierService.refundDepositPurchase(settleOrdersAccount.getDepositSurplusAmt(), cashierEntity, cashierRefundApply,RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT);
          }
            
            // 2记录退还 租车押金 结算费用明细
            AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrdersAccount,entity);
            entity.setMemNo(settleOrdersAccount.getRenterMemNo());
            entity.setAmt(-settleOrdersAccount.getDepositSurplusAmt());
            entity.setCostCode(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT.getCashNo());
            entity.setCostDetail(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(id));
            cashierSettleService.insertAccountRenterCostSettleDetail(entity);
            orderStatusDTO.setDepositRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());

        }

    }
    
    // -------------------------------------------------------------------------------------------------------------
    

    /**
     * 租车费用退还
     * @param settleOrdersAccount
     * @param rentCostSurplusAmt
     * @return
     */
    private CashierRefundApplyReqVO getCashierRefundApplyReqVO(SettleOrdersAccount settleOrdersAccount, int rentCostSurplusAmt) {
        CashierRefundApplyReqVO vo = new CashierRefundApplyReqVO();
        BeanUtils.copyProperties(settleOrdersAccount,vo);
        vo.setAmt(rentCostSurplusAmt);
        vo.setRemake("结算退还");
        vo.setMemNo(settleOrdersAccount.getRenterMemNo());
        vo.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_COST_TO_RETURN_AMT);
        vo.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getCashNo());
        return vo;
    }

    /**
     * 结算退还钱包金额 费用明细
     * @param settleOrdersAccount
     * @param returnWalletAmt
     * @return
     */
    private AccountRenterCostSettleDetailEntity getAccountRenterCostSettleDetailEntityForWallet(SettleOrdersAccount settleOrdersAccount, int returnWalletAmt) {
        AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(settleOrdersAccount,entity);
        entity.setAmt(returnWalletAmt);
        entity.setCostCode(RenterCashCodeEnum.WALLET_DEDUCT.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.WALLET_DEDUCT.getTxt());
        return entity;
    }
    
    /**
     * 结算退还租车费用金额 费用明细
     * @param settleOrdersAccount
     * @param rentCostSurplusAmt
     * @return
     */
    private AccountRenterCostSettleDetailEntity getAccountRenterCostSettleDetailEntityForRentCost(SettleOrdersAccount settleOrdersAccount, int rentCostSurplusAmt, int id) {
        AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(settleOrdersAccount,entity);
        entity.setAmt(rentCostSurplusAmt);
        entity.setCostCode(RenterCashCodeEnum.SETTLE_RENT_COST_TO_RETURN_AMT.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.SETTLE_RENT_COST_TO_RETURN_AMT.getTxt());
        entity.setUniqueNo(String.valueOf(id));
        return entity;
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
        //2.1 还车运能加价 200309
        if(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getTxt());
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
        //2.2 取车运能加价
        if(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getTxt());
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
        
        // ----------------------------------------- 平台补贴 -----------------------------------------
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
    
    //----------------------------------------------------------------------------------------------------------------
    

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

    /***
     * 在车主端处理，是根据是否代管车来处理，是代管车归平台，否则归车主。200308
     * @param accountRenterCostSettleDetail
     * @param settleOrdersDefinition
     */
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


    /**
     * 订单车辆结算事件
     * flag  0：成功 1：失败
     * @param orderNo
     */
    public void sendOrderSettleMq(String orderNo,String renterMemNo,RentCosts rentCosts,int status,String ownerMemNo) {
        log.info("sendOrderSettleMq start [{}],[{}],[{}],[{}]",orderNo,renterMemNo,GsonUtils.toJson(rentCosts),status);
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
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getOrderConsoleSubsidyDetails())){
            subsidyPlamtAmt = subsidyPlamtAmt + rentCosts.getOrderConsoleSubsidyDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo().equals(obj.getSubsidyTypeCode());
            }).mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
             subsidyOwnerAmt = subsidyOwnerAmt + rentCosts.getOrderConsoleSubsidyDetails().stream().filter(obj ->{
                return RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo().equals(obj.getSubsidyTypeCode());
            }).mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderSubsidyDetails())){
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
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderCostDetails())){
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
        orderSettlementMq.setStatus(status);
        orderSettlementMq.setOrderNo(orderNo);
        ///增加租客会员号，车主会员号 200228
        orderSettlementMq.setRenterMemNo(Integer.valueOf(renterMemNo));
        orderSettlementMq.setOwnerMemNo(Integer.valueOf(ownerMemNo));
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderSettlementMq);
        NewOrderMQActionEventEnum eventEnum = null;
        if(status==0){
            eventEnum = NewOrderMQActionEventEnum.ORDER_SETTLEMENT_SUCCESS;
        }else{
            eventEnum = NewOrderMQActionEventEnum.ORDER_SETTLEMENT_FAIL;
        }
        //TODO 发短信
        log.info("sendOrderSettleMq remote start [{}],[{}]",eventEnum,GsonUtils.toJson(orderMessage));
        baseProducer.sendTopicMessage(eventEnum.exchange,eventEnum.routingKey,orderMessage);
        log.info("sendOrderSettleMq remote end [{}],[{}]",eventEnum,GsonUtils.toJson(orderMessage));
    }
    

    

    /**
     * 查询 租客 应付金额     暂不使用。200309
     * @param rentCosts
     * @return
     */
    public int getYingfuRenterCost(RentCosts rentCosts) {
        int renterCost=0;
        // 租车费用
        if(!CollectionUtils.isEmpty(rentCosts.getRenterOrderCostDetails())){
            renterCost = renterCost +  rentCosts.getRenterOrderCostDetails().stream().mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
        }
        
        //计算重复,rentCosts.getRenterOrderCostDetails() 已经包含在里面了。
//        //交接车油费
//        if(Objects.nonNull(rentCosts.getOilAmt())){
//            String oilDifferenceCrash = rentCosts.getOilAmt().getOilDifferenceCrash();
//            if(!StringUtil.isBlank(oilDifferenceCrash)){
//                renterCost = renterCost + Integer.valueOf(oilDifferenceCrash);
//            }
//        }
//        //交接 超历程
//        if(Objects.nonNull(rentCosts.getMileageAmt())){
//            Integer totalFee = rentCosts.getMileageAmt().getTotalFee();
//            if(Objects.nonNull(totalFee)){
//                renterCost = renterCost + totalFee;
//            }
//        }
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
