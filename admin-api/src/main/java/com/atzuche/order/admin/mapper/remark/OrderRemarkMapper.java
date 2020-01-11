package com.atzuche.order.admin.mapper.remark;

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

    /**
     * 添加备注
     * @param orderRemarkEntity
     */
    void addOrderRemark(OrderRemarkEntity orderRemarkEntity);

    /**
     * 获取备注信息
     * @param orderRemarkInformationRequestVO
     * @return
     */
    OrderRemarkEntity getOrderRemarkInformation(OrderRemarkInformationRequestVO orderRemarkInformationRequestVO);

    /**
     * 获取取送车备注信息remarkType=12
     * @param orderRemarkRequestVO
     * @return
     */
    OrderRemarkEntity getOrderCarServiceRemarkInformation(OrderRemarkRequestVO orderRemarkRequestVO);


    /**
     * 修改订单备注
     * @param orderRemarkEntity
     */
    void updateRemarkById(OrderRemarkEntity orderRemarkEntity);


    /**
     * 获取备注列表
     * @param orderRemarkListRequestVO
     * @return
     */
    List<OrderRemarkEntity> selectRemarkList(OrderRemarkListRequestVO orderRemarkListRequestVO);

    /**
     * 获取备注序号
     * @param orderRemarkAdditionRequestVO
     * @return
     */
    String getRemarkNumber(OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO);


}
