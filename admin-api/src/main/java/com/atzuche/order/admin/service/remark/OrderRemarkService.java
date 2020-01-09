package com.atzuche.order.admin.service.remark;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.entity.OrderRemarkOverviewEntity;
import com.atzuche.order.admin.enums.DepartmentEnum;
import com.atzuche.order.admin.enums.RemarkTypeEnum;
import com.atzuche.order.admin.mapper.OrderRemarkMapper;
import com.atzuche.order.admin.vo.req.remark.*;
import com.atzuche.order.admin.vo.resp.remark.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderRemarkService {

    public static final String DELETE_FLAG = "1";
    private static final Logger logger = LoggerFactory.getLogger(OrderRemarkService.class);

    @Autowired
    private OrderRemarkMapper orderRemarkMapper;

    /**
     * 获取备注总览
     * @param orderRemarkRequestVO
     * @return
     */
    public OrderRemarkOverviewListResponseVO getOrderRemarkOverview(OrderRemarkRequestVO orderRemarkRequestVO) {
        List<OrderRemarkOverviewResponseVO> orderRemarkOverviewList = new ArrayList();
        List<OrderRemarkOverviewEntity> orderRemarkOverviewEntityList = orderRemarkMapper.getOrderRemarkOverview(orderRemarkRequestVO);
        OrderRemarkOverviewListResponseVO orderRemarkOverviewListResponseVO = new OrderRemarkOverviewListResponseVO();
        if (!CollectionUtils.isEmpty(orderRemarkOverviewEntityList)) {
            for (OrderRemarkOverviewEntity orderRemarkOverviewEntity : orderRemarkOverviewEntityList) {
                OrderRemarkOverviewResponseVO orderRemarkOverviewResponseVO = new OrderRemarkOverviewResponseVO();
                BeanUtils.copyProperties(orderRemarkOverviewEntity, orderRemarkOverviewResponseVO);
                orderRemarkOverviewResponseVO.setRemarkTypeText(RemarkTypeEnum.getDescriptionByType(orderRemarkOverviewResponseVO.getRemarkType()));
                orderRemarkOverviewList.add(orderRemarkOverviewResponseVO);
            }
            orderRemarkOverviewListResponseVO.setRemarkOverviewList(orderRemarkOverviewList);
        }
        return orderRemarkOverviewListResponseVO;
    }

    /**
     * 添加备注
     * @param orderRemarkAdditionRequestVO
     */
    public void addOrderRemark(OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO) {
        OrderRemarkEntity orderRemarkEntity = new OrderRemarkEntity();
        BeanUtils.copyProperties(orderRemarkAdditionRequestVO,orderRemarkEntity);
        String number = orderRemarkMapper.getRemarkNumber(orderRemarkAdditionRequestVO);
        orderRemarkEntity.setNumber(number);
        String userName = AdminUserUtil.getAdminUser().getAuthName();
        orderRemarkEntity.setCreateOp(userName);
        orderRemarkEntity.setUpdateOp(userName);
        orderRemarkMapper.addOrderRemark(orderRemarkEntity);
    }

    public OrderRemarkResponseVO getOrderRemarkInformation(OrderRemarkInformationRequestVO orderRemarkInformationRequestVO){
        OrderRemarkResponseVO orderRemarkResponseVO = new OrderRemarkResponseVO();
        OrderRemarkEntity orderRemarkEntity = orderRemarkMapper.getOrderRemarkInformation(orderRemarkInformationRequestVO);
        BeanUtils.copyProperties(orderRemarkEntity, orderRemarkResponseVO);
        return orderRemarkResponseVO;
    }

    /**
     * 修改订单备注
     * @param orderRemarkUpdateRequestVO
     */
    public void updateRemarkById(OrderRemarkUpdateRequestVO orderRemarkUpdateRequestVO){
        OrderRemarkEntity orderRemarkEntity = new OrderRemarkEntity();
        BeanUtils.copyProperties(orderRemarkUpdateRequestVO,orderRemarkEntity);
        orderRemarkEntity.setUpdateOp(AdminUserUtil.getAdminUser().getAuthName());
        orderRemarkMapper.updateRemarkById(orderRemarkEntity);
    }


    /**
     * 删除订单备注
     * @param orderRemarkDeleteRequestVO
     */
    public void deleteRemarkById( OrderRemarkDeleteRequestVO orderRemarkDeleteRequestVO){
        OrderRemarkEntity orderRemarkEntity = new OrderRemarkEntity();
        BeanUtils.copyProperties(orderRemarkDeleteRequestVO,orderRemarkEntity);
        orderRemarkEntity.setIsDelete(DELETE_FLAG);
        orderRemarkEntity.setUpdateOp(AdminUserUtil.getAdminUser().getAuthName());
        orderRemarkMapper.updateRemarkById(orderRemarkEntity);
    }


    /**
     * 查询备注列表
     * @param orderRemarkListRequestVO
     * @return
     */
    public OrderRemarkPageListResponseVO selectRemarklist(OrderRemarkListRequestVO orderRemarkListRequestVO){
        OrderRemarkPageListResponseVO orderRemarkPageListResponseVO = new OrderRemarkPageListResponseVO();
        List<OrderRemarkListResponseVO> orderRemarkPageList = new ArrayList<>();
        List<OrderRemarkEntity> remarkList = orderRemarkMapper.selectRemarkList(orderRemarkListRequestVO);
        if(!CollectionUtils.isEmpty(remarkList)) {
            remarkList.forEach(remarkEntity -> {
                OrderRemarkListResponseVO orderRemarkListResponseVO = new OrderRemarkListResponseVO();
                BeanUtils.copyProperties(remarkEntity, orderRemarkListResponseVO);
                orderRemarkListResponseVO.setOperatorName(remarkEntity.getUpdateOp());
                orderRemarkListResponseVO.setDepartmentName(DepartmentEnum.getDescriptionByType(remarkEntity.getDepartmentId()));
                orderRemarkPageList.add(orderRemarkListResponseVO);
            });
        }
        orderRemarkPageListResponseVO.setOrderRemarkList(orderRemarkPageList);
        return orderRemarkPageListResponseVO;
    }



}
