package com.atzuche.accountrenterdeposit.mapper;

import com.atzuche.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 租车押金资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:48:32
 */
@Mapper
public interface AccountRenterDepositDetailMapper{

    AccountRenterDepositDetailEntity selectByPrimaryKey(Integer id);

    List<AccountRenterDepositDetailEntity> selectALL();

    int insert(AccountRenterDepositDetailEntity record);
    
    int insertSelective(AccountRenterDepositDetailEntity record);

    int updateByPrimaryKey(AccountRenterDepositDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterDepositDetailEntity record);

}
