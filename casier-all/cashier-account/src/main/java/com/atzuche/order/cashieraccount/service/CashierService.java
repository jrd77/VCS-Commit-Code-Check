package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountdebt.service.AccountDebtCatService;
import com.atzuche.order.accountdebt.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositCatService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositCatService;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.mapper.CashierMapper;
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
    private AccountRenterDepositCatService accountRenterDepositCatService;
    @Autowired AccountRenterWzDepositCatService accountRenterWzDepositCatService;
    @Autowired AccountDebtCatService accountDebtCatService;

    /**  ***************************************车辆押金 start****************************************************/
    /**
     * 记录应收车俩押金
     * 下单成功  调收银台 记录 车俩押金应付
     */
    @Transactional(rollbackFor=Exception.class)
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO){
        accountRenterDepositCatService.insertRenterDeposit(createOrderRenterDepositReqVO);
    }
    /**
     * 查询车辆押金是否付清
     */
    public boolean isPayOffForRenterDeposit(String orderNo, String memNo){
       return accountRenterDepositCatService.isPayOffForRenterDeposit(orderNo, memNo);
    }
    /**
     * 查询车辆押金余额
     */
    public int getSurplusRenterDeposit(String orderNo, String memNo){
        return accountRenterDepositCatService.getSurplusRenterDeposit(orderNo, memNo);
    }

    /**  ***************************************** 押金 end *************************************************     */
    /**  ***************************************** 违章押金 start *************************************************     */

    /**
     * 记录应收违章押金
     * 下单成功  调收银台 记录 违章押金应付
     */
    @Transactional(rollbackFor=Exception.class)
    public void insertRenterDeposit(CreateOrderRenterWZDepositReqVO createOrderRenterWZDepositReq){
        accountRenterWzDepositCatService.insertRenterWZDeposit(createOrderRenterWZDepositReq);
    }
    /**
     * 查询违章押金是否付清
     */
    public boolean isPayOffForRenterWZDeposit(String orderNo, String memNo){
        return accountRenterWzDepositCatService.isPayOffForRenterWZDeposit(orderNo, memNo);
    }
    /**
     * 查询违章押金余额
     */
    public int getSurplusRenterWZDeposit(String orderNo, String memNo){
        return accountRenterWzDepositCatService.getSurplusRenterWZDeposit(orderNo, memNo);
    }

    /**  ***************************************** 违章押金 end *************************************************     */

    /**  ***************************************** 历史欠款 start  *************************************************    */
    /**
     * 7）押金/违章押金抵扣历史欠款
     */
    public CashierDeductDebtResVO deductDebt(CashierDeductDebtReqVO cashierDeductDebtReqVO){
        Assert.notNull(cashierDeductDebtReqVO, ErrorCode.PARAMETER_ERROR.getText());
        cashierDeductDebtReqVO.check();
        //1 查询历史总欠款
        int debtAmt = accountDebtCatService.getAccountDebtNumByMemNo(cashierDeductDebtReqVO.getMemNo());
        if(debtAmt>=0){
            return new CashierDeductDebtResVO(cashierDeductDebtReqVO, NumberUtils.INTEGER_ZERO);
        }
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        //2 抵扣
        BeanUtils.copyProperties(cashierDeductDebtReqVO,accountDeductDebt);
        int debtedAmt = accountDebtCatService.deductDebt(accountDeductDebt);
        return new CashierDeductDebtResVO(cashierDeductDebtReqVO, debtedAmt);
    }
    /**  ***************************************** 历史欠款 end *************************************************     */
}
