package com.atzuche.order.accountrenterclaim.mapper;

import com.atzuche.order.accountrenterclaim.entity.AccountRenterClaimCostDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 理赔费用资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Mapper
public interface AccountRenterClaimCostDetailMapper{

    AccountRenterClaimCostDetailEntity selectByPrimaryKey(Integer id);

    List<AccountRenterClaimCostDetailEntity> selectALL();

    int insert(AccountRenterClaimCostDetailEntity record);
    
    int insertSelective(AccountRenterClaimCostDetailEntity record);

    int updateByPrimaryKey(AccountRenterClaimCostDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterClaimCostDetailEntity record);

}
