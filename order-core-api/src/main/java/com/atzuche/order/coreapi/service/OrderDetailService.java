package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleEntity;
import com.atzuche.order.accountownercost.service.AccountOwnerCostSettleService;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeDetailNoTService;
import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositDetailNoTService;
import com.atzuche.order.accountrenterdetain.entity.AccountRenterDetainCostEntity;
import com.atzuche.order.accountrenterdetain.entity.AccountRenterDetainDetailEntity;
import com.atzuche.order.accountrenterdetain.service.notservice.AccountRenterDetainCostNoTService;
import com.atzuche.order.accountrenterdetain.service.notservice.AccountRenterDetainDetailNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.commons.CostStatUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberRightDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.coreapi.modifyorder.exception.NoEffectiveErrException;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.service.handover.OwnerHandoverCarService;
import com.atzuche.order.delivery.service.handover.RenterHandoverCarService;
import com.atzuche.order.delivery.vo.delivery.DeliveryOilCostVO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.flow.entity.OrderFlowEntity;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.*;
import com.atzuche.order.ownercost.service.*;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.*;
import com.atzuche.order.renterorder.service.*;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderDetailService {
    @Autowired
    RenterOrderChangeApplyService renterOrderChangeApplyService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderSourceStatService orderSourceStatService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    private RenterMemberService renterMemberService;
    @Autowired
    private OwnerMemberService ownerMemberService;
    @Autowired
    private RenterOrderCostService renterOrderCostService;
    @Autowired
    private OrderCancelReasonService orderCancelReasonService;
    @Autowired
    private RenterHandoverCarService renterHandoverCarInfoService;
    @Autowired
    private RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired
    private AccountOwnerIncomeDetailNoTService accountOwnerIncomeDetailNoTService;
    @Autowired
    private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    private RenterAdditionalDriverService renterAdditionalDriverService;
    @Autowired
    private OwnerHandoverCarService ownerHandoverCarService;
    @Autowired
    private AccountRenterDepositService accountRenterDepositService;
    @Autowired
    private AccountRenterDepositDetailNoTService accountRenterDepositDetailNoTService;
    @Autowired
    private AccountRenterDetainDetailNoTService accountRenterDetainDetailNoTService;
    @Autowired
    private AccountRenterDetainCostNoTService accountRenterDetainCostNoTService;
    @Autowired
    private RenterDepositDetailService renterDepositDetailService;
    @Autowired
    private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
    @Autowired
    private OwnerOrderFineDeatailService ownerOrderFineDeatailService;
    @Autowired
    private RenterOrderFineDeatailService renterOrderFineDeatailService;
    @Autowired
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
    @Autowired
    private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired
    private OrderCouponService orderCouponService;
    @Autowired
    private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
    @Autowired
    private AccountOwnerCostSettleService accountOwnerCostSettleService;
    @Autowired
    private DeliveryCarInfoPriceService deliveryCarInfoPriceService;
    @Autowired
    private OrderFlowService orderFlowService;

    @Autowired
    private OwnerOrderCostService ownerOrderCostService;


    public ResponseData<OrderDetailRespDTO> orderDetail(OrderDetailReqDTO orderDetailReqDTO){
        log.info("准备获取订单详情orderDetailReqDTO={}", JSON.toJSONString(orderDetailReqDTO));
        ResponseData responseData = new ResponseData();
        try{
            OrderDetailRespDTO orderDetailRespDTO = orderDetailProxy(orderDetailReqDTO);
            responseData.setResCode(ErrorCode.SUCCESS.getCode());
            responseData.setData(orderDetailRespDTO);
            responseData.setResMsg(ErrorCode.SUCCESS.getText());
        }catch (OrderException e){
            log.error("订单详情转化失败orderDetailReqDTO={}",JSON.toJSONString(orderDetailReqDTO),e);
            responseData.setResCode(e.getErrorCode());
            responseData.setData(null);
            responseData.setResMsg(e.getErrorMsg());
        }catch (Exception e){
            log.error("订单详情转化失败orderDetailReqDTO={}",JSON.toJSONString(orderDetailReqDTO),e);
            responseData.setResCode(ErrorCode.SYS_ERROR.getCode());
            responseData.setData(null);
            responseData.setResMsg(ErrorCode.SYS_ERROR.getText());
        }
        return responseData;
    }

    public ResponseData<OrderStatusRespDTO> orderStatus(OrderDetailReqDTO orderDetailReqDTO) {
        log.info("准备获取订单状态orderDetailReqDTO={}", JSON.toJSONString(orderDetailReqDTO));
        ResponseData responseData = new ResponseData();
        try{
            OrderStatusRespDTO orderStatusRespDTO = orderStatusProxy(orderDetailReqDTO);
            responseData.setResCode(ErrorCode.SUCCESS.getCode());
            responseData.setData(orderStatusRespDTO);
            responseData.setResMsg(ErrorCode.SUCCESS.getText());
        }catch (OrderException e){
            log.error("订单详情转化失败orderDetailReqDTO={}",JSON.toJSONString(orderDetailReqDTO),e);
            responseData.setResCode(e.getErrorCode());
            responseData.setData(null);
            responseData.setResMsg(e.getErrorMsg());
        }catch (Exception e){
            log.error("订单详情转化失败orderDetailReqDTO={}",JSON.toJSONString(orderDetailReqDTO),e);
            responseData.setResCode(ErrorCode.SYS_ERROR.getCode());
            responseData.setData(null);
            responseData.setResMsg(ErrorCode.SYS_ERROR.getText());
        }
        return responseData;
    }

    public ResponseData<OrderHistoryRespDTO> orderHistory(OrderHistoryReqDTO orderHistoryReqDTO) {
        log.info("准备获取历史订单orderDetailReqDTO={}", JSON.toJSONString(orderHistoryReqDTO));
        ResponseData responseData = new ResponseData();
        try{
            OrderHistoryRespDTO orderHistoryProxy = orderHistoryProxy(orderHistoryReqDTO);
            responseData.setResCode(ErrorCode.SUCCESS.getCode());
            responseData.setData(orderHistoryProxy);
            responseData.setResMsg(ErrorCode.SUCCESS.getText());
        }catch (OrderException e){
            log.error("取历史订单获取失败orderDetailReqDTO={}",JSON.toJSONString(orderHistoryReqDTO),e);
            responseData.setResCode(e.getErrorCode());
            responseData.setData(null);
            responseData.setResMsg(e.getErrorMsg());
        }catch (Exception e){
            log.error("取历史订单获取失败orderDetailReqDTO={}",JSON.toJSONString(orderHistoryReqDTO),e);
            responseData.setResCode(ErrorCode.SYS_ERROR.getCode());
            responseData.setData(null);
            responseData.setResMsg(ErrorCode.SYS_ERROR.getText());
        }
        return responseData;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/11 16:20
     * @Description: 获取account费用详情
     *
     **/
    public ResponseData<OrderAccountDetailRespDTO> orderAccountDetail(OrderDetailReqDTO orderDetailReqDTO) {
        log.info("准备获取订单费用详情orderDetailReqDTO={}", JSON.toJSONString(orderDetailReqDTO));
        ResponseData responseData = new ResponseData();
        try{
            OrderAccountDetailRespDTO orderDetailRespDTO = orderAccountDetailProxy(orderDetailReqDTO);
            responseData.setResCode(ErrorCode.SUCCESS.getCode());
            responseData.setData(orderDetailRespDTO);
            responseData.setResMsg(ErrorCode.SUCCESS.getText());
        }catch (OrderException e){
            log.error("订单费用详情转化失败orderDetailReqDTO={}",JSON.toJSONString(orderDetailReqDTO),e);
            responseData.setResCode(e.getErrorCode());
            responseData.setData(null);
            responseData.setResMsg(e.getErrorMsg());
        }catch (Exception e){
            log.error("订单费用详情转化失败orderDetailReqDTO={}",JSON.toJSONString(orderDetailReqDTO),e);
            responseData.setResCode(ErrorCode.SYS_ERROR.getCode());
            responseData.setData(null);
            responseData.setResMsg(ErrorCode.SYS_ERROR.getText());
        }
        return responseData;
    }


    private OrderHistoryRespDTO orderHistoryProxy(OrderHistoryReqDTO orderHistoryReqDTO) {
        String orderNo = orderHistoryReqDTO.getOrderNo();
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        //统计信息
        OrderSourceStatEntity orderSourceStatEntity = orderSourceStatService.selectByOrderNo(orderNo);
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        OrderFlowEntity orderFlowEntity = orderFlowService.getByOrderNoAndStatus(orderNo, orderStatusEntity.getStatus());
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();

        orderDetailDTO.orderStatus = OrderStatusEnum.getDescByStatus(orderStatusEntity.getStatus());
        orderDetailDTO.totalRentTime = String.valueOf(ChronoUnit.HOURS.between(orderEntity.getExpRentTime(), orderEntity.getExpRevertTime()));
        if(orderSourceStatEntity != null){
            String category = CategoryEnum.getNameByCode(orderSourceStatEntity.getCategory());
            String businessParentType = BusinessParentTypeEnum.getNameByCode(orderSourceStatEntity.getBusinessParentType());
            String platformParentType = PlatformParentTypeEnum.getNameByCode(orderSourceStatEntity.getPlatformParentType());
            String platformChildType = PlatformChildTypeEnum.getNameByCode(orderSourceStatEntity.getPlatformChildType());
            orderDetailDTO.paySource = PaySourceEnum.getNameByCode(orderSourceStatEntity.getPaySource());
            orderDetailDTO.orderSource = (category==null?"":category) + "/" + (businessParentType==null?"":businessParentType) + "/" + (platformParentType==null?"":platformParentType) + "/" + (platformChildType==null?"":platformChildType);
        }
        if(orderFlowEntity != null){
            LocalDateTime createTime = orderFlowEntity.getCreateTime();
            orderDetailDTO.statusUpdateTIme = createTime != null ? LocalDateTimeUtils.localdateToString(createTime,GlobalConstant.FORMAT_DATE_STR1):null;
        }
        orderDetailDTO.rentTimeStr = LocalDateTimeUtils.localdateToString(orderEntity.getExpRentTime(),GlobalConstant.FORMAT_DATE_STR1);
        orderDetailDTO.revertTimeStr = LocalDateTimeUtils.localdateToString(orderEntity.getExpRevertTime(),GlobalConstant.FORMAT_DATE_STR1);
        orderDetailDTO.orderNo = orderNo;


        //租客历史订单
        List<RenterDetailDTO> renterDetailDTOS = new ArrayList<>();
        if(orderHistoryReqDTO.getIsNeedOwnerOrderHistory()){
            List<RenterOrderEntity> renterOrderEntities = renterOrderService.queryHostiryRenterOrderByOrderNo(orderNo);
            renterOrderEntities.stream().forEach(x->{
                RenterDetailDTO renterDetailDTO = new RenterDetailDTO();
                RenterOrderDTO renterOrderDTO = new RenterOrderDTO();
                BeanUtils.copyProperties(x,renterOrderDTO);
                renterOrderDTO.setIsEffectiveTxt(EffectiveEnum.getName(x.getIsEffective()));
                renterOrderDTO.setExpRevertTimeStr(x.getExpRevertTime()!=null? LocalDateTimeUtils.localdateToString(x.getExpRevertTime(), GlobalConstant.FORMAT_DATE_STR1):null);
                renterOrderDTO.setExpRentTimeStr(x.getExpRentTime()!=null?LocalDateTimeUtils.localdateToString(x.getExpRentTime(), GlobalConstant.FORMAT_DATE_STR1):null);
                RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(x.getRenterOrderNo(), false);
                RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(x.getRenterOrderNo(), false);
                renterDetailDTO.setRenterOrderDTO(renterOrderDTO);
                renterDetailDTO.setRenterMemberDTO(renterMemberDTO);
                renterDetailDTO.setRenterGoodsDetail(renterGoodsDetail);
                renterDetailDTOS.add(renterDetailDTO);
            });
        }
        //车主历史订单
        List<OwnerDetailDTO> ownerDetailDTOS = new ArrayList<>();
        if(orderHistoryReqDTO.getIsNeedOwnerOrderHistory()){
            List<OwnerOrderEntity> ownerOrderEntities = ownerOrderService.queryHostiryOwnerOrderByOrderNo(orderNo);
            ownerOrderEntities.stream().forEach(x->{
                OwnerDetailDTO ownerDetailDTO = new OwnerDetailDTO();
                OwnerOrderDTO ownerOrderDTO = new OwnerOrderDTO();
                BeanUtils.copyProperties(x,ownerOrderDTO);
                ownerOrderDTO.setIsEffectiveTxt(EffectiveEnum.getName(x.getIsEffective()));
                ownerOrderDTO.setExpRevertTimeStr(x.getExpRevertTime()!=null? LocalDateTimeUtils.localdateToString(x.getExpRevertTime(), GlobalConstant.FORMAT_DATE_STR1):null);
                ownerOrderDTO.setExpRentTimeStr(x.getExpRentTime()!=null?LocalDateTimeUtils.localdateToString(x.getExpRentTime(), GlobalConstant.FORMAT_DATE_STR1):null);
                OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(x.getOwnerOrderNo(), false);
                OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(x.getOwnerOrderNo(), false);
                ownerDetailDTO.setOwnerOrderDTO(ownerOrderDTO);
                ownerDetailDTO.setOwnerMemberDTO(ownerMemberDTO);
                ownerDetailDTO.setOwnerGoodsDetailDTO(ownerGoodsDetail);
                ownerDetailDTOS.add(ownerDetailDTO);
            });
        }
        OrderHistoryRespDTO orderHistoryRespDTO = new OrderHistoryRespDTO();
        orderHistoryRespDTO.orderDetailDTO = orderDetailDTO;
        orderHistoryRespDTO.ownerDetailDTOS = ownerDetailDTOS;
        orderHistoryRespDTO.renterDetailDTOS = renterDetailDTOS;

        return orderHistoryRespDTO;
    }
    private OrderAccountDetailRespDTO orderAccountDetailProxy(OrderDetailReqDTO orderDetailReqDTO) {
        String orderNo = orderDetailReqDTO.getOrderNo();
        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);

        //订单状态
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        OrderStatusDTO orderStatusDTO = null;
        if(orderStatusEntity != null){
            orderStatusDTO = new OrderStatusDTO();
            BeanUtils.copyProperties(orderStatusEntity,orderStatusDTO);
        }

        //押金比例
        RenterDepositDetailEntity renterDepositDetailEntity = renterDepositDetailService.queryByOrderNo(orderNo);
        RenterDepositDetailDTO renterDepositDetailDTO = null;
        if(renterDepositDetailEntity != null){
            renterDepositDetailDTO = new RenterDepositDetailDTO();
            BeanUtils.copyProperties(renterDepositDetailEntity,renterDepositDetailDTO);
        }
        //租客押金
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositService.selectByOrderNo(orderNo);
        AccountRenterDepositDTO accountRenterDepositDTO = null;
        if(accountRenterDepositEntity != null){
            accountRenterDepositDTO =  new AccountRenterDepositDTO();
            BeanUtils.copyProperties(accountRenterDepositEntity,accountRenterDepositDTO);
        }
        //租客押金流水
        List<AccountRenterDepositDetailEntity> accountRenterDepositDetailEntityList = accountRenterDepositDetailNoTService.findByOrderNo(orderNo);
        List<AccountRenterDepositDetailDTO> accountRenterDepositDetailDTOList = new ArrayList<>();
        if(accountRenterDepositDetailEntityList !=null){
            Optional.ofNullable(accountRenterDepositDetailEntityList).orElseGet(ArrayList::new).stream().forEach(x->{
                AccountRenterDepositDetailDTO dto = new AccountRenterDepositDetailDTO();
                BeanUtils.copyProperties(x,dto);
                accountRenterDepositDetailDTOList.add(dto);
            });
        }
        //租客暂扣费用
        AccountRenterDetainCostEntity accountRenterDetainCostEntity = accountRenterDetainCostNoTService.getRenterDetaint(orderNo);
        AccountRenterDetainCostDTO accountRenterDetainCostDTO = null;
        if(accountRenterDetainCostEntity != null){
            accountRenterDetainCostDTO = new AccountRenterDetainCostDTO();
            BeanUtils.copyProperties(accountRenterDetainCostEntity,accountRenterDetainCostDTO);
        }
        //租客暂扣流水
        List<AccountRenterDetainDetailEntity> accountRenterDetainDetailEntities = accountRenterDetainDetailNoTService.selectByOrderNo(orderNo);
        List<AccountRenterDetainDetailDTO> accountRenterDetainDetailDTOList = new ArrayList<>();
        Optional.ofNullable(accountRenterDetainDetailEntities).orElseGet(ArrayList::new).stream().forEach(x->{
            AccountRenterDetainDetailDTO dto = new AccountRenterDetainDetailDTO();
            BeanUtils.copyProperties(x,dto);
            accountRenterDetainDetailDTOList.add(dto);
        });
        //租车费用流水
        List<AccountRenterCostDetailEntity> accountRenterCostDetailsByOrderNo = accountRenterCostDetailNoTService.getAccountRenterCostDetailsByOrderNo(orderNo);
        List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOList = new ArrayList<>();
        accountRenterCostDetailsByOrderNo.stream().forEach(x->{
            AccountRenterCostDetailDTO accountRenterCostDetailDTO = new AccountRenterCostDetailDTO();
            BeanUtils.copyProperties(x,accountRenterCostDetailDTO);
            accountRenterCostDetailDTOList.add(accountRenterCostDetailDTO);
        });

        OrderAccountDetailRespDTO orderAccountDetailRespDTO = new OrderAccountDetailRespDTO();
        orderAccountDetailRespDTO.orderDTO = orderDTO;
        orderAccountDetailRespDTO.orderStatusDTO = orderStatusDTO;
        orderAccountDetailRespDTO.renterDepositDetailDTO = renterDepositDetailDTO;
        orderAccountDetailRespDTO.accountRenterDepositDetailDTOList = accountRenterDepositDetailDTOList;
        orderAccountDetailRespDTO.accountRenterDepositDTO = accountRenterDepositDTO;
        orderAccountDetailRespDTO.accountRenterDetainCostDTO = accountRenterDetainCostDTO;
        orderAccountDetailRespDTO.accountRenterDetainDetailDTOList = accountRenterDetainDetailDTOList;
        orderAccountDetailRespDTO.accountRenterCostDetailDTOS = accountRenterCostDetailDTOList;

        return orderAccountDetailRespDTO;
    }


    private OrderDetailRespDTO orderDetailProxy(OrderDetailReqDTO orderDetailReqDTO) {
        String orderNo = orderDetailReqDTO.getOrderNo();
        String ownerOrderNo = orderDetailReqDTO.getOwnerOrderNo();
        String renterOrderNo = orderDetailReqDTO.getRenterOrderNo();

        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);

        //订单状态
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        OrderStatusDTO orderStatusDTO = null;
        if(orderStatusEntity != null){
            orderStatusDTO = new OrderStatusDTO();
            BeanUtils.copyProperties(orderStatusEntity,orderStatusDTO);
        }


        //统计信息
        OrderSourceStatEntity orderSourceStatEntity = orderSourceStatService.selectByOrderNo(orderNo);
        OrderSourceStatDTO orderSourceStatDTO = new OrderSourceStatDTO();
        if(orderSourceStatEntity != null){
            orderSourceStatDTO = new OrderSourceStatDTO();
            BeanUtils.copyProperties(orderSourceStatEntity,orderSourceStatDTO);
        }


        //租客订单
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        RenterOrderDTO renterOrderDTO = null;
        if(renterOrderEntity != null){
            renterOrderDTO = new RenterOrderDTO();
            BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);
            renterOrderNo = renterOrderNo==null?renterOrderEntity.getRenterOrderNo():renterOrderNo;
        }

        //车主订单
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        OwnerOrderDTO ownerOrderDTO = new OwnerOrderDTO();
        if(ownerOrderEntity !=null){
            ownerOrderDTO = new OwnerOrderDTO();
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);
            ownerOrderNo = ownerOrderNo==null?ownerOrderEntity.getOwnerOrderNo():ownerOrderNo;
        }

        //租客商品
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderNo, false);
        RenterGoodsDTO renterGoodsDTO = null;
        if(renterGoodsDetail != null){
            renterGoodsDTO = new RenterGoodsDTO();
            BeanUtils.copyProperties(renterGoodsDetail,renterGoodsDTO);
        }


        //车主商品
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, false);
        OwnerGoodsDTO ownerGoodsDTO = null;
        if(ownerGoodsDetail != null){
            ownerGoodsDTO = new OwnerGoodsDTO();
            BeanUtils.copyProperties(ownerGoodsDetail,ownerGoodsDTO);
        }

        //会员权益
        RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderNo, true);
        com.atzuche.order.commons.entity.orderDetailDto.RenterMemberDTO renterMember = new com.atzuche.order.commons.entity.orderDetailDto.RenterMemberDTO();
        BeanUtils.copyProperties(renterMemberDTO,renterMember);
        List<com.atzuche.order.commons.entity.dto.RenterMemberRightDTO> renterMemberRightDTOList = renterMemberDTO.getRenterMemberRightDTOList();
        List<com.atzuche.order.commons.entity.orderDetailDto.RenterMemberRightDTO> renterMemberRightDTOS = new ArrayList<>();
        renterMemberRightDTOList.stream().forEach(x->{
            com.atzuche.order.commons.entity.orderDetailDto.RenterMemberRightDTO renterMemberRightDTO = new com.atzuche.order.commons.entity.orderDetailDto.RenterMemberRightDTO();
            BeanUtils.copyProperties(x,renterMemberRightDTO);
            renterMemberRightDTOS.add(renterMemberRightDTO);
        });

        //车主会员
        OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderNo, true);
        com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO ownerMember = new com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO();
        BeanUtils.copyProperties(ownerMemberDTO,ownerMember);
        List<OwnerMemberRightDTO> ownerMemberRightDTOList = ownerMemberDTO.getOwnerMemberRightDTOList();
        List<com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberRightDTO> ownerMemberRightDTOS = new ArrayList<>();
        ownerMemberRightDTOList.stream().forEach(x->{
            com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberRightDTO ownerMemberRightDTO = new com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberRightDTO();
            BeanUtils.copyProperties(x,ownerMemberRightDTO);
            ownerMemberRightDTOS.add(ownerMemberRightDTO);
        });

        //租客费用
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo, renterOrderNo);
        RenterOrderCostDTO renterOrderCostDTO = new RenterOrderCostDTO();
        BeanUtils.copyProperties(renterOrderCostEntity,renterOrderCostDTO);

        //订单取消原因
        OrderCancelReasonEntity orderCancelReasonEntity = orderCancelReasonService.selectByOrderNo(orderNo);
        OrderCancelReasonDTO orderCancelReasonDTO = null;
        if(orderCancelReasonEntity != null){
            orderCancelReasonDTO = new OrderCancelReasonDTO();
            BeanUtils.copyProperties(orderCancelReasonEntity,orderCancelReasonDTO);
        }

        //租客交接车
        RenterHandoverCarInfoEntity renterHandoverCarInfoEntity = renterHandoverCarInfoService.selectByRenterOrderNoAndType(renterOrderNo, RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue());
        RenterHandoverCarInfoDTO renterHandoverCarInfoDTO = null;
        if(renterHandoverCarInfoEntity != null){
            renterHandoverCarInfoDTO = new RenterHandoverCarInfoDTO();
            BeanUtils.copyProperties(renterHandoverCarInfoEntity,renterHandoverCarInfoDTO);
        }

        //车主交接车
        OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = ownerHandoverCarService.selectByRenterOrderNoAndType(renterOrderNo, RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue());
        OwnerHandoverCarInfoDTO ownerHandoverCarInfoDTO = null;
        if(ownerHandoverCarInfoDTO != null){
            ownerHandoverCarInfoDTO = new OwnerHandoverCarInfoDTO();
            BeanUtils.copyProperties(ownerHandoverCarInfoEntity,ownerHandoverCarInfoDTO);
        }

        //车主订单费用明细
        List<RenterOrderCostDetailEntity> renterOrderCostDetailList = renterOrderCostDetailService.listRenterOrderCostDetail(orderNo, renterOrderNo);
        List<RenterOrderCostDetailDTO> renterOrderCostDetailDTOS = new ArrayList<>();
        renterOrderCostDetailList.stream().forEach(x->{
            RenterOrderCostDetailDTO renterOrderCostDetailDTO = new RenterOrderCostDetailDTO();
            BeanUtils.copyProperties(x,renterOrderCostDetailDTO);
            renterOrderCostDetailDTOS.add(renterOrderCostDetailDTO);
        });
        //租客订单费用明细
        List<RenterOrderCostDetailDTO> RenterOrderCostDetailDTOList = new ArrayList<>();
        renterOrderCostDetailList.stream().forEach(x->{
            RenterOrderCostDetailDTO renterOrderCostDetailDTO = new RenterOrderCostDetailDTO();
            BeanUtils.copyProperties(x,renterOrderCostDetailDTO);
            RenterOrderCostDetailDTOList.add(renterOrderCostDetailDTO);
        });

        //车主收益
        List<AccountOwnerIncomeDetailEntity> accountOwnerIncomeDetailList = accountOwnerIncomeDetailNoTService.selectByOrderNo(orderNo);
        List<AccountOwnerIncomeDetailDTO> accountOwnerIncomeDetailDTOList = new ArrayList<>();
        accountOwnerIncomeDetailList.stream().forEach(x->{
            AccountOwnerIncomeDetailDTO accountOwnerIncomeDetailDTO = new AccountOwnerIncomeDetailDTO();
            BeanUtils.copyProperties(x,accountOwnerIncomeDetailDTO);
            accountOwnerIncomeDetailDTOList.add(accountOwnerIncomeDetailDTO);
        });
        //车主租金
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailList = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(orderNo, ownerOrderNo);
        List<OwnerOrderPurchaseDetailDTO> ownerOrderPurchaseDetailDTOList = new ArrayList<>();
        ownerOrderPurchaseDetailList.stream().forEach(x->{
            OwnerOrderPurchaseDetailDTO ownerOrderPurchaseDetailDTO = new OwnerOrderPurchaseDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderPurchaseDetailDTO);
            ownerOrderPurchaseDetailDTOList.add(ownerOrderPurchaseDetailDTO);
        });
        //全局的车主订单罚金明细
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo);
        List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDeatailDTOList = new ArrayList<>();
        consoleOwnerOrderFineDeatailList.stream().forEach(x->{
            ConsoleOwnerOrderFineDeatailDTO consoleOwnerOrderFineDeatailDTO = new ConsoleOwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,consoleOwnerOrderFineDeatailDTO);
            consoleOwnerOrderFineDeatailDTOList.add(consoleOwnerOrderFineDeatailDTO);
        });
        //配送订单
        List<RenterOrderDeliveryEntity> renterOrderDeliveryList = renterOrderDeliveryService.selectByRenterOrderNo(renterOrderNo);
        RenterOrderDeliveryEntity renterOrderDeliveryGet = filterDeliveryOrderByType(renterOrderDeliveryList, DeliveryOrderTypeEnum.GET_CAR);
        RenterOrderDeliveryEntity renterOrderDeliveryReturn = filterDeliveryOrderByType(renterOrderDeliveryList, DeliveryOrderTypeEnum.RETURN_CAR);
        RenterOrderDeliveryDTO renterOrderDeliveryGetDto = null;
        if(renterOrderDeliveryGet != null){
            renterOrderDeliveryGetDto = new RenterOrderDeliveryDTO();
            BeanUtils.copyProperties(renterOrderDeliveryGet,renterOrderDeliveryGetDto);
        }
        RenterOrderDeliveryDTO renterOrderDeliveryReturnDto =  null;
        if(renterOrderDeliveryReturn != null){
            renterOrderDeliveryReturnDto = new RenterOrderDeliveryDTO();
            BeanUtils.copyProperties(renterOrderDeliveryReturn,renterOrderDeliveryReturnDto);
        }
        //附加驾驶人
        List<RenterAdditionalDriverEntity> renterAdditionalDriverList = renterAdditionalDriverService.listDriversByRenterOrderNo(renterOrderNo);
        List<RenterAdditionalDriverDTO>  renterAdditionalDriverDTOList = new ArrayList<>();
        renterAdditionalDriverList.stream().forEach(x->{
            RenterAdditionalDriverDTO renterAdditionalDriverDTO = new RenterAdditionalDriverDTO();
            BeanUtils.copyProperties(x,renterAdditionalDriverDTO);
            renterAdditionalDriverDTOList.add(renterAdditionalDriverDTO);
        });

        //租客罚金
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatailList = renterOrderFineDeatailService.getRenterOrderFineDeatailByOwnerOrderNo(renterOrderNo);
        List<RenterOrderFineDeatailDTO> renterOrderFineDeatailDTOS = new ArrayList<>();
        renterOrderFineDeatailList.stream().forEach(x->{
            RenterOrderFineDeatailDTO renterOrderFineDeatailDTO = new RenterOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,renterOrderFineDeatailDTO);
            renterOrderFineDeatailDTOS.add(renterOrderFineDeatailDTO);
        });

        //车主罚金
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailList = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOwnerOrderNo(ownerOrderNo);
        List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailDTOS = new ArrayList<>();
        ownerOrderFineDeatailList.stream().forEach(x->{
            OwnerOrderFineDeatailDTO ownerOrderFineDeatailDTO = new OwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,ownerOrderFineDeatailDTO);
            ownerOrderFineDeatailDTOS.add(ownerOrderFineDeatailDTO);
        });


        //车主补贴
        List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS = new ArrayList<>();
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(orderNo, ownerOrderNo);
        ownerOrderSubsidyDetailEntities.stream().forEach(x->{
            OwnerOrderSubsidyDetailDTO ownerOrderSubsidyDetailDTO = new OwnerOrderSubsidyDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderSubsidyDetailDTO);
            ownerOrderSubsidyDetailDTOS.add(ownerOrderSubsidyDetailDTO);
        });

        //租客补贴
        List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOS = new ArrayList<>();
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntities = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
        renterOrderSubsidyDetailEntities.stream().forEach(x->{
            RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();
            BeanUtils.copyProperties(x,renterOrderSubsidyDetailDTO);
            renterOrderSubsidyDetailDTOS.add(renterOrderSubsidyDetailDTO);
        });

        //车主费用
        OwnerOrderCostEntity ownerOrderCostEntity = ownerOrderCostService.getOwnerOrderCostByOwnerOrderNo(ownerOrderNo);
        OwnerOrderCostDTO ownerOrderCostDTO = null;
        if(ownerOrderCostEntity != null){
            ownerOrderCostDTO = new OwnerOrderCostDTO();
            BeanUtils.copyProperties(ownerOrderCostEntity,ownerOrderCostDTO);
        }

        OrderDetailRespDTO orderDetailRespDTO = new OrderDetailRespDTO();
        orderDetailRespDTO.order = orderDTO;
        orderDetailRespDTO.renterOrder = renterOrderDTO;
        orderDetailRespDTO.ownerOrder = ownerOrderDTO;
        orderDetailRespDTO.orderStatus = orderStatusDTO;
        orderDetailRespDTO.orderSourceStat = orderSourceStatDTO;
        orderDetailRespDTO.renterGoods = renterGoodsDTO;
        orderDetailRespDTO.ownerGoods = ownerGoodsDTO;
        orderDetailRespDTO.renterOrderDeliveryGet = renterOrderDeliveryGetDto;
        orderDetailRespDTO.renterOrderDeliveryReturn = renterOrderDeliveryReturnDto;
        orderDetailRespDTO.renterMember = renterMember;
        orderDetailRespDTO.ownerMember = ownerMember;
        orderDetailRespDTO.renterOrderCost = renterOrderCostDTO;
        orderDetailRespDTO.renterHandoverCarInfo = renterHandoverCarInfoDTO;
        orderDetailRespDTO.orderCancelReason = orderCancelReasonDTO;
        orderDetailRespDTO.ownerMemberRightList = ownerMemberRightDTOS;
        orderDetailRespDTO.renterMemberRightList = renterMemberRightDTOS;
        orderDetailRespDTO.renterOrderCostDetailList = renterOrderCostDetailDTOS;
        orderDetailRespDTO.accountOwnerIncomeDetailList = accountOwnerIncomeDetailDTOList;
        orderDetailRespDTO.ownerOrderPurchaseDetailList = ownerOrderPurchaseDetailDTOList;
        orderDetailRespDTO.consoleOwnerOrderFineDetailList = consoleOwnerOrderFineDeatailDTOList;
        orderDetailRespDTO.renterAdditionalDriverList = renterAdditionalDriverDTOList;
        orderDetailRespDTO.ownerOrderFineDeatailList = ownerOrderFineDeatailDTOS;
        orderDetailRespDTO.renterOrderFineDeatailList = renterOrderFineDeatailDTOS;
        orderDetailRespDTO.renterOrderSubsidyDetailDTOS = renterOrderSubsidyDetailDTOS;
        orderDetailRespDTO.ownerOrderSubsidyDetailDTOS = ownerOrderSubsidyDetailDTOS;
        orderDetailRespDTO.ownerOrderCostDTO = ownerOrderCostDTO;
        return orderDetailRespDTO;
    }

    private OrderStatusRespDTO orderStatusProxy(OrderDetailReqDTO orderDetailReqDTO) {
        String orderNo = orderDetailReqDTO.getOrderNo();
        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);

        OrderStatusRespDTO orderStatusRespDTO = new OrderStatusRespDTO();
        orderStatusRespDTO.isChange = false;

        //主订单状态
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        BeanUtils.copyProperties(orderStatusEntity,orderStatusDTO);

        //车主子订单状态
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        OwnerOrderStatusDTO ownerOrderStatusDTO = null;
        if(ownerOrderEntity != null ){
            ownerOrderStatusDTO = new OwnerOrderStatusDTO();
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderStatusDTO);
        }

        String renterOrderNo = null;
        //租客子订单状态
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        RenterOrderStatusDTO renterOrderDTO = null;
        if(renterOrderEntity != null){
            renterOrderDTO = new RenterOrderStatusDTO();
            BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);
            renterOrderNo = renterOrderEntity.getRenterOrderNo();
        }


        //改变中的租客子订单状态
        RenterOrderEntity changeRenterOrder = renterOrderService.getChangeRenterOrderByOrderNo(orderNo);
        RenterOrderStatusDTO changeRenterStaus = null;
        if(changeRenterOrder != null){
            orderStatusRespDTO.isChange = true;
            changeRenterStaus = new RenterOrderStatusDTO();
            BeanUtils.copyProperties(changeRenterStaus,changeRenterStaus);
        }

        //改变中的车主子订单状态
        OwnerOrderEntity changeOwner = ownerOrderService.getChangeOwnerByOrderNo(orderNo);
        OwnerOrderStatusDTO changeOwnerStatus = null;
        if(changeOwner != null){
            changeOwnerStatus = new OwnerOrderStatusDTO();
            BeanUtils.copyProperties(changeOwner,changeOwnerStatus);
        }

        //申请信息
        RenterOrderChangeApplyEntity renterOrderChangeApply = null;
        RenterOrderChangeApplyStatusDTO renterOrderChangeApplyStatusDTO = null;
        if(renterOrderNo != null){
            renterOrderChangeApply = renterOrderChangeApplyService.getRenterOrderChangeApplyByRenterOrderNo(renterOrderNo);
            renterOrderChangeApplyStatusDTO = new RenterOrderChangeApplyStatusDTO();
            if(renterOrderChangeApply!=null) {
                BeanUtils.copyProperties(renterOrderChangeApply, renterOrderChangeApplyStatusDTO);
                LocalDateTime createTime = renterOrderChangeApply.getCreateTime();
                String createTimeStr = LocalDateTimeUtils.localdateToString(createTime, GlobalConstant.FORMAT_DATE_STR1);
                renterOrderChangeApplyStatusDTO.setCreateTimeStr(createTimeStr);
            }

        }
        orderStatusRespDTO.orderDTO = orderDTO;
        orderStatusRespDTO.orderStatusDTO = orderStatusDTO;
        orderStatusRespDTO.renterOrderStatusDTO = renterOrderDTO;
        orderStatusRespDTO.ownerOrderStatusDTO = ownerOrderStatusDTO;
        orderStatusRespDTO.renterOrderStatusChangeDTO = changeRenterStaus;
        orderStatusRespDTO.ownerOrderStatusChangeDTO = changeOwnerStatus;
        orderStatusRespDTO.renterOrderChangeApplyStatusDTO = renterOrderChangeApplyStatusDTO;
        return orderStatusRespDTO;
    }
    /*
     * @Author ZhangBin
     * @Date 2020/1/9 11:53
     * @Description: 过滤出取还车配送订单
     * 
     **/
    private RenterOrderDeliveryEntity filterDeliveryOrderByType(List<RenterOrderDeliveryEntity> renterOrderDeliveryList, DeliveryOrderTypeEnum deliveryTypeEnum){
        List<RenterOrderDeliveryEntity> list = Optional.ofNullable(renterOrderDeliveryList).orElseGet(ArrayList::new)
                .stream()
                .filter(x -> deliveryTypeEnum.getCode() == x.getType())
                .collect(Collectors.toList());
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 15:49
     * @Description: 车主订单详情
     *
     **/
    public ResponseData<AdminOwnerOrderDetailDTO> adminOwnerOrderDetail(String ownerOrderNo,String orderNo) {
        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);
        //车主罚金
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailList = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOwnerOrderNo(ownerOrderNo);
        List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailDTOS = new ArrayList<>();
        ownerOrderFineDeatailList.stream().forEach(x->{
            OwnerOrderFineDeatailDTO ownerOrderFineDeatailDTO = new OwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,ownerOrderFineDeatailDTO);
            ownerOrderFineDeatailDTOS.add(ownerOrderFineDeatailDTO);
        });
        //全局的车主订单罚金明细
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo);
        List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDeatailDTOList = new ArrayList<>();
        consoleOwnerOrderFineDeatailList.stream().forEach(x->{
            ConsoleOwnerOrderFineDeatailDTO consoleOwnerOrderFineDeatailDTO = new ConsoleOwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,consoleOwnerOrderFineDeatailDTO);
            consoleOwnerOrderFineDeatailDTOList.add(consoleOwnerOrderFineDeatailDTO);
        });
        //车主罚金
        int ownerFienAmt = CostStatUtils.getOwnerFienAmt(ownerOrderFineDeatailDTOS, consoleOwnerOrderFineDeatailDTOList);

        //车主租金
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailList = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(orderNo, ownerOrderNo);
        List<OwnerOrderPurchaseDetailDTO> ownerOrderPurchaseDetailDTOList = new ArrayList<>();
        ownerOrderPurchaseDetailList.stream().forEach(x->{
            OwnerOrderPurchaseDetailDTO ownerOrderPurchaseDetailDTO = new OwnerOrderPurchaseDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderPurchaseDetailDTO);
            ownerOrderPurchaseDetailDTOList.add(ownerOrderPurchaseDetailDTO);
        });
        //车主租金
        int ownerRentAmt = CostStatUtils.getOwnerRentAmt(ownerOrderPurchaseDetailDTOList);

        //车主补贴
        List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS = new ArrayList<>();
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(orderNo, ownerOrderNo);
        ownerOrderSubsidyDetailEntities.stream().forEach(x->{
            OwnerOrderSubsidyDetailDTO ownerOrderSubsidyDetailDTO = new OwnerOrderSubsidyDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderSubsidyDetailDTO);
            ownerOrderSubsidyDetailDTOS.add(ownerOrderSubsidyDetailDTO);
        });
        //补贴
        RenterOwnerPriceDTO renterOwnerPriceDTO = CostStatUtils.ownerRenterPrice(ownerOrderSubsidyDetailDTOS);
        //车主券补贴
        OwnerOrderSubsidyDetailDTO ownerCoupon = CostStatUtils.ownerSubsidtyFilterByCashNo(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo(), ownerOrderSubsidyDetailDTOS);

        //车主券
        //租客订单
        String renterOrderNo = null;
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(renterOrderNo == null){
            NoEffectiveErrException noEffectiveErrException = new NoEffectiveErrException();
            log.error("无有效子订单orderNo={}",orderNo);
            throw noEffectiveErrException;
        }
        RenterOrderDTO renterOrderDTO = null;
        if(renterOrderEntity != null){
            renterOrderDTO = new RenterOrderDTO();
            BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);
            renterOrderNo = renterOrderNo==null?renterOrderEntity.getRenterOrderNo():renterOrderNo;
        }
        String ownerCouponName = "";
        if(ownerCoupon != null){
            OrderCouponEntity orderCouponEntity = orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(orderNo,renterOrderNo);
            ownerCouponName = orderCouponEntity.getCouponName();
        }
        //平台给车主的补贴
        PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO = getPlatformToOwnerSubsidyDTO(ownerOrderSubsidyDetailDTOS);

        //车主配送服务费（车主增值订单）
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailEntities = ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(orderNo, ownerOrderNo);
        List<OwnerOrderIncrementDetailDTO> ownerOrderIncrementDetailDTOS = new ArrayList<>();
        ownerOrderIncrementDetailEntities.stream().forEach(x->{
            OwnerOrderIncrementDetailDTO ownerOrderIncrementDetailDTO = new OwnerOrderIncrementDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderIncrementDetailDTO);
            ownerOrderIncrementDetailDTOS.add(ownerOrderIncrementDetailDTO);
        });
        int incrGetCarAmt = CostStatUtils.getIncrementByCashNo(OwnerCashCodeEnum.SRV_GET_COST_OWNER, ownerOrderIncrementDetailDTOS);
        int incrReturnCarAmt = CostStatUtils.getIncrementByCashNo(OwnerCashCodeEnum.SRV_RETURN_COST_OWNER, ownerOrderIncrementDetailDTOS);

        //车主收益
        AccountOwnerCostSettleEntity accountOwnerCostSettleEntity = accountOwnerCostSettleService.getsettleAmtByOrderNo(orderNo, ownerOrderNo);

        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, true);
        OwnerRentDetailDTO ownerRentDetailDTO = new OwnerRentDetailDTO();
        if(ownerGoodsDetail != null && ownerGoodsDetail.getOwnerGoodsPriceDetailDTOList()!=null && ownerGoodsDetail.getOwnerGoodsPriceDetailDTOList().size()>0){
            List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = ownerGoodsDetail.getOwnerGoodsPriceDetailDTOList();
            ownerRentDetailDTO.setDayAverageAmt(ownerGoodsPriceDetailDTOList.get(0).getCarUnitPrice());
            ownerRentDetailDTO.setOwnerGoodsPriceDetailDTOS(ownerGoodsPriceDetailDTOList);
        }

        ownerRentDetailDTO.setReqTimeStr(orderDTO.getReqTime()!=null?LocalDateTimeUtils.localdateToString(orderDTO.getReqTime(),GlobalConstant.FORMAT_DATE_STR1):null);
        ownerRentDetailDTO.setRevertTimeStr(orderDTO.getExpRevertTime()!=null? LocalDateTimeUtils.localdateToString(orderDTO.getExpRevertTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        ownerRentDetailDTO.setRentTimeStr(orderDTO.getExpRentTime()!=null?LocalDateTimeUtils.localdateToString(orderDTO.getExpRentTime(), GlobalConstant.FORMAT_DATE_STR1):null);

        //超里程费用 + 油费
        DeliveryOilCostVO oilCostByRenterOrderNo = deliveryCarInfoPriceService.getOilCostByRenterOrderNo(orderNo, ownerGoodsDetail.getCarEngineType());
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = null;
        if(oilCostByRenterOrderNo != null && oilCostByRenterOrderNo.getOwnerGetAndReturnCarDTO()!=null){
            ownerGetAndReturnCarDTO = oilCostByRenterOrderNo.getOwnerGetAndReturnCarDTO();
        }

        ServiceDetailDTO serviceDetailDTO = new ServiceDetailDTO();
        //serviceDetailDTO.setCarType(ownerGoodsDetail.getCarOwnerType());
        //serviceDetailDTO.setServiceAmt(ownerGoodsDetail.getServiceRate());

        AdminOwnerOrderDetailDTO adminOwnerOrderDetailDTO = new AdminOwnerOrderDetailDTO();
        adminOwnerOrderDetailDTO.setExpIncome(null);
        adminOwnerOrderDetailDTO.setSettleincome(accountOwnerCostSettleEntity!=null?accountOwnerCostSettleEntity.getIncomeAmt():null);
        adminOwnerOrderDetailDTO.setIncome(null);
        adminOwnerOrderDetailDTO.setRentAmt(ownerRentAmt);
        adminOwnerOrderDetailDTO.setOwnerRentDetailDTO(ownerRentDetailDTO);
        adminOwnerOrderDetailDTO.setFienAmt(ownerFienAmt);
        adminOwnerOrderDetailDTO.setFienAmtDetailDTO(null);
        adminOwnerOrderDetailDTO.setOwnerRenterPrice(renterOwnerPriceDTO.getOwnerToRenterPrice());
        adminOwnerOrderDetailDTO.setRenterOwnerPriceDTO(renterOwnerPriceDTO);
        adminOwnerOrderDetailDTO.setMileageAmt(ownerGetAndReturnCarDTO!=null?ownerGetAndReturnCarDTO.getOverKNCrash():"0");
        adminOwnerOrderDetailDTO.setOilAmt(ownerGetAndReturnCarDTO!=null?ownerGetAndReturnCarDTO.getCarOwnerOilCrash():"0");
        adminOwnerOrderDetailDTO.setOilServiceAmt(null);
        adminOwnerOrderDetailDTO.setDeductionAmt(null);
        adminOwnerOrderDetailDTO.setServiceAmt(null);
        adminOwnerOrderDetailDTO.setServiceDetailDTO(serviceDetailDTO);
        adminOwnerOrderDetailDTO.setPlatformOilServiceAmt(null);
        adminOwnerOrderDetailDTO.setOwnerPayPlatformAmt(null);
        adminOwnerOrderDetailDTO.setGpsServiceAmt(accountOwnerCostSettleEntity!=null?accountOwnerCostSettleEntity.getGpsAmt():0);
        adminOwnerOrderDetailDTO.setGpsDepositAmt(null);
        adminOwnerOrderDetailDTO.setDeliveryAmt(incrGetCarAmt + incrReturnCarAmt);
        adminOwnerOrderDetailDTO.setRenterDiscountAmt(ownerCoupon.getSubsidyAmount());
        adminOwnerOrderDetailDTO.setOwnerCouponName(ownerCouponName);
        adminOwnerOrderDetailDTO.setOwnerCouponAmt(ownerCoupon==null?0:ownerCoupon.getSubsidyAmount());
        adminOwnerOrderDetailDTO.setPlatformSubsidyAmt(platformToOwnerSubsidyDTO.getTotal());
        adminOwnerOrderDetailDTO.setPlatformToOwnerSubsidyAmt(platformToOwnerSubsidyDTO.getTotal());
        adminOwnerOrderDetailDTO.setPlatformToOwnerSubsidyDTO(platformToOwnerSubsidyDTO);
        return ResponseData.success(adminOwnerOrderDetailDTO);
    }


    private PlatformToOwnerSubsidyDTO getPlatformToOwnerSubsidyDTO(List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS ){
        int mileageAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_MILEAGE_COST_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int oilSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_OIL_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int washCarSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_WASH_CAR_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int carGoodsLossSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_GOODS_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int delaySubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_DELAY_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int trafficSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_TRAFFIC_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int incomeSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_INCOME_SUBSIDY, ownerOrderSubsidyDetailDTOS);

        List<OwnerOrderSubsidyDetailDTO> platformToOwnerSubsidyList = CostStatUtils.getPlatformToOwnerSubsidyList(ownerOrderSubsidyDetailDTOS);
        int platformToOwnerAmt = CostStatUtils.calAmt(platformToOwnerSubsidyList);
        int otherSubsidyAmt = platformToOwnerAmt - (
            + mileageAmt
            + oilSubsidyAmt
            + washCarSubsidyAmt
            + carGoodsLossSubsidyAmt
            + delaySubsidyAmt
            + trafficSubsidyAmt
            + incomeSubsidyAmt);
        PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO = new PlatformToOwnerSubsidyDTO();
        platformToOwnerSubsidyDTO.setMileageAmt(mileageAmt);
        platformToOwnerSubsidyDTO.setOilSubsidyAmt(oilSubsidyAmt);
        platformToOwnerSubsidyDTO.setWashCarSubsidyAmt(washCarSubsidyAmt);
        platformToOwnerSubsidyDTO.setCarGoodsLossSubsidyAmt(carGoodsLossSubsidyAmt);
        platformToOwnerSubsidyDTO.setDelaySubsidyAmt(delaySubsidyAmt);
        platformToOwnerSubsidyDTO.setTrafficSubsidyAmt(trafficSubsidyAmt);
        platformToOwnerSubsidyDTO.setIncomeSubsidyAmt(incomeSubsidyAmt);
        platformToOwnerSubsidyDTO.setOtherSubsidyAmt(otherSubsidyAmt);
        platformToOwnerSubsidyDTO.setTotal(platformToOwnerAmt);
        return platformToOwnerSubsidyDTO;
    }


    public ResponseData<OrderHistoryListDTO> dispatchHistory(String orderNo) {
        List<OrderHistoryDTO> orderHistoryDTOS = new ArrayList<>();
        List<RenterOrderEntity> renterOrderEntities = renterOrderService.queryHostiryRenterOrderByOrderNo(orderNo);
        Map<String, RenterGoodsDetailDTO> rentergoodsMap = new HashMap<>();
        Map<Integer,OwnerMemberDTO> ownerMemberMap = new HashMap<>();
        Map<String,OrderSourceStatEntity> orderSourceStatMap = new HashMap<>();
        Map<String,RenterOrderCostEntity> renterOrderCostEntityMap = new HashMap<>();
        //主订单信息
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        Optional.ofNullable(renterOrderEntities)
                .orElseGet(ArrayList::new)
                .stream()
                .forEach(x->{
                    OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO();
                    String renterOrderNo = x.getRenterOrderNo();
                    RenterGoodsDetailDTO renterGoodsDetail = null;
                    //商品详情
                    if(rentergoodsMap.get(x.getRenterOrderNo())!= null){
                        renterGoodsDetail = rentergoodsMap.get(x.getRenterOrderNo());
                    }else{
                        renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderNo, true);
                        rentergoodsMap.put(renterGoodsDetail.getRenterOrderNo(),renterGoodsDetail);
                    }

                    //车主姓名、车主电话
                    Integer carNo = renterGoodsDetail.getCarNo();
                    OwnerMemberDTO ownerMemberDTO = null;
                    if(ownerMemberMap.get(carNo)!=null){
                        ownerMemberDTO = ownerMemberMap.get(carNo);
                    }else{
                        OwnerGoodsEntity ownerGoodsByCarNo = ownerGoodsService.getOwnerGoodsByCarNo(carNo);
                        ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerGoodsByCarNo.getOwnerOrderNo(), false);
                        ownerMemberMap.put(carNo,ownerMemberDTO);
                    }
                    //订单类型
                    OrderSourceStatEntity orderSourceStatEntity = null;
                    if(orderSourceStatMap.get(orderNo) != null){
                        orderSourceStatEntity = orderSourceStatMap.get(orderNo);
                    }else{
                        orderSourceStatEntity = orderSourceStatService.selectByOrderNo(orderNo);
                        orderSourceStatMap.put(orderNo,orderSourceStatEntity);
                    }

                    //总租金、总保险
                    RenterOrderCostEntity renterOrderCostEntity = null;
                    if(renterOrderCostEntityMap.get(x.getRenterOrderNo()) != null){
                        renterOrderCostEntityMap.get(renterOrderNo);
                    }else{
                        renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo, renterOrderNo);
                        renterOrderCostEntityMap.put(renterOrderNo,renterOrderCostEntity);
                    }

                    orderHistoryDTO.orderNo = orderNo;
                    orderHistoryDTO.category = CategoryEnum.getNameByCode(orderSourceStatEntity.getCategory());
                    orderHistoryDTO.ownerName = ownerMemberDTO.getRealName();
                    orderHistoryDTO.ownerPhone = ownerMemberDTO.getPhone();
                    orderHistoryDTO.cityCode = orderEntity.getCityCode();
                    orderHistoryDTO.cityName = orderEntity.getCityName();
                    orderHistoryDTO.reqAdd = orderSourceStatEntity.getReqAddr();
                    orderHistoryDTO.rentTime = x.getExpRentTime()==null?null:LocalDateTimeUtils.localdateToString(x.getExpRentTime(),GlobalConstant.FORMAT_DATE_STR1);
                    orderHistoryDTO.revertTime = x.getExpRevertTime()==null?null:LocalDateTimeUtils.localdateToString(x.getExpRevertTime(),GlobalConstant.FORMAT_DATE_STR1);

                    //默认车辆显示地址,使用取车服务使用取车地址
                    orderHistoryDTO.addr = renterGoodsDetail.getCarShowAddr();
                    RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByRenterOrderNo(x.getRenterOrderNo(),DeliveryOrderTypeEnum.GET_CAR.getCode());
                    if(null != renterOrderDeliveryEntity){
                        orderHistoryDTO.addr = renterOrderDeliveryEntity.getRenterGetReturnAddr();
                    }

                    orderHistoryDTO.carTypeTxt = renterGoodsDetail.getCarTypeTxt();
                    orderHistoryDTO.carUseType = CarUseTypeEnum.getNameByCode(renterGoodsDetail.getCarUseType());
                    orderHistoryDTO.carGearboxType = GearboxTypeEnum.getNameByCode(renterGoodsDetail.getCarGearboxType());
                    orderHistoryDTO.carStatus = CarStatusEnum.getNameByCode(renterGoodsDetail.getCarStatus());
                    orderHistoryDTO.carDayMileage = renterGoodsDetail.getCarDayMileage();
                    orderHistoryDTO.rentTotalAmt = Math.abs(renterOrderCostEntity.getRentCarAmount());
                    orderHistoryDTO.totalInsurance = Math.abs(renterOrderCostEntity.getBasicEnsureAmount()+renterOrderCostEntity.getBasicEnsureAmount());
                    orderHistoryDTO.avragePrice = renterGoodsDetail.getRenterGoodsPriceDetailDTOList()!=null&&renterGoodsDetail.getRenterGoodsPriceDetailDTOList().size()>0?renterGoodsDetail.getRenterGoodsPriceDetailDTOList().get(0).getCarUnitPrice():null;
                    orderHistoryDTO.dispatchFailReason = "";
                    orderHistoryDTO.isLocal = IsLocalEnum.getNameByCode(renterGoodsDetail.getIsLocal())==null?null:IsLocalEnum.getNameByCode(renterGoodsDetail.getIsLocal())+"本地";
                    orderHistoryDTO.sucessRate = renterGoodsDetail.getSucessRate();
                    orderHistoryDTO.carAge = renterGoodsDetail.getCarAge();
                    orderHistoryDTO.choiceCar = ChoiceCarEnum.getNameByCode(renterGoodsDetail.isChoiceCar()==true?1:0);
                    orderHistoryDTOS.add(orderHistoryDTO);
                });
        OrderHistoryListDTO orderHistoryListDTO = new OrderHistoryListDTO();
        orderHistoryListDTO.setOrderHistoryList(orderHistoryDTOS);
        return ResponseData.success(orderHistoryListDTO);
    }

    /**
     * 根据申请变更表中租客子订单号或者车主的会员号
     * @param renterOrderNo
     * @return
     */
    public String getOwnerMemNo(String renterOrderNo){
        RenterOrderChangeApplyEntity renterOrderChangeApplyEntity = renterOrderChangeApplyService.getRenterOrderChangeApplyByRenterOrderNo(renterOrderNo);
        String ownerOrderNo = renterOrderChangeApplyEntity.getOwnerOrderNo();

        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);

        return ownerOrderEntity.getMemNo();

    }
}
