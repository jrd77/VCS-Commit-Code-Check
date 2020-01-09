package com.atzuche.order.accountrenterclaim.mapper;

import com.atzuche.order.accountrenterclaim.entity.AccountRenterClaimCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 理赔费用/及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Mapper
public interface AccountRenterClaimCostSettleMapper{

    AccountRenterClaimCostSettleEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountRenterClaimCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterClaimCostSettleEntity record);

    AccountRenterClaimCostSettleEntity getRenterClaimCostAmt(@Param("orderNo") String orderNo, @Param("memNo")String memNo);
}
