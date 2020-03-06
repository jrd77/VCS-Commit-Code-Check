package com.atzuche.order.wallet.server.service;

import com.atzuche.order.commons.exceptions.NotEnoughBalanceException;
import com.atzuche.order.wallet.server.entity.BalanceDeductLogEntity;
import com.atzuche.order.wallet.server.entity.BalanceEntity;
import com.atzuche.order.wallet.server.mapper.BalanceDeductLogMapper;
import com.atzuche.order.wallet.server.mapper.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/5 2:20 下午
 **/
@Service
public class MemBalanceService {
    private final static Logger logger = LoggerFactory.getLogger(MemBalanceService.class);
    
    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private BalanceLogService logService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public void deductBalance(String memNo,Integer deduct){
        int count = memberMapper.deductBalance(memNo,deduct);

        logger.info("deductBalance,param=[memNo={},deduct={}],result=[{}]",memNo,deduct,count);

        if(count==0){
            int totalBalance =getTotalBalance(memNo);
            logService.log(memNo,deduct,"操作失败，余额不足");
            logger.warn("deductBalance,param=[memNo={},deduct={},total={}] 余额不足，操作失败",memNo,deduct,totalBalance);
            throw new NotEnoughBalanceException("memNo="+memNo+";deduct="+deduct+";balance="+totalBalance);
        }

        logService.log(memNo,deduct,"操作成功");
        logger.info("deductBalance,param=[memNo={},deduct={}] 扣减成功",memNo,deduct);

    }

    public int getTotalBalance(String memNo){
        BalanceEntity balanceEntity = memberMapper.getByMemNo(memNo);
        int balance=0;
        if(balanceEntity!=null&&balanceEntity.getBalance()!=null){
            balance = balanceEntity.getBalance();
        }
        return balance;
    }


}
