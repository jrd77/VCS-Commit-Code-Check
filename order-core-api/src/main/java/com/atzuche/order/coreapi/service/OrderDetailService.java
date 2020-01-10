package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeDetailNoTService;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberRightDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.enums.DeliveryOrderTypeEnum;
import com.atzuche.order.coreapi.submitOrder.exception.OrderDetailException;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.service.OwnerHandoverCarInfoService;
import com.atzuche.order.delivery.service.RenterHandoverCarInfoService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderPurchaseDetailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
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
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderDetailService {
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
    private RenterHandoverCarInfoService renterHandoverCarInfoService;
    @Autowired
    private RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired
    private AccountOwnerIncomeDetailNoTService accountOwnerIncomeDetailNoTService;
    @Autowired
    private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    private OwnerHandoverCarInfoService ownerHandoverCarInfoService;

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

        return null;
    }

    private OrderDetailRespDTO orderDetailProxy(OrderDetailReqDTO orderDetailReqDTO) {
        String orderNo = orderDetailReqDTO.getOrderNo();
        String ownerOrderNo = orderDetailReqDTO.getOwnerOrderNo();
        String renterOrderNo = orderDetailReqDTO.getRenterOrderNo();

        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderDetailException();
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);

        //订单状态
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        BeanUtils.copyProperties(orderStatusEntity,orderStatusDTO);

        //统计信息
        OrderSourceStatEntity orderSourceStatEntity = orderSourceStatService.selectByOrderNo(orderNo);
        OrderSourceStatDTO orderSourceStatDTO = new OrderSourceStatDTO();
        BeanUtils.copyProperties(orderSourceStatEntity,orderSourceStatDTO);

        //租客订单
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        RenterOrderDTO renterOrderDTO = new RenterOrderDTO();
        BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);

        //车主订单
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        OwnerOrderDTO ownerOrderDTO = new OwnerOrderDTO();
        BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);

        renterOrderNo = renterOrderNo==null?renterOrderEntity.getRenterOrderNo():renterOrderNo;
        ownerOrderNo = ownerOrderNo==null?ownerOrderEntity.getOwnerOrderNo():ownerOrderNo;

        //租客商品
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderNo, false);
        RenterGoodsDTO renterGoodsDTO = new RenterGoodsDTO();
        BeanUtils.copyProperties(renterGoodsDetail,renterGoodsDTO);

        //车主商品
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, false);
        OwnerGoodsDTO ownerGoodsDTO = new OwnerGoodsDTO();
        BeanUtils.copyProperties(ownerGoodsDetail,ownerGoodsDTO);

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
        OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = ownerHandoverCarInfoService.selectByRenterOrderNoAndType(renterOrderNo, RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue());
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

        return orderDetailRespDTO;
    }

    private OrderStatusRespDTO orderStatusProxy(OrderDetailReqDTO orderDetailReqDTO) {
        String orderNo = orderDetailReqDTO.getOrderNo();
        //主订单状态
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        BeanUtils.copyProperties(orderStatusEntity,orderStatusDTO);

        //车主子订单状态
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        OwnerOrderStatusDTO ownerOrderStatusDTO = new OwnerOrderStatusDTO();
        BeanUtils.copyProperties(ownerOrderEntity,ownerOrderStatusDTO);

        //租客子订单状态
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        RenterOrderDTO renterOrderDTO = new RenterOrderDTO();
        BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);


        //改变中的车主子订单状态
        OwnerOrderEntity changeOwnerByOrder = ownerOrderService.getChangeOwnerByOrderNo(orderNo);


        //改变中的租客子订单状态


        OrderStatusRespDTO orderStatusRespDTO = new OrderStatusRespDTO();


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


}
