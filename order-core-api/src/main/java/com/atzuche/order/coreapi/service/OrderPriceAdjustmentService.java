package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;


import com.atzuche.order.commons.JsonUtil;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.NoEffectiveErrException;
import com.atzuche.order.commons.exceptions.RenterOrderNotFoundException;
import com.atzuche.order.commons.vo.req.OrderPriceAdjustmentReqVO;
import com.atzuche.order.commons.vo.req.RenterAdjustCostReqVO;
import com.atzuche.order.commons.vo.res.OrderPriceAdjustmentVO;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OrderPriceAdjustmentEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.exception.OrderPriceAdjustmentReqVOTypeException;
import com.atzuche.order.ownercost.exception.OwnerCostParameterException;
import com.atzuche.order.ownercost.mapper.OrderPriceAdjustmentMapper;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderPriceAdjustmentService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OrderPriceAdjustmentMapper orderPriceAdjustmentMapper;
    @Autowired
    private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private OwnerMemberService ownerMemberService;
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
    @Transactional
    public int insertAndUpdateIsDelete(OrderPriceAdjustmentReqVO request) throws Exception {
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

        //更改补贴表
        RenterAdjustCostReqVO renterAdjustCostReqVO = renterAdjustCostReqVOBuild(request);
        updateRenterPriceAdjustmentByOrderNo(renterAdjustCostReqVO);

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
    private RenterAdjustCostReqVO renterAdjustCostReqVOBuild(OrderPriceAdjustmentReqVO orderPriceAdjustmentReqVO){
        RenterAdjustCostReqVO renterAdjustCostReqVO = new RenterAdjustCostReqVO();
        Integer type = orderPriceAdjustmentReqVO.getType();
        Integer amt = orderPriceAdjustmentReqVO.getAmt();
        String orderNo = orderPriceAdjustmentReqVO.getOrderNo();
        String ownerMember = orderPriceAdjustmentReqVO.getOwnerMemberCode();
        String renterMember = orderPriceAdjustmentReqVO.getRenterMemberCode();
        if(amt == null || amt<0 || orderNo==null || orderNo.trim().length()<=0
                || ownerMember==null || ownerMember.trim().length()<=0
                ||renterMember==null  || renterMember.trim().length()<=0){
            logger.error("参数错误orderPriceAdjustmentReqVO={}", JSON.toJSONString(orderPriceAdjustmentReqVO));
            throw new OrderPriceAdjustmentReqVOTypeException("");
        }
        if (Objects.equals(OrderPriceAdjustmentReqVO.OrderPriceAdjustmentReqVOType.OWNER_TO_RENTER.getCode(), type)) {
            renterAdjustCostReqVO.setOwnerToRenterAdjustAmt(String.valueOf(amt));
        } else if (Objects.equals(OrderPriceAdjustmentReqVO.OrderPriceAdjustmentReqVOType.RENTER_TO_OWNER.getCode(), type)) {
            renterAdjustCostReqVO.setRenterToOwnerAdjustAmt(String.valueOf(amt));
        } else {
            logger.error("车主租客调价类型错误type={}",type);
            throw new OrderPriceAdjustmentReqVOTypeException("");
        }
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(renterOrderEntity == null){
            logger.error("获取不到有效的租客子订单号orderNo={}",orderNo);
            throw new NoEffectiveErrException();
        }
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(ownerOrderEntity == null){
            logger.error("获取不到有效的车主子订单号orderNo={}",orderNo);
            throw new NoEffectiveErrException();
        }
        renterAdjustCostReqVO.setOrderNo(orderNo);
        renterAdjustCostReqVO.setOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo());
        renterAdjustCostReqVO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        return renterAdjustCostReqVO;
    }



    /**
     * 保存调价
     * @param renterCostReqVO
     * @return
     * @throws Exception
     */
    private void updateRenterPriceAdjustmentByOrderNo(RenterAdjustCostReqVO renterCostReqVO) throws Exception {
        //根据订单号查询会员号
        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
        if(orderEntity == null){
            logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
            throw new Exception("获取订单数据为空");
        }

        OwnerOrderEntity orderEntityOwner = null;
        if(StringUtils.isNotBlank(renterCostReqVO.getOwnerOrderNo())) {
            orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(renterCostReqVO.getOwnerOrderNo());
            if(orderEntityOwner == null){
                logger.error("获取订单数据(车主)为空orderNo={}",renterCostReqVO.getOrderNo());
                throw new Exception("获取订单数据(车主)为空");
            }
        }
//	    else {
//	    	//否则根据主订单号查询
//	    	orderEntityOwner = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(renterCostReqVO.getOrderNo());
//
//	    }

        //String userName = AdminUserUtil.getAdminUser().getAuthName();  //获取的管理后台的用户名。

        /**
         * 租客给车主的调价
         */
        if(StringUtils.isNotBlank(renterCostReqVO.getRenterToOwnerAdjustAmt())) {
            SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.OWNER;
            SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.RENTER;
            RenterCashCodeEnum cash = RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST;

            CostBaseDTO costBaseDTO = new CostBaseDTO();
            costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
            costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
            OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, -Integer.valueOf(renterCostReqVO.getRenterToOwnerAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
            //
            //record.setCreateOp(userName);
            //record.setUpdateOp(userName);
            //record.setOperatorId(userName);
            //orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
            orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetailByMemNo(record);
            /*if(orderEntityOwner != null) {*/
            //反向记录
            RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterCostReqVO.getRenterOrderNo(), false);
            OwnerGoodsEntity ownerGoodsEntity = ownerGoodsService.getOwnerGoodsByCarNoAndOrderNo(renterGoodsDetail.getCarNo(), renterCostReqVO.getOrderNo());
            OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerGoodsEntity.getOwnerOrderNo(), false);
            costBaseDTO.setMemNo(ownerMemberDTO.getMemNo());
            OrderConsoleSubsidyDetailEntity recordConvert = orderConsoleSubsidyDetailService.buildData(costBaseDTO, Integer.valueOf(renterCostReqVO.getRenterToOwnerAdjustAmt()),targetEnum,sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
            //
            //recordConvert.setCreateOp(userName);
            //recordConvert.setUpdateOp(userName);
            //recordConvert.setOperatorId(userName);
            orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetailByMemNo(recordConvert);
            /*}*/
        }

        /**
         * 车主给租客的调价
         */
        if(StringUtils.isNotBlank(renterCostReqVO.getOwnerToRenterAdjustAmt())) {
            SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
            SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.OWNER;
            RenterCashCodeEnum cash = RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST;

            CostBaseDTO costBaseDTO = new CostBaseDTO();
            costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
            if(orderEntityOwner != null) {
                costBaseDTO.setMemNo(orderEntityOwner.getMemNo());
                OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, -Integer.valueOf(renterCostReqVO.getOwnerToRenterAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
                //
                //record.setCreateOp(userName);
                //record.setUpdateOp(userName);
                //record.setOperatorId(userName);
                orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
            }

            //反向记录
            costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
            OrderConsoleSubsidyDetailEntity recordConvert = orderConsoleSubsidyDetailService.buildData(costBaseDTO, Integer.valueOf(renterCostReqVO.getOwnerToRenterAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
            //
            //recordConvert.setCreateOp(userName);
            //recordConvert.setUpdateOp(userName);
            //recordConvert.setOperatorId(userName);
            orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(recordConvert);
        }

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
