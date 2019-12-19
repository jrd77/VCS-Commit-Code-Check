package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountdebt.service.AccountDebtService;
import com.atzuche.order.accountdebt.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.accountdebt.vo.res.AccountDebtResVO;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * 收银台操作
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierService {
    @Autowired
    private AccountRenterDepositService accountRenterDepositService;
    @Autowired AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired AccountDebtService accountDebtService;
    @Autowired CashierRefundApplyNoTService cashierRefundApplyNoTService;


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
     * 7）押金/违章押金抵扣历史欠款
     */
    @CatAnnotation
    @Transactional(rollbackFor=Exception.class)
    public CashierDeductDebtResVO deductDebt(CashierDeductDebtReqVO cashierDeductDebtReqVO){
        Assert.notNull(cashierDeductDebtReqVO, ErrorCode.PARAMETER_ERROR.getText());
        cashierDeductDebtReqVO.check();
        //1 查询历史总欠款
        int debtAmt = accountDebtService.getAccountDebtNumByMemNo(cashierDeductDebtReqVO.getMemNo());
        if(debtAmt>=0){
            return new CashierDeductDebtResVO(cashierDeductDebtReqVO, NumberUtils.INTEGER_ZERO);
        }
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        //2 抵扣
        BeanUtils.copyProperties(cashierDeductDebtReqVO,accountDeductDebt);
        int debtedAmt = accountDebtService.deductDebt(accountDeductDebt);
        //3 记录费用抵扣记录
        //TODO
        //1 违章押金抵扣
        PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail = null;
        accountRenterWzDepositService.updateRenterWZDepositChange(payedOrderRenterWZDepositDetail);
        //2车辆押金抵扣
        PayedOrderRenterDepositDetailReqVO payedOrderRenterDepositDetail =null;
        accountRenterDepositService.updateRenterDepositChange(payedOrderRenterDepositDetail);

        return new CashierDeductDebtResVO(cashierDeductDebtReqVO, debtedAmt);
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
     * 退还车辆/违章押金押金
     */
    @CatAnnotation
    @Transactional(rollbackFor=Exception.class)
    public void refundDeposit(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        //1 记录退还记录
        cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        //2 记录费用平账

    }
    /**  ***************************************** 退还押金 end ************************************************* */

}
