package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.OrderStatus;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderResVO;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderDTO;
import com.atzuche.order.parentorder.dto.OrderSourceStatDTO;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.dto.ParentOrderDTO;
import com.atzuche.order.parentorder.service.ParentOrderService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.RenterOrderResVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class SubmitOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderService.class);

    @Autowired
    private MemberService memberService;
    @Autowired
    private CarService carService;
    @Resource
    private UniqueOrderNoService uniqueOrderNoService;
    @Resource
    private ParentOrderService parentOrderService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private RenterMemberService renterMemberService;
    @Autowired
    private OwnerMemberService ownerMemberService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private DeliveryCarService deliveryCarService;

    /**
     * 提交订单
     *
     * @param normalOrderReqVO 下单请求信息
     * @return NormalOrderResVO 下单返回结果
     */
    public NormalOrderResVO submitOrder(NormalOrderReqVO normalOrderReqVO) {
        //1.请求参数处理
        OrderReqContext reqContext = new OrderReqContext();
        reqContext.setNormalOrderReqVO(normalOrderReqVO);
        reqContext.setRenterMemberDto(memberService.getRenterMemberInfo(normalOrderReqVO.getMemNo()));

        CarService.CarDetailReqVO carDetailReqVO = new CarService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(StringUtils.isBlank(normalOrderReqVO.getCarAddrIndex()) ? 0 : Integer.parseInt(normalOrderReqVO.getCarAddrIndex()));
        carDetailReqVO.setCarNo(normalOrderReqVO.getCarNo());
        carDetailReqVO.setRentTime(normalOrderReqVO.getRentTime());
        carDetailReqVO.setRevertTime(normalOrderReqVO.getRevertTime());
        carDetailReqVO.setUseSpecialPrice(false);
        RenterGoodsDetailDTO renterGoodsDetailDTO = carService.getRenterGoodsDetail(carDetailReqVO);
        reqContext.setRenterGoodsDetailDto(renterGoodsDetailDTO);

        reqContext.setOwnerGoodsDetailDto(null);
        reqContext.setOwnerMemberDto(memberService.getOwnerMemberInfo(""));
        //2.下单校验
        //2.1库存
        //2.2风控
        //2.3校验链



        //3.生成主订单号
        String orderNo = uniqueOrderNoService.getOrderNo();
        //4.创建租客子订单
        //4.1.生成租客子订单号
        String renterOrderNo = uniqueOrderNoService.getRenterOrderNo(orderNo);
        //4.2.调用租客订单模块处理租客订单相关业务
        RenterOrderResVO renterOrderResVO = renterOrderService.generateRenterOrderInfo(null);
        //4.3.接收租客订单返回信息

        //4.4.租客商品信息处理
        renterGoodsService.save(null);

        //4.5.租客信息处理
        //4.6.租客权益信息处理
        renterMemberService.save(null);

        //5.创建车主子订单
        //5.1.生成车主子订单号
        String ownerOrderNo = uniqueOrderNoService.getOwnerOrderNo(orderNo);
        //5.2.调用车主订单模块处理车主订单相关业务
        ownerOrderService.generateRenterOrderInfo(null);
        //5.3.接收车主订单返回信息

        //5.4.车主商品
        ownerGoodsService.save(null);

        //5.5.车主会员
        ownerMemberService.save(null);

        //配送订单处理..............
        deliveryCarService.addRenYunFlowOrderInfo(null);


        //6.主订单相关信息处理
        ParentOrderDTO parentOrderDTO = new ParentOrderDTO();
        //6.1主订单信息处理
        OrderDTO orderDTO = buildOrderDTO(normalOrderReqVO);
        orderDTO.setOrderNo(orderNo);
        orderDTO.setRiskAuditId(null);
        parentOrderDTO.setOrderDTO(orderDTO);

        //6.2主订单扩展信息(统计信息)处理
        OrderSourceStatDTO orderSourceStatDTO = buildOrderSourceStatDTO(normalOrderReqVO);
        orderSourceStatDTO.setOrderNo(orderNo);
        parentOrderDTO.setOrderSourceStatDTO(orderSourceStatDTO);

        //6.3主订单状态信息(统计信息)处理
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        orderStatusDTO.setIsDispatch(OrderConstant.NO);
        orderStatusDTO.setDispatchStatus(OrderConstant.NO);
        if (null == renterGoodsDetailDTO.getReplyFlag() || renterGoodsDetailDTO.getReplyFlag() == OrderConstant.NO) {
            orderStatusDTO.setStatus(OrderStatus.TO_CONFIRM.getStatus());
        } else {
            orderStatusDTO.setStatus(OrderStatus.TO_PAY.getStatus());
        }
        parentOrderDTO.setOrderStatusDTO(orderStatusDTO);
        parentOrderService.saveParentOrderInfo(parentOrderDTO);

        //6.4 order_flow


        //7.订单完成事件发送

        //8.组装接口返回


        return new NormalOrderResVO();
    }


    /**
     * 组装主订单基本信息
     *
     * @param normalOrderReqVO 下单请求参数
     * @return OrderDTO 主订单基本信息
     */
    private OrderDTO buildOrderDTO(NormalOrderReqVO normalOrderReqVO) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setMemNoRenter(normalOrderReqVO.getMemNo());
        orderDTO.setCategory(Integer.valueOf(normalOrderReqVO.getOrderCategory()));
        orderDTO.setCityCode(normalOrderReqVO.getCityCode());
        orderDTO.setCityName(normalOrderReqVO.getCityName());
        orderDTO.setEntryCode(normalOrderReqVO.getSceneCode());
        orderDTO.setSource(Integer.valueOf(normalOrderReqVO.getSource()));
        orderDTO.setExpRentTime(normalOrderReqVO.getRentTime());
        orderDTO.setExpRevertTime(normalOrderReqVO.getRevertTime());
        //orderDTO.setIsFreeDeposit(Integer.valueOf(normalOrderReqVO.getFreeDoubleTypeId()));
        orderDTO.setIsOutCity(normalOrderReqVO.getIsLeaveCity());
        orderDTO.setReqTime(LocalDateTime.now());
        orderDTO.setIsUseAirPortService(normalOrderReqVO.getUseAirportService());

        LOGGER.info("Build order dto,result is ,orderDTO:[{}]", JSON.toJSONString(orderDTO));
        return orderDTO;
    }


    /**
     * 组装主订单来源统计信息
     *
     * @param normalOrderReqVO 下单请求参数
     * @return OrderSourceStatDTO 主订单来源统计信息
     */
    private OrderSourceStatDTO buildOrderSourceStatDTO(NormalOrderReqVO normalOrderReqVO) {
        OrderSourceStatDTO orderSourceStatDTO = new OrderSourceStatDTO();
        BeanCopier beanCopier = BeanCopier.create(NormalOrderReqVO.class, OrderSourceStatDTO.class, false);
        beanCopier.copy(normalOrderReqVO, orderSourceStatDTO, null);

        //差异处理
        orderSourceStatDTO.setAppVersion(normalOrderReqVO.getAppVersion());
        orderSourceStatDTO.setCategory(normalOrderReqVO.getOrderCategory());
        orderSourceStatDTO.setEntryCode(normalOrderReqVO.getSceneCode());
        orderSourceStatDTO.setModuleName(normalOrderReqVO.getModuleName());
        orderSourceStatDTO.setFunctionName(normalOrderReqVO.getFunctionName());
        orderSourceStatDTO.setOaid(normalOrderReqVO.getOAID());
        orderSourceStatDTO.setImei(normalOrderReqVO.getIMEI());
        orderSourceStatDTO.setOs(normalOrderReqVO.getOS());
        orderSourceStatDTO.setAppChannelId(normalOrderReqVO.getAppChannelId());
        orderSourceStatDTO.setAndroidId(normalOrderReqVO.getAndroidID());
        return orderSourceStatDTO;
    }


}
