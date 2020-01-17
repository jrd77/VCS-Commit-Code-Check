package com.atzuche.order.accountrenterwzdepost.service.notservice;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.exception.RenterWZDepositCostException;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositCostMapper;
import com.atzuche.order.accountrenterwzdepost.vo.req.RenterWZDepositCostReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 违章费用总表及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositCostNoTService {
    @Autowired
    private AccountRenterWzDepositCostMapper accountRenterWzDepositCostMapper;

    /**
     * 查看实扣违章费用
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getWZDepositCostAmt(String orderNo, String memNo) {
        AccountRenterWzDepositCostEntity result = accountRenterWzDepositCostMapper.getWZDepositCostAmt(orderNo,memNo);
        if(Objects.isNull(result) || Objects.isNull(result.getShifuAmt())){
            return NumberUtils.INTEGER_ZERO;
        }
        return result.getShifuAmt();
    }
    /**
     * 违章费用资金进出
     */
    public void changeWZDepositCost(RenterWZDepositCostReqVO renterWZDepositCost) {
        //1 校验
        AccountRenterWzDepositCostEntity accountRenterWzDepositCost = accountRenterWzDepositCostMapper.getWZDepositCostAmt(renterWZDepositCost.getOrderNo(),renterWZDepositCost.getMemNo());
        if(Objects.isNull(accountRenterWzDepositCost) || Objects.isNull(accountRenterWzDepositCost.getShifuAmt())){
            throw new RenterWZDepositCostException();
        }
        int amt = renterWZDepositCost.getAmt() + accountRenterWzDepositCost.getShifuAmt();
        if(amt<0){
            throw new RenterWZDepositCostException();
        }
        // 2 更新违章费用账户余额
        AccountRenterWzDepositCostEntity accountRenterWzDepositCostEntity = new AccountRenterWzDepositCostEntity();
        accountRenterWzDepositCostEntity.setVersion(accountRenterWzDepositCost.getVersion());
        accountRenterWzDepositCostEntity.setShifuAmt(amt);
        accountRenterWzDepositCostEntity.setId(accountRenterWzDepositCost.getId());
        int result = accountRenterWzDepositCostMapper.updateByPrimaryKeySelective(accountRenterWzDepositCostEntity);
        if(result==0){
            throw new RenterWZDepositCostException();
        }

    }

    /**
     * 查询违章押金
     * @param orderNo
     */
    public AccountRenterWzDepositCostEntity queryWzDeposit(String orderNo) {
        AccountRenterWzDepositCostEntity entity = accountRenterWzDepositCostMapper.selectByOrderNo(orderNo);
        return entity;
    }
}
