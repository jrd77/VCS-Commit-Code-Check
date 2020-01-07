package com.atzuche.order.admin.mapper;

import com.atzuche.order.admin.entity.OrderRemarkOverviewEntity;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkRequestVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface OrderRemarkMapper {

    /**
     * 获取订单备注总览信息
     * @param orderRemarkRequestVO
     * @return
     */
    List<OrderRemarkOverviewEntity> getOrderRemarkOverview(OrderRemarkRequestVO orderRemarkRequestVO);

}
