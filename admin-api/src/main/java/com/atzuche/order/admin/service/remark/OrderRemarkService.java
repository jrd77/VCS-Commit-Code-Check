package com.atzuche.order.admin.service.remark;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.entity.OrderRemarkOverviewEntity;
import com.atzuche.order.admin.enums.*;
import com.atzuche.order.admin.mapper.OrderRemarkMapper;
import com.atzuche.order.admin.vo.req.remark.*;
import com.atzuche.order.admin.vo.resp.remark.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderRemarkService {
    private static final Logger logger = LoggerFactory.getLogger(OrderRemarkService.class);

    @Autowired
    private OrderRemarkMapper orderRemarkMapper;
    @Autowired
    OrderRemarkLogService orderRemarkLogService;

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
        //保存操作日志
        orderRemarkLogService.addOrderRemarkLog(orderRemarkEntity, OperateTypeEnum.ADD.getType());
    }

    /**
     * 获取备注信息
     * @param orderRemarkInformationRequestVO
     * @return
     */
    public OrderRemarkResponseVO getOrderRemarkInformation(OrderRemarkInformationRequestVO orderRemarkInformationRequestVO){
        OrderRemarkResponseVO orderRemarkResponseVO = new OrderRemarkResponseVO();
        OrderRemarkEntity orderRemarkEntity = orderRemarkMapper.getOrderRemarkInformation(orderRemarkInformationRequestVO);
        BeanUtils.copyProperties(orderRemarkEntity, orderRemarkResponseVO);
        return orderRemarkResponseVO;
    }

    /**
     * 获取备注信息
     * @param remarkId
     * @return
     */
    public OrderRemarkEntity getOrderRemarkInformation(String remarkId){
        OrderRemarkInformationRequestVO orderRemarkInformationRequestVO = new OrderRemarkInformationRequestVO();
        orderRemarkInformationRequestVO.setRemarkId(remarkId);
        OrderRemarkEntity orderRemarkEntity = orderRemarkMapper.getOrderRemarkInformation(orderRemarkInformationRequestVO);
        return orderRemarkEntity;
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
        //保存操作日志
        orderRemarkLogService.addOrderRemarkLog(orderRemarkEntity, OperateTypeEnum.UPDATE.getType());
    }

    /**
     * 修改取送车备注remark_type=12，取送车备注一直累加,读取时取最新一条
     * @param orderCarServiceRemarkUpdateRequestVO
     */
    public void updateCarServiceRemarkByOrderNo(OrderCarServiceRemarkRequestVO orderCarServiceRemarkUpdateRequestVO){
        OrderRemarkEntity orderRemarkEntity = new OrderRemarkEntity();
        BeanUtils.copyProperties(orderCarServiceRemarkUpdateRequestVO,orderRemarkEntity);
        String userName = AdminUserUtil.getAdminUser().getAuthName();
        orderRemarkEntity.setUpdateOp(userName);
        orderRemarkEntity.setCreateOp(userName);
        orderRemarkEntity.setRemarkType(RemarkTypeEnum.CAR_SERVICE.getType());
        orderRemarkMapper.addOrderRemark(orderRemarkEntity);
    }


    /**
     * 删除订单备注
     * @param orderRemarkDeleteRequestVO
     */
    public void deleteRemarkById( OrderRemarkDeleteRequestVO orderRemarkDeleteRequestVO){
        OrderRemarkEntity orderRemarkEntity = new OrderRemarkEntity();
        BeanUtils.copyProperties(orderRemarkDeleteRequestVO,orderRemarkEntity);
        orderRemarkEntity.setIsDelete(YesNoEnum.YES.getType());
        orderRemarkEntity.setUpdateOp(AdminUserUtil.getAdminUser().getAuthName());
        orderRemarkMapper.updateRemarkById(orderRemarkEntity);
        //保存操作日志
        orderRemarkLogService.addOrderRemarkLog(orderRemarkEntity, OperateTypeEnum.DELETE.getType());
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
                orderRemarkListResponseVO.setRemarkType(RemarkTypeEnum.getDescriptionByType(remarkEntity.getRemarkType()));
                orderRemarkListResponseVO.setOperatorName(remarkEntity.getUpdateOp());
                orderRemarkListResponseVO.setDepartmentName(DepartmentEnum.getDescriptionByType(remarkEntity.getDepartmentId()));
                orderRemarkListResponseVO.setFollowStatusText(FollowStatusEnum.getDescriptionByType(remarkEntity.getFollowStatus()));
                orderRemarkListResponseVO.setFollowFailReasonText(FollowfailReasonEnum.getDescriptionByType(remarkEntity.getFollowFailReason()));
                orderRemarkListResponseVO.setLimitDelayedText(YesNoEnum.getDescriptionByType(remarkEntity.getLimitDelayed()));
                orderRemarkPageList.add(orderRemarkListResponseVO);
            });
        }
        orderRemarkPageListResponseVO.setOrderRemarkList(orderRemarkPageList);
        return orderRemarkPageListResponseVO;
    }



    /**
     * 获取取送车备注remarkType=12
     * @param orderRemarkRequestVO
     * @return
     */
    public OrderRemarkResponseVO getOrderCarServiceRemarkInformation(OrderRemarkRequestVO orderRemarkRequestVO){
        OrderRemarkResponseVO orderRemarkResponseVO = new OrderRemarkResponseVO();
        OrderRemarkEntity orderRemarkEntity = orderRemarkMapper.getOrderCarServiceRemarkInformation(orderRemarkRequestVO);
        if(!ObjectUtils.isEmpty(orderRemarkEntity)){
            BeanUtils.copyProperties(orderRemarkEntity, orderRemarkResponseVO);
        }
        return orderRemarkResponseVO;
    }


}
