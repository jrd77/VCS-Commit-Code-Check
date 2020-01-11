package com.atzuche.order.accountrenterdeposit.mapper;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租车押金资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-17 17:09:45
 */
@Mapper
public interface AccountRenterDepositDetailMapper{

    AccountRenterDepositDetailEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountRenterDepositDetailEntity record);

    int updateByPrimaryKeySelective(AccountRenterDepositDetailEntity record);


    /**
     * 查询某个订单的押金支付流水情况
     * @param orderNo
     * @return
     */
    List<AccountRenterDepositDetailEntity> findByOrderNo(@Param("orderNo")String orderNo);
    
}
