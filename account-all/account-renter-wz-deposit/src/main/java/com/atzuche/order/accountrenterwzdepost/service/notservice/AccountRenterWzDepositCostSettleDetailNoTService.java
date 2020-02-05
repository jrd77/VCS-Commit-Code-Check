package com.atzuche.order.accountrenterwzdepost.service.notservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositCostSettleDetailMapper;


/**
 * 违章费用结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositCostSettleDetailNoTService {
    @Autowired
    private AccountRenterWzDepositCostSettleDetailMapper accountRenterWzDepositCostSettleDetailMapper;

    /**
     * 根据订单号查询 租客违章结算明细列表
     * @param orderNo
     * @return
     */
    public List<AccountRenterWzDepositCostSettleDetailEntity> getAccountRenterWzDepositCostSettleDetail(String orderNo) {
        return accountRenterWzDepositCostSettleDetailMapper.selectByOrderNo(orderNo);
    }

}
