package com.atzuche.order.settle.service.notservice;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.service.CashierWzSettleService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.req.DeductDepositToRentCostReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SysOrHandEnum;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.RentCostsWz;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersWz;
import com.autoyol.commons.utils.GsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jhuang
 *
 */
@Service
@Slf4j
public class OrderWzSettleNoTService {

    //更新订单状态和订单流转
    @Autowired 
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderFlowService orderFlowService;

    @Autowired 
    private CashierWzSettleService cashierWzSettleService;

    @Autowired
    RenterOrderWzCostDetailService renterOrderWzCostDetailService;

    /**
     * 查询租客费用明细
     * @param settleOrders
     */
    public void getRenterWzCostSettleDetail(SettleOrdersWz settleOrders) {
    	RentCostsWz rentCostsWz = new RentCostsWz();
    	
        //1 查询违章费用
        List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetails = renterOrderWzCostDetailService.queryInfosByOrderNo(settleOrders.getOrderNo());
        //参考 com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService#querySettleInfoByOrder

       
        int renterOrderCostWz = 0;
        if(!CollectionUtils.isEmpty(renterOrderWzCostDetails)){
        	for (RenterOrderWzCostDetailEntity renterOrderWzCostDetailEntity : renterOrderWzCostDetails) {
        		renterOrderCostWz += renterOrderWzCostDetailEntity.getAmount();
			}
        }
        
        rentCostsWz.setRenterOrderWzCostDetails(renterOrderWzCostDetails);
        settleOrders.setRenterOrderCostWz(renterOrderCostWz);
        settleOrders.setRentCostsWz(rentCostsWz);
    }
    
    
    /**
     * 结算租客 还历史欠款    等于违章费用无法从“违章费用里面扣除”，只能通过违章押金来扣除。本身作为“欠款”的方式。
     * @param settleOrdersAccount
     */
    public void repayWzHistoryDebtRent(SettleOrdersAccount settleOrdersAccount) {
        //车辆押金存在 且 租车费用没有抵扣完 ，使用车辆押金抵扣 历史欠款
        if(settleOrdersAccount.getDepositSurplusAmt()>0){
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
     	* 违章退款
     * @param settleOrdersAccount
     * @param orderStatusDTO
     */
    public void refundWzDepositAmt(SettleOrdersAccount settleOrdersAccount,OrderStatusDTO orderStatusDTO) {
        if(settleOrdersAccount.getDepositSurplusAmt()>0){
            //1退还违章押金
            CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
            BeanUtils.copyProperties(settleOrdersAccount,cashierRefundApply);
            cashierRefundApply.setMemNo(settleOrdersAccount.getRenterMemNo());
            cashierRefundApply.setAmt(-settleOrdersAccount.getDepositSurplusAmt());
            cashierRefundApply.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT);
            cashierRefundApply.setRemake(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT.getTxt());
            cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
            cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
            int id =cashierWzSettleService.refundWzDeposit(cashierRefundApply);
            
          //wzTotalCost-todo
            //account_renter_wz_deposit_cost_settle_detail  违章费用结算明细表 跟租车的有些区别。不记录。  都往费用表里面记录！！
            // 2记录退还 租车押金 结算费用明细
            AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrdersAccount,entity);
            entity.setMemNo(settleOrdersAccount.getRenterMemNo());
            entity.setAmt(-settleOrdersAccount.getDepositSurplusAmt());
            entity.setCostCode(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT.getCashNo());
            entity.setCostDetail(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(id));
            entity.setType(10);
            cashierWzSettleService.insertAccountRenterCostSettleDetail(entity);
            log.info("(记录租客费用总账明细)新增租客COST明细表(退款费用)，accountRenterCostSettleDetailEntity params=[{}]",GsonUtils.toJson(entity));
            
            orderStatusDTO.setWzRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());

        }

    }
    
    
    
    /**
     * 租客费用结余 处理 （如果应付 大于实付，这个订单存在未支付信息，优先 押金抵扣，未支付信息）
     * @param settleOrdersAccount
     */
    public void wzCostSettle(SettleOrdersWz settleOrders , SettleOrdersAccount settleOrdersAccount) {
    	//应付 大于 实付,产生本单的欠款
        if(settleOrdersAccount.getRentCostPayAmt() < settleOrdersAccount.getRentCostAmtFinal()){
            //1.1押金 抵扣 租车费用欠款
            if(settleOrdersAccount.getDepositAmt()>0){
                DeductDepositToRentCostReqVO vo = new DeductDepositToRentCostReqVO();
                BeanUtils.copyProperties(settleOrders,vo);
                vo.setMemNo(settleOrders.getRenterMemNo());
                int debtAmt = -(settleOrdersAccount.getRentCostAmtFinal() - settleOrdersAccount.getRentCostPayAmt());  //正数   转负数
                //真实抵扣金额
                int amt = debtAmt+settleOrdersAccount.getDepositSurplusAmt()>=0?debtAmt:-settleOrdersAccount.getDepositSurplusAmt();
                vo.setAmt(amt);
                //违章押金抵扣 违章费用金额 返回 已抵扣部分
                cashierWzSettleService.deductWzDepositToWzCost(vo);
                //计算剩余违章押金
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() + amt);  //amt是负数
                // 实付费用加上 违章押金已抵扣部分
                settleOrdersAccount.setRentCostPayAmt(settleOrdersAccount.getRentCostPayAmt() + Math.abs(amt));

            }
        }
        
        //2如果 步骤1 结算 应付还是大于实付  此订单产生历史欠款
        if( settleOrdersAccount.getRentCostPayAmt() < settleOrdersAccount.getRentCostAmtFinal()){   //加上违章押金的抵扣，仍然产生
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
            AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
            BeanUtils.copyProperties(settleOrders,entity);
            entity.setMemNo(settleOrders.getRenterMemNo());
            entity.setAmt(-amt);
            entity.setCostCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            entity.setCostDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            entity.setUniqueNo(String.valueOf(debtDetailId));
            entity.setType(10);
            cashierWzSettleService.insertAccountRenterCostSettleDetail(entity);
            log.info("(记录租客费用总账明细)新增租客COST明细表(产生欠款)，accountRenterCostSettleDetailEntity params=[{}]",GsonUtils.toJson(entity));
            
            // 实付费用加上 历史欠款转移部分，存在欠款 1走历史欠款，2当前订单 账户拉平
            settleOrdersAccount.setRentCostPayAmt(settleOrdersAccount.getRentCostPayAmt() + Math.abs(amt));
        }
    }

    /**
     * 车辆结算成功 更新订单状态
     * @param settleOrdersAccount
     */
    public void saveOrderStatusInfo(SettleOrdersAccount settleOrdersAccount) {
        //1更新 订单流转状态
        orderStatusService.saveOrderStatusInfo(settleOrdersAccount.getOrderStatusDTO());
        //2记录订单流传信息
        orderFlowService.inserOrderStatusChangeProcessInfo(settleOrdersAccount.getOrderNo(), OrderStatusEnum.TO_CLAIM_SETTLE);
    }

    
    
    // ---------------------------------------------------------------------------------------------------------------------------------------------
    

  
}
