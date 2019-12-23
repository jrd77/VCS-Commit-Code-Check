package com.atzuche.order.accountrenterrentcost.service.notservice;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.exception.AccountRenterRentCostSettleException;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostSettleMapper;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
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
     * 查询订单 已付租车费用
     */
    public int getCostPaidRent(String orderNo,String memNo) {
        AccountRenterCostSettleEntity accountRenterCostSettle = accountRenterCostSettleMapper.selectByOrderNo(orderNo,memNo);
        if(Objects.isNull(accountRenterCostSettle)){
           return NumberUtils.INTEGER_ZERO;
        }
        return accountRenterCostSettle.getShifuAmt()==null?0:accountRenterCostSettle.getShifuAmt();
    }


    /**
     * 租车费用计算总表落库、更新
     * @param accountRenterCost
     */
    public void insertOrUpdateRenterCostSettle(AccountRenterCostReqVO accountRenterCost) {
        AccountRenterCostSettleEntity accountRenterCostSettle = new AccountRenterCostSettleEntity();
        AccountRenterCostSettleEntity accountRenterCostSettleExits = accountRenterCostSettleMapper.selectByOrderNo(accountRenterCost.getOrderNo(),accountRenterCost.getMemNo());
        int result;
        if(Objects.isNull(accountRenterCostSettleExits)){
            BeanUtils.copyProperties(accountRenterCost,accountRenterCostSettle);
            //不存在插入
            result = accountRenterCostSettleMapper.insert(accountRenterCostSettle);
        }else{
            //存在更新
            accountRenterCostSettleExits.setShifuAmt(accountRenterCostSettleExits.getShifuAmt() + accountRenterCost.getShifuAmt());
            result = accountRenterCostSettleMapper.updateByPrimaryKeySelective(accountRenterCostSettleExits);
        }
        if(result==0){
            throw new AccountRenterRentCostSettleException() ;
        }
    }
}
