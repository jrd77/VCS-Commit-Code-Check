package com.atzuche.order.ownercost.service;

import com.atzuche.order.commons.JsonUtil;
import com.atzuche.order.commons.vo.req.OrderPriceAdjustmentReqVO;
import com.atzuche.order.commons.vo.res.OrderPriceAdjustmentVO;
import com.atzuche.order.ownercost.entity.OrderPriceAdjustmentEntity;
import com.atzuche.order.ownercost.exception.OrderPriceAdjustmentReqVOTypeException;
import com.atzuche.order.ownercost.exception.OwnerCostParameterException;
import com.atzuche.order.ownercost.mapper.OrderPriceAdjustmentMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class OrderPriceAdjustmentService {
    @Autowired
    private OrderPriceAdjustmentMapper orderPriceAdjustmentMapper;

    /**
     * 新增
     */
    public int insert(OrderPriceAdjustmentReqVO request) {
        OrderPriceAdjustmentEntity entity = copy(request);
        if (Objects.isNull(entity)) {
            return 0;
        }
        entity.setIsDelete(0);
        entity.setCreateTime(new Date());
        return orderPriceAdjustmentMapper.insertSelective(entity);
    }

    /**
     * 修改
     */
    public int update(OrderPriceAdjustmentReqVO request) {
        OrderPriceAdjustmentEntity entity = copy(request);
        if (Objects.isNull(entity)) {
            return 0;
        }
        return orderPriceAdjustmentMapper.updateByExampleSelectiveById(entity);
    }

    public OrderPriceAdjustmentVO selectEnableObjByOrderNoAndMemberCode(OrderPriceAdjustmentReqVO request) {
        OrderPriceAdjustmentEntity entity = copy(request);
        if (Objects.isNull(entity)) {
            return null;
        }
        entity.setIsDelete(0);
        OrderPriceAdjustmentEntity dbEntity = orderPriceAdjustmentMapper.selectEnableObjByOrderNoAndMemberCode(entity);
        return copy(dbEntity);
    }

    public List <OrderPriceAdjustmentVO> selectEnableObjsByOrderNoAndMemberCode(OrderPriceAdjustmentReqVO request) {
        List <OrderPriceAdjustmentVO> returnList = new ArrayList <>();

        OrderPriceAdjustmentEntity entity = new OrderPriceAdjustmentEntity();
        entity.setOrderNo(request.getOrderNo());
        entity.setOwnerMemberCode(request.getOwnerMemberCode());
        if(StringUtils.isEmpty(entity.getOwnerMemberCode())){
            entity.setOwnerMemberCode("");
        }
        entity.setType(OrderPriceAdjustmentReqVO.OrderPriceAdjustmentReqVOType.OWNER_TO_RENTER.getCode());
        entity.setIsDelete(0);

        OrderPriceAdjustmentEntity dbEntity = orderPriceAdjustmentMapper.selectEnableObjByOrderNoAndMemberCode(entity);
        OrderPriceAdjustmentVO adjustmentVO = copy(dbEntity);
        if (Objects.nonNull(adjustmentVO)) {
            returnList.add(adjustmentVO);
        }

        entity.setOwnerMemberCode(null);
        entity.setRenterMemberCode(request.getRenterMemberCode());
        entity.setType(OrderPriceAdjustmentReqVO.OrderPriceAdjustmentReqVOType.RENTER_TO_OWNER.getCode());
        if(StringUtils.isEmpty(entity.getRenterMemberCode())){
            entity.setRenterMemberCode("");
        }

        dbEntity = orderPriceAdjustmentMapper.selectEnableObjByOrderNoAndMemberCode(entity);
        adjustmentVO = copy(dbEntity);
        if (Objects.nonNull(adjustmentVO)) {
            returnList.add(adjustmentVO);
        }

        return returnList;
    }

    /****
     *  添加数据，在添加数据前将原数据修改成已删除
     * @param request
     * @return
     */
    public int insertAndUpdateIsDelete(OrderPriceAdjustmentReqVO request) {
        if (Objects.isNull(request)) {
            throw new OwnerCostParameterException();
        }
        OrderPriceAdjustmentEntity entity = new OrderPriceAdjustmentEntity();
        entity.setOrderNo(request.getOrderNo());
        entity.setIsDelete(0);
        entity.setType(request.getType());

        if (Objects.equals(OrderPriceAdjustmentReqVO.OrderPriceAdjustmentReqVOType.OWNER_TO_RENTER.getCode(), request.getType())) {
            entity.setOwnerMemberCode(request.getOwnerMemberCode());
        } else if (Objects.equals(OrderPriceAdjustmentReqVO.OrderPriceAdjustmentReqVOType.RENTER_TO_OWNER.getCode(), request.getType())) {
            entity.setRenterMemberCode(request.getRenterMemberCode());
        } else {
            throw new OrderPriceAdjustmentReqVOTypeException(JsonUtil.toJson(request));
        }

        List <OrderPriceAdjustmentEntity> lists = orderPriceAdjustmentMapper.selectObjByOrderNoAndMemberCode(entity);
        if (CollectionUtils.isNotEmpty(lists)) {
            for (OrderPriceAdjustmentEntity adjustmentEntity : lists) {
                OrderPriceAdjustmentEntity updateEntity = new OrderPriceAdjustmentEntity();
                updateEntity.setId(adjustmentEntity.getId());
                updateEntity.setUpdateTime(new Date());
                updateEntity.setUpdator(request.getOperation());
                updateEntity.setIsDelete(1);

                orderPriceAdjustmentMapper.updateByExampleSelectiveById(updateEntity);
            }
        }

        return insert(request);
    }

    private OrderPriceAdjustmentEntity copy(OrderPriceAdjustmentReqVO request) {
        if (Objects.isNull(request)) {
            return null;
        }
        OrderPriceAdjustmentEntity entity = new OrderPriceAdjustmentEntity();
        BeanUtils.copyProperties(request, entity);

        return entity;
    }

    private OrderPriceAdjustmentVO copy(OrderPriceAdjustmentEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        OrderPriceAdjustmentVO adjustmentVO = new OrderPriceAdjustmentVO();
        BeanUtils.copyProperties(entity, adjustmentVO);
        return adjustmentVO;
    }
}
