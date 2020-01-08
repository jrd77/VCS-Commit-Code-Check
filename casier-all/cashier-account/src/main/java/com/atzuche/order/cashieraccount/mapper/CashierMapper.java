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
     * @param payKind (押金、违章押金、租车费用)
     * @return
     */
    CashierEntity getPayDeposit(@Param("orderNo") String orderNo, @Param("memNo")String memNo, @Param("payKind")String payKind,@Param("payType")String payType);

    /**
     * 查询收银台应付金额
     * @param orderNo
     * @param memNo
     * @param payKind (押金、违章押金、租车费用)
     * @return
     */
    CashierEntity getPayDetail(@Param("orderNo") String orderNo, @Param("memNo")String memNo, @Param("payKind")String payKind,@Param("payType")String payType,@Param("paySource")String paySource);

    /**
     * 查询用户订单 租车费用支付总条数
     * @param orderNo
     * @param menNo
     * @param payKind
     * @param payType
     * @return
     */
    List<CashierEntity> getCashierRentCosts(@Param("orderNo")String orderNo, @Param("memNo")String menNo, @Param("payKind")String payKind, @Param("payType")String payType);
}
