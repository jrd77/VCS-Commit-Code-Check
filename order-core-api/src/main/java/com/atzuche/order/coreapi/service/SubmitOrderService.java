package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.ListUtil;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.OrderStatus;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderResVO;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderReqDTO;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderDTO;
import com.atzuche.order.parentorder.dto.OrderSourceStatDTO;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.dto.ParentOrderDTO;
import com.atzuche.order.parentorder.service.ParentOrderService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.RenterOrderCarDepositResVO;
import com.atzuche.order.renterorder.vo.RenterOrderIllegalResVO;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.atzuche.order.renterorder.vo.RenterOrderResVO;
import com.autoyol.car.api.feign.api.CarDetailQueryFeignApi;
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
    @Autowired
    private CarDetailQueryFeignApi carDetailQueryFeignApi;
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
    private CashierService cashierService;
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

        RenterMemberDTO renterMemberDTO =
                memberService.getRenterMemberInfo(String.valueOf(normalOrderReqVO.getMemNo()));
        reqContext.setRenterMemberDto(renterMemberDTO);
        CarService.CarDetailReqVO carDetailReqVO = new CarService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(StringUtils.isBlank(normalOrderReqVO.getCarAddrIndex()) ? 0 : Integer.parseInt(normalOrderReqVO.getCarAddrIndex()));
        carDetailReqVO.setCarNo(normalOrderReqVO.getCarNo());
        carDetailReqVO.setRentTime(normalOrderReqVO.getRentTime());
        carDetailReqVO.setRevertTime(normalOrderReqVO.getRevertTime());
        carDetailReqVO.setUseSpecialPrice(false);
        RenterGoodsDetailDTO renterGoodsDetailDTO = carService.getRenterGoodsDetail(carDetailReqVO);
        reqContext.setRenterGoodsDetailDto(renterGoodsDetailDTO);
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = carService.getOwnerGoodsDetail(renterGoodsDetailDTO);
        reqContext.setOwnerGoodsDetailDto(ownerGoodsDetailDTO);
        OwnerMemberDTO ownerMemberDTO = memberService.getOwnerMemberInfo(renterGoodsDetailDTO.getOwnerMemNo());
        reqContext.setOwnerMemberDto(ownerMemberDTO);
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
        RenterOrderReqVO renterOrderReqVO = buildRenterOrderReqVO(orderNo, renterOrderNo, reqContext);
        RenterOrderResVO renterOrderResVO = renterOrderService.generateRenterOrderInfo(renterOrderReqVO);
        //4.3.接收租客订单返回信息
        //4.3.1 车辆押金处理
        BeanCopier beanCopierCarDeposit = BeanCopier.create(RenterOrderCarDepositResVO.class,
                CreateOrderRenterDepositReqVO.class
                , false);
        CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO = new CreateOrderRenterDepositReqVO();
        beanCopierCarDeposit.copy(renterOrderResVO.getRenterOrderCarDepositResVO(), createOrderRenterDepositReqVO, null);
        cashierService.insertRenterDeposit(createOrderRenterDepositReqVO);
        //4.3.2 违章押金处理
        BeanCopier beanCopierIllegal = BeanCopier.create(RenterOrderIllegalResVO.class, CreateOrderRenterWZDepositReqVO.class
                , false);
        CreateOrderRenterWZDepositReqVO renterOrderIllegalDepositReq = new CreateOrderRenterWZDepositReqVO();
        beanCopierIllegal.copy(renterOrderResVO.getRenterOrderIllegalResVO(), renterOrderIllegalDepositReq, null);
        cashierService.insertRenterWZDeposit(renterOrderIllegalDepositReq);

        //4.4.租客商品信息处理
        renterGoodsDetailDTO.setOrderNo(orderNo);
        renterGoodsDetailDTO.setRenterOrderNo(renterOrderNo);
        renterGoodsService.save(renterGoodsDetailDTO);

        //4.5.租客信息处理
        //4.6.租客权益信息处理
        renterMemberDTO.setOrderNo(orderNo);
        renterMemberDTO.setRenterOrderNo(renterOrderNo);
        renterMemberDTO.setMemNo(normalOrderReqVO.getMemNo().toString());
        renterMemberService.save(renterMemberDTO);

        //5.创建车主子订单
        //5.1.生成车主子订单号
        String ownerOrderNo = uniqueOrderNoService.getOwnerOrderNo(orderNo);
        //5.2.调用车主订单模块处理车主订单相关业务
        OwnerOrderReqDTO ownerOrderReqDTO = buildOwnerOrderReqDTO(orderNo, ownerOrderNo, reqContext);
        ownerOrderReqDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());
        ownerOrderService.generateRenterOrderInfo(ownerOrderReqDTO);
        //5.3.接收车主订单返回信息

        //5.4.车主商品
        ownerGoodsDetailDTO.setOrderNo(orderNo);
        ownerGoodsDetailDTO.setOwnerOrderNo(ownerOrderNo);
        ownerGoodsDetailDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());
        ownerGoodsService.save(ownerGoodsDetailDTO);
        //5.5.车主会员
        ownerMemberDTO.setOwnerOrderNo(orderNo);
        ownerMemberDTO.setOwnerOrderNo(ownerOrderNo);
        ownerMemberDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());
        ownerMemberService.save(ownerMemberDTO);

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


        //7. 优惠券绑定、凹凸币扣除等


        //8.订单完成事件发送


        //end 组装接口返回


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
        orderDTO.setIsFreeDeposit(StringUtils.isBlank(normalOrderReqVO.getFreeDoubleTypeId())
                || Integer.parseInt(normalOrderReqVO.getFreeDoubleTypeId()) == FreeDepositTypeEnum.CONSUME.getCode() ?
                0 : 1);
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

        LOGGER.info("Build order source stat dto,result is ,orderSourceStatDTO:[{}]", JSON.toJSONString(orderSourceStatDTO));
        return orderSourceStatDTO;
    }


    /**
     * 租客订单请求参数封装
     *
     * @param orderNo       主订单号
     * @param renterOrderNo 租客子订单号
     * @param reqContext    下单请求参数
     * @return RenterOrderReqVO 租客订单请求参数
     */
    private RenterOrderReqVO buildRenterOrderReqVO(String orderNo, String renterOrderNo, OrderReqContext reqContext) {

        RenterOrderReqVO renterOrderReqVO = new RenterOrderReqVO();
        renterOrderReqVO.setOrderNo(orderNo);
        renterOrderReqVO.setRenterOrderNo(renterOrderNo);

        BeanCopier beanCopier = BeanCopier.create(NormalOrderReqVO.class, RenterOrderReqVO.class, false);
        beanCopier.copy(reqContext.getNormalOrderReqVO(), renterOrderReqVO, null);

        NormalOrderReqVO normalOrderReqVO = reqContext.getNormalOrderReqVO();
        renterOrderReqVO.setEntryCode(normalOrderReqVO.getSceneCode());
        renterOrderReqVO.setSource(Integer.valueOf(normalOrderReqVO.getSource()));
        String driverIds = normalOrderReqVO.getDriverIds();
        renterOrderReqVO.setDriverIds(ListUtil.parseString(driverIds, ","));
        renterOrderReqVO.setGetCarBeforeTime(60);
        renterOrderReqVO.setReturnCarAfterTime(60);

        RenterGoodsDetailDTO goodsDetail = reqContext.getRenterGoodsDetailDto();
        renterOrderReqVO.setGuidPrice(goodsDetail.getCarGuidePrice());
        renterOrderReqVO.setCarSurplusPrice(goodsDetail.getCarSurplusPrice());
        renterOrderReqVO.setInmsrp(goodsDetail.getCarInmsrp());
        renterOrderReqVO.setBrandId(goodsDetail.getBrand());
        renterOrderReqVO.setTypeId(goodsDetail.getType());
        renterOrderReqVO.setLicenseDay(goodsDetail.getLicenseDay());
        renterOrderReqVO.setLabelIds(goodsDetail.getLabelIds());
        renterOrderReqVO.setRenterGoodsPriceDetailDTOList(goodsDetail.getRenterGoodsPriceDetailDTOList());

        RenterMemberDTO renterMember = reqContext.getRenterMemberDto();
        renterOrderReqVO.setCertificationTime(renterMember.getCertificationTime());
        renterOrderReqVO.setIsNew(null == renterMember.getIsNew() || renterMember.getIsNew() == 0);
        renterOrderReqVO.setRenterMemberRightDTOList(renterMember.getRenterMemberRightDTOList());
        renterOrderReqVO.setCommUseDriverList(renterMember.getCommUseDriverList());

        LOGGER.info("Build renter order reqVO,result is ,renterOrderReqVO:[{}]",
                JSON.toJSONString(renterOrderReqVO));
        return renterOrderReqVO;
    }


    /**
     * 车主订单请求参数封装
     *
     * @param orderNo      主订单号
     * @param ownerOrderNo 车主订单号
     * @param reqContext   下单请求参数
     * @return OwnerOrderReqDTO 车主订单请求参数
     */
    private OwnerOrderReqDTO buildOwnerOrderReqDTO(String orderNo, String ownerOrderNo, OrderReqContext reqContext) {

        OwnerOrderReqDTO ownerOrderReqDTO = new OwnerOrderReqDTO();
        ownerOrderReqDTO.setOrderNo(orderNo);
        ownerOrderReqDTO.setOwnerOrderNo(ownerOrderNo);



        LOGGER.info("Build owner order reqDTO,result is ,ownerOrderReqDTO:[{}]",
                JSON.toJSONString(ownerOrderReqDTO));

        return ownerOrderReqDTO;
    }


}
