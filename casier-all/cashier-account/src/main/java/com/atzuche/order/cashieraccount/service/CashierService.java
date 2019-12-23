package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountdebt.service.AccountDebtService;
import com.atzuche.order.accountdebt.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.accountdebt.vo.res.AccountDebtResVO;
import com.atzuche.order.accountownerincome.service.AccountOwnerIncomeService;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositCostService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.RenterWZDepositCostReqVO;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.res.AccountPayAbleResVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.web.ErrorCode;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;


/**
 * 收银台操作
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierService {
    @Autowired AccountRenterDepositService accountRenterDepositService;
    @Autowired AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired AccountDebtService accountDebtService;
    @Autowired CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired AccountOwnerIncomeService accountOwnerIncomeService;
    @Autowired AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired AccountRenterWzDepositCostService accountRenterWzDepositCostService;



    /**  ***************************************车辆押金 start****************************************************/
    /**
     * 记录应收车俩押金
     * 下单成功  调收银台 记录 车俩押金应付
     */
    @Transactional(rollbackFor=Exception.class)
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO){
        accountRenterDepositService.insertRenterDeposit(createOrderRenterDepositReqVO);
    }

    /**
     * 车俩押金支付成功回调
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateRenterDeposit(PayedOrderRenterDepositReqVO payedOrderRenterDeposit){
        accountRenterDepositService.updateRenterDeposit(payedOrderRenterDeposit);
    }

    /**
     * 扣减/暂扣 车俩押金
     */
    @Transactional(rollbackFor=Exception.class)
    public void detainRenterDeposit(DetainRenterDepositReqVO detainRenterDepositReqVO){
        accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);
    }

    /**
     * 查询车辆押金是否付清
     */
    public boolean isPayOffForRenterDeposit(String orderNo, String memNo){
       return accountRenterDepositService.isPayOffForRenterDeposit(orderNo, memNo);
    }
    /**
     * 查询车辆押金余额
     */
    public int getSurplusRenterDeposit(String orderNo, String memNo){
        return accountRenterDepositService.getSurplusRenterDeposit(orderNo, memNo);
    }

    /**  ***************************************** 押金 end *************************************************     */
    /**  ***************************************** 违章押金 start *************************************************     */

    /**
     * 记录应收违章押金
     * 下单成功  调收银台 记录 违章押金应付
     */
    @Transactional(rollbackFor=Exception.class)
    public void insertRenterDeposit(CreateOrderRenterWZDepositReqVO createOrderRenterWZDepositReq){
        accountRenterWzDepositService.insertRenterWZDeposit(createOrderRenterWZDepositReq);
    }
    /**
     * 支付成功后记录 实付违章押金信息 和违章押金资金进出信息
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateRenterWZDeposit(PayedOrderRenterWZDepositReqVO payedOrderWZRenterDeposit){
        accountRenterWzDepositService.updateRenterWZDeposit(payedOrderWZRenterDeposit);
    }

    /**
     * 扣减/暂扣 车俩押金
     */
    @Transactional(rollbackFor=Exception.class)
    public void detainRenterWZDeposit(PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail){
        accountRenterWzDepositService.updateRenterWZDepositChange(payedOrderRenterWZDepositDetail);
    }
    /**
     * 查询违章押金是否付清
     */
    public boolean isPayOffForRenterWZDeposit(String orderNo, String memNo){
        return accountRenterWzDepositService.isPayOffForRenterWZDeposit(orderNo, memNo);
    }
    /**
     * 查询违章押金余额
     */
    public int getSurplusRenterWZDeposit(String orderNo, String memNo){
        return accountRenterWzDepositService.getSurplusRenterWZDeposit(orderNo, memNo);
    }

    /**  ***************************************** 违章押金 end *************************************************     */

    /**  ***************************************** 历史欠款 start  *************************************************    */
    /**
     * 7）违章押金抵扣历史欠款
     */
    @CatAnnotation
    @Transactional(rollbackFor=Exception.class)
    public CashierDeductDebtResVO deductWZDebt(CashierDeductDebtReqVO cashierDeductDebtReqVO){
        Assert.notNull(cashierDeductDebtReqVO, ErrorCode.PARAMETER_ERROR.getText());
        cashierDeductDebtReqVO.check();
        //1 查询历史总欠款
        int debtAmt = accountDebtService.getAccountDebtNumByMemNo(cashierDeductDebtReqVO.getMemNo());
        if(debtAmt>=0){
            return new CashierDeductDebtResVO(cashierDeductDebtReqVO, NumberUtils.INTEGER_ZERO);
        }
        //2 抵扣
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReqVO,accountDeductDebt);
        int debtedAmt = accountDebtService.deductDebt(accountDeductDebt);
        //3 记录费用抵扣记录
        PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail = new PayedOrderRenterDepositWZDetailReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReqVO,payedOrderRenterWZDepositDetail);
        accountRenterWzDepositService.updateRenterWZDepositChange(payedOrderRenterWZDepositDetail);
        return new CashierDeductDebtResVO(cashierDeductDebtReqVO, debtedAmt);
    }
    /**
     * 7）押金抵扣历史欠款
     */
    @CatAnnotation
    @Transactional(rollbackFor=Exception.class)
    public CashierDeductDebtResVO deductDebt(CashierDeductDebtReqVO cashierDeductDebtReq){
        Assert.notNull(cashierDeductDebtReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierDeductDebtReq.check();
        //1 查询历史总欠款
        int debtAmt = accountDebtService.getAccountDebtNumByMemNo(cashierDeductDebtReq.getMemNo());
        if(debtAmt>=0){
            return new CashierDeductDebtResVO(cashierDeductDebtReq, NumberUtils.INTEGER_ZERO);
        }
        //2 抵扣
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReq,accountDeductDebt);
        int debtedAmt = accountDebtService.deductDebt(accountDeductDebt);
        //3 记录费用抵扣记录
        DetainRenterDepositReqVO detainRenterDepositReqVO = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReq,detainRenterDepositReqVO);
        accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);
        return new CashierDeductDebtResVO(cashierDeductDebtReq, debtedAmt);
    }
    /**
     * 查询用户历史欠款信息
     * @param memNo
     * @return
     */
    public AccountDebtResVO getAccountDebtByMemNo(String memNo) {
       return accountDebtService.getAccountDebtByMemNo(memNo);
    }

    /**  ***************************************** 历史欠款 end ************************************************* */
    /**  ***************************************** 退还押金 start ************************************************* */

    /**
     * 退还车辆押金
     */
    @CatAnnotation
    @Transactional(rollbackFor=Exception.class)
    public void refundDeposit(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        cashierRefundApplyReq.setSourceCode(Integer.parseInt(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getCashNo()));
        cashierRefundApplyReq.setSourceDetail(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getTxt());
        //1 记录退还记录
        cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        //2 记录费用平账
        DetainRenterDepositReqVO detainRenterDepositReqVO = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(cashierRefundApplyReq,detainRenterDepositReqVO);
        accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);
    }
    /**
     * 退还违章押金
     */
    @CatAnnotation
    @Transactional(rollbackFor=Exception.class)
    public void refundWZDeposit(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        cashierRefundApplyReq.setSourceCode(Integer.parseInt(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo()));
        cashierRefundApplyReq.setSourceDetail(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getTxt());
        //1 记录退还记录
        cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        //2 记录费用平账
        PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail = new PayedOrderRenterDepositWZDetailReqVO();
        BeanUtils.copyProperties(cashierRefundApplyReq,payedOrderRenterWZDepositDetail);
        accountRenterWzDepositService.updateRenterWZDepositChange(payedOrderRenterWZDepositDetail);
    }
    /**  ***************************************** 退还押金 end ************************************************* */

    /**  ***************************************** 车主收益 start ************************************************* */
    /**
     * 查询车主收益信息
     */
    public void getOwnerIncomeAmt(String memNo){
        accountOwnerIncomeService.getOwnerIncomeAmt(memNo);
    }
    /**
     * 结算产生车主待审核收益
     */
    public void insertOwnerIncomeExamine(AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamineReq){
        accountOwnerIncomeService.insertOwnerIncomeExamine(accountOwnerIncomeExamineReq);
    }
    /**
     * 收益审核通过 更新车主收益信息
     */
    public void examineOwnerIncomeExamine(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq){
        accountOwnerIncomeService.examineOwnerIncomeExamine(accountOwnerIncomeExamineOpReq);
    }

    /**
     * 收益提现
     */
    public void cashOwnerIncome(){
        accountOwnerIncomeService.cashOwnerIncome();
    }
    /**  ***************************************** 车主收益 end ************************************************* */

    /**  ***************************************** 违章费用 start ************************************************* */
    /**
     * 查询实扣违章费用总和
     */
    public int getWZDepositCostAmt(String orderNo ,String memNo){
        return accountRenterWzDepositCostService.getWZDepositCostAmt(orderNo,memNo);
    }
    /**
     * 违章费用资金进出
     */
    @CatAnnotation
    @Transactional(rollbackFor=Exception.class)
    public void changeWZDepositCost(RenterWZDepositCostReqVO renterWZDepositCost){
        accountRenterWzDepositCostService.changeWZDepositCost(renterWZDepositCost);
    }

    /**  ***************************************** 违章费用 end ************************************************* */

    /**  ***************************************** 结算租车费用（三方） start ************************************************* */


    /**  ***************************************** 结算租车费用（三方） end ************************************************* */

    /**
     * 当前需要支付的相关信息供支付平台使用
     */
    @CatAnnotation
    public OrderPayableAmountResVO getOrderPayableAmount(String orderNo,String memNo){
        OrderPayableAmountResVO result = new OrderPayableAmountResVO();
        int amtDeposit = accountRenterDepositService.getSurplusRenterDeposit(orderNo,memNo);
        int amtWZDeposit = accountRenterWzDepositService.getSurplusRenterWZDeposit(orderNo,memNo);
        //TODO 查询租车费用应收金额 海水
        int amtRenterCost =0;
        List<AccountPayAbleResVO> accountPayAbles = ImmutableList.of(
                new AccountPayAbleResVO(orderNo,memNo,amtDeposit,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT),
                new AccountPayAbleResVO(orderNo,memNo,amtWZDeposit,RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT),
                new AccountPayAbleResVO(orderNo,memNo,amtRenterCost,RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST)
        );
        result.setAccountPayAbles(accountPayAbles);
        result.setAmt(amtDeposit + amtWZDeposit + amtRenterCost);
        result.setMemNo(memNo);
        result.setOrderNo(orderNo);
        return result;
    }
}
