package com.atzuche.order.wallet.server.service;

import com.atzuche.order.wallet.server.entity.WalletEntity;
import com.atzuche.order.wallet.server.entity.WalletLogEntity;
import com.atzuche.order.wallet.server.mapper.WalletLogMapper;
import com.atzuche.order.wallet.server.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 11:56 上午
 **/
@Service
public class WalletBaseService {
    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private WalletLogMapper walletLogMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    public void doUpdateWallet(String memNo, String orderNo, int expensePayBalance, int expenseGiveBalance){
        int count = walletMapper.updateWallet(memNo,expensePayBalance,expenseGiveBalance);
        if(count==0){
            throw new RuntimeException("update wallet version error");
        }
        if(expensePayBalance!=0){
            WalletLogEntity logEntity = new WalletLogEntity();
            logEntity.setAmt(-expensePayBalance);
            logEntity.setFlag("21");
            logEntity.setFlagTxt("租车消费");
            logEntity.setMemNo(memNo);
            logEntity.setOrderNo(orderNo);
            walletLogMapper.insertWalletLog(logEntity);
        }
        if(expenseGiveBalance!=0){
            WalletLogEntity logEntity = new WalletLogEntity();
            logEntity.setAmt(-expenseGiveBalance);
            logEntity.setFlag("22");
            logEntity.setFlagTxt("租车消费");
            logEntity.setMemNo(memNo);
            logEntity.setOrderNo(orderNo);
            walletLogMapper.insertWalletLog(logEntity);
        }
    }



    /**
     * 返回用户的钱包余额
     * @param memNo
     * @return
     */
    public int getWalletTotal(String memNo){
        WalletEntity entity = walletMapper.getWalletByMemNo(memNo);
        if(entity!=null){
            return entity.getBalance();
        }
        return 0;
    }

    /**
     * 返回赠送账户该订单的消费，转成了正值；
     * @param memNo
     * @param orderNo
     * @return
     */
    public int getExpenseGiveBalance(String memNo,String orderNo){
        List<WalletLogEntity> walletLogEntityList = walletLogMapper.findByMemNoAndOrderNo(memNo,orderNo);
        int totalExpenseGiveBalance=0;
        for(WalletLogEntity entity:walletLogEntityList){
            if(entity.getFlag()!=null&&"22".equalsIgnoreCase(entity.getFlag())){
               totalExpenseGiveBalance = totalExpenseGiveBalance+entity.getAmt();
            }
        }
        return -totalExpenseGiveBalance;
    }
}
