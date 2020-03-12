package com.atzuche.order.accountrenterwzdepost.service.notservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    
    /**
     * 落库
     * @param accountRenterWzDepositCostSettleDetails
     */
	public void insertAccountRenterWzDepositCostSettleDetails(
			List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetails) {
		
		if(!CollectionUtils.isEmpty(accountRenterWzDepositCostSettleDetails)){
            for(int i =0;i<accountRenterWzDepositCostSettleDetails.size();i++){
            	AccountRenterWzDepositCostSettleDetailEntity entity = accountRenterWzDepositCostSettleDetails.get(i);
            	accountRenterWzDepositCostSettleDetailMapper.insertSelective(entity);
            }
        }
		
	}

    /**
     * 新增违章费用结算信息
     *
     * @param record 违章费用结算信息
     * @return 成功记录数
     */
	public int insertAccountRenterWzDepositCostSettleDetail(AccountRenterWzDepositCostSettleDetailEntity record) {
	    return accountRenterWzDepositCostSettleDetailMapper.insertSelective(record);
    }

}
