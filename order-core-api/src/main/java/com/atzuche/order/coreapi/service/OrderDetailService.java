package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleEntity;
import com.atzuche.order.accountownercost.service.AccountOwnerCostSettleService;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeDetailNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
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
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.CostStatUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberRightDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.*;
import com.atzuche.order.coreapi.modifyorder.exception.NoEffectiveErrException;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterDeliveryAddrEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.service.handover.OwnerHandoverCarService;
import com.atzuche.order.delivery.service.handover.RenterHandoverCarService;
import com.atzuche.order.delivery.vo.delivery.DeliveryOilCostVO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.detain.service.RenterDetainReasonService;
import com.atzuche.order.flow.entity.OrderFlowEntity;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.*;
import com.atzuche.order.ownercost.service.*;
import com.atzuche.order.parentorder.entity.*;
import com.atzuche.order.parentorder.service.*;
import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
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
import com.atzuche.order.settle.entity.AccountDebtReceivableaDetailEntity;
import com.atzuche.order.settle.service.notservice.AccountDebtReceivableaDetailNoTService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
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
    @Autowired
    private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;
    @Autowired
    private AccountDebtReceivableaDetailNoTService accountDebtReceivableaDetailNoTService;
    @Autowired
    private ModifyOrderFeeService modifyOrderFeeService;
    @Autowired
    private CashierNoTService cashierNoTService;
    @Autowired
    private AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired
    private OrderRefundRecordService orderRefundRecordService;
    @Autowired
    private RenterDetainReasonService renterDetainReasonService;


    private static final String UNIT_HOUR = "小时";


    public ResponseData<OrderDetailRespDTO> orderDetailByRenter(OrderDetailReqDTO orderDetailReqDTO){
        log.info("准备获取订单详情orderDetailReqDTO={}", JSON.toJSONString(orderDetailReqDTO));
        ResponseData responseData = new ResponseData();
        try{
            OrderDetailRespDTO orderDetailRespDTO = renterOrderDetailTransProxy(orderDetailReqDTO);
            log.info("准备获取订单详情.result is,orderDetailRespDTO:[{}]", JSON.toJSONString(orderDetailRespDTO));
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
    public ResponseData<OrderDetailRespDTO> orderDetailByOwner(OrderOwnerDetailReqDTO orderOwnerDetailReqDTO){
        log.info("准备获取订单详情orderOwnerDetailReqDTO={}", JSON.toJSONString(orderOwnerDetailReqDTO));
        ResponseData responseData = new ResponseData();
        try{
            OrderDetailRespDTO orderDetailRespDTO = ownerOrderDetailTransProxy(orderOwnerDetailReqDTO);
            responseData.setResCode(ErrorCode.SUCCESS.getCode());
            responseData.setData(orderDetailRespDTO);
            responseData.setResMsg(ErrorCode.SUCCESS.getText());
        }catch (OrderException e){
            log.error("订单详情转化失败orderOwnerDetailReqDTO={}",JSON.toJSONString(orderOwnerDetailReqDTO),e);
            responseData.setResCode(e.getErrorCode());
            responseData.setData(null);
            responseData.setResMsg(e.getErrorMsg());
        }catch (Exception e){
            log.error("订单详情转化失败orderOwnerDetailReqDTO={}",JSON.toJSONString(orderOwnerDetailReqDTO),e);
            responseData.setResCode(ErrorCode.SYS_ERROR.getCode());
            responseData.setData(null);
            responseData.setResMsg(ErrorCode.SYS_ERROR.getText());
        }
        return responseData;
    }

    private OrderDetailRespDTO renterOrderDetailTransProxy(OrderDetailReqDTO orderDetailReqDTO){
        String orderNo = orderDetailReqDTO.getOrderNo();
        String renterOrderNo = orderDetailReqDTO.getRenterOrderNo();
        String ownerOrderNo = orderDetailReqDTO.getOwnerOrderNo();

        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);


        //租客订单
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        RenterOrderDTO renterOrderDTO = null;
        if(renterOrderEntity != null){
            renterOrderDTO = new RenterOrderDTO();
            BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);
            renterOrderNo = renterOrderNo==null?renterOrderEntity.getRenterOrderNo():renterOrderNo;
        }


        //车主订单
        OwnerOrderDTO ownerOrderDTO = new OwnerOrderDTO();
        OwnerGoodsDTO ownerGoodsDTO = null;
        RenterGoodsDTO renterGoodsDTO = null;
        if(ownerOrderNo != null && ownerOrderNo.trim().length()>0){
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
            if(ownerOrderEntity == null){
                log.error("获取车主订单信息为空ownerOrderNo={}",ownerOrderNo);
                throw new OwnerOrderNotFoundException(ownerOrderNo);
            }
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);
        }else{
            //租客商品
            RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderNo, false);
            if(renterGoodsDetail != null){
                renterGoodsDTO = new RenterGoodsDTO();
                BeanUtils.copyProperties(renterGoodsDetail,renterGoodsDTO);
            }
            //车主商品
            OwnerGoodsEntity ownerGoodsEntity = ownerGoodsService.getOwnerGoodsByCarNoAndOrderNo(renterGoodsDetail.getCarNo(), orderNo);
            if(ownerGoodsEntity == null){
                log.info("获取车主商品信息为空carNo{},orderNo={}",renterGoodsDetail.getCarNo(),orderNo);
                throw new OwnerGoodsNotFoundException(renterGoodsDetail.getCarNo(),orderNo);
            }
            ownerGoodsDTO = new OwnerGoodsDTO();
            BeanUtils.copyProperties(ownerGoodsEntity,ownerGoodsDTO);
            ownerOrderNo = ownerGoodsEntity.getOwnerOrderNo();
            //车主订单
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
            if(ownerOrderEntity == null){
                log.error("获取车主订单信息为空ownerOrderNo={}",ownerOrderNo);
                throw new OwnerOrderNotFoundException(ownerOrderNo);
            }
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);
        }

        OrderDetailRespDTO  orderDetailRespDTO = new OrderDetailRespDTO();
        orderDetailRespDTO.order = orderDTO;
        orderDetailRespDTO.ownerOrder = ownerOrderDTO;
        orderDetailRespDTO.renterOrder = renterOrderDTO;
        orderDetailRespDTO.ownerGoods = ownerGoodsDTO;
        orderDetailRespDTO.renterGoods = renterGoodsDTO;
        orderDetailProxy(orderDetailRespDTO,orderNo,renterOrderNo,ownerOrderNo);
        return orderDetailRespDTO;

    }

    private OrderDetailRespDTO ownerOrderDetailTransProxy(OrderOwnerDetailReqDTO orderOwnerDetailReqDTO){
        String orderNo = orderOwnerDetailReqDTO.getOrderNo();
        String ownerMemNo = orderOwnerDetailReqDTO.getOwnerMemNo();
        String ownerOrderNo = orderOwnerDetailReqDTO.getOwnerOrderNo();

        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);

        //车主订单
        OwnerOrderDTO ownerOrderDTO = new OwnerOrderDTO();
        if(ownerOrderNo != null && ownerOrderNo.trim().length()>0){
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
            if(ownerOrderEntity == null){
                log.error("获取车主订单信息为空ownerOrderNo={}",ownerOrderNo);
                throw new OwnerOrderNotFoundException(ownerOrderNo);
            }
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);
        }else if(ownerMemNo != null && ownerMemNo.trim().length()>0){
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerByMemNoAndOrderNo(orderNo,ownerMemNo);
            if(ownerOrderEntity == null){
                log.error("获取车主订单信息为空orderNo={},ownerMemNo={}",orderNo,ownerMemNo);
                throw new OwnerOrderDetailNotFoundException(orderNo,ownerMemNo);
            }
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);
            ownerOrderNo = ownerOrderEntity.getOwnerOrderNo();
        }
        //车主商品
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, false);
        OwnerGoodsDTO ownerGoodsDTO = null;
        if(ownerGoodsDetail == null){
            log.error("获取车主商品信息失败ownerOrderNo={}",ownerOrderNo);
            throw new OwnerGoodsByOwnerOrderNoNotFoundException(ownerOrderNo);
        }
        ownerGoodsDTO = new OwnerGoodsDTO();
        BeanUtils.copyProperties(ownerGoodsDetail,ownerGoodsDTO);

        //租客商品
        RenterGoodsEntity renterGoodsEntity = renterGoodsService.queryCarInfoByOrderNoAndCarNo(orderNo, String.valueOf(ownerGoodsDetail.getCarNo()));
        if(renterGoodsEntity == null){
            log.info("获取租客商品信息失败carNo={},orderNo={}",ownerGoodsDetail.getCarNo(),orderNo);
            throw new RenterGoodsNotFoundException(ownerGoodsDetail.getCarNo(),orderNo);
        }
        RenterGoodsDTO renterGoodsDTO = new RenterGoodsDTO();
        BeanUtils.copyProperties(renterGoodsEntity,renterGoodsDTO);
        String renterOrderNo = renterGoodsEntity.getRenterOrderNo();
        //租客订单
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
        RenterOrderDTO renterOrderDTO = null;
        if(renterOrderEntity != null){
            renterOrderDTO = new RenterOrderDTO();
            BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);
        }

        OrderDetailRespDTO  orderDetailRespDTO = new OrderDetailRespDTO();
        orderDetailRespDTO.order = orderDTO;
        orderDetailRespDTO.ownerOrder = ownerOrderDTO;
        orderDetailRespDTO.renterOrder = renterOrderDTO;
        orderDetailRespDTO.ownerGoods = ownerGoodsDTO;
        orderDetailRespDTO.renterGoods = renterGoodsDTO;
        orderDetailProxy(orderDetailRespDTO,orderNo,renterOrderNo,ownerOrderNo);
        return orderDetailRespDTO;

    }


    private OrderDetailRespDTO orderDetailProxy(OrderDetailRespDTO  orderDetailRespDTO,String orderNo, String renterOrderNo,String ownerOrderNo) {
        log.info("准备获取订单详情orderDetailProxy--->>当前参数转化：orderNo={},renterOrderNo={},ownerOrderNo={}",orderNo,renterOrderNo,ownerOrderNo);
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

        //租客商品
        if(orderDetailRespDTO.renterGoods  == null){
            RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderNo, false);
            RenterGoodsDTO renterGoodsDTO = null;
            if(renterGoodsDetail != null){
                renterGoodsDTO = new RenterGoodsDTO();
                BeanUtils.copyProperties(renterGoodsDetail,renterGoodsDTO);
                orderDetailRespDTO.renterGoods = renterGoodsDTO;
            }
        }


        //车主商品
        if(orderDetailRespDTO.ownerGoods== null){
            OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, false);
            OwnerGoodsDTO ownerGoodsDTO = null;
            if(ownerGoodsDetail != null){
                ownerGoodsDTO = new OwnerGoodsDTO();
                BeanUtils.copyProperties(ownerGoodsDetail,ownerGoodsDTO);
                orderDetailRespDTO.ownerGoods = ownerGoodsDTO;
            }

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
        List<OwnerOrderEntity> ownerOrderEntities = ownerOrderService.queryByOwnerOrderNoAndMemNo(orderNo,ownerMemberDTO.getMemNo());
        List<OrderCancelReasonDTO> orderCancelReasonDTOS = new ArrayList<>();
        if(ownerOrderEntities!=null && ownerOrderEntities.size()>0){
            List<String> ownerOrderNos = ownerOrderEntities.stream().map(x -> x.getOwnerOrderNo()).collect(Collectors.toList());
            List<OrderCancelReasonEntity> orderCancelReasonEntities = orderCancelReasonService.selectListByOrderNos(ownerOrderNos);
            orderCancelReasonEntities.stream().forEach(x->{
                OrderCancelReasonDTO  orderCancelReasonDTO = new OrderCancelReasonDTO();
                BeanUtils.copyProperties(x,orderCancelReasonDTO);
                orderCancelReasonDTOS.add(orderCancelReasonDTO);
            });
        }


        //租客取消详情表
        OrderRefundRecordEntity orderRefundRecordEntity = orderRefundRecordService.getByOrderNo(orderNo);
        OrderRefundRecordDTO orderRefundRecordDTO = null;
        if(orderRefundRecordEntity != null){
            orderRefundRecordDTO = new OrderRefundRecordDTO();
            BeanUtils.copyProperties(orderRefundRecordEntity,orderRefundRecordDTO);
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

        //租客订单费用明细
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
        log.info("获取订单详情-orderDetailProxy获取车主收益ownerMemNo={}",ownerMemberDTO.getMemNo());
        List<AccountOwnerIncomeDetailEntity> accountOwnerIncomeDetailList = accountOwnerIncomeDetailNoTService.selectByOrderNo(orderNo,ownerMemberDTO.getMemNo());
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
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo,ownerMemberDTO.getMemNo());
        List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDeatailDTOList = new ArrayList<>();
        consoleOwnerOrderFineDeatailList.stream().forEach(x->{
            ConsoleOwnerOrderFineDeatailDTO consoleOwnerOrderFineDeatailDTO = new ConsoleOwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,consoleOwnerOrderFineDeatailDTO);
            consoleOwnerOrderFineDeatailDTOList.add(consoleOwnerOrderFineDeatailDTO);
        });
        //配送订单
        List<RenterOrderDeliveryEntity> renterOrderDeliveryList = renterOrderDeliveryService.findRenterOrderListByOrderNo(orderNo);
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
        //配送地址表
        RenterDeliveryAddrEntity renterDeliveryAddrEntity = renterOrderDeliveryService.selectAddrByOrderNo(orderNo);
        RenterDeliveryAddrDTO renterDeliveryAddrDTO = null;
        if(renterDeliveryAddrEntity != null){
            renterDeliveryAddrDTO = new RenterDeliveryAddrDTO();
            BeanUtils.copyProperties(renterDeliveryAddrEntity,renterDeliveryAddrDTO);
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
        //车主收益审核
        List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamineEntities = accountOwnerIncomeExamineNoTService.selectByOwnerOrderNo(ownerOrderNo);
        List<AccountOwnerIncomeExamineDTO> accountOwnerIncomeExamineDTOS = new ArrayList<>();
        accountOwnerIncomeExamineEntities.stream().forEach(x->{
            AccountOwnerIncomeExamineDTO accountOwnerIncomeExamineDTO = new AccountOwnerIncomeExamineDTO();
            BeanUtils.copyProperties(x,accountOwnerIncomeExamineDTO);
            accountOwnerIncomeExamineDTOS.add(accountOwnerIncomeExamineDTO);
        });
        //租客修改申请
        List<RenterOrderChangeApplyEntity> renterOrderChangeApplyEntityList = renterOrderChangeApplyService.getByOrderNo(orderNo);

        List<RenterOrderChangeApplyDTO> renterOrderChangeApplyDTOS = new ArrayList<>();
        renterOrderChangeApplyEntityList.stream().forEach(x->{
            RenterOrderChangeApplyDTO renterOrderChangeApplyDTO = new RenterOrderChangeApplyDTO();
            BeanUtils.copyProperties(x,renterOrderChangeApplyDTO);
            renterOrderChangeApplyDTOS.add(renterOrderChangeApplyDTO);
        });

        RenterOrderChangeApplyDTO renterOrderChangeApplyDTO = filterByAuditStatus(renterOrderChangeApplyEntityList, 0);
        if(renterOrderChangeApplyDTO != null){
            orderDetailRespDTO.changeApplyRenterOrderNo = renterOrderChangeApplyDTO.getRenterOrderNo();
            orderDetailRespDTO.isChangeApply = true;
            Integer ownerRentAmt = 0;
            try{
                ownerRentAmt = modifyOrderFeeService.getOwnerRentAmt(renterOrderChangeApplyDTO.getRenterOrderNo());
            }catch (Exception e){
                log.error("计算预算租金失败modifyOrderFeeService.getOwnerRentAmt renterOrderNo={}",renterOrderChangeApplyDTO.getRenterOrderNo());
            }
            orderDetailRespDTO.changeApplyPreIncomAmt = ownerRentAmt;
        }
        if(filterByAuditStatus(renterOrderChangeApplyEntityList,3)!=null){
            orderDetailRespDTO.isAutoRefuse = true;
        }
        //租客押金
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositService.selectByOrderNo(orderNo);
        AccountRenterDepositDTO accountRenterDepositDTO = null;
        if(accountRenterDepositEntity != null){
            accountRenterDepositDTO =  new AccountRenterDepositDTO();
            BeanUtils.copyProperties(accountRenterDepositEntity,accountRenterDepositDTO);
        }
        //违章押金
        AccountRenterWzDepositEntity accountRenterWzDepositEntity = accountRenterWzDepositNoTService.getAccountRenterWZDepositByOrder(orderNo);
        AccountRenterWzDepositDTO accountRenterWzDepositDTO = null;
        if(accountRenterWzDepositEntity != null){
            accountRenterWzDepositDTO = new AccountRenterWzDepositDTO();
            BeanUtils.copyProperties(accountRenterWzDepositEntity,accountRenterWzDepositDTO);
        }

        //account租车费用
        List<AccountRenterCostDetailEntity> accountRenterCostDetailEntityList = accountRenterCostDetailNoTService.getAccountRenterCostDetailsByOrderNo(orderNo);
        List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOS = new ArrayList<>();
        accountRenterCostDetailEntityList.stream().forEach(x->{
            AccountRenterCostDetailDTO accountRenterCostDetailDTO = new AccountRenterCostDetailDTO();
            BeanUtils.copyProperties(x,accountRenterCostDetailDTO);
            accountRenterCostDetailDTOS.add(accountRenterCostDetailDTO);
        });


        orderDetailRespDTO.orderStatus = orderStatusDTO;
        orderDetailRespDTO.orderSourceStat = orderSourceStatDTO;
        //orderDetailRespDTO.renterGoods = renterGoodsDTO;
        //orderDetailRespDTO.ownerGoods = ownerGoodsDTO;
        orderDetailRespDTO.renterOrderDeliveryGet = renterOrderDeliveryGetDto;
        orderDetailRespDTO.renterOrderDeliveryReturn = renterOrderDeliveryReturnDto;
        orderDetailRespDTO.renterMember = renterMember;
        orderDetailRespDTO.ownerMember = ownerMember;
        orderDetailRespDTO.renterOrderCost = renterOrderCostDTO;
        orderDetailRespDTO.renterHandoverCarInfo = renterHandoverCarInfoDTO;
        orderDetailRespDTO.orderCancelReasons = orderCancelReasonDTOS;
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
        orderDetailRespDTO.accountOwnerIncomeExamineDTOS = accountOwnerIncomeExamineDTOS;
        orderDetailRespDTO.renterOrderChangeApplyDTO = renterOrderChangeApplyDTO;
        orderDetailRespDTO.renterOrderChangeApplyDTOS = renterOrderChangeApplyDTOS;
        orderDetailRespDTO.accountRenterDepositDTO = accountRenterDepositDTO;
        orderDetailRespDTO.accountRenterWzDepositDTO = accountRenterWzDepositDTO;
        orderDetailRespDTO.accountRenterCostDetailDTO = accountRenterCostDetailDTOS;
        orderDetailRespDTO.orderRefundRecordDTO = orderRefundRecordDTO;
        orderDetailRespDTO.renterDeliveryAddrDTO = renterDeliveryAddrDTO;
        return orderDetailRespDTO;
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
    public ResponseData<OwnerOrderDetailRespDTO> ownerOrderDetail(String orderNo, String ownerOrderNo, String ownerMemNo) {
        log.info("准备获取订单详情orderNo={},ownerOrderNo={},ownerMemNo={}", orderNo,ownerOrderNo,ownerMemNo);
        ResponseData responseData = new ResponseData();
        try{
            OwnerOrderDetailRespDTO orderDetailReqDTO = ownerOrderDetailProxy(orderNo,ownerOrderNo,ownerMemNo);
            responseData.setResCode(ErrorCode.SUCCESS.getCode());
            responseData.setData(orderDetailReqDTO);
            responseData.setResMsg(ErrorCode.SUCCESS.getText());
        }catch (OrderException e){
            log.error("车主订单详情转化失败orderNo={},ownerOrderNo={},ownerMemNo={}", orderNo,ownerOrderNo,ownerMemNo,e);
            responseData.setResCode(e.getErrorCode());
            responseData.setData(null);
            responseData.setResMsg(e.getErrorMsg());
        }catch (Exception e){
            log.error("车主订单详情转化异常orderNo={},ownerOrderNo={},ownerMemNo={}", orderNo,ownerOrderNo,ownerMemNo,e);
            responseData.setResCode(ErrorCode.SYS_ERROR.getCode());
            responseData.setData(null);
            responseData.setResMsg(ErrorCode.SYS_ERROR.getText());
        }
        return responseData;
    }

    public ResponseData<OwnerOrderDetailRespDTO> renterOrderDetail(String orderNo, String renterOrderNo) {
        log.info("准备获取订单详情orderNo={},ownerOrderNo={}", orderNo,renterOrderNo);
        ResponseData responseData = new ResponseData();
        try{
            RenterOrderDetailRespDTO renterOrderDetailRespDTO = renterOrderDetailProxy(orderNo,renterOrderNo);
            responseData.setResCode(ErrorCode.SUCCESS.getCode());
            responseData.setData(renterOrderDetailRespDTO);
            responseData.setResMsg(ErrorCode.SUCCESS.getText());
        }catch (OrderException e){
            log.error("租客订单详情转化失败orderNo={},renterOrderNo={}", orderNo,renterOrderNo,e);
            responseData.setResCode(e.getErrorCode());
            responseData.setData(null);
            responseData.setResMsg(e.getErrorMsg());
        }catch (Exception e){
            log.error("租客订单详情转化异常orderNo={},renterOrderNo={}", orderNo,renterOrderNo,e);
            responseData.setResCode(ErrorCode.SYS_ERROR.getCode());
            responseData.setData(null);
            responseData.setResMsg(ErrorCode.SYS_ERROR.getText());
        }
        return responseData;
    }

    private RenterOrderDetailRespDTO renterOrderDetailProxy(String orderNo, String renterOrderNo) {
        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);

        //租客订单
        String newRenterOrderNo = renterOrderNo;
        RenterOrderDTO renterOrderDTO = null;
        RenterOrderEntity renterOrderEntity = null;
        if(renterOrderNo!=null && renterOrderNo.trim().length()>0){
            renterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
        }else{
            renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        }
        if(renterOrderEntity != null){
            renterOrderDTO = new RenterOrderDTO();
            BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);
            newRenterOrderNo = renterOrderEntity.getRenterOrderNo();
        }

        //租客商品
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(newRenterOrderNo, false);
        RenterGoodsDTO renterGoodsDTO = null;
        if(renterGoodsDetail != null){
            renterGoodsDTO = new RenterGoodsDTO();
            BeanUtils.copyProperties(renterGoodsDetail,renterGoodsDTO);
        }
        //会员权益
        RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(newRenterOrderNo, true);
        com.atzuche.order.commons.entity.orderDetailDto.RenterMemberDTO renterMember = new com.atzuche.order.commons.entity.orderDetailDto.RenterMemberDTO();
        BeanUtils.copyProperties(renterMemberDTO,renterMember);
        List<com.atzuche.order.commons.entity.dto.RenterMemberRightDTO> renterMemberRightDTOList = renterMemberDTO.getRenterMemberRightDTOList();
        List<com.atzuche.order.commons.entity.orderDetailDto.RenterMemberRightDTO> renterMemberRightDTOS = new ArrayList<>();
        renterMemberRightDTOList.stream().forEach(x->{
            com.atzuche.order.commons.entity.orderDetailDto.RenterMemberRightDTO renterMemberRightDTO = new com.atzuche.order.commons.entity.orderDetailDto.RenterMemberRightDTO();
            BeanUtils.copyProperties(x,renterMemberRightDTO);
            renterMemberRightDTOS.add(renterMemberRightDTO);
        });
        //租客费用
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo, newRenterOrderNo);
        RenterOrderCostDTO renterOrderCostDTO = new RenterOrderCostDTO();
        BeanUtils.copyProperties(renterOrderCostEntity,renterOrderCostDTO);

        //租客交接车
        RenterHandoverCarInfoEntity renterHandoverCarInfoEntity = renterHandoverCarInfoService.selectByRenterOrderNoAndType(newRenterOrderNo, RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue());
        RenterHandoverCarInfoDTO renterHandoverCarInfoDTO = null;
        if(renterHandoverCarInfoEntity != null){
            renterHandoverCarInfoDTO = new RenterHandoverCarInfoDTO();
            BeanUtils.copyProperties(renterHandoverCarInfoEntity,renterHandoverCarInfoDTO);
        }
        //租客订单费用明细
        List<RenterOrderCostDetailEntity> renterOrderCostDetailList = renterOrderCostDetailService.listRenterOrderCostDetail(orderNo, newRenterOrderNo);
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
        //配送订单
        List<RenterOrderDeliveryEntity> renterOrderDeliveryList = renterOrderDeliveryService.selectByRenterOrderNo(newRenterOrderNo);
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
        List<RenterAdditionalDriverEntity> renterAdditionalDriverList = renterAdditionalDriverService.listDriversByRenterOrderNo(newRenterOrderNo);
        List<RenterAdditionalDriverDTO>  renterAdditionalDriverDTOList = new ArrayList<>();
        renterAdditionalDriverList.stream().forEach(x->{
            RenterAdditionalDriverDTO renterAdditionalDriverDTO = new RenterAdditionalDriverDTO();
            BeanUtils.copyProperties(x,renterAdditionalDriverDTO);
            renterAdditionalDriverDTOList.add(renterAdditionalDriverDTO);
        });

        //租客罚金
        List<RenterOrderFineDeatailEntity> renterOrderFineDeatailList = renterOrderFineDeatailService.getRenterOrderFineDeatailByOwnerOrderNo(newRenterOrderNo);
        List<RenterOrderFineDeatailDTO> renterOrderFineDeatailDTOS = new ArrayList<>();
        renterOrderFineDeatailList.stream().forEach(x->{
            RenterOrderFineDeatailDTO renterOrderFineDeatailDTO = new RenterOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,renterOrderFineDeatailDTO);
            renterOrderFineDeatailDTOS.add(renterOrderFineDeatailDTO);
        });

        //租客补贴
        List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOS = new ArrayList<>();
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntities = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, newRenterOrderNo);
        renterOrderSubsidyDetailEntities.stream().forEach(x->{
            RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();
            BeanUtils.copyProperties(x,renterOrderSubsidyDetailDTO);
            renterOrderSubsidyDetailDTOS.add(renterOrderSubsidyDetailDTO);
        });
        RenterOrderDetailRespDTO renterOrderDetailRespDTO = new RenterOrderDetailRespDTO();
        renterOrderDetailRespDTO.order = orderDTO;
        renterOrderDetailRespDTO.renterOrder = renterOrderDTO;
        renterOrderDetailRespDTO.renterGoods = renterGoodsDTO;
        renterOrderDetailRespDTO.renterOrderDeliveryGet = renterOrderDeliveryGetDto;
        renterOrderDetailRespDTO.renterOrderDeliveryReturn = renterOrderDeliveryReturnDto;
        renterOrderDetailRespDTO.renterMember = renterMember;
        renterOrderDetailRespDTO.renterOrderCost = renterOrderCostDTO;
        renterOrderDetailRespDTO.renterHandoverCarInfo = renterHandoverCarInfoDTO;
        renterOrderDetailRespDTO.renterMemberRightList = renterMemberRightDTOS;
        renterOrderDetailRespDTO.renterOrderCostDetailList = renterOrderCostDetailDTOS;
        renterOrderDetailRespDTO.renterAdditionalDriverList = renterAdditionalDriverDTOList;
        renterOrderDetailRespDTO.renterOrderFineDeatailList = renterOrderFineDeatailDTOS;
        renterOrderDetailRespDTO.renterOrderSubsidyDetailDTOS = renterOrderSubsidyDetailDTOS;
        return renterOrderDetailRespDTO;
    }

    private OwnerOrderDetailRespDTO ownerOrderDetailProxy(String orderNo, String ownerOrderNo, String ownerMemNo){
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

        com.atzuche.order.commons.entity.orderDetailDto.RenterMemberDTO renterMember = null;
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        RenterOrderDTO renterOrderDTO = null;
        if(renterOrderEntity != null){
            //renterOrderDTO = new RenterOrderDTO();
            //BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);
            //会员
            RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderEntity.getRenterOrderNo(), false);
            renterMember = new com.atzuche.order.commons.entity.orderDetailDto.RenterMemberDTO();
            BeanUtils.copyProperties(renterMemberDTO,renterMember);
        }



        //车主订单
        String newOwnerOrderNo = ownerOrderNo;
        String newOwnerMemNo = ownerMemNo;
        OwnerOrderDTO ownerOrderDTO = new OwnerOrderDTO();
        if(ownerOrderNo != null && ownerOrderNo.trim().length()>0){
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
            if(ownerOrderEntity == null){
                log.error("获取车主订单信息为空ownerOrderNo={}",ownerOrderNo);
                throw new OwnerOrderNotFoundException(ownerOrderNo);
            }
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);
            newOwnerMemNo = ownerOrderEntity.getMemNo();
        }else if(ownerMemNo != null && ownerMemNo.trim().length()>0){
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerByMemNoAndOrderNo(orderNo,ownerMemNo);
            if(ownerOrderEntity == null){
                log.error("获取车主订单信息为空orderNo={},ownerMemNo={}",orderNo,ownerMemNo);
                throw new OwnerOrderDetailNotFoundException(orderNo,ownerMemNo);
            }
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);
            newOwnerOrderNo = ownerOrderEntity.getOwnerOrderNo();
        }

        //车主商品
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(newOwnerOrderNo, false);
        OwnerGoodsDTO ownerGoodsDTO = null;
        if(ownerGoodsDetail != null){
            ownerGoodsDTO = new OwnerGoodsDTO();
            BeanUtils.copyProperties(ownerGoodsDetail,ownerGoodsDTO);
        }
        //车主会员
        OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(newOwnerOrderNo, true);
        com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO ownerMember = new com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO();
        BeanUtils.copyProperties(ownerMemberDTO,ownerMember);
        List<OwnerMemberRightDTO> ownerMemberRightDTOList = ownerMemberDTO.getOwnerMemberRightDTOList();
        List<com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberRightDTO> ownerMemberRightDTOS = new ArrayList<>();
        ownerMemberRightDTOList.stream().forEach(x->{
            com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberRightDTO ownerMemberRightDTO = new com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberRightDTO();
            BeanUtils.copyProperties(x,ownerMemberRightDTO);
            ownerMemberRightDTOS.add(ownerMemberRightDTO);
        });

        //车主交接车
        OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = ownerHandoverCarService.selectByRenterOrderNoAndType(newOwnerOrderNo, RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue());
        OwnerHandoverCarInfoDTO ownerHandoverCarInfoDTO = null;
        if(ownerHandoverCarInfoDTO != null){
            ownerHandoverCarInfoDTO = new OwnerHandoverCarInfoDTO();
            BeanUtils.copyProperties(ownerHandoverCarInfoEntity,ownerHandoverCarInfoDTO);
        }
        //车主收益
        List<AccountOwnerIncomeDetailEntity> accountOwnerIncomeDetailList = accountOwnerIncomeDetailNoTService.selectByOrderNo(orderNo,newOwnerMemNo);
        List<AccountOwnerIncomeDetailDTO> accountOwnerIncomeDetailDTOList = new ArrayList<>();
        accountOwnerIncomeDetailList.stream().forEach(x->{
            AccountOwnerIncomeDetailDTO accountOwnerIncomeDetailDTO = new AccountOwnerIncomeDetailDTO();
            BeanUtils.copyProperties(x,accountOwnerIncomeDetailDTO);
            accountOwnerIncomeDetailDTOList.add(accountOwnerIncomeDetailDTO);
        });
        //车主租金
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailList = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(orderNo, newOwnerMemNo);
        List<OwnerOrderPurchaseDetailDTO> ownerOrderPurchaseDetailDTOList = new ArrayList<>();
        ownerOrderPurchaseDetailList.stream().forEach(x->{
            OwnerOrderPurchaseDetailDTO ownerOrderPurchaseDetailDTO = new OwnerOrderPurchaseDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderPurchaseDetailDTO);
            ownerOrderPurchaseDetailDTOList.add(ownerOrderPurchaseDetailDTO);
        });
        //全局的车主订单罚金明细
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo,newOwnerMemNo);
        List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDeatailDTOList = new ArrayList<>();
        consoleOwnerOrderFineDeatailList.stream().forEach(x->{
            ConsoleOwnerOrderFineDeatailDTO consoleOwnerOrderFineDeatailDTO = new ConsoleOwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,consoleOwnerOrderFineDeatailDTO);
            consoleOwnerOrderFineDeatailDTOList.add(consoleOwnerOrderFineDeatailDTO);
        });
        //车主罚金
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailList = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOwnerOrderNo(newOwnerOrderNo);
        List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailDTOS = new ArrayList<>();
        ownerOrderFineDeatailList.stream().forEach(x->{
            OwnerOrderFineDeatailDTO ownerOrderFineDeatailDTO = new OwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,ownerOrderFineDeatailDTO);
            ownerOrderFineDeatailDTOS.add(ownerOrderFineDeatailDTO);
        });

        //车主补贴
        List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS = new ArrayList<>();
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(orderNo, newOwnerOrderNo);
        ownerOrderSubsidyDetailEntities.stream().forEach(x->{
            OwnerOrderSubsidyDetailDTO ownerOrderSubsidyDetailDTO = new OwnerOrderSubsidyDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderSubsidyDetailDTO);
            ownerOrderSubsidyDetailDTOS.add(ownerOrderSubsidyDetailDTO);
        });
        //车主费用
        OwnerOrderCostEntity ownerOrderCostEntity = ownerOrderCostService.getOwnerOrderCostByOwnerOrderNo(newOwnerOrderNo);
        OwnerOrderCostDTO ownerOrderCostDTO = null;
        if(ownerOrderCostEntity != null){
            ownerOrderCostDTO = new OwnerOrderCostDTO();
            BeanUtils.copyProperties(ownerOrderCostEntity,ownerOrderCostDTO);
        }

        //车主配送服务费（车主增值订单）
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailEntities = ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(orderNo, newOwnerOrderNo);
        List<OwnerOrderIncrementDetailDTO> ownerOrderIncrementDetailDTOS = new ArrayList<>();
        ownerOrderIncrementDetailEntities.stream().forEach(x->{
            OwnerOrderIncrementDetailDTO ownerOrderIncrementDetailDTO = new OwnerOrderIncrementDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderIncrementDetailDTO);
            ownerOrderIncrementDetailDTOS.add(ownerOrderIncrementDetailDTO);
        });

        //车主收益审核
        List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamineEntities = accountOwnerIncomeExamineNoTService.selectByOwnerOrderNo(ownerOrderNo);
        List<AccountOwnerIncomeExamineDTO> accountOwnerIncomeExamineDTOS = new ArrayList<>();
        accountOwnerIncomeExamineEntities.stream().forEach(x->{
            AccountOwnerIncomeExamineDTO accountOwnerIncomeExamineDTO = new AccountOwnerIncomeExamineDTO();
            BeanUtils.copyProperties(x,accountOwnerIncomeExamineDTO);
            accountOwnerIncomeExamineDTOS.add(accountOwnerIncomeExamineDTO);
        });


        OwnerOrderDetailRespDTO ownerOrderDetailRespDTO = new OwnerOrderDetailRespDTO();
        ownerOrderDetailRespDTO.order = orderDTO;
        ownerOrderDetailRespDTO.orderStatus = orderStatusDTO;
        ownerOrderDetailRespDTO.orderSourceStat = orderSourceStatDTO;
        ownerOrderDetailRespDTO.renterMemberDTO = renterMember;
        ownerOrderDetailRespDTO.ownerOrder = ownerOrderDTO;
        ownerOrderDetailRespDTO.ownerGoods = ownerGoodsDTO;
        ownerOrderDetailRespDTO.ownerMember = ownerMember;
        ownerOrderDetailRespDTO.ownerHandoverCarInfo = ownerHandoverCarInfoDTO;
        ownerOrderDetailRespDTO.ownerMemberRightList = ownerMemberRightDTOS;
        ownerOrderDetailRespDTO.accountOwnerIncomeDetailList = accountOwnerIncomeDetailDTOList;
        ownerOrderDetailRespDTO.ownerOrderPurchaseDetailList = ownerOrderPurchaseDetailDTOList;
        ownerOrderDetailRespDTO.consoleOwnerOrderFineDetailList = consoleOwnerOrderFineDeatailDTOList;
        ownerOrderDetailRespDTO.ownerOrderFineDeatailList = ownerOrderFineDeatailDTOS;
        ownerOrderDetailRespDTO.ownerOrderSubsidyDetailDTOS = ownerOrderSubsidyDetailDTOS;
        ownerOrderDetailRespDTO.ownerOrderCostDTO = ownerOrderCostDTO;
        ownerOrderDetailRespDTO.accountOwnerIncomeExamineDTOS = accountOwnerIncomeExamineDTOS;
        ownerOrderDetailRespDTO.ownerOrderIncrementDetailDTOS = ownerOrderIncrementDetailDTOS;
        return ownerOrderDetailRespDTO;
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
        orderDetailDTO.status = orderStatusEntity.getStatus();
        orderDetailDTO.orderStatus = OrderStatusEnum.getDescByStatus(orderStatusEntity.getStatus());
        orderDetailDTO.totalRentTime = ChronoUnit.HOURS.between(orderEntity.getExpRentTime(), orderEntity.getExpRevertTime()) + UNIT_HOUR;
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
            if(null != orderStatusEntity.getIsDetain() && orderStatusEntity.getIsDetain() == OrderConstant.YES) {
                accountRenterDepositDTO.setSurplusDepositAmt(0);
            }
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
        //入账的历史欠款
        List<AccountDebtReceivableaDetailEntity> accountDebtReceivableaDetailEntities = accountDebtReceivableaDetailNoTService.getByOrderNoAndMemNo(orderNo, orderEntity.getMemNoRenter());
        List<AccountDebtReceivableaDetailDTO> accountDebtReceivableaDetailDTOist = new ArrayList<>();
        accountDebtReceivableaDetailEntities.stream().forEach(x->{
            AccountDebtReceivableaDetailDTO accountDebtReceivableaDetailDTO = new AccountDebtReceivableaDetailDTO();
            BeanUtils.copyProperties(x,accountDebtReceivableaDetailDTO);
            accountDebtReceivableaDetailDTOist.add(accountDebtReceivableaDetailDTO);
        });

        //收银台
        CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderNo, accountRenterDepositEntity.getMemNo(), DataPayKindConstant.RENT);
        CashierDTO cashierDTO = null;
        if(cashierEntity != null){
            cashierDTO = new CashierDTO();
            BeanUtils.copyProperties(cashierEntity,cashierDTO);
        }

        //租车押金暂扣原因
        List<RenterDetainReasonDTO> dtos = renterDetainReasonService.getListByOrderNo(orderNo);

        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(renterOrderEntity == null){
            throw new RenterOrderEffectiveNotFoundException(orderNo);
        }
        RenterOrderDTO renterOrderDTO = new RenterOrderDTO();
        BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);

        OrderAccountDetailRespDTO orderAccountDetailRespDTO = new OrderAccountDetailRespDTO();
        orderAccountDetailRespDTO.orderDTO = orderDTO;
        orderAccountDetailRespDTO.orderStatusDTO = orderStatusDTO;
        orderAccountDetailRespDTO.renterDepositDetailDTO = renterDepositDetailDTO;
        orderAccountDetailRespDTO.accountRenterDepositDetailDTOList = accountRenterDepositDetailDTOList;
        orderAccountDetailRespDTO.accountRenterDepositDTO = accountRenterDepositDTO;
        orderAccountDetailRespDTO.accountRenterDetainCostDTO = accountRenterDetainCostDTO;
        orderAccountDetailRespDTO.accountRenterDetainDetailDTOList = accountRenterDetainDetailDTOList;
        orderAccountDetailRespDTO.accountRenterCostDetailDTOS = accountRenterCostDetailDTOList;
        orderAccountDetailRespDTO.accountDebtReceivableaDetailDTOS = accountDebtReceivableaDetailDTOist;
        orderAccountDetailRespDTO.cashierDTO = cashierDTO;
        orderAccountDetailRespDTO.detainReasons = dtos;
        orderAccountDetailRespDTO.renterOrderDTO = renterOrderDTO;
        return orderAccountDetailRespDTO;
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
        RenterOrderChangeApplyStatusDTO renterOrderChangeApplyStatusDTO = null;
        RenterOrderChangeApplyEntity renterOrderChangeApply  = renterOrderChangeApplyService.getByOrderNoLimit(orderNo);
        renterOrderChangeApplyStatusDTO = new RenterOrderChangeApplyStatusDTO();
        if(renterOrderChangeApply!=null) {
            BeanUtils.copyProperties(renterOrderChangeApply, renterOrderChangeApplyStatusDTO);
            LocalDateTime createTime = renterOrderChangeApply.getCreateTime();
            String createTimeStr = LocalDateTimeUtils.localdateToString(createTime, GlobalConstant.FORMAT_DATE_STR1);
            renterOrderChangeApplyStatusDTO.setCreateTimeStr(createTimeStr);
            renterOrderChangeApplyStatusDTO.setRenterOrderNo(renterOrderChangeApply.getRenterOrderNo());
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
        OwnerOrderEntity entity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
        if(entity == null){
            log.error("获取订单数据子订单为空ownerOrderNo={}",ownerOrderNo);
            throw new OrderNotFoundException(ownerOrderNo);
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
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo,entity.getMemNo());
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
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        String renterMemNo = orderEntity.getMemNoRenter();
        List<OrderEntity> orderEntityList = orderService.getOrderByRenterMemNo(renterMemNo);
        Optional.ofNullable(orderEntityList)
                .orElseGet(ArrayList::new)
                .stream()
                .forEach(x->{
                    String curOrderNo = x.getOrderNo();
                    OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
                    if(orderStatusEntity == null || orderStatusEntity.getSettleStatus()==null || orderStatusEntity.getSettleStatus() != SettleStatusEnum.SETTLED.getCode()){
                        log.info("dispatchHistory 获取不到订单状态或者未车辆结算（租车费用结算），跳过查询 orderNo={}",curOrderNo);
                        return;
                    }
                    RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(curOrderNo);
                    if(renterOrderEntity == null || renterOrderEntity.getRenterOrderNo() == null){
                        log.info("dispatchHistory 当前订单没有有效的子订单号,跳过查询 orderNo={}",curOrderNo);
                        return;
                    }

                    //车主姓名、车主电话
                    OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
                    if(ownerOrderEntity == null || ownerOrderEntity.getOwnerOrderNo() == null){
                        log.info("dispatchHistory 当前订单没有有效的车主子订单，跳过查询 orderNo={}",curOrderNo);
                        return;
                    }

                    String renterOrderNo = renterOrderEntity.getRenterOrderNo();
                    RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderNo,false);
                    String ownerOrderNo = ownerOrderEntity.getOwnerOrderNo();
                    OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderNo,false);
                    OrderSourceStatEntity orderSourceStatEntity = orderSourceStatService.selectByOrderNo(curOrderNo);
                    RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(curOrderNo,renterOrderNo);

                    OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO();
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
                    RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByRenterOrderNoHistory(renterOrderNo,DeliveryOrderTypeEnum.GET_CAR.getCode());
                    if(null != renterOrderDeliveryEntity){
                        orderHistoryDTO.addr = renterOrderDeliveryEntity.getRenterGetReturnAddr();
                    }

                    orderHistoryDTO.carTypeTxt = renterGoodsDetail.getCarTypeTxt();
                    orderHistoryDTO.carUseType = CarUseTypeEnum.getNameByCode(renterGoodsDetail.getCarUseType());
                    orderHistoryDTO.carGearboxType = GearboxTypeEnum.getNameByCode(renterGoodsDetail.getCarGearboxType());
                    orderHistoryDTO.carStatus = CarStatusEnum.getNameByCode(renterGoodsDetail.getCarStatus());
                    orderHistoryDTO.carDayMileage = renterGoodsDetail.getCarDayMileage();
                    orderHistoryDTO.avragePrice = renterGoodsDetail.getRenterGoodsPriceDetailDTOList()!=null&&renterGoodsDetail.getRenterGoodsPriceDetailDTOList().size()>0?renterGoodsDetail.getRenterGoodsPriceDetailDTOList().get(0).getCarUnitPrice():null;
                    orderHistoryDTO.isLocal = IsLocalEnum.getNameByCode(renterGoodsDetail.getIsLocal())==null?null:IsLocalEnum.getNameByCode(renterGoodsDetail.getIsLocal())+"本地";
                    orderHistoryDTO.sucessRate = renterGoodsDetail.getSucessRate();
                    orderHistoryDTO.carAge = renterGoodsDetail.getCarAge();
                    orderHistoryDTO.choiceCar = ChoiceCarEnum.getNameByCode(renterGoodsDetail.isChoiceCar()==true?1:0);

                    if(renterOrderCostEntity != null){
                        orderHistoryDTO.rentTotalAmt = Math.abs(renterOrderCostEntity.getRentCarAmount()==null?0:renterOrderCostEntity.getRentCarAmount());
                        orderHistoryDTO.totalInsurance = Math.abs( (renterOrderCostEntity.getBasicEnsureAmount()==null?0:renterOrderCostEntity.getBasicEnsureAmount())
                                +(renterOrderCostEntity.getComprehensiveEnsureAmount()==null?0:renterOrderCostEntity.getComprehensiveEnsureAmount()));
                    }else{
                        orderHistoryDTO.rentTotalAmt = 0;
                        orderHistoryDTO.totalInsurance = 0;
                    }
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

    public ProcessRespDTO queryInProcess(){
        ProcessRespDTO processRespDTO = new ProcessRespDTO();
        List<OrderStatusEntity> orderStatusEntityList =  orderStatusService.queryInProcess();
        List<OrderStatusDTO> orderStatusDTOList = new ArrayList<>();
        orderStatusEntityList.stream().forEach(x->{
            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
            BeanUtils.copyProperties(x,orderStatusDTO);
            orderStatusDTOList.add(orderStatusDTO);
            });

        List<String> orderNos = orderStatusEntityList
                .stream()
                .map(x -> x.getOrderNo())
                .collect(Collectors.toList());
        List<OrderEntity> orderEntityList = orderService.getByOrderNos(orderNos);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        orderEntityList.stream().forEach(x->{
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(x,orderDTO);
            orderDTOS.add(orderDTO);
        });

        processRespDTO.setOrderDTOs(orderDTOS);
        processRespDTO.setOrderStatusDTOs(orderStatusDTOList);
        return processRespDTO;
    }

    public OrderDetailRespDTO queryChangeApplyByOwnerOrderNo(String ownerOrderNo) {
        OrderDetailRespDTO orderDetailRespDTO = new OrderDetailRespDTO();
        //主订单
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
        OwnerOrderDTO ownerOrderDTO = null;
        if(ownerOrderEntity == null){
            log.error("车主子订单号获取订单为空ownerOrderNo={}",ownerOrderNo);
            throw new OwnerOrderNotFoundException(ownerOrderNo);
        }
        ownerOrderDTO = new OwnerOrderDTO();
        BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);


        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(ownerOrderEntity.getOrderNo());
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",ownerOrderEntity.getOrderNo());
            throw new OrderNotFoundException(ownerOrderEntity.getOrderNo());
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);

        //订单状态
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderEntity.getOrderNo());
        OrderStatusDTO orderStatusDTO = null;
        if(orderStatusEntity != null){
            orderStatusDTO = new OrderStatusDTO();
            BeanUtils.copyProperties(orderStatusEntity,orderStatusDTO);
        }

        //车主费用
        OwnerOrderCostEntity ownerOrderCostEntity = ownerOrderCostService.getOwnerOrderCostByOwnerOrderNo(ownerOrderNo);
        OwnerOrderCostDTO ownerOrderCostDTO = null;
        if(ownerOrderCostEntity != null){
            ownerOrderCostDTO = new OwnerOrderCostDTO();
            BeanUtils.copyProperties(ownerOrderCostEntity,ownerOrderCostDTO);
        }

        //租客修改申请
        List<RenterOrderChangeApplyEntity> renterOrderChangeApplyEntityList = renterOrderChangeApplyService.getByOrderNo(orderEntity.getOrderNo());
        RenterOrderChangeApplyDTO renterOrderChangeApplyEntity = filterByAuditStatus(renterOrderChangeApplyEntityList, 0);
        RenterOrderChangeApplyDTO renterOrderChangeApplyDTO = null;

        if(renterOrderChangeApplyEntity != null){
            orderDetailRespDTO.changeApplyRenterOrderNo = renterOrderChangeApplyEntity.getRenterOrderNo();
            orderDetailRespDTO.isChangeApply = true;
            renterOrderChangeApplyDTO = new RenterOrderChangeApplyDTO();
            BeanUtils.copyProperties(renterOrderChangeApplyEntity,renterOrderChangeApplyDTO);
            Integer ownerRentAmt = 0;
            try{
                ownerRentAmt = modifyOrderFeeService.getOwnerRentAmt(renterOrderChangeApplyEntity.getRenterOrderNo());
            }catch (Exception e){
                log.error("计算预算租金失败modifyOrderFeeService.getOwnerRentAmt renterOrderNo={}",renterOrderChangeApplyEntity.getRenterOrderNo());
            }
            orderDetailRespDTO.changeApplyPreIncomAmt = ownerRentAmt;
        }
        orderDetailRespDTO.order = orderDTO;
        orderDetailRespDTO.orderStatus = orderStatusDTO;
        orderDetailRespDTO.ownerOrder = ownerOrderDTO;
        orderDetailRespDTO.renterOrderChangeApplyDTO = renterOrderChangeApplyDTO;
        orderDetailRespDTO.ownerOrderCostDTO = ownerOrderCostDTO;
        return orderDetailRespDTO;
    }

    private RenterOrderChangeApplyDTO filterByAuditStatus(List<RenterOrderChangeApplyEntity> list,int auditStatus){
        Optional<RenterOrderChangeApplyEntity> first = Optional.ofNullable(list).orElseGet(ArrayList::new)
                .stream()
                .filter(x -> auditStatus == x.getAuditStatus())
                .findFirst();

        if(first.isPresent()){
            RenterOrderChangeApplyDTO renterOrderChangeApplyDTO = new RenterOrderChangeApplyDTO();
            BeanUtils.copyProperties(first.get(),renterOrderChangeApplyDTO);
            return renterOrderChangeApplyDTO;
        }
        return null;
    }


    public OrderNoListDTO getOrderNoAll() {
        log.info("准备获取所有订单号");
        OrderNoListDTO orderNoListDTO = new OrderNoListDTO();
        List<String> orderNos = orderService.getorderNoAll();
        log.info("获取所有订单号，当前订单数量 size={}",orderNos.size());
        orderNoListDTO.setOrderNo(orderNos);
        return orderNoListDTO;
    }

    public ProcessRespDTO queryRefuse() {
        ProcessRespDTO processRespDTO = new ProcessRespDTO();
        List<OrderStatusEntity> orderStatusEntityList =  orderStatusService.queryByStatus(Arrays.asList(OrderStatusEnum.CLOSED));
        List<OrderStatusDTO> orderStatusDTOList = new ArrayList<>();
        List<String> orderNos = orderStatusEntityList
                .stream()
                .map(x -> x.getOrderNo())
                .collect(Collectors.toList());
        int maxLen = 2000;
        int size = orderNos.size();
        log.info("当前订单条数size={}",size);
        int count = size%maxLen==0?(size / maxLen):(size / maxLen) + 1;
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for(int i=0;i<count;i++){
            int toIndex = maxLen * i;
            int fromIndex = (toIndex + maxLen)>=size?size:toIndex + maxLen;
            List<String> curOrderNos = orderNos.subList(toIndex,fromIndex);
            List<OrderEntity> orderEntityList = orderService.getByOrderNos(curOrderNos);
            orderEntityList.stream().forEach(x->{
                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(x,orderDTO);
                orderDTOS.add(orderDTO);
            });
        }
        processRespDTO.setOrderDTOs(orderDTOS);
        processRespDTO.setOrderStatusDTOs(orderStatusDTOList);
        return processRespDTO;
    }

    public static void main(String[] args) {
        System.out.println(1001/1000);
    }

}
