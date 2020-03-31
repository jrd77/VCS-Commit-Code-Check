package com.atzuche.order.accountrenterwzdepost.service.notservice;

import java.util.List;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.exception.PayOrderRenterWZDepositException;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositDetailMapper;
import com.atzuche.order.accountrenterwzdepost.vo.req.DetainRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;


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

    public int insertRenterDepositDetailEntity(AccountRenterWzDepositDetailEntity accountRenterDepositDetailEntity) {
        int result = accountRenterWzDepositDetailMapper.insertSelective(accountRenterDepositDetailEntity);
        if(result==0){
            throw new PayOrderRenterWZDepositException();
        }
        return result;
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


	public int insertRenterDepositDetail(DetainRenterWZDepositReqVO detainRenterDepositReqVO) {
		 AccountRenterWzDepositDetailEntity accountRenterDepositDetailEntity = new AccountRenterWzDepositDetailEntity();
         BeanUtils.copyProperties(detainRenterDepositReqVO,accountRenterDepositDetailEntity);
         accountRenterDepositDetailEntity.setSourceCode(detainRenterDepositReqVO.getRenterCashCodeEnum().getCashNo());
         accountRenterDepositDetailEntity.setSourceDetail(detainRenterDepositReqVO.getRenterCashCodeEnum().getTxt());
         int result = accountRenterWzDepositDetailMapper.insertSelective(accountRenterDepositDetailEntity);
         if(result==0){
            throw new PayOrderRenterWZDepositException();
         }
         return accountRenterDepositDetailEntity.getId();
	}


	public void updateRenterDepositUniqueNo(String uniqueNo, int renterDepositDetailId) {
		AccountRenterWzDepositDetailEntity entity = new AccountRenterWzDepositDetailEntity();
        entity.setId(renterDepositDetailId);
        entity.setUniqueNo(uniqueNo);
        accountRenterWzDepositDetailMapper.updateByPrimaryKeySelective(entity);
		
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
}
