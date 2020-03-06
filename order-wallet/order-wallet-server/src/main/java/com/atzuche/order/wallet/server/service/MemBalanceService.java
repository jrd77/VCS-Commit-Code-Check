package com.atzuche.order.wallet.server.service;

import com.atzuche.order.commons.exceptions.NotEnoughBalanceException;
import com.atzuche.order.wallet.server.entity.BalanceEntity;
import com.atzuche.order.wallet.server.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/5 2:20 下午
 **/
@Service
public class MemBalanceService {
    @Autowired
    private MemberMapper memberMapper;

    public void deductBalance(String memNo,Integer deduct){
        int count = memberMapper.deductBalance(memNo,deduct);
        if(count==0){
            int totalBalance =getTotalBalance(memNo);
            throw new NotEnoughBalanceException("memNo="+memNo+";deduct="+deduct+";balance="+totalBalance);
        }
        //FIXME: 添加日志操作记录，方便后期查找
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
