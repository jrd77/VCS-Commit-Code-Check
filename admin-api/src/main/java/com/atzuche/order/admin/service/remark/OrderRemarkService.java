package com.atzuche.order.admin.service.remark;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.entity.OrderRemarkEntity;
import com.atzuche.order.admin.entity.OrderRemarkLogEntity;
import com.atzuche.order.admin.entity.OrderRemarkOverviewEntity;
import com.atzuche.order.admin.enums.DepartmentEnum;
import com.atzuche.order.admin.enums.OperateTypeEnum;
import com.atzuche.order.admin.enums.RemarkTypeEnum;
import com.atzuche.order.admin.mapper.OrderRemarkLogMapper;
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
    @Autowired
    private OrderRemarkLogMapper orderRemarkLogMapper;

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
        OrderRemarkLogEntity orderRemarkLogEntity = new OrderRemarkLogEntity();
        BeanUtils.copyProperties(orderRemarkEntity, orderRemarkLogEntity);
        orderRemarkLogEntity.setOperateType(OperateTypeEnum.ADD.getType());
        orderRemarkLogMapper.addOrderRemarkLog(orderRemarkLogEntity);
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
        OrderRemarkInformationRequestVO orderRemarkInformationRequestVO = new OrderRemarkInformationRequestVO();
        orderRemarkInformationRequestVO.setRemarkId(orderRemarkUpdateRequestVO.getRemarkId());
        OrderRemarkEntity oldOrderRemarkEntity = orderRemarkMapper.getOrderRemarkInformation(orderRemarkInformationRequestVO);
        OrderRemarkEntity orderRemarkEntity = new OrderRemarkEntity();
        BeanUtils.copyProperties(orderRemarkUpdateRequestVO,orderRemarkEntity);
        orderRemarkEntity.setUpdateOp(AdminUserUtil.getAdminUser().getAuthName());
        orderRemarkMapper.updateRemarkById(orderRemarkEntity);
        //保存操作日志
        OrderRemarkLogEntity orderRemarkLogEntity = new OrderRemarkLogEntity();
        BeanUtils.copyProperties(orderRemarkEntity, orderRemarkLogEntity);
        orderRemarkLogEntity.setOperateType(OperateTypeEnum.UPDATE.getType());
        orderRemarkLogEntity.setRemarkHistory(oldOrderRemarkEntity.getRemarkContent());
        String userName = AdminUserUtil.getAdminUser().getAuthName();
        orderRemarkLogEntity.setOrderNo(oldOrderRemarkEntity.getOrderNo());
        orderRemarkLogEntity.setCreateOp(userName);
        orderRemarkLogEntity.setUpdateOp(userName);
        orderRemarkLogEntity.setNumber(oldOrderRemarkEntity.getNumber());
        orderRemarkLogEntity.setDepartmentId(oldOrderRemarkEntity.getDepartmentId());
        orderRemarkLogMapper.addOrderRemarkLog(orderRemarkLogEntity);
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

        //保存操作日志
        OrderRemarkInformationRequestVO orderRemarkInformationRequestVO = new OrderRemarkInformationRequestVO();
        orderRemarkInformationRequestVO.setRemarkId(orderRemarkDeleteRequestVO.getRemarkId());
        OrderRemarkEntity oldOrderRemarkEntity = orderRemarkMapper.getOrderRemarkInformation(orderRemarkInformationRequestVO);
        OrderRemarkLogEntity orderRemarkLogEntity = new OrderRemarkLogEntity();
        BeanUtils.copyProperties(orderRemarkEntity, orderRemarkLogEntity);
        orderRemarkLogEntity.setOperateType(OperateTypeEnum.DELETE.getType());
        String remarkContent = oldOrderRemarkEntity.getRemarkContent();
        orderRemarkLogEntity.setRemarkHistory(remarkContent);
        orderRemarkLogEntity.setRemarkContent(remarkContent);
        String userName = AdminUserUtil.getAdminUser().getAuthName();
        orderRemarkLogEntity.setCreateOp(userName);
        orderRemarkLogEntity.setUpdateOp(userName);

        orderRemarkLogEntity.setNumber(oldOrderRemarkEntity.getNumber());
        orderRemarkLogEntity.setOrderNo(oldOrderRemarkEntity.getOrderNo());
        orderRemarkLogMapper.addOrderRemarkLog(orderRemarkLogEntity);


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
