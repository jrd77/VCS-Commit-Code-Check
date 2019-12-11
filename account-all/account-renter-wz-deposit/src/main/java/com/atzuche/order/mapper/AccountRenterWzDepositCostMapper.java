package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterWzDepositCostEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 违章费用总表及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Mapper
public interface AccountRenterWzDepositCostMapper{

    AccountRenterWzDepositCostEntity selectByPrimaryKey(Integer id);

    List<AccountRenterWzDepositCostEntity> selectALL();

    int insert(AccountRenterWzDepositCostEntity record);
    
    int insertSelective(AccountRenterWzDepositCostEntity record);

    int updateByPrimaryKey(AccountRenterWzDepositCostEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterWzDepositCostEntity record);

}
