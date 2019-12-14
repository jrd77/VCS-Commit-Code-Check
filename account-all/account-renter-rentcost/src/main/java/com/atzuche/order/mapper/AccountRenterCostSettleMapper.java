package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountRenterCostSettleEntity;
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

    List<AccountRenterCostSettleEntity> selectALL();

    int insert(AccountRenterCostSettleEntity record);
    
    int updateByPrimaryKeySelective(AccountRenterCostSettleEntity record);

    AccountRenterCostSettleEntity selectByOrderNo(@Param("orderNo") Long orderNo, @Param("memNo")Integer memNo);
}
