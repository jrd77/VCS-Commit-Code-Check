package com.atzuche.order.rentercost.mapper;

import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租客订单费用总表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:35:56
 */
@Mapper
public interface RenterOrderCostMapper{

    RenterOrderCostEntity selectByPrimaryKey(Integer id);

    List<RenterOrderCostEntity> selectALL();

    int insert(RenterOrderCostEntity record);
    
    int saveRenterOrderCost(RenterOrderCostEntity record);

    int updateByPrimaryKey(RenterOrderCostEntity record);
    
    int updateByPrimaryKeySelective(RenterOrderCostEntity record);


    /**
     * 根据主订单号和租客订单号查询租客费用信息
     *
     * @param orderNo 主订单号
     * @param renterOrderNo 租客订单号
     * @return RenterOrderCostEntity
     */
    RenterOrderCostEntity selectByOrderNoAndRenterNo(@Param("orderNo") String orderNo, @Param("renterOrderNo") String renterOrderNo);

}
