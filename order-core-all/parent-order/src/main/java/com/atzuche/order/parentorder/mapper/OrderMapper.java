package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 主订单表
 *
 * @author ZhangBin
 * @date 2019-12-24 16:19:33
 */
@Mapper
public interface OrderMapper {

    /**
     * 依据订单编号获取主订单信息
     *
     * @param orderNo 主订单编码
     * @return OrderEntity 主订单信息
     */
    OrderEntity selectByOrderNo(String orderNo);

    /**
     * 新增订单信息
     *
     * @param record 主订单信息
     * @return int 操作成功记录数
     */
    int insert(OrderEntity record);

    /**
     * 新增订单信息
     *
     * @param record 主订单信息
     * @return int 操作成功记录数
     */
    int insertSelective(OrderEntity record);

    /**
     * 更新订单信息
     *
     * @param record 主订单信息
     * @return int 操作成功记录数
     */
    int updateByPrimaryKey(OrderEntity record);


    /**
     * 更新订单信息
     *
     * @param record 主订单信息
     * @return int 操作成功记录数
     */
    int updateByPrimaryKeySelective(OrderEntity record);

}
