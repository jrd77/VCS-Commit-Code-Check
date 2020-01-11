package com.atzuche.order.accountrenterwzdepost.service.notservice;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.exception.PayOrderRenterWZDepositException;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositDetailMapper;

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
     * 新增违章押金 流水记录
     * @param payedOrderRenterWZDepositDetailReqVO
     */
    public int insertRenterWZDepositDetail(PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetailReqVO) {
        AccountRenterWzDepositDetailEntity accountRenterDepositDetailEntity = new AccountRenterWzDepositDetailEntity();
        BeanUtils.copyProperties(payedOrderRenterWZDepositDetailReqVO,accountRenterDepositDetailEntity);
        accountRenterDepositDetailEntity.setCostCode(payedOrderRenterWZDepositDetailReqVO.getRenterCashCodeEnum().getCashNo());
        accountRenterDepositDetailEntity.setCostDetail(payedOrderRenterWZDepositDetailReqVO.getRenterCashCodeEnum().getTxt());
        accountRenterDepositDetailEntity.setSourceCode(payedOrderRenterWZDepositDetailReqVO.getRenterCashCodeEnum().getCashNo());
        accountRenterDepositDetailEntity.setSourceDetail(payedOrderRenterWZDepositDetailReqVO.getRenterCashCodeEnum().getTxt());
        int result = accountRenterWzDepositDetailMapper.insertSelective(accountRenterDepositDetailEntity);
        if(result==0){
            throw new PayOrderRenterWZDepositException();
        }
        return accountRenterDepositDetailEntity.getId();
    }


    /**
     * 返回指定订单的违章押金的支付流水记录
     * @param orderNo
     * @return
     */
    public List<AccountRenterWzDepositDetailEntity> findByOrderNo(String orderNo){
        return accountRenterWzDepositDetailMapper.findByOrderNo(orderNo);
    }
}
