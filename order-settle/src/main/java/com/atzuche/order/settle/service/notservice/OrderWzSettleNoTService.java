package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.CashierWzSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.req.DeductDepositToRentCostReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.enums.cashier.PayLineEnum;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.settle.service.AccountDebtService;
import com.atzuche.order.settle.vo.req.*;
import com.atzuche.order.settle.vo.res.AccountOldDebtResVO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.commons.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @author jhuang
 */
@Service
@Slf4j
public class OrderWzSettleNoTService {
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderFlowService orderFlowService;
    @Autowired
    private CashierWzSettleService cashierWzSettleService;
    @Autowired
    RenterOrderWzCostDetailService renterOrderWzCostDetailService;
    @Autowired 
    CashierNoTService cashierNoTService;
    @Autowired
    private AccountDebtService accountDebtService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OwnerOrderService ownerOrderService;


    /**
     * 查询租客费用明细
     *
     * @param settleOrders 公共参数
     */
    public void getRenterWzCostSettleDetail(SettleOrdersWz settleOrders) {
        RentCostsWz rentCostsWz = new RentCostsWz();
        //1 查询违章费用
        List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetails = renterOrderWzCostDetailService.queryInfosByOrderNo(settleOrders.getOrderNo());

        int renterOrderCostWz = 0;
        if (!CollectionUtils.isEmpty(renterOrderWzCostDetails)) {
            for (RenterOrderWzCostDetailEntity renterOrderWzCostDetailEntity : renterOrderWzCostDetails) {
                renterOrderCostWz += renterOrderWzCostDetailEntity.getAmount();
            }
        }
        rentCostsWz.setRenterOrderWzCostDetails(renterOrderWzCostDetails);

        settleOrders.setRenterOrderCostWz(renterOrderCostWz);
        settleOrders.setRentCostsWz(rentCostsWz);
        settleOrders.setShouldTakeWzCost(renterOrderCostWz);
    }
    
    
    /**
     * 结算租客 还历史欠款    等于违章费用无法从“违章费用里面扣除”，只能通过违章押金来扣除。本身作为“欠款”的方式。
     * @param settleOrdersAccount 结算公共参数
     */
    public void repayWzHistoryDebtRent(SettleOrdersAccount settleOrdersAccount) {
    	boolean wzCostVirtualFlag = settleOrdersAccount.getWzCostVirtualFlag() == null ? false:settleOrdersAccount.getWzCostVirtualFlag();
        //车辆押金存在 且 租车费用没有抵扣完 ，使用车辆押金抵扣 历史欠款
        if(settleOrdersAccount.getDepositSurplusAmt()>0 && !wzCostVirtualFlag){
            CashierDeductDebtReqVO cashierDeductDebtReq = new CashierDeductDebtReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierDeductDebtReq);
            cashierDeductDebtReq.setMemNo(settleOrdersAccount.getRenterMemNo());
            cashierDeductDebtReq.setAmt(settleOrdersAccount.getDepositSurplusAmt());
            cashierDeductDebtReq.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_WZ_TO_HISTORY_AMT);
            CashierDeductDebtResVO result = cashierWzSettleService.deductWZDebt(cashierDeductDebtReq);
            if(Objects.nonNull(result)){
                //已抵扣抵扣金额
                int deductAmt = result.getDeductAmt();
                //计算 还完历史欠款 剩余 应退 剩余车俩押金
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() - deductAmt);
            }
        }
    }
    
    /**
     * 违章押金抵扣老系统欠款
     * @param settleOrdersAccount 结算公共参数
     * @return int
     */
    public int oldRepayWzHistoryDebtRent(SettleOrdersAccount settleOrdersAccount) {
    	boolean wzCostVirtualFlag = settleOrdersAccount.getWzCostVirtualFlag() == null ? false:settleOrdersAccount.getWzCostVirtualFlag();
    	List<AccountOldDebtReqVO> oldDebtList = new ArrayList<>();
    	// 违章押金抵扣
    	if (settleOrdersAccount.getDepositSurplusAmt() > 0 && !wzCostVirtualFlag) {
    		AccountOldDebtReqVO accountOldDebtReqVO = new AccountOldDebtReqVO();
    		accountOldDebtReqVO.setOrderNo(settleOrdersAccount.getOrderNo());
    		accountOldDebtReqVO.setRenterOrderNo(settleOrdersAccount.getRenterOrderNo());
    		accountOldDebtReqVO.setMemNo(settleOrdersAccount.getRenterMemNo());
    		accountOldDebtReqVO.setSurplusAmt(settleOrdersAccount.getDepositSurplusAmt());
    		accountOldDebtReqVO.setCahsCodeEnum(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_OLD_HISTORY_AMT);
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
    		if (cahsCodeEnum == RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_OLD_HISTORY_AMT) {
    			// 违章押金
    			cashierWzSettleService.saveDeductWZDebt(debtRes);
    			settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() - debtRes.getRealDebtAmt());
    		}
    	}
    	return totalRealDebtAmt;
    }


    /**
     * 违章退款
     *
     * @param settleOrdersAccount 结算公共参数
     * @param orderStatusDTO      订单状态信息
     */
    public void refundWzDepositAmt(SettleOrdersAccount settleOrdersAccount, OrderStatusDTO orderStatusDTO) {
        if (settleOrdersAccount.getDepositSurplusAmt() > 0) {
            //1退还违章押金
            CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
            BeanUtils.copyProperties(settleOrdersAccount, cashierRefundApply);

            //方法重构
            CashierEntity cashierEntity = cashierNoTService.getCashierEntity(settleOrdersAccount.getOrderNo(), settleOrdersAccount.getRenterMemNo(), DataPayKindConstant.DEPOSIT);
            BeanUtils.copyProperties(cashierEntity, cashierRefundApply);

            int id;
            if (DataPayTypeConstant.PAY_PRE.equals(cashierEntity.getPayType())) {
                //预授权处理
                id = cashierWzSettleService.refundWzDepositPreAuthAll(settleOrdersAccount.getDepositSurplusAmt(), cashierEntity, cashierRefundApply, RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT);
            } else {
                //消费 退货
                id = cashierWzSettleService.refundWzDepositPurchase(settleOrdersAccount.getDepositSurplusAmt(), cashierEntity, cashierRefundApply, RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT);
            }

            // 结算费用明细
            AccountRenterWzDepositCostSettleDetailEntity entity = new AccountRenterWzDepositCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrdersAccount, entity);
            entity.setMemNo(settleOrdersAccount.getRenterMemNo());
            entity.setWzAmt(-settleOrdersAccount.getDepositSurplusAmt());
            entity.setCostCode(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT.getCashNo());
            entity.setCostDetail(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(id));
            entity.setType(10);
            cashierWzSettleService.insertAccountRenterWzDepositCostSettleDetail(entity);
            log.info("(记录租客费用总账明细)新增租客COST明细表(退款费用)，accountRenterCostSettleDetailEntity. entity:[{}]",
                    GsonUtils.toJson(entity));
            Integer payLine = cashierRefundApply.getPayLine();
            // 0-线上支付，1-线下支付，2-虚拟支付
            if (payLine != null) {
                if (payLine.equals(PayLineEnum.OFF_LINE_PAY.getCode()) ||
                        payLine.equals(PayLineEnum.VIRTUAL_PAY.getCode())) {
                    orderStatusDTO.setWzRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
                } else {
                    orderStatusDTO.setWzRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());
                }
            } else {
                orderStatusDTO.setWzRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());
            }
        }
    }
    
    
    
    /**
     * 租客费用结余 处理 （如果应付 大于实付，这个订单存在未支付信息，优先 押金抵扣，未支付信息）
     * @param settleOrdersAccount 结算公共参数
     */
    public void wzCostSettle(SettleOrdersWz settleOrders , SettleOrdersAccount settleOrdersAccount) {
    	//应付 大于 实付,产生本单的欠款
        if(settleOrdersAccount.getRentCostPayAmt() < settleOrdersAccount.getRentCostAmtFinal()){
            //1.1押金 抵扣 租车费用欠款
            if(settleOrdersAccount.getDepositAmt()>0){
                DeductDepositToRentCostReqVO vo = new DeductDepositToRentCostReqVO();
                BeanUtils.copyProperties(settleOrders,vo);
                vo.setMemNo(settleOrders.getRenterMemNo());
                //正数   转负数
                int debtAmt = -(settleOrdersAccount.getRentCostAmtFinal() - settleOrdersAccount.getRentCostPayAmt());
                //真实抵扣金额
                int amt = debtAmt+settleOrdersAccount.getDepositSurplusAmt()>=0?debtAmt:-settleOrdersAccount.getDepositSurplusAmt();
                vo.setAmt(amt);
                //违章押金抵扣 违章费用金额 返回 已抵扣部分
                cashierWzSettleService.deductWzDepositToWzCost(vo);
                //计算剩余违章押金  amt是负数
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() + amt);
                // 实付费用加上 违章押金已抵扣部分
                settleOrdersAccount.setRentCostPayAmt(settleOrdersAccount.getRentCostPayAmt() + Math.abs(amt));

            }
        }
        
        //2如果 步骤1 结算 应付还是大于实付  此订单产生历史欠款
        //加上违章押金的抵扣，仍然产生
        if( settleOrdersAccount.getRentCostPayAmt() < settleOrdersAccount.getRentCostAmtFinal()){
            //2.1 记录历史欠款
            int amt = -(settleOrdersAccount.getRentCostAmtFinal() - settleOrdersAccount.getRentCostPayAmt());
            AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
            BeanUtils.copyProperties(settleOrders,accountInsertDebt);
            accountInsertDebt.setMemNo(settleOrders.getRenterMemNo());
            accountInsertDebt.setType(DebtTypeEnum.SETTLE.getCode());
            accountInsertDebt.setAmt(amt);
            accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            int debtDetailId = cashierWzSettleService.createWzDebt(accountInsertDebt);
            log.info("违章费用产生欠款,params=[{}],debtDetailId=[{}]",GsonUtils.toJson(accountInsertDebt),debtDetailId);
            
            
            //wzTotalCost-todo
            //2.2记录结算费用状态,    暂时都往租客费用总表里面记录吧
            AccountRenterWzDepositCostSettleDetailEntity entity = new AccountRenterWzDepositCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrders,entity);
            entity.setMemNo(settleOrders.getRenterMemNo());
            entity.setWzAmt(-Math.abs(amt));
            entity.setCostCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            entity.setCostDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(debtDetailId));
            entity.setType(10);

            cashierWzSettleService.insertAccountRenterWzDepositCostSettleDetail(entity);
            log.info("(记录租客费用总账明细)新增租客COST明细表(产生欠款)，accountRenterCostSettleDetailEntity params=[{}]",GsonUtils.toJson(entity));
            
            // 实付费用加上 历史欠款转移部分，存在欠款 1走历史欠款，2当前订单 账户拉平
            settleOrdersAccount.setRentCostPayAmt(settleOrdersAccount.getRentCostPayAmt() + Math.abs(amt));
        }
    }

    /**
     * 车辆结算成功 更新订单状态
     * @param settleOrdersAccount 结算公共参数
     */
    public void saveOrderStatusInfo(SettleOrdersAccount settleOrdersAccount) {
        //1更新 订单流转状态
        orderStatusService.saveOrderStatusInfo(settleOrdersAccount.getOrderStatusDTO());
        //2记录订单流传信息
        orderFlowService.inserOrderStatusChangeProcessInfo(settleOrdersAccount.getOrderNo(), OrderStatusEnum.TO_CLAIM_SETTLE);
        //2记录订单流传信息
        orderFlowService.inserOrderStatusChangeProcessInfo(settleOrdersAccount.getOrderNo(), OrderStatusEnum.COMPLETED);
        // 更新租客订单状态
        renterOrderService.updateRenterStatusByRenterOrderNo(settleOrdersAccount.getRenterOrderNo(), OrderStatusEnum.COMPLETED.getStatus());
        // 更新车主订单状态
        ownerOrderService.updateOwnerStatusByOwnerOrderNo(settleOrdersAccount.getOwnerOrderNo(), OrderStatusEnum.COMPLETED.getStatus());
    }

    
    
    // ---------------------------------------------------------------------------------------------------------------------------------------------
    

  
}
