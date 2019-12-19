package com.atzuche.order.accountrenterwzdepost.service;


import com.atzuche.order.accountrenterwzdepost.common.Constant;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
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
 * 违章押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
@Slf4j
public class AccountRenterWzDepositCatService {
    @Autowired
    private AccountRenterWzDepositService accountRenterWzDepositService;

    /**
     * 查询押金状态信息
     */
    public AccountRenterWZDepositResVO getAccountRenterWZDeposit(String orderNo, String memNo){
        log.info("AccountRenterWzDepositCatService getAccountRenterDepositEntity start param  orderNo ,[{}] ，memNo ,[{}]", orderNo,memNo);
        AccountRenterWZDepositResVO  result = accountRenterWzDepositService.getAccountRenterWZDeposit(orderNo, memNo);
        log.info("AccountRenterWzDepositCatService getAccountRenterDepositEntity start param  orderNo ,[{}] ，memNo ,[{}]", orderNo,memNo);
        return result;
    }
    /**
     * 下单成功记录应付违章押金
     */
    public void insertRenterWZDeposit(CreateOrderRenterWZDepositReqVO createOrderRenterWZDepositReq){
        log.info("AccountRenterDepositCatService insertRenterWZDeposit start param [{}]", GsonUtils.toJson(createOrderRenterWZDepositReq));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_INSERT_RENTER_WZ_DEPOSIT, Constant.CAT_TRANSACTION_INSERT_RENTER_WZ_DEPOSIT);
        try {
            accountRenterWzDepositService.insertRenterWZDeposit(createOrderRenterWZDepositReq);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountRenterDepositCatService insertRenterWZDeposit error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountRenterDepositCatService insertRenterWZDeposit end param [{}]", GsonUtils.toJson(createOrderRenterWZDepositReq));
    }

    /**
     * 支付成功后记录实付违章押金信息 和违章押金资金进出信息
     */
    public void updateRenterWZDeposit(PayedOrderRenterWZDepositReqVO payedOrderRenterDeposit){
        log.info("AccountRenterDepositCatService updateRenterWZDeposit start param [{}]", GsonUtils.toJson(payedOrderRenterDeposit));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_UPDATE_RENTER_WZ_DEPOSIT, Constant.CAT_TRANSACTION_UPDATE_RENTER_WZ_DEPOSIT);
        try {
            accountRenterWzDepositService.updateRenterWZDeposit(payedOrderRenterDeposit);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountRenterDepositCatService updateRenterWZDeposit error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountRenterDepositCatService updateRenterWZDeposit end param [{}]", GsonUtils.toJson(payedOrderRenterDeposit));
    }
    /**
     * 支户头违章押金资金进出 操作
     */
    public void updateRenterWZDepositChange(PayedOrderRenterDepositWZDetailReqVO payedOrderRenterDepositDetail){
        log.info("AccountRenterDepositCatService updateRenterWZDepositChange start param [{}]", GsonUtils.toJson(payedOrderRenterDepositDetail));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_UPDATE_RENTER_WZ_DEPOSIT_CHANGE, Constant.CAT_TRANSACTION_UPDATE_RENTER_WZ_DEPOSIT_CHANGE);
        try {
            accountRenterWzDepositService.updateRenterWZDepositChange(payedOrderRenterDepositDetail);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountRenterDepositCatService updateRenterWZDepositChange error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountRenterDepositCatService updateRenterWZDepositChange end param [{}]", GsonUtils.toJson(payedOrderRenterDepositDetail));
    }

    /**
     * 查询违章押金是否付清
     */
    public boolean isPayOffForRenterWZDeposit(String orderNo, String memNo) {
        AccountRenterWZDepositResVO accountRenterWZDepositRes = getAccountRenterWZDeposit(orderNo,memNo);
        // 1 记录不存在
        if(Objects.isNull(accountRenterWZDepositRes) || Objects.isNull(accountRenterWZDepositRes.getOrderNo())){
            return Boolean.FALSE;
        }
        //2开启免疫
        if(YesNoEnum.YES.getCode()==accountRenterWZDepositRes.getIsFreeDeposit()){
            return Boolean.TRUE;
        }
        //3 应付 和实付
        int yingfuAmt = accountRenterWZDepositRes.getYingshouDeposit();
        int shifuAmt = accountRenterWZDepositRes.getShishouDeposit();
        return shifuAmt+yingfuAmt>=0;
    }
    /**
     * 查询违章押金余额
     */
    public int getSurplusRenterWZDeposit(String orderNo, String memNo) {
        //查询车辆押金信息
        AccountRenterWZDepositResVO accountRenterDepositRes = getAccountRenterWZDeposit(orderNo,memNo);
        //1 校验 是否存在车辆押金记录
        Assert.notNull(accountRenterDepositRes, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(accountRenterDepositRes.getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        //2 返回计算剩余押金余额  即应退余额
        int shouldReturnDeposit = accountRenterDepositRes.getShouldReturnDeposit();
        return shouldReturnDeposit;
    }
}
