package com.atzuche.order.accountownercost.mapper;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    int insertSelective(AccountOwnerCostSettleDetailEntity record);

    List<AccountOwnerCostSettleDetailEntity> getAccountOwnerCostSettleDetails(@Param("orderNo") String orderNo,@Param("memNo")String memNo);
    
    List<AccountOwnerCostSettleDetailEntity> listOwnerSettleCostBySourceCode(@Param("orderNoList") List<String> orderNoList,@Param("memNo") String memNo,@Param("sourceCode") String sourceCode);

    List<AccountOwnerCostSettleDetailEntity> getAccountOwnerCostSettleDetailsByOrderNo(String orderNo);
}
