package com.atzuche.order.ownercost.mapper;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.atzuche.order.ownercost.entity.*;

import java.util.List;

/**
 * 车主订单子表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
 */
@Mapper
public interface OwnerOrderMapper{

    OwnerOrderEntity selectByPrimaryKey(Integer id);

    int insert(OwnerOrderEntity record);
    
    int insertSelective(OwnerOrderEntity record);

    int updateByPrimaryKey(OwnerOrderEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderEntity record);
    
    OwnerOrderEntity getOwnerOrderByOrderNoAndIsEffective(@Param("orderNo") String orderNo);
    
    Integer updateOwnerOrderInvalidById(@Param("id") Integer id);

    Integer updateOwnerOrderChildStatus(@Param("id") Integer id, @Param("childStatus") Integer childStatus);

    Integer updateChildStatusByOrderNo(@Param("orderNo") String orderNo, @Param("childStatus") Integer childStatus);

    OwnerOrderEntity getChangeOwnerByOrderNo(@Param("orderNo") String orderNo);

    List<OwnerOrderEntity> queryHostiryOwnerOrderByOrderNo(@Param("orderNo")String orderNo);


	OwnerOrderEntity getOwnerOrderByOwnerOrderNo(@Param("ownerOrderNo")String ownerOrderNo);

    OwnerOrderEntity queryCancelOwnerOrderByOrderNoIsEffective(@Param("orderNo")String orderNo);

    Integer updateDispatchReasonByOrderNo(@Param("orderNo") String orderNo, @Param("dispatchReason") String dispatchReason);

    OwnerOrderEntity getOwnerByMemNoAndOrderNo(@Param("orderNo")String orderNo, @Param("ownerMemNo") String ownerMemNo);

    int updateByMemeNo(@Param("ownerMemNo") String ownerMemNo);


    List<OwnerOrderEntity> queryByOwnerOrderNoAndMemNo(@Param("orderNo") String orderNo, @Param("ownerMemNo") String ownerMemNo);
}
