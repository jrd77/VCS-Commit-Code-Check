package com.atzuche.order.ownercost.mapper;


import com.atzuche.order.ownercost.entity.OrderPriceAdjustmentEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderPriceAdjustmentMapper {

    /**
     * 插入有值的列
     */
    Integer insertSelective(OrderPriceAdjustmentEntity orderPriceAdjustmentEntity);

    /**
     * 根据ID修改 非空的列
     */
    Integer updateByExampleSelectiveById(OrderPriceAdjustmentEntity orderPriceAdjustmentEntity);

    /****
     * 查询可用的车主给租客或租客给车主的调价信息
     * @param entity
     * @return
     */
    OrderPriceAdjustmentEntity selectEnableObjByOrderNoAndMemberCode(OrderPriceAdjustmentEntity entity);

    /****
     * 查询车主给租客或租客给车主的调价信息
     * @param entity
     * @re
     * */
    List <OrderPriceAdjustmentEntity> selectObjByOrderNoAndMemberCode(OrderPriceAdjustmentEntity entity);
}
