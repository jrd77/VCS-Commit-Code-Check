package com.atzuche.order.accountrenterrentcost.service.notservice;

import com.atzuche.order.accountrenterrentcost.exception.AccountRenterRentCostDetailException;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostDetailMapper;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;



/**
 * 租车费用资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostDetailNoTService {
    @Autowired
    private AccountRenterCostDetailMapper accountRenterCostDetailMapper;


    /**
     * 费用明细落库
     * @param accountRenterCostDetailReqVO
     */
    public void insertAccountRenterCostDetail(AccountRenterCostDetailReqVO accountRenterCostDetailReqVO) {
        AccountRenterCostDetailEntity accountRenterCostDetail = new AccountRenterCostDetailEntity();
        BeanUtils.copyProperties(accountRenterCostDetailReqVO,accountRenterCostDetail);
        int result = accountRenterCostDetailMapper.insert(accountRenterCostDetail);
        if(result==0){
            throw new AccountRenterRentCostDetailException();
        }
    }
}
