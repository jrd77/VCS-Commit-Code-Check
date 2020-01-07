package com.atzuche.order.accountrenterdeposit.service.notservice;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import com.atzuche.order.accountrenterdeposit.exception.PayOrderRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositDetailMapper;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


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
     * @param detainRenterDeposit
     */
    public int insertRenterDepositDetail(DetainRenterDepositReqVO detainRenterDeposit) {
        AccountRenterDepositDetailEntity accountRenterDepositDetailEntity = new AccountRenterDepositDetailEntity();
        BeanUtils.copyProperties(detainRenterDeposit,accountRenterDepositDetailEntity);
        accountRenterDepositDetailEntity.setSourceCode(detainRenterDeposit.getRenterCashCodeEnum().getCashNo());
        accountRenterDepositDetailEntity.setSourceDetail(detainRenterDeposit.getRenterCashCodeEnum().getCashNo());
        int result = accountRenterDepositDetailMapper.insert(accountRenterDepositDetailEntity);
        if(result==0){
            throw new PayOrderRenterDepositDBException();
        }
        return accountRenterDepositDetailEntity.getId();
    }

    public void updateRenterDepositUniqueNo(String uniqueNo, int renterDepositDetailId) {
        AccountRenterDepositDetailEntity entity = new AccountRenterDepositDetailEntity();
        entity.setId(renterDepositDetailId);
        entity.setUniqueNo(uniqueNo);
        accountRenterDepositDetailMapper.updateByPrimaryKeySelective(entity);

    }

    /**
     * 返回押金的支付流水
     * @param orderNo
     * @return
     */
    public List<AccountRenterDepositDetailEntity> findByOrderNo(String orderNo){
        return accountRenterDepositDetailMapper.findByOrderNo(orderNo);
    }
}
