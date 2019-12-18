package com.atzuche.order.accountrenterdeposit.service;

import com.atzuche.order.accountrenterdeposit.common.Constant;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositDetailReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 租车押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-17 17:09:45
 */
@Service
@Slf4j
public class AccountRenterDepositCatService {
    @Autowired
    private AccountRenterDepositService accountRenterDepositService;


    /**
     * 查询车俩押金是否付清
     * @param orderNo
     * @param memNo
     * @return
     */
    public boolean isPayOffForRenterDeposit(String orderNo, String memNo) {
        AccountRenterDepositResVO accountRenterDepositRes = getAccountRenterDepositEntity(orderNo,memNo);
        // 1 记录不存在
        if(Objects.isNull(accountRenterDepositRes) || Objects.isNull(accountRenterDepositRes.getOrderNo())){
            return Boolean.FALSE;
        }
        //2开启免疫
        if(YesNoEnum.YES.getCode()==accountRenterDepositRes.getIsFreeDeposit()){
            return Boolean.TRUE;
        }
        //3 实付 可能是getShifuDepositAmt ，预授权getAuthorizeDepositAmt 或者 信用支付getCreditPayAmt（） 均不为负数
        //应付 负数    相加大于等于0 表示 已经付过押金
        int yingfuAmt = accountRenterDepositRes.getYingfuDepositAmt();
        int shifuAmt = accountRenterDepositRes.getShifuDepositAmt() + accountRenterDepositRes.getAuthorizeDepositAmt()+accountRenterDepositRes.getCreditPayAmt();
        return yingfuAmt + shifuAmt>=0;
    }
    /**
     * 查询车辆押金余额
     */
    public int getSurplusRenterDeposit(String orderNo, String memNo) {
        //查询车辆押金信息
        AccountRenterDepositResVO accountRenterDepositRes = getAccountRenterDepositEntity(orderNo,memNo);
        //1 校验 是否存在车辆押金记录
        Assert.notNull(accountRenterDepositRes, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(accountRenterDepositRes.getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        //2 返回计算剩余押金余额
        int surplusRenterDeposit = accountRenterDepositRes.getSurplusDepositAmt() + accountRenterDepositRes.getSurplusAuthorizeDepositAmt() + accountRenterDepositRes.getSurplusCreditPayAmt();
        return surplusRenterDeposit;
    }

    /**
     * 查询押金状态信息
     */
    public AccountRenterDepositResVO getAccountRenterDepositEntity(String orderNo, String memNo){
        log.info("AccountRenterDepositCatService getAccountRenterDepositEntity start param  orderNo ,[{}] ，memNo ,[{}]", orderNo,memNo);
        AccountRenterDepositResVO result = accountRenterDepositService.getAccountRenterDepositEntity(orderNo,memNo);
        log.info("AccountRenterDepositCatService getAccountRenterDepositEntity end  orderNo [{}] ，result [{}]", orderNo,result);
        return result;
    }
    /**
     * 下单成功记录应付押金
     */
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO){
        log.info("AccountRenterDepositCatService insertRenterDeposit start param [{}]", GsonUtils.toJson(createOrderRenterDepositReqVO));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_INSERT_RENTER_DEPOSIT, Constant.CAT_TRANSACTION_INSERT_RENTER_DEPOSIT);
        try {
            accountRenterDepositService.insertRenterDeposit(createOrderRenterDepositReqVO);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountRenterDepositCatService insertRenterDeposit error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountRenterDepositCatService insertRenterDeposit end param [{}]", GsonUtils.toJson(createOrderRenterDepositReqVO));
    }
    /**
     * 支付成功后记录实付押金信息 和押金资金进出信息
     */
    public void updateRenterDeposit(PayedOrderRenterDepositReqVO payedOrderRenterDeposit){
        log.info("AccountRenterDepositCatService updateRenterDeposit start param [{}]", GsonUtils.toJson(payedOrderRenterDeposit));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_UPDATE_RENTER_DEPOSIT, Constant.CAT_TRANSACTION_UPDATE_RENTER_DEPOSIT);
        try {
            accountRenterDepositService.updateRenterDeposit(payedOrderRenterDeposit);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountRenterDepositCatService updateRenterDeposit error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountRenterDepositCatService updateRenterDeposit end param [{}]", GsonUtils.toJson(payedOrderRenterDeposit));
    }

    /**
     * 支户头押金资金进出 操作
     */
    public void updateRenterDepositChange(PayedOrderRenterDepositDetailReqVO payedOrderRenterDepositDetail){
        log.info("AccountRenterDepositCatService updateRenterDepositChange start param [{}]", GsonUtils.toJson(payedOrderRenterDepositDetail));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_UPDATE_RENTER_DEPOSIT_CHANGE, Constant.CAT_TRANSACTION_UPDATE_RENTER_DEPOSIT_CHANGE);
        try {
            accountRenterDepositService.updateRenterDepositChange(payedOrderRenterDepositDetail);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountRenterDepositCatService updateRenterDepositChange error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountRenterDepositCatService updateRenterDepositChange end param [{}]", GsonUtils.toJson(payedOrderRenterDepositDetail));
    }


}
