package com.atzuche.order.accountrenterclaim.service.notservice;

import com.atzuche.order.accountrenterclaim.entity.AccountRenterClaimCostDetailEntity;
import com.atzuche.order.accountrenterclaim.exception.AccountRenterClaimException;
import com.atzuche.order.accountrenterclaim.vo.req.AccountRenterClaimDetailReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterclaim.mapper.AccountRenterClaimCostDetailMapper;


/**
 * 理赔费用资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Service
public class AccountRenterClaimCostDetailNoTService {
    @Autowired
    private AccountRenterClaimCostDetailMapper accountRenterClaimCostDetailMapper;


    /**
     * 记录理赔费用账户费用流水
     * @param accountRenterClaimDetail
     */
    public void insertCostDetail(AccountRenterClaimDetailReqVO accountRenterClaimDetail) {
        AccountRenterClaimCostDetailEntity entity = new AccountRenterClaimCostDetailEntity();
        BeanUtils.copyProperties(accountRenterClaimDetail,entity);
        entity.setSourceCode(Integer.parseInt(accountRenterClaimDetail.getRenterCashCodeEnum().getCashNo()));
        entity.setSourceDetail(accountRenterClaimDetail.getRenterCashCodeEnum().getTxt());
        int result = accountRenterClaimCostDetailMapper.insert(entity);
        if(result==0){
            throw new AccountRenterClaimException();
        }
    }
}
