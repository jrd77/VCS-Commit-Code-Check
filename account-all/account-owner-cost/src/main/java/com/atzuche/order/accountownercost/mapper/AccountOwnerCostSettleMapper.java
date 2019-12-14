package com.atzuche.order.accountownercost.mapper;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 *   车主结算费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:41:37
 */
@Mapper
public interface AccountOwnerCostSettleMapper{

    AccountOwnerCostSettleEntity selectByPrimaryKey(Integer id);

    int insert(AccountOwnerCostSettleEntity record);


}
