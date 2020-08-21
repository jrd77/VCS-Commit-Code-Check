package com.atzuche.order.settle.service;

import com.alibaba.fastjson.JSON;
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
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.coin.service.AccountRenterCostCoinService;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.enums.cashier.PayLineEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.settle.dto.OrderSettleCommonParamDTO;
import com.atzuche.order.settle.dto.OrderSettleCommonResultDTO;
import com.atzuche.order.settle.service.notservice.OrderSettleProxyService;
import com.atzuche.order.settle.vo.req.*;
import com.atzuche.order.settle.vo.res.AccountOldDebtResVO;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderSettlementMq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderSettleNewService {

    @Autowired CashierNoTService cashierNoTService;
    @Autowired AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;
    @Autowired AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired AccountPlatformSubsidyDetailNoTService accountPlatformSubsidyDetailNoTService;
    @Autowired AccountPlatformProfitDetailNotService accountPlatformProfitDetailNotService;
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
    @Autowired
    OrderSourceStatService orderSourceStatService;
    @Autowired
    OrderSettleRefundHandleService orderSettleRefundHandleService;

    /**
     *  先查询  发现 有结算数据停止结算 手动处理
     * @param orderNo 订单号
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
     * @param settleOrdersAccount 公共参数
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
     * @param settleOrdersAccount 公共参数
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
     * @param settleOrdersAccount 公共参数
     * @return int
     */
    public int oldRepayHistoryDebtRent(SettleOrdersAccount settleOrdersAccount) {
    	boolean rentCostVirtualPayFlag = settleOrdersAccount.getRentCostVirtualPayFlag() == null ? false:settleOrdersAccount.getRentCostVirtualPayFlag();
    	boolean rentDepositVirtualPayFlag = settleOrdersAccount.getRentDepositVirtualPayFlag() == null ? false:settleOrdersAccount.getRentDepositVirtualPayFlag();
    	List<AccountOldDebtReqVO> oldDebtList = new ArrayList<>();
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
    		if (cahsCodeEnum == RenterCashCodeEnum.SETTLE_RENT_COST_TO_OLD_HISTORY_AMT) {
    			// 租车费用
    			cashierService.saveRentCostDebt(debtRes);
    			settleOrdersAccount.setRentCostSurplusAmt(settleOrdersAccount.getRentCostSurplusAmt() - debtRes.getRealDebtAmt());
    		} else if (cahsCodeEnum == RenterCashCodeEnum.SETTLE_DEPOSIT_TO_OLD_HISTORY_AMT) {
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
     * @param settleOrdersAccount            公共参数
     * @param accountRenterCostSettleDetails 租车费用列表
     * @param orderStatusDTO                 订单状态信息
     * @param settleOrders                   全局公共参数
     */
    public void refundRentCost(SettleOrdersAccount settleOrdersAccount, List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails, OrderStatusDTO orderStatusDTO, SettleOrders settleOrders) {
        log.info("OrderSettleNewService.refundRentCost >> param is,settleOrdersAccount:[{}]," +
                        "accountRenterCostSettleDetails:[{}],orderStatusDTO:[{}],settleOrders:[{}]",
                JSON.toJSONString(settleOrdersAccount), JSON.toJSONString(accountRenterCostSettleDetails),
                JSON.toJSONString(orderStatusDTO), JSON.toJSONString(settleOrders));

        //租车费用退还(包含钱包支付部分)
        int rentCostSurplusAmt = settleOrdersAccount.getRentCostSurplusAmt();
        if (rentCostSurplusAmt > OrderConstant.ZERO) {
            OrderSettleCommonParamDTO common = new OrderSettleCommonParamDTO();
            common.setOrderNo(settleOrders.getOrderNo());
            common.setRenterOrderNo(settleOrders.getRenterOrderNo());
            common.setMemNo(settleOrders.getRenterMemNo());
            common.setCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_COST_TO_RETURN_AMT);
            common.setRentCarCostCashCodeEnum(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_REFUND);
            OrderSettleCommonResultDTO result = orderSettleRefundHandleService.rentCarCostRefundHandle(common,
                    rentCostSurplusAmt);

            log.info("OrderSettleNewService.refundRentCost >> result is,result:[{}]",
                    JSON.toJSONString(result));
            orderStatusDTO.setRentCarRefundStatus(result.getStatus());
            rentCostSurplusAmt = result.getSurplusAmt();
        }

        // 退还凹凸币 coinAmt为订单真实使用的凹凸币
        int coinAmt = accountRenterCostSettleDetails.stream().filter(obj -> RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(obj.getCostCode())).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
        if (coinAmt > 0) {
            //退还多余凹凸币
            coinAmt = rentCostSurplusAmt >= coinAmt ? coinAmt : rentCostSurplusAmt;
            accountRenterCostCoinService.settleAutoCoin(settleOrdersAccount.getRenterMemNo(), settleOrdersAccount.getOrderNo(), coinAmt);
        }
    }

    /**
     * 退还剩余的 车辆押金
     *
     * @param settleOrdersAccount 公共参数
     * @param orderStatusDTO      订单状态
     */
    public void refundDepositAmt(SettleOrdersAccount settleOrdersAccount, OrderStatusDTO orderStatusDTO) {
        log.info("OrderSettleNewService.refundDepositAmt >> param is,settleOrdersAccount:[{}],orderStatusDTO:[{}]",
                JSON.toJSONString(settleOrdersAccount), JSON.toJSONString(orderStatusDTO));
        
        //bugfix:处理无退款的支付宝预授权完成的情况。200820
        //加上等于0的情况
        if (settleOrdersAccount.getDepositSurplusAmt() >= OrderConstant.ZERO) {
            OrderSettleCommonParamDTO common = new OrderSettleCommonParamDTO();
            common.setOrderNo(settleOrdersAccount.getOrderNo());
            common.setRenterOrderNo(settleOrdersAccount.getRenterOrderNo());
            common.setMemNo(settleOrdersAccount.getRenterMemNo());
            common.setCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT);
            OrderSettleCommonResultDTO result = orderSettleRefundHandleService.rentCarDepositRefundHandle(common,
                    settleOrdersAccount.getDepositSurplusAmt());
            log.info("OrderSettleNewService.refundDepositAmt >> result is,result:[{}]",
                    JSON.toJSONString(result));
            orderStatusDTO.setDepositRefundStatus(result.getStatus());
        }
    }



    /**
     * 结算退还租车费用金额 费用明细
     * @param settleOrdersAccount 公共参数
     * @param rentCostSurplusAmt 剩余租车费用
     * @return AccountRenterCostSettleDetailEntity
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
        
        // 精准取车服务费
        if(RenterCashCodeEnum.ACCURATE_GET_SRV_AMT.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.ACCURATE_GET_SRV_AMT.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.ACCURATE_GET_SRV_AMT.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        
        // 精准还车服务费
        if(RenterCashCodeEnum.ACCURATE_RETURN_SRV_AMT.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.ACCURATE_RETURN_SRV_AMT.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.ACCURATE_RETURN_SRV_AMT.getTxt());
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
        //4 补充保障服务费 费用收益方 平台   平台端记录冲账流水
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
        
        //轮胎/轮毂保障费 add 200529
        if(RenterCashCodeEnum.TYRE_INSURE_TOTAL_PRICES.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.TYRE_INSURE_TOTAL_PRICES.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.TYRE_INSURE_TOTAL_PRICES.getTxt());
            accountPlatformProfitDetail.setUniqueNo(String.valueOf(renterOrderCostDetail.getId()));
            accountPlatformProfitDetail.setOrderNo(renterOrderCostDetail.getOrderNo());
            settleOrdersDefinition.addPlatformProfit(accountPlatformProfitDetail);
        }
        
        //驾乘无忧保障费 200529
        if(RenterCashCodeEnum.DRIVER_INSURE_TOTAL_PRICES.getCashNo().equals(renterOrderCostDetail.getCostCode())){
            int totalAmount = renterOrderCostDetail.getTotalAmount();
            AccountPlatformProfitDetailEntity accountPlatformProfitDetail = new AccountPlatformProfitDetailEntity();
            accountPlatformProfitDetail.setAmt(-totalAmount);
            accountPlatformProfitDetail.setSourceCode(RenterCashCodeEnum.DRIVER_INSURE_TOTAL_PRICES.getCashNo());
            accountPlatformProfitDetail.setSourceDesc(RenterCashCodeEnum.DRIVER_INSURE_TOTAL_PRICES.getTxt());
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
    
    /**
     * 订单车辆结算事件
     * flag  0：成功 1：失败
     * @param orderNo 订单号
     */
    public void sendOrderSettleMq(String orderNo,String renterMemNo,RentCosts rentCosts,int status,String ownerMemNo,RenterOrderEntity renterOrder) {
        log.info("sendOrderSettleMq start [{}],[{}],[{}],[{}]",orderNo,renterMemNo,GsonUtils.toJson(rentCosts),status);
        AccountRenterCostSettleEntity entity=cashierSettleService.getAccountRenterCostSettleEntity(orderNo,renterMemNo);
        OrderSettlementMq orderSettlementMq = new OrderSettlementMq();
        if(Objects.nonNull(entity)){
            String insureTotalPrices = Objects.nonNull(entity.getBasicEnsureAmount())?String.valueOf(entity.getBasicEnsureAmount()):"0";
            orderSettlementMq.setInsureTotalPrices(insureTotalPrices);
            String abatementInsure = Objects.nonNull(entity.getComprehensiveEnsureAmount())?String.valueOf(entity.getComprehensiveEnsureAmount()):"0";
            orderSettlementMq.setAbatementInsure(abatementInsure);
            orderSettlementMq.setYingkouAmt(entity.getYingkouAmt());
        }
        int subsidyPlamtAmt=0;
        int subsidyOwnerAmt=0;
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getOrderConsoleSubsidyDetails())){
            subsidyPlamtAmt = subsidyPlamtAmt + rentCosts.getOrderConsoleSubsidyDetails().stream().filter(obj -> RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo().equals(obj.getSubsidyTypeCode())).mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
            subsidyOwnerAmt = subsidyOwnerAmt + rentCosts.getOrderConsoleSubsidyDetails().stream().filter(obj -> RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo().equals(obj.getSubsidyTypeCode())).mapToInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderSubsidyDetails())){
            subsidyPlamtAmt = subsidyPlamtAmt + rentCosts.getRenterOrderSubsidyDetails().stream().filter(obj -> RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo().equals(obj.getSubsidyTypeCode())).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
            subsidyOwnerAmt = subsidyOwnerAmt + rentCosts.getRenterOrderSubsidyDetails().stream().filter(obj -> RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo().equals(obj.getSubsidyTypeCode())).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        orderSettlementMq.setPlatformCouponDeductionAmount(String.valueOf(subsidyPlamtAmt));
        orderSettlementMq.setOwnerCouponDeductionAmount(String.valueOf(subsidyOwnerAmt));

        //查询租车费用  过滤租金 取 日均价 多个的话 按id倒叙  取第一个
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderCostDetails())){
           int price = rentCosts.getRenterOrderCostDetails().stream().filter(obj -> RenterCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getCostCode())).sorted(Comparator.comparing(RenterOrderCostDetailEntity::getId).reversed())
                    .limit(1).mapToInt(RenterOrderCostDetailEntity::getUnitPrice).sum();
            int rentAmt = rentCosts.getRenterOrderCostDetails().stream().filter(obj -> RenterCashCodeEnum.RENT_AMT.getCashNo().equals(obj.getCostCode())).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
            orderSettlementMq.setHolidayAverage(String.valueOf(price));
            orderSettlementMq.setRentAmt(String.valueOf(rentAmt));
        }
     // 获取订单来源信息
        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
        if(osse != null) {
	        orderSettlementMq.setBusinessParentType(osse.getBusinessParentType());
	        orderSettlementMq.setBusinessChildType(osse.getBusinessChildType());
	        orderSettlementMq.setPlatformParentType(osse.getPlatformParentType());
	        orderSettlementMq.setPlatformChildType(osse.getPlatformChildType());
        }
		
        orderSettlementMq.setStatus(status);
        orderSettlementMq.setOrderNo(orderNo);
        ///增加租客会员号，车主会员号 200228
        orderSettlementMq.setRenterMemNo(Integer.valueOf(renterMemNo));
        orderSettlementMq.setOwnerMemNo(Integer.valueOf(ownerMemNo));
        //补充字段
        if(renterOrder != null) {
	        orderSettlementMq.setCarNo(Integer.valueOf(renterOrder.getGoodsCode()));
	        orderSettlementMq.setRentTime(LocalDateTimeUtils.localDateTimeToDate(renterOrder.getExpRentTime()));
	        orderSettlementMq.setRevertTime(LocalDateTimeUtils.localDateTimeToDate(renterOrder.getExpRevertTime()));
        }
        
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderSettlementMq);
        NewOrderMQActionEventEnum eventEnum;
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


}
