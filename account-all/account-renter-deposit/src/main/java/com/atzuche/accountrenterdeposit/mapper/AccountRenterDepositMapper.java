package com.atzuche.accountrenterdeposit.mapper;

import com.atzuche.accountrenterdeposit.entity.AccountRenterDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租车押金状态及其总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:48:32
 */
@Mapper
public interface AccountRenterDepositMapper{

    AccountRenterDepositEntity selectByPrimaryKey(Integer id);

    List<AccountRenterDepositEntity> selectALL();

    int insert(AccountRenterDepositEntity record);
    
    int insertSelective(AccountRenterDepositEntity record);

    int updateByPrimaryKey(AccountRenterDepositEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterDepositEntity record);

}
