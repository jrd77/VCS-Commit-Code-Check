package com.atzuche.order.accountrenterwzdepost.service.notservice;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostDetailEntity;
import com.atzuche.order.accountrenterwzdepost.exception.RenterWZDepositCostException;
import com.atzuche.order.accountrenterwzdepost.vo.req.RenterWZDepositCostReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositCostDetailMapper;


/**
 * 违章费用资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositCostDetailNoTService {
    @Autowired
    private AccountRenterWzDepositCostDetailMapper accountRenterWzDepositCostDetailMapper;


    /**
     * 记录流水
     * @param renterWZDepositCost
     */
    public void insertCostDetail(RenterWZDepositCostReqVO renterWZDepositCost) {
        AccountRenterWzDepositCostDetailEntity entity = new AccountRenterWzDepositCostDetailEntity();
        BeanUtils.copyProperties(renterWZDepositCost,entity);
        entity.setSourceCode(Integer.parseInt(renterWZDepositCost.getRenterCashCodeEnum().getCashNo()));
        entity.setSourceDetail(renterWZDepositCost.getRenterCashCodeEnum().getTxt());
        int result = accountRenterWzDepositCostDetailMapper.insertSelective(entity);
        if(result==0){
            throw new RenterWZDepositCostException();
        }

    }
}
