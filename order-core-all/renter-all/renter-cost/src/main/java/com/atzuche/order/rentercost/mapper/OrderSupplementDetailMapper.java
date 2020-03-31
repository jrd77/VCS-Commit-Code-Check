package com.atzuche.order.rentercost.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;

/**
 * 订单补付表
 * 
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Mapper
public interface OrderSupplementDetailMapper{

    OrderSupplementDetailEntity selectByPrimaryKey(Integer id);

    int insert(OrderSupplementDetailEntity record);
    
    int insertSelective(OrderSupplementDetailEntity record);

    int updateByPrimaryKey(OrderSupplementDetailEntity record);
    
    int updateByPrimaryKeySelective(OrderSupplementDetailEntity record);
    
    List<OrderSupplementDetailEntity> listOrderSupplementDetailByOrderNoAndMemNo(@Param("orderNo") String orderNo,@Param("memNo") String memNo);
    
    Integer updatePayFlagById(@Param("id") Integer id, @Param("payFlag") Integer payFlag, @Param("payTime") Date payTime,@Param("amt") Integer amt);
    Integer updatePayFlagNewById(@Param("id") Integer id, @Param("payFlag") Integer payFlag, @Param("payTime") Date payTime);
    
    List<OrderSupplementDetailEntity> listOrderSupplementDetailByOrderNo(@Param("orderNo") String orderNo);
    
    Integer updateDeleteById(@Param("id") Integer id);


    /**
     * 获取未支付的补付信息
     *
     * @param orderNo 订单号
     * @return List<OrderSupplementDetailEntity> 补付记录
     */
    List<OrderSupplementDetailEntity> selectNotPayByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 更新补付记录操作状态
     *
     * @param id       主键
     * @param opStatus 操作状态
     * @return int 成功记录数
     */
    int updateOpStatusByPrimaryKey(@Param("id") Integer id, @Param("opStatus") Integer opStatus);


	List<OrderSupplementDetailEntity> listOrderSupplementDetailByMemNo(String memNo);
	
	List<OrderSupplementDetailEntity> listOrderSupplementDetailByMemNoAndOrderNos(@Param("memNo") String memNo, @Param("orderNoList") List<String> orderNoList);
	
}
