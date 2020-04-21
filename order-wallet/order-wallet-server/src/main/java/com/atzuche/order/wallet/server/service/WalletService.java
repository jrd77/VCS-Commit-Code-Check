package com.atzuche.order.wallet.server.service;

import com.atzuche.order.wallet.server.entity.WalletEntity;
import com.atzuche.order.wallet.server.mapper.WalletMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 11:12 上午
 **/
@Service
public class WalletService {
    private final static Logger logger = LoggerFactory.getLogger(WalletService.class);
    
    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private WalletBaseService baseService;


    /**
     * 租客订单扣减钱包
     * @param memNo
     * @param orderNo
     * @param amt
     */
    public int deductWallet(String memNo,String orderNo,Integer amt,String expDesc){
        WalletEntity entity = walletMapper.getWalletByMemNo(memNo);
        if(entity==null){
            throw new RuntimeException("wallet canot be null for mem_no="+memNo);
        }
        int total = entity.getBalance();
        if(total>=amt) {
            int payBalance = entity.getPayBalance();
            int giveBalance = entity.getGiveBalance();

            int expensePayBalance = 0;
            int expenseGiveBalance = 0;

            if(payBalance>=amt){
                expensePayBalance = amt;
            }else{
                expensePayBalance = payBalance;
                expenseGiveBalance = amt-payBalance;
            }
            try {
                baseService.doUpdateWallet(memNo, orderNo, expensePayBalance, expenseGiveBalance,expDesc);
                return amt;
            }catch (RuntimeException e){
                logger.error("扣减失败：memNo={},orderNo={},amt={}",memNo,orderNo,amt);
            }
            return 0;

        }

        //FIXME:

        return 0;
    }

    public int getTotalWallet(String memNo){
        return baseService.getWalletTotal(memNo);
    }

    /**
     * 如果退还的钱大于该订单消费的赠送部分，则新增的值添加充值部分；
     * @param memNo
     * @param orderNo
     * @param amt
     */
    public void returnOrCharge(String memNo,String orderNo,int amt,String expDesc){
         int expenseGive = baseService.getExpenseGiveBalance(memNo,orderNo);
         if(amt<=expenseGive){
             baseService.doUpdateWallet(memNo,orderNo,0,-amt,expDesc);
         }else{
             baseService.doUpdateWallet(memNo,orderNo,-(amt-expenseGive),-expenseGive,expDesc);
         }
    }



}
