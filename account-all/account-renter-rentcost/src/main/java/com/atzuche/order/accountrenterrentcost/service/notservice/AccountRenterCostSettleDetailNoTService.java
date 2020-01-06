package com.atzuche.order.accountrenterrentcost.service.notservice;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostSettleDetailMapper;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 租车费用结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostSettleDetailNoTService {
    @Autowired
    private AccountRenterCostSettleDetailMapper accountRenterCostSettleDetailMapper;


    /**
     * 批量插入租客费用明细
     * @param accountRenterCostSettleDetails
     */
    public void insertAccountRenterCostSettleDetails(List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails) {
        if(!CollectionUtils.isEmpty(accountRenterCostSettleDetails)){
            for(int i =0;i<accountRenterCostSettleDetails.size();i++){
                AccountRenterCostSettleDetailEntity entity = accountRenterCostSettleDetails.get(i);
                accountRenterCostSettleDetailMapper.insertSelective(entity);
            }
        }
    }

}
