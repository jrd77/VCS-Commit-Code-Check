package com.atzuche.order.accountrenterdeposit.service;

import com.atzuche.order.accountrenterdeposit.common.Constant;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositDetailReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
