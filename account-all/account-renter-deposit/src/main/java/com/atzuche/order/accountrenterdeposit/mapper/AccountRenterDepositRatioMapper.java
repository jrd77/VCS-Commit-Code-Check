package com.atzuche.order.accountrenterdeposit.mapper;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositRatioEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车辆押金减免明细表
 * 
 * @author ZhangBin
 * @date 2019-12-20 11:58:57
 */
@Mapper
public interface AccountRenterDepositRatioMapper{

    AccountRenterDepositRatioEntity selectByPrimaryKey(Integer id);

    int insert(AccountRenterDepositRatioEntity record);
    
    int insertSelective(AccountRenterDepositRatioEntity record);

    int updateByPrimaryKey(AccountRenterDepositRatioEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterDepositRatioEntity record);

}
