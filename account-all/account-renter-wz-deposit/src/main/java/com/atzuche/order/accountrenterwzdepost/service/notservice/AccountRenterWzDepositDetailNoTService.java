package com.atzuche.order.accountrenterwzdepost.service.notservice;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.exception.PayOrderRenterWZDepositException;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositDetailMapper;


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
     * 新增违章押金 流水记录
     * @param payedOrderRenterWZDepositDetailReqVO
     */
    public void insertRenterWZDepositDetail(PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetailReqVO) {
        AccountRenterWzDepositDetailEntity accountRenterDepositDetailEntity = new AccountRenterWzDepositDetailEntity();
        BeanUtils.copyProperties(payedOrderRenterWZDepositDetailReqVO,accountRenterDepositDetailEntity);
        int result = accountRenterWzDepositDetailMapper.insert(accountRenterDepositDetailEntity);
        if(result==0){
            throw new PayOrderRenterWZDepositException();
        }

    }
}
