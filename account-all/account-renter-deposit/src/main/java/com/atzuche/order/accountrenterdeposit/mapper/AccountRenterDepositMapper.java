package com.atzuche.order.accountrenterdeposit.mapper;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租车押金状态及其总表
 * 
 * @author ZhangBin
 * @date 2019-12-17 17:09:45
 */
@Mapper
public interface AccountRenterDepositMapper{

    AccountRenterDepositEntity selectByPrimaryKey(Integer id);

    int insert(AccountRenterDepositEntity record);
    
    int insertSelective(AccountRenterDepositEntity record);

    int updateByPrimaryKey(AccountRenterDepositEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterDepositEntity record);

}
