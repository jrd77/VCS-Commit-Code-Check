package com.atzuche.order.cashieraccount.mapper;

import com.atzuche.order.cashieraccount.entity.OfflineRefundApplyEntity;
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
public interface OfflineRefundApplyMapper{

    OfflineRefundApplyEntity selectByPrimaryKey(Integer id);

    int insertSelective(OfflineRefundApplyEntity record);
    
    int updateByPrimaryKeySelective(OfflineRefundApplyEntity record);

    OfflineRefundApplyEntity selectRefundByQn(@Param("memNo") String menNo, @Param("orderNo")String orderNo, @Param("qn")String qn);

    OfflineRefundApplyEntity selectRefundByMd5(@Param("memNo") String memNo, @Param("orderNo")String orderNo, @Param("payMd5")String payMd5);

    Date queryRefundTimeByOrderNo(@Param("orderNo") String orderNo,@Param("payKind") String payKind);


    OfflineRefundApplyEntity selectRefundByType(@Param("orderNo")String orderNo,@Param("payKind")String payKind);

    List<OfflineRefundApplyEntity> getCashierRefundApplyByTime(@Param("date")LocalDateTime date);

    List<OfflineRefundApplyEntity> getRefundApplyByOrderNo(@Param("orderNo")String orderNo);
}
