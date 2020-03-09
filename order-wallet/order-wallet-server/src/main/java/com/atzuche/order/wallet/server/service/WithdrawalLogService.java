package com.atzuche.order.wallet.server.service;

import com.atzuche.order.wallet.server.entity.WithdrawalLogEntity;
import com.atzuche.order.wallet.server.mapper.WithdrawalLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 9:34 上午
 **/
@Service
public class WithdrawalLogService {
    @Autowired
    private WithdrawalLogMapper withdrawalLogMapper;

    public List<WithdrawalLogEntity> findByMemNo(String memNo){
        //TODO:
        return null;
    }
}
