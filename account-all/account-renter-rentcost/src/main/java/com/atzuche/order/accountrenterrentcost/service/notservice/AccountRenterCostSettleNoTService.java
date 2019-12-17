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
        List<AccountRenterCostSettleEntity> accountRenterCostSettles = accountRenterCostSettleMapper.selectByOrderNo(orderNo,memNo);
        if(CollectionUtils.isEmpty(accountRenterCostSettles)){
           return NumberUtils.INTEGER_ZERO;
        }
        int result = accountRenterCostSettles.stream().mapToInt(AccountRenterCostSettleEntity::getRentAmt).sum();
        return result;
    }


    /**
     * 租车费用计算总表落库、更新
     * @param accountRenterCost
     */
    public void insertOrUpdateRenterCostSettle(AccountRenterCostReqVO accountRenterCost) {
        AccountRenterCostSettleEntity accountRenterCostSettle = new AccountRenterCostSettleEntity();
        BeanUtils.copyProperties(accountRenterCost,accountRenterCostSettle);
        List<AccountRenterCostSettleEntity> accountRenterCostSettleExits = accountRenterCostSettleMapper.selectByOrderNo(accountRenterCost.getOrderNo(),accountRenterCost.getMemNo());
        int result;
        if(CollectionUtils.isEmpty(accountRenterCostSettleExits)){
            //不存在插入
            result = accountRenterCostSettleMapper.insert(accountRenterCostSettle);
        }else{
            //存在更新
            result = accountRenterCostSettleMapper.updateByPrimaryKeySelective(accountRenterCostSettle);
        }
        if(result==0){
            throw new AccountRenterRentCostSettleException() ;
        }
    }
}
