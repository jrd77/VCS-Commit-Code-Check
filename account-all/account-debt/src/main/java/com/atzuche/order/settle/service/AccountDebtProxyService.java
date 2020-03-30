/**
 * 
 */
package com.atzuche.order.settle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.settle.service.notservice.AccountDebtNoTService;
import com.atzuche.order.settle.service.notservice.AccountDebtReceivableaDetailNoTService;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.autoyol.commons.web.ErrorCode;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Slf4j
@Service
public class AccountDebtProxyService {
    @Autowired
    private AccountDebtNoTService accountDebtNoTService;
    @Autowired
    private AccountDebtDetailNoTService accountDebtDetailNoTService;
    @Autowired
    private AccountDebtReceivableaDetailNoTService accountDebtReceivableaDetailNoTService;
    
    /**
     * 支付欠款异步通知抵扣欠款
     * @param orderNo
     * @param payDebtAmt
     * @param memNo  包括车主或租客
     * @param qn
     * @return
     */
    public int deductDebtByOrderNo(String orderNo,int payDebtAmt,String memNo,String qn) {
    	log.info("payDebt notice deductDebtByOrderNo params orderNo=[{}],payDebtAmt=[{}],renterNo=[{}],qn=[{}]",orderNo,payDebtAmt,memNo,qn);
    	//构造对象
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        accountDeductDebt.setSourceCode(RenterCashCodeEnum.PAY_DEBT_COST_TO_HISTORY_AMT.getCashNo());
        accountDeductDebt.setSourceDetail(RenterCashCodeEnum.PAY_DEBT_COST_TO_HISTORY_AMT.getTxt());
        accountDeductDebt.setAmt(payDebtAmt);
        accountDeductDebt.setMemNo(memNo);
        accountDeductDebt.setUniqueNo(qn);
        
        // 1 参数校验
//        Assert.notNull(accountDeductDebt, ErrorCode.PARAMETER_ERROR.getText());
//        accountDeductDebt.check();
        
        // 2 查询用户所有待还的记录
        List<AccountDebtDetailEntity> accountDebtDetailAlls =  accountDebtDetailNoTService.getDebtListByOrderNoMemNo(orderNo,memNo);
        //3 根据租客还款总额  从用户所有待还款记录中 过滤本次 待还款的记录
        List<AccountDebtDetailEntity> accountDebtDetails = accountDebtDetailNoTService.getDebtListByDebtAll(accountDebtDetailAlls,accountDeductDebt);
        //5更新欠款表 当前欠款数
        accountDebtDetailNoTService.updateAlreadyDeductDebt(accountDebtDetails);
        //6 记录欠款收款详情
        accountDebtReceivableaDetailNoTService.insertAlreadyReceivablea(accountDeductDebt.getAccountDebtReceivableaDetails());
        //7 更新总欠款表
        accountDebtNoTService.deductAccountDebt(accountDeductDebt);
        return accountDeductDebt.getRealAmt();
    }
    
    //根据ID来更新
    public int deductDebtByDebtId(String id,int payDebtAmt,String memNo,String qn) {
    	try {
    		log.info("payDebt notice deductDebtByOrderNo params id=[{}],payDebtAmt=[{}],renterNo=[{}],qn=[{}]",id,payDebtAmt,memNo,qn);
        	//构造对象
            AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
            accountDeductDebt.setSourceCode(RenterCashCodeEnum.PAY_DEBT_COST_TO_HISTORY_AMT.getCashNo());
            accountDeductDebt.setSourceDetail(RenterCashCodeEnum.PAY_DEBT_COST_TO_HISTORY_AMT.getTxt());
            accountDeductDebt.setAmt(payDebtAmt); //正数
            accountDeductDebt.setMemNo(memNo);
            accountDeductDebt.setUniqueNo(qn);
            
            // 1 参数校验
//            Assert.notNull(accountDeductDebt, ErrorCode.PARAMETER_ERROR.getText());
//            accountDeductDebt.check();
            
            // 2 查询ID待还的记录
//            List<AccountDebtDetailEntity> accountDebtDetailAlls =  accountDebtDetailNoTService.getDebtListByOrderNoMemNo(orderNo,memNo);
            List<AccountDebtDetailEntity> accountDebtDetailAlls =  accountDebtDetailNoTService.getDebtListById(id);
            
            //3 根据租客还款总额  从用户所有待还款记录中 过滤本次 待还款的记录
            List<AccountDebtDetailEntity> accountDebtDetails = accountDebtDetailNoTService.getDebtListByDebtAll(accountDebtDetailAlls,accountDeductDebt);
            //5更新欠款表 当前欠款数
            accountDebtDetailNoTService.updateAlreadyDeductDebt(accountDebtDetails);
            //6 记录欠款收款详情
            accountDebtReceivableaDetailNoTService.insertAlreadyReceivablea(accountDeductDebt.getAccountDebtReceivableaDetails());
            //7 更新总欠款表
            accountDebtNoTService.deductAccountDebt(accountDeductDebt);
            return accountDeductDebt.getRealAmt();
		} catch (Exception e) {
			log.info("deductDebtByDebtId error:",e);
		}
    	return 0;
    }
    
    
}
