package com.atzuche.order.parentorder.mapper;

import com.atzuche.order.parentorder.entity.OrderRefundRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单取消退款延时记录库操作
 * 
 * @author pengcheng.fu
 * @date 2020-03-16 13:59:58
 */
@Mapper
public interface OrderRefundRecordMapper{

    /**
     * 根据主键获取数据
     *
     * @param id 主键
     * @return OrderRefundRecordEntity
     */
    OrderRefundRecordEntity selectByPrimaryKey(Integer id);

    /**
     * 根据主键订单号获取数据
     *
     * @param orderNo 订单号
     * @return OrderRefundRecordEntity
     */
    OrderRefundRecordEntity selectByOrderNo(String orderNo);

    /**
     * 无过滤新增数据
     *
     * @param record 数据
     * @return int 成功条数
     */
    int insert(OrderRefundRecordEntity record);

    /**
     * 有过滤新增数据
     *
     * @param record 数据
     * @return int 成功条数
     */
    int insertSelective(OrderRefundRecordEntity record);

    /**
     * 无过滤根据主键跟新数据
     *
     * @param record 更新数据
     * @return int 成功条数
     */
    int updateByPrimaryKey(OrderRefundRecordEntity record);

    /**
     * 有过滤根据主键跟新数据
     *
     * @param record 更新数据
     * @return int 成功条数
     */
    int updateByPrimaryKeySelective(OrderRefundRecordEntity record);

}
