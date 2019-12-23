package com.atzuche.order.accountrenterdeposit.service.notservice;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositRatioEntity;
import com.atzuche.order.accountrenterdeposit.exception.AccountRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositRatioMapper;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterReductionDepositReqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 车辆押金减免明细表
 *
 * @author ZhangBin
 * @date 2019-12-20 11:58:57
 */
@Service
public class AccountRenterDepositRatioNotService {
    @Autowired private AccountRenterDepositRatioMapper accountRenterDepositRatioMapper;


    /**
     * 记录减免押金 详情
     * @param createOrderRenterReductionDeposits
     */
    public void insertReductDeposit(List<CreateOrderRenterReductionDepositReqVO> createOrderRenterReductionDeposits) {
        if(!CollectionUtils.isEmpty(createOrderRenterReductionDeposits)){
           for(int i=0;i<createOrderRenterReductionDeposits.size();i++){
               AccountRenterDepositRatioEntity entity = new AccountRenterDepositRatioEntity();
               BeanUtils.copyProperties(createOrderRenterReductionDeposits,entity);
               int result = accountRenterDepositRatioMapper.insert(entity);
               if(result==0){
                   throw new AccountRenterDepositDBException();
               }
           }
        }
    }
}
