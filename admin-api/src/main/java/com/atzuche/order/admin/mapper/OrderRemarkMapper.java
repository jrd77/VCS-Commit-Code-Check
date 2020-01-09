package com.atzuche.order.admin.mapper;

import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.entity.OrderRemarkOverviewEntity;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkAdditionRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkInformationRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkListRequestVO;
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

    void addOrderRemark(OrderRemarkEntity orderRemarkEntity);

    OrderRemarkEntity getOrderRemarkInformation(OrderRemarkInformationRequestVO orderRemarkInformationRequestVO);

    /**
     * 修改订单备注
     * @param orderRemarkEntity
     */
    void updateRemarkById(OrderRemarkEntity orderRemarkEntity);


    List<OrderRemarkEntity> selectRemarkList(OrderRemarkListRequestVO orderRemarkListRequestVO);

    String getRemarkNumber(OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO);


}
