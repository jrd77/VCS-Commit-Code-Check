package com.atzuche.order.accountrenterrentcost.mapper;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租车费用资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Mapper
public interface AccountRenterCostDetailMapper{

    AccountRenterCostDetailEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountRenterCostDetailEntity record);

    /**
     * 根据订单号查询租车资金明细
     * @param orderNo
     * @return
     */
    List<AccountRenterCostDetailEntity> getAccountRenterCostDetailsByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询支付费用明细
     * @param orderNo
     * @param renterMemNo
     * @param paySourceCode
     * @return
     */
    List<AccountRenterCostDetailEntity> selectRenterCostSettleDetail(@Param("orderNo")String orderNo, @Param("memNo")String renterMemNo, @Param("paySourceCode")String paySourceCode);
}