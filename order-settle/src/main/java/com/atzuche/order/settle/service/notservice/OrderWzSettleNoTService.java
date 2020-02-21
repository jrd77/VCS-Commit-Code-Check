package com.atzuche.order.settle.service.notservice;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountrenterdetain.service.notservice.AccountRenterDetainDetailNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
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
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzSettleFlagEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.renterwz.service.RenterOrderWzSettleFlagService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.service.OrderWzSettleNewService;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.RentCostsWz;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersWz;
import com.autoyol.doc.util.StringUtil;

import lombok.extern.slf4j.Slf4j;
//
///**
// * 订单结算
// */
@Service
@Slf4j
public class OrderWzSettleNoTService {
    @Autowired CashierNoTService cashierNoTService;
    @Autowired private CashierService cashierService;
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private RenterOrderService renterOrderService;
    @Autowired private OrderStatusService orderStatusService;
    
    @Autowired
    private OrderFlowService orderFlowService;

    
    @Autowired
    private AccountRenterDetainDetailNoTService accountRenterDetainDetailNoTService;
    @Autowired
    private OrderWzSettleNewService orderWzSettleNewService;
    @Autowired
    RenterOrderWzCostDetailService renterOrderWzCostDetailService;
    @Autowired
    private RenterOrderWzSettleFlagService renterOrderWzSettleFlagService;
    
    /**
     * 初始化结算对象
     * @param orderNo
     */
    public SettleOrdersWz initSettleOrders(String orderNo) {
        //1 校验参数
        if(StringUtil.isBlank(orderNo)){
            throw new OrderSettleFlatAccountException();
        }
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())){
            throw new OrderSettleFlatAccountException();
        }

        // 2 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
        this.check(renterOrder);
        // 3 初始化数据

        // 3.1获取租客子订单 和 租客会员号
        String renterOrderNo = renterOrder.getRenterOrderNo();
        String renterMemNo = renterOrder.getRenterMemNo();

        SettleOrdersWz settleOrdersWz = new SettleOrdersWz();
        settleOrdersWz.setOrderNo(orderNo);
        settleOrdersWz.setRenterOrderNo(renterOrderNo);
        settleOrdersWz.setRenterMemNo(renterMemNo);
        settleOrdersWz.setRenterOrder(renterOrder);
        return settleOrdersWz;
    }
    
    /**
     * 校验是否可以结算 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
     * @param renterOrder
     */
    public void check(RenterOrderEntity renterOrder) {
        // 1 订单校验是否可以结算
        OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(renterOrder.getOrderNo());
        if(OrderStatusEnum.TO_WZ_SETTLE.getStatus() != orderStatus.getStatus()){
            throw new RuntimeException("租客订单状态不是待结算，不能结算");
        }
        //3 校验是否存在 理赔  存在不结算  这个跟违章是一起的。
        boolean isClaim = cashierSettleService.getOrderClaim(renterOrder.getOrderNo());
        if(isClaim){
            throw new RuntimeException("租客存在理赔信息不能结算");
        }
        /**
         * 根据费用编码来判断是否暂扣。
         */
        boolean isDetain = accountRenterDetainDetailNoTService.isWzDepositDetain(renterOrder.getOrderNo());
        if(isDetain){
            throw new RuntimeException("租客存在暂扣信息不能结算");
        }
        
        //com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService#querySettleInfoByOrder
        List<RenterOrderWzSettleFlagEntity> settleInfos = renterOrderWzSettleFlagService.getIllegalSettleInfosByOrderNo(renterOrder.getOrderNo());
        //过滤未结算和结算失败的
        if(CollectionUtils.isEmpty(settleInfos)){
            //无法结算
        	throw new RuntimeException("租客未找到违章记录信息不能结算");
        }

        //4 先查询  发现 有结算数据停止结算 手动处理
        orderWzSettleNewService.checkIsSettle(renterOrder.getOrderNo());
    }
    
    /**
     * 查询租客费用明细
     * @param settleOrders
     */
    public void getRenterCostSettleDetail(SettleOrdersWz settleOrders) {
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
            CashierDeductDebtResVO result = cashierService.deductWZDebt(cashierDeductDebtReq);
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
            int id =cashierService.refundWzDeposit(cashierRefundApply);
            
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
            cashierSettleService.insertAccountRenterCostSettleDetail(entity);
            
            orderStatusDTO.setWzRefundStatus(OrderRefundStatusEnum.REFUNDING.getStatus());

        }

    }
    
    
    
    /**
     * 租客费用结余 处理 （如果应付 大于实付，这个订单存在未支付信息，优先 押金抵扣，未支付信息）
     * @param settleOrdersAccount
     */
    public void wzCostSettle(SettleOrdersWz settleOrders , SettleOrdersAccount settleOrdersAccount) {
        //1 如果租车费用计算应付总额大于 实际支付 车辆押金抵扣租车费用欠款
    	
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
                //车俩押金抵扣 租车费用金额 返回 已抵扣部分
                cashierSettleService.deductWzDepositToRentCost(vo);
                //计算剩余车俩押金
                settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() + amt);  //amt是负数
                // 实付费用加上 押金已抵扣部分
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
            int debtDetailId = cashierService.createDebt(accountInsertDebt);
            
            
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
            cashierSettleService.insertAccountRenterCostSettleDetail(entity);
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
        orderFlowService.inserOrderStatusChangeProcessInfo(settleOrdersAccount.getOrderNo(), OrderStatusEnum.COMPLETED);
    }

    
    
    // ---------------------------------------------------------------------------------------------------------------------------------------------
    

  
}
