package com.atzuche.order.accountrenterdetain.service.notservice;

import com.atzuche.order.accountrenterdetain.entity.AccountRenterDetainCostEntity;
import com.atzuche.order.accountrenterdetain.exception.AccountRenterDetainDetailException;
import com.atzuche.order.accountrenterdetain.mapper.AccountRenterDetainCostMapper;
import com.atzuche.order.accountrenterdetain.vo.req.ChangeDetainRenterDepositReqVO;
import com.atzuche.order.commons.constant.OrderConstant;
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
     *
     * @param detainRenterDeposit 暂扣信息
     */
    public void changeRenterDetainCost(ChangeDetainRenterDepositReqVO detainRenterDeposit) {
        if (detainRenterDeposit.getAmt() == 0) {
            throw new AccountRenterDetainDetailException();
        }
        AccountRenterDetainCostEntity accountRenterWzDepositCost = accountRenterDetainCostMapper.getRenterDetainAmt(detainRenterDeposit.getOrderNo(), detainRenterDeposit.getMemNo());
        AccountRenterDetainCostEntity accountRenterDetainCostEntity = new AccountRenterDetainCostEntity();
        if (null == accountRenterWzDepositCost) {
            accountRenterDetainCostEntity.setVersion(OrderConstant.ZERO);
            accountRenterDetainCostEntity.setAmt(detainRenterDeposit.getAmt());
            accountRenterDetainCostEntity.setOrderNo(detainRenterDeposit.getOrderNo());
            accountRenterDetainCostEntity.setMemNo(detainRenterDeposit.getMemNo());
            accountRenterDetainCostMapper.insertSelective(accountRenterDetainCostEntity);
        } else {
            accountRenterDetainCostEntity.setVersion(accountRenterWzDepositCost.getVersion());
            accountRenterDetainCostEntity.setAmt(detainRenterDeposit.getAmt() + accountRenterWzDepositCost.getAmt());
            accountRenterDetainCostEntity.setId(accountRenterWzDepositCost.getId());
            accountRenterDetainCostMapper.updateByPrimaryKeySelective(accountRenterDetainCostEntity);
        }
    }
}
