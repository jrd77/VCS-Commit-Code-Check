package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderCancelAppealEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author ZhangBin
 * @date 2020-03-02 11:10:10
 */
@Mapper
public interface OrderCancelAppealMapper{

    /**
     * 查询申述信息依据主键
     *
     * @param id 主键
     * @return 申述信息
     */
    OrderCancelAppealEntity selectByPrimaryKey(Integer id);

    /**
     * 查询申述信息依据订单号
     *
     * @param orderNo 订单号
     * @return 申述信息
     */
    OrderCancelAppealEntity selectByOrderNo(String orderNo);

    /**
     * 新增申述信息依据主键
     *
     * @param record 申述信息
     * @return 成功条数
     */
    int insert(OrderCancelAppealEntity record);

    /**
     * 新增申述信息依据主键
     *
     * @param record 申述信息
     * @return 成功条数
     */
    int insertSelective(OrderCancelAppealEntity record);

    /**
     * 更新申述信息依据主键
     *
     * @param record 申述信息
     * @return 成功条数
     */
    int updateByPrimaryKey(OrderCancelAppealEntity record);

    /**
     * 更新申述信息依据主键
     *
     * @param record 申述信息
     * @return 成功条数
     */
    int updateByPrimaryKeySelective(OrderCancelAppealEntity record);

}
