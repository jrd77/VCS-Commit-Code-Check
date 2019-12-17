package com.atzuche.order.accountrenterdeposit.service.notservice;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import com.atzuche.order.accountrenterdeposit.exception.PayOrderRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositDetailMapper;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositDetailReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 租车押金资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-17 17:09:45
 */
@Service
public class AccountRenterDepositDetailNoTService {
    @Autowired
    private AccountRenterDepositDetailMapper accountRenterDepositDetailMapper;


    /**
     * 新增车辆押金 流水记录
     * @param payedOrderRenterDepositDetailReqVO
     */
    public void insertRenterDepositDetail(PayedOrderRenterDepositDetailReqVO payedOrderRenterDepositDetailReqVO) {
        AccountRenterDepositDetailEntity accountRenterDepositDetailEntity = new AccountRenterDepositDetailEntity();
        BeanUtils.copyProperties(payedOrderRenterDepositDetailReqVO,accountRenterDepositDetailEntity);
        int result = accountRenterDepositDetailMapper.insert(accountRenterDepositDetailEntity);
        if(result==0){
            throw new PayOrderRenterDepositDBException();
        }
    }
}
