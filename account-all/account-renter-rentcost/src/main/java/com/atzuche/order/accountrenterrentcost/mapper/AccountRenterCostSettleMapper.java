package com.atzuche.order.accountrenterrentcost.mapper;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客费用及其结算总表
 * 
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Mapper
public interface AccountRenterCostSettleMapper{

    AccountRenterCostSettleEntity selectByPrimaryKey(Integer id);


    int insert(AccountRenterCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterCostSettleEntity record);

    AccountRenterCostSettleEntity selectByOrderNo(@Param("orderNo") String orderNo, @Param("memNo")String memNo);
}
