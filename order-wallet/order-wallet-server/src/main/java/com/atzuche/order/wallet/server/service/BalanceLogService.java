package com.atzuche.order.wallet.server.service;

import com.atzuche.order.wallet.server.entity.BalanceDeductLogEntity;
import com.atzuche.order.wallet.server.mapper.BalanceDeductLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 2:46 下午
 **/
@Service
public class BalanceLogService {
    @Autowired
    private BalanceDeductLogMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String memNo,Integer deduct,String result){
        BalanceDeductLogEntity balanceDeductLogEntity = new BalanceDeductLogEntity();
        balanceDeductLogEntity.setMemNo(memNo);
        balanceDeductLogEntity.setDeduct(deduct);
        balanceDeductLogEntity.setResult(result);
        mapper.insertSelective(balanceDeductLogEntity);
    }
}
