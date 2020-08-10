package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 主订单表状态
 * 
 * @author ZhangBin
 * @date 2019-12-24 16:19:32
 */
@Mapper
public interface OrderStatusMapper{

    OrderStatusEntity selectByOrderNo(@Param("orderNo") String orderNo);

    int insert(OrderStatusEntity record);
    
    int insertSelective(OrderStatusEntity record);

    int updateByPrimaryKey(OrderStatusEntity record);
    
    int updateByPrimaryKeySelective(OrderStatusEntity record);

    List<String> queryOrderNoByStartTimeAndEndTime(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    int updateRenterOrderByOrderNo(OrderStatusEntity orderStatusEntity);

    Integer getStatusByOrderNo(@Param("orderNo") String orderNo);
    
    Integer updateDispatchStatus(@Param("orderNo") String orderNo, @Param("dispatchStatus") Integer dispatchStatus, @Param("isDispatch") Integer isDispatch);
    
    Integer updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);

    List<OrderStatusEntity> queryInProcess();

    List<OrderStatusEntity> queryByStatus(@Param("statusList")List<Integer> statusList);

    OrderStatusEntity getOrderStatusForUpdate(@Param("orderNo") String orderNo);

    /**
     * 查询符合 wzSettleStatus 的订单状态信息
     *
     * @param wzSettleStatus 违章结算状态
     * @return List<OrderStatusEntity>
     */
    List<OrderStatusEntity> selectByWzSettleStatus(@Param("wzSettleStatus") int wzSettleStatus);

}
