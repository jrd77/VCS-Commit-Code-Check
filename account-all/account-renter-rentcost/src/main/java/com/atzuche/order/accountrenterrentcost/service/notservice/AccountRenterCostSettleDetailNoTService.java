package com.atzuche.order.accountrenterrentcost.service.notservice;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostSettleDetailMapper;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;


/**
 * 租车费用结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostSettleDetailNoTService {
    @Autowired private AccountRenterCostSettleDetailMapper accountRenterCostSettleDetailMapper;


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

    /**
     * 插入租客费用明细
     * @param accountRenterCostSettleDetail
     */
    public int insertAccountRenterCostSettleDetail(AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail) {
        return accountRenterCostSettleDetailMapper.insertSelective(accountRenterCostSettleDetail);
    }

    /**
     * 根据订单号 和会员号 查询 订单 钱包支付金额
     * @param orderNo
     * @param renterMemNo
     */
    public int getRentCostPayByWallet(String orderNo, String renterMemNo) {
       List<AccountRenterCostSettleDetailEntity> result = accountRenterCostSettleDetailMapper.selectRenterCostSettleDetail(orderNo,renterMemNo, RenterCashCodeEnum.WALLET_DEDUCT.getCashNo());
        if(CollectionUtils.isEmpty(result)){
            return 0;
        }
        return result.stream().mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
    }

    /**
     * 根据订单号查询 租客结算明细列表
     * @param orderNo
     * @return
     */
    public List<AccountRenterCostSettleDetailEntity> getAccountRenterCostSettleDetail(String orderNo) {
        return accountRenterCostSettleDetailMapper.selectRenterCostSettleDetailByOrderNo(orderNo);
    }
}