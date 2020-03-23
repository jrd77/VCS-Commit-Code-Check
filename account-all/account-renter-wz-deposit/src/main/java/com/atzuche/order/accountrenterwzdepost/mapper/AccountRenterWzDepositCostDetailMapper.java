package com.atzuche.order.accountrenterwzdepost.mapper;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 违章费用资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Mapper
public interface AccountRenterWzDepositCostDetailMapper{

    AccountRenterWzDepositCostDetailEntity selectByPrimaryKey(Integer id);

    List<AccountRenterWzDepositCostDetailEntity> selectALL();

    int insertSelective(AccountRenterWzDepositCostDetailEntity record);

    int updateByPrimaryKey(AccountRenterWzDepositCostDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterWzDepositCostDetailEntity record);


}
