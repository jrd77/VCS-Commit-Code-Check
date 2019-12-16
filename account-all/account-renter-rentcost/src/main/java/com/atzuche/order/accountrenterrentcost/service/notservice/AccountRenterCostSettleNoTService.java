package com.atzuche.order.accountrenterrentcost.service.notservice;

import com.atzuche.order.accountrenterrentcost.exception.AccountRenterRentCostException;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostSettleMapper;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostReqVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * 租客费用及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostSettleNoTService {
    @Autowired
    private AccountRenterCostSettleMapper accountRenterCostSettleMapper;


    /**
     * 租车费用计算总表落库、g更新
     * @param accountRenterCost
     */
    public void insertOrUpdateRenterCostSettle(AccountRenterCostReqVO accountRenterCost) {
        AccountRenterCostSettleEntity accountRenterCostSettle = new AccountRenterCostSettleEntity();
        BeanUtils.copyProperties(accountRenterCost,accountRenterCostSettle);
        LocalDateTime now = LocalDateTime.now();
        accountRenterCostSettle.setCreateTime(now);
        accountRenterCostSettle.setCreateTime(now);
        AccountRenterCostSettleEntity accountRenterCostSettleExit = accountRenterCostSettleMapper.selectByOrderNo(accountRenterCost.getOrderNo(),accountRenterCost.getMemNo());
        int result;
        if(Objects.isNull(accountRenterCostSettleExit) || Objects.isNull(accountRenterCostSettleExit.getId())){
            //不存在插入
            result = accountRenterCostSettleMapper.insert(accountRenterCostSettle);
        }else{
            //存在更新
            result = accountRenterCostSettleMapper.updateByPrimaryKeySelective(accountRenterCostSettle);
        }
        if(result==0){
            throw new AccountRenterRentCostException(ErrorCode.FAILED);
        }
    }
}
