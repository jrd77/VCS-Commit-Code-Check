package com.atzuche.order.accountrenterdetain.service.notservice;

import com.atzuche.order.accountrenterdetain.entity.AccountRenterDetainCostEntity;
import com.atzuche.order.accountrenterdetain.exception.AccountRenterDetainDetailException;
import com.atzuche.order.accountrenterdetain.mapper.AccountRenterDetainCostMapper;
import com.atzuche.order.accountrenterdetain.vo.req.DetainRenterDepositReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 暂扣费用总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:51:17
 */
@Service
public class AccountRenterDetainCostNoTService {
    @Autowired
    private AccountRenterDetainCostMapper accountRenterDetainCostMapper;


    /**
     * 查询用户订单暂扣总额
     * @param orderNo
     * @return
     */
    public AccountRenterDetainCostEntity getRenterDetaint(String orderNo) {
        AccountRenterDetainCostEntity accountRenterDetainCostEntity = accountRenterDetainCostMapper.getRenterDetain(orderNo);
        return  accountRenterDetainCostEntity;
    }

    /**
     * 查询用户订单暂扣总额
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getRenterDetainAmt(String orderNo, String memNo) {
        AccountRenterDetainCostEntity accountRenterDetainCostEntity = accountRenterDetainCostMapper.getRenterDetainAmt(orderNo, memNo);
        if(Objects.isNull(accountRenterDetainCostEntity) || Objects.isNull(accountRenterDetainCostEntity.getAmt())){
            return NumberUtils.INTEGER_ZERO;
        }
        return  accountRenterDetainCostEntity.getAmt();
    }

    /**
     * 更新暂扣总额
     * @param detainRenterDeposit
     */
    public void changeRenterDetainCost(DetainRenterDepositReqVO detainRenterDeposit) {
        //1 校验
        AccountRenterDetainCostEntity accountRenterWzDepositCost = accountRenterDetainCostMapper.getRenterDetainAmt(detainRenterDeposit.getOrderNo(),detainRenterDeposit.getMemNo());
        if(Objects.isNull(accountRenterWzDepositCost) || Objects.isNull(accountRenterWzDepositCost.getAmt())){
            throw new AccountRenterDetainDetailException();
        }
        int amt = detainRenterDeposit.getAmt() + accountRenterWzDepositCost.getAmt();
        if(amt<0){
            throw new AccountRenterDetainDetailException();
        }
        // 2 更新违章费用账户余额
        AccountRenterDetainCostEntity accountRenterWzDepositCostEntity = new AccountRenterDetainCostEntity();
        accountRenterWzDepositCostEntity.setVersion(accountRenterWzDepositCost.getVersion());
        accountRenterWzDepositCostEntity.setAmt(amt);
        accountRenterWzDepositCostEntity.setId(accountRenterWzDepositCost.getId());
        int result = accountRenterDetainCostMapper.updateByPrimaryKeySelective(accountRenterWzDepositCostEntity);
        if(result==0){
            throw new AccountRenterDetainDetailException();
        }
    }
}
