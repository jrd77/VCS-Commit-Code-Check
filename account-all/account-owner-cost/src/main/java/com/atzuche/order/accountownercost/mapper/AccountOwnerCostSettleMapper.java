package com.atzuche.order.accountownercost.mapper;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *   车主结算费用总表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:34:08
 */
@Mapper
public interface AccountOwnerCostSettleMapper{

    AccountOwnerCostSettleEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountOwnerCostSettleEntity record);

    int updateByPrimaryKey(AccountOwnerCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountOwnerCostSettleEntity record);

    AccountOwnerCostSettleEntity getsettleAmtByOrderNo(@Param("orderNo")String orderNo, @Param("ownerOrderNo")String ownerOrderNo);
}
