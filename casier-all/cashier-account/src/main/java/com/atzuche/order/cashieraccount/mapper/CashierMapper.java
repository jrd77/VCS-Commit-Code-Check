package com.atzuche.order.cashieraccount.mapper;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收银表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Mapper
public interface CashierMapper{

    CashierEntity selectByPrimaryKey(Integer id);

    int insert(CashierEntity record);
    
    int updateByPrimaryKeySelective(CashierEntity record);

    /**
     * 查询收银台应付金额
     * @param orderNo
     * @param memNo
     * @param payKind (押金、违章押金)
     * @return
     */
    CashierEntity getPayDeposit(@Param("orderNo") String orderNo, @Param("memNo")String memNo, @Param("payKind")String payKind,@Param("payType")String payType);
}
