package com.atzuche.order.accountownercost.mapper;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 *   车主结算费用总表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:34:08
 */
@Mapper
public interface AccountOwnerCostSettleMapper{

    AccountOwnerCostSettleEntity selectByPrimaryKey(Integer id);

    int insert(AccountOwnerCostSettleEntity record);
    
    int insertSelective(AccountOwnerCostSettleEntity record);

    int updateByPrimaryKey(AccountOwnerCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountOwnerCostSettleEntity record);

}
