package com.atzuche.violation.service;


import com.atzuche.violation.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.violation.enums.RenterCashCodeEnum;
import com.atzuche.violation.mapper.AccountRenterWzDepositDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 违章押金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositDetailNoTService {
    @Autowired
    private AccountRenterWzDepositDetailMapper accountRenterWzDepositDetailMapper;


    /**
     * 返回指定订单的违章押金的支付流水记录
     * @param orderNo
     * @return
     */
    public List<AccountRenterWzDepositDetailEntity> findByOrderNo(String orderNo){
        return accountRenterWzDepositDetailMapper.findByOrderNo(orderNo);
    }

    /**
     * 返回指定订单的违章押金的支付流水记录
     * @param orderNo
     * @return
     */
    public AccountRenterWzDepositDetailEntity findByOrderNoAndCode(String orderNo){
        AccountRenterWzDepositDetailEntity accountRenterWzDepositDetailEntity = new AccountRenterWzDepositDetailEntity();
        accountRenterWzDepositDetailEntity.setOrderNo(orderNo);
        accountRenterWzDepositDetailEntity.setCostCode(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT.getCashNo());
        return accountRenterWzDepositDetailMapper.findByOrderNoAndCode(accountRenterWzDepositDetailEntity);
    }



    /**
     * 查询剩余 违章押金 金额
     * @param orderNo
     * @param renterMemNo
     * @return
     */
    public int getSurplusWZDepositCostAmt(String orderNo, String renterMemNo) {
        List<AccountRenterWzDepositDetailEntity> list = findByOrderNo(orderNo);
        if(CollectionUtils.isEmpty(list)){
            return 0;
        }
       return list.stream().mapToInt(AccountRenterWzDepositDetailEntity::getAmt).sum();
    }





	public void updateRenterDepositUniqueNo(String uniqueNo, int renterDepositDetailId) {
		AccountRenterWzDepositDetailEntity entity = new AccountRenterWzDepositDetailEntity();
        entity.setId(renterDepositDetailId);
        entity.setUniqueNo(uniqueNo);
        accountRenterWzDepositDetailMapper.updateByPrimaryKeySelective(entity);
		
	}
    
}
