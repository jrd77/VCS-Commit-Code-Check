package com.atzuche.order.cashieraccount.mapper;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 退款申请表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Mapper
public interface CashierRefundApplyMapper{

    CashierRefundApplyEntity selectByPrimaryKey(Integer id);

    int insertSelective(CashierRefundApplyEntity record);
    
    int updateByPrimaryKeySelective(CashierRefundApplyEntity record);

    CashierRefundApplyEntity selectRefundByQn(@Param("memNo") String menNo, @Param("orderNo")String orderNo, @Param("qn")String qn);

    CashierRefundApplyEntity selectRefundByMd5(@Param("memNo") String memNo, @Param("orderNo")String orderNo, @Param("payMd5")String payMd5);

    Date queryRefundTimeByOrderNo(@Param("orderNo") String orderNo,@Param("payKind") String payKind);


    CashierRefundApplyEntity selectRefundByType(@Param("orderNo")String orderNo,@Param("payKind")String payKind);
    
    
    List<CashierRefundApplyEntity> getCashierRefundApplyByTime(@Param("date")LocalDateTime date);
    List<CashierRefundApplyEntity> getCashierRefundApplyByTimeForPreAuth(@Param("date")LocalDateTime date);
    
    List<CashierRefundApplyEntity> getRefundApplyByOrderNo(@Param("orderNo")String orderNo);
    List<CashierRefundApplyEntity> getRefundApplyByOrderNoPayKind(@Param("orderNo")String orderNo,@Param("payKind")String payKind);
}
