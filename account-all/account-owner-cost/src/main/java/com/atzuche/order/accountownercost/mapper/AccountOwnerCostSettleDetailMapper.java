package com.atzuche.order.accountownercost.mapper;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主费用结算明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:41:36
 */
@Mapper
public interface AccountOwnerCostSettleDetailMapper{

    AccountOwnerCostSettleDetailEntity selectByPrimaryKey(Integer id);

    List<AccountOwnerCostSettleDetailEntity> selectALL();

    int insert(AccountOwnerCostSettleDetailEntity record);
    
    int insertSelective(AccountOwnerCostSettleDetailEntity record);

    int updateByPrimaryKey(AccountOwnerCostSettleDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountOwnerCostSettleDetailEntity record);

}
