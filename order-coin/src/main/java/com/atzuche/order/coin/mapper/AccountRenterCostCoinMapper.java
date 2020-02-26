package com.atzuche.order.coin.mapper;

import com.atzuche.order.coin.entity.AccountRenterCostCoinEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客订单使凹凸币流水
 * 
 * @author ZhangBin
 * @date 2020-01-07 20:23:33
 */
@Mapper
public interface AccountRenterCostCoinMapper{

    AccountRenterCostCoinEntity selectByPrimaryKey(Integer id);

    int insert(AccountRenterCostCoinEntity record);

    List<AccountRenterCostCoinEntity> getAccountRenterCostCoins(@Param("orderNo") String orderNo, @Param("memNo") String memNo);

    /**
     * 获取某个订单的凹凸币使用的总量
     * @param orderNo
     * @return
     */
    int getAccountRenterCostCoinTotal(@Param("orderNo") String orderNo);
}
