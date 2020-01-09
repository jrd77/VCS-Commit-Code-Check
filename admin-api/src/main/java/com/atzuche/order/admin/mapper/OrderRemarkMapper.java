package com.atzuche.order.admin.mapper;

import com.atzuche.order.admin.dto.OrderRemarkAdditionRequestDTO;
import com.atzuche.order.admin.entity.OrderInsuranceAdditionRequestEntity;
import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.entity.OrderRemarkOverviewEntity;
import com.atzuche.order.admin.vo.req.remark.*;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkResponseVO;
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

    void addOrderRemark(OrderInsuranceAdditionRequestEntity orderInsuranceAdditionRequestEntity);

    OrderRemarkEntity getOrderRemarkInformation(OrderRemarkInformationRequestVO orderRemarkInformationRequestVO);

    /**
     * 修改订单备注
     * @param orderRemarkEntity
     */
    void updateRemarkById(OrderRemarkEntity orderRemarkEntity);


    List<OrderRemarkEntity> selectRemarkList(OrderRemarkListRequestVO orderRemarkListRequestVO);

}
