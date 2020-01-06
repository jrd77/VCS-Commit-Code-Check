package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.commons.ListUtil;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.coreapi.entity.vo.req.AutoCoinDeductReqVO;
import com.atzuche.order.coreapi.entity.vo.req.CarRentTimeRangeReqVO;
import com.atzuche.order.coreapi.entity.vo.req.OwnerCouponBindReqVO;
import com.atzuche.order.coreapi.entity.vo.req.SubmitOrderRiskCheckReqVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.coreapi.utils.BizAreaUtil;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderReqDTO;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderDTO;
import com.atzuche.order.parentorder.dto.OrderSourceStatDTO;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.dto.ParentOrderDTO;
import com.atzuche.order.parentorder.service.ParentOrderService;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 订单业务处理类
 *
 * @author pengcheng.fu
 * @date 2020/01/02 14:37
 */
@Service
public class SubmitOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderService.class);

    @Autowired
    private MemberService memberService;
    @Autowired
    private GoodsService goodsService;
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
    @Autowired
    private CouponAndCoinHandleService couponAndCoinHandleService;
    @Autowired
    private OrderFlowService orderFlowService;
    @Autowired
    private RenterCommodityService renterCommodityService;
    @Autowired
    private SubmitOrderRiskAuditService submitOrderRiskAuditService;


    /**
     * 提交订单
     *
     * @param orderReqVO 下单请求信息
     * @return OrderResVO 下单返回结果
     */
    @Transactional
    public OrderResVO submitOrder(OrderReqVO orderReqVO) {
        LocalDateTime reqTime = LocalDateTime.now();
        //1.请求参数处理
        OrderReqContext reqContext = new OrderReqContext();
        reqContext.setOrderReqVO(orderReqVO);
        //租客会员信息
        RenterMemberDTO renterMemberDTO =
                memberService.getRenterMemberInfo(String.valueOf(orderReqVO.getMemNo()));
        reqContext.setRenterMemberDto(renterMemberDTO);
        //租客商品明细
        RenterGoodsDetailDTO renterGoodsDetailDTO = goodsService.getRenterGoodsDetail(buildCarDetailReqVO(orderReqVO));
        reqContext.setRenterGoodsDetailDto(renterGoodsDetailDTO);

        //一天一价分组
        renterGoodsDetailDTO = renterCommodityService.setPriceAndGroup(renterGoodsDetailDTO);

        //车主商品明细
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = goodsService.getOwnerGoodsDetail(renterGoodsDetailDTO);
        reqContext.setOwnerGoodsDetailDto(ownerGoodsDetailDTO);
        //车主会员信息
        OwnerMemberDTO ownerMemberDTO = memberService.getOwnerMemberInfo(renterGoodsDetailDTO.getOwnerMemNo());
        reqContext.setOwnerMemberDto(ownerMemberDTO);
        //2.下单校验
        //2.1库存
        //2.2风控
        Integer riskAuditId = null;
//        Integer riskAuditId = submitOrderRiskAuditService.check(buildSubmitOrderRiskCheckReqVO(orderReqVO, reqTime));
        //2.3校验链

        //提前延后时间计算
        CarRentTimeRangeResVO carRentTimeRangeResVO = goodsService.getCarRentTimeRange(buildCarRentTimeRangeReqVO(orderReqVO));
        //3.生成主订单号
        String orderNo = uniqueOrderNoService.getOrderNo();
        //4.创建租客子订单
        //4.1.生成租客子订单号
        String renterOrderNo = uniqueOrderNoService.getRenterOrderNo(orderNo);
        //4.2.调用租客订单模块处理租客订单相关业务
        RenterOrderResVO renterOrderResVO = renterOrderService.generateRenterOrderInfo(buildRenterOrderReqVO(orderNo, renterOrderNo, reqContext, carRentTimeRangeResVO));
        //4.3.接收租客订单返回信息
        //4.3.1 车辆押金处理
        BeanCopier beanCopierCarDeposit = BeanCopier.create(RenterOrderCarDepositResVO.class,
                CreateOrderRenterDepositReqVO.class, false);
        CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO = new CreateOrderRenterDepositReqVO();
        beanCopierCarDeposit.copy(renterOrderResVO.getRenterOrderCarDepositResVO(), createOrderRenterDepositReqVO, null);
        cashierService.insertRenterDeposit(createOrderRenterDepositReqVO);
        //4.3.2 违章押金处理
        BeanCopier beanCopierIllegal = BeanCopier.create(RenterOrderIllegalResVO.class,
                CreateOrderRenterWZDepositReqVO.class, false);
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
        renterMemberService.save(renterMemberDTO);

        //5.创建车主子订单
        //5.1.生成车主子订单号
        String ownerOrderNo = uniqueOrderNoService.getOwnerOrderNo(orderNo);
        //5.2.调用车主订单模块处理车主订单相关业务
        OwnerOrderReqDTO ownerOrderReqDTO = buildOwnerOrderReqDTO(orderNo, ownerOrderNo, reqContext);
        ownerOrderReqDTO.setOwnerOrderSubsidyDetailEntity(buildOwnerOrderSubsidyDetailEntity(orderNo, ownerOrderNo,
                renterGoodsDetailDTO.getOwnerMemNo(), renterOrderResVO.getOwnerCoupon()));
        ownerOrderReqDTO.setOwnerOrderPurchaseDetailEntity(buildOwnerOrderPurchaseDetailEntity(orderNo, ownerOrderNo,
                renterGoodsDetailDTO.getOwnerMemNo(), renterOrderResVO.getRentAmtEntity()));

        ownerOrderReqDTO.setShowRentTime(null == carRentTimeRangeResVO ? null : carRentTimeRangeResVO.getAdvanceStartDate());
        ownerOrderReqDTO.setShowRevertTime(null == carRentTimeRangeResVO ? null : carRentTimeRangeResVO.getDelayEndDate());
        ownerOrderReqDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());
        ownerOrderService.generateRenterOrderInfo(ownerOrderReqDTO);
        //5.3.接收车主订单返回信息

        //5.4.车主商品
        ownerGoodsDetailDTO.setOrderNo(orderNo);
        ownerGoodsDetailDTO.setOwnerOrderNo(ownerOrderNo);
        ownerGoodsDetailDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());
        ownerGoodsService.save(ownerGoodsDetailDTO);
        //5.5.车主会员
        ownerMemberDTO.setOrderNo(orderNo);
        ownerMemberDTO.setOwnerOrderNo(ownerOrderNo);
        ownerMemberDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());

        ownerMemberService.save(ownerMemberDTO);

        //配送订单处理..............
        deliveryCarService.addFlowOrderInfo(null == carRentTimeRangeResVO ? null :
                        carRentTimeRangeResVO.getGetMinutes(), null == carRentTimeRangeResVO ? null :
                        carRentTimeRangeResVO.getReturnMinutes(),
                reqContext);

        //6.主订单相关信息处理
        ParentOrderDTO parentOrderDTO = new ParentOrderDTO();
        //6.1主订单信息处理
        parentOrderDTO.setOrderDTO(buildOrderDTO(orderNo, riskAuditId, orderReqVO, reqTime));

        //6.2主订单扩展信息(统计信息)处理
        parentOrderDTO.setOrderSourceStatDTO(buildOrderSourceStatDTO(orderNo, orderReqVO));

        //6.3主订单状态信息(统计信息)处理
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        if (null == renterGoodsDetailDTO.getReplyFlag() || renterGoodsDetailDTO.getReplyFlag() == OrderConstant.NO) {
            orderStatusDTO.setStatus(OrderStatusEnum.TO_CONFIRM.getStatus());
        } else {
            orderStatusDTO.setStatus(OrderStatusEnum.TO_PAY.getStatus());
        }
        parentOrderDTO.setOrderStatusDTO(orderStatusDTO);

        parentOrderService.saveParentOrderInfo(parentOrderDTO);

        //6.4 order_flow
        orderFlowService.inserOrderStatusChangeProcessInfo(orderNo, OrderStatusEnum.TO_CONFIRM);
        if (null != renterGoodsDetailDTO.getReplyFlag() && renterGoodsDetailDTO.getReplyFlag() == OrderConstant.YES) {
            orderFlowService.inserOrderStatusChangeProcessInfo(orderNo, OrderStatusEnum.TO_PAY);
        }

        //7. 优惠券绑定、凹凸币扣除等
        OwnerCouponBindReqVO ownerCouponBindReqVO =
                buildOwnerCouponBindReqVO(orderNo, renterOrderResVO.getCouponAndAutoCoinResVO(), reqContext);
        boolean bindOwnerCouponResult = couponAndCoinHandleService.bindOwnerCoupon(ownerCouponBindReqVO);
        LOGGER.info("Bind owner coupon result is:[{}]", bindOwnerCouponResult);
        boolean bindPlatformCouponResult = couponAndCoinHandleService.bindCoupon(orderNo,
                orderReqVO.getDisCouponIds(), 1,
                renterOrderResVO.getCouponAndAutoCoinResVO().getIsUsePlatformCoupon());
        LOGGER.info("Bind platf coupon result is:[{}]", bindPlatformCouponResult);
        boolean bindGetCarFeeCouponResult = couponAndCoinHandleService.bindCoupon(orderNo,
                orderReqVO.getGetCarFreeCouponId(), 2,
                renterOrderResVO.getCouponAndAutoCoinResVO().getIsUseGetCarFeeCoupon());
        LOGGER.info("Bind getCarFee coupon result is:[{}]", bindGetCarFeeCouponResult);
        AutoCoinDeductReqVO autoCoinDeductReqVO = buildAutoCoinDeductReqVO(orderNo, renterOrderResVO.getCouponAndAutoCoinResVO(), reqContext);
        boolean deductionAotuCoinResult = couponAndCoinHandleService.deductionAotuCoin(autoCoinDeductReqVO);
        LOGGER.info("Deduct autoCoin result is:[{}]", deductionAotuCoinResult);

        //8.订单完成事件发送
        //todo


        //end 组装接口返回
        OrderResVO orderResVO = new OrderResVO();
        orderResVO.setOrderNo(orderNo);
        return orderResVO;
    }

    private GoodsService.CarDetailReqVO buildCarDetailReqVO(OrderReqVO orderReqVO) {
        GoodsService.CarDetailReqVO carDetailReqVO = new GoodsService.CarDetailReqVO();
        carDetailReqVO.setAddrIndex(StringUtils.isBlank(orderReqVO.getCarAddrIndex()) ? 0 : Integer.parseInt(orderReqVO.getCarAddrIndex()));
        carDetailReqVO.setCarNo(orderReqVO.getCarNo());
        carDetailReqVO.setRentTime(orderReqVO.getRentTime());
        carDetailReqVO.setRevertTime(orderReqVO.getRevertTime());
        carDetailReqVO.setUseSpecialPrice(StringUtils.equals("0",
                orderReqVO.getUseSpecialPrice()));
        return carDetailReqVO;
    }


    /**
     * 组装主订单基本信息
     *
     * @param orderNo     主订单号
     * @param riskAuditId 风控审核结果ID
     * @param orderReqVO  下单请求参数
     * @param reqTime     下单时间
     * @return OrderDTO 主订单基本信息
     */
    private OrderDTO buildOrderDTO(String orderNo, Integer riskAuditId, OrderReqVO orderReqVO, LocalDateTime reqTime) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setMemNoRenter(orderReqVO.getMemNo());
        orderDTO.setCategory(Integer.valueOf(orderReqVO.getOrderCategory()));
        orderDTO.setCityCode(orderReqVO.getCityCode());
        orderDTO.setCityName(orderReqVO.getCityName());
        orderDTO.setEntryCode(orderReqVO.getSceneCode());
        orderDTO.setSource(orderReqVO.getSource());
        orderDTO.setExpRentTime(orderReqVO.getRentTime());
        orderDTO.setExpRevertTime(orderReqVO.getRevertTime());
        orderDTO.setIsFreeDeposit(StringUtils.isBlank(orderReqVO.getFreeDoubleTypeId())
                || Integer.parseInt(orderReqVO.getFreeDoubleTypeId()) == FreeDepositTypeEnum.CONSUME.getCode() ?
                0 : 1);
        orderDTO.setIsOutCity(orderReqVO.getIsLeaveCity());
        orderDTO.setReqTime(reqTime);
        orderDTO.setIsUseAirPortService(orderReqVO.getUseAirportService());
        orderDTO.setFlightId(orderReqVO.getFlightNo());
        orderDTO.setRiskAuditId(riskAuditId);
        orderDTO.setLimitAmt(StringUtils.isBlank(orderReqVO.getReductiAmt()) ? 0 : Integer.valueOf(orderReqVO.getReductiAmt()));
        orderDTO.setBasePath(CommonUtils.createTransBasePath(orderNo));
        orderDTO.setOrderNo(orderNo);
        orderDTO.setMemNoRenter(orderReqVO.getMemNo());
        LOGGER.info("Build order dto,result is ,orderDTO:[{}]", JSON.toJSONString(orderDTO));
        return orderDTO;
    }


    /**
     * 组装主订单来源统计信息
     *
     * @param orderNo    主订单号
     * @param orderReqVO 下单请求参数
     * @return OrderSourceStatDTO 主订单来源统计信息
     */
    private OrderSourceStatDTO buildOrderSourceStatDTO(String orderNo, OrderReqVO orderReqVO) {
        OrderSourceStatDTO orderSourceStatDTO = new OrderSourceStatDTO();
        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, OrderSourceStatDTO.class, false);
        beanCopier.copy(orderReqVO, orderSourceStatDTO, null);

        //差异处理
        orderSourceStatDTO.setAppVersion(orderReqVO.getAppVersion());
        orderSourceStatDTO.setCategory(orderReqVO.getOrderCategory());
        orderSourceStatDTO.setEntryCode(orderReqVO.getSceneCode());
        orderSourceStatDTO.setModuleName(orderReqVO.getModuleName());
        orderSourceStatDTO.setFunctionName(orderReqVO.getFunctionName());
        orderSourceStatDTO.setOaid(orderReqVO.getOAID());
        orderSourceStatDTO.setImei(orderReqVO.getIMEI());
        orderSourceStatDTO.setOs(orderReqVO.getOS());
        orderSourceStatDTO.setAppChannelId(orderReqVO.getAppChannelId());
        orderSourceStatDTO.setAndroidId(orderReqVO.getAndroidID());
        orderSourceStatDTO.setOrderNo(orderNo);
        orderSourceStatDTO.setPublicLongitude(orderReqVO.getPublicLongitude());
        orderSourceStatDTO.setPublicLatitude(orderReqVO.getPublicLatitude());
        orderSourceStatDTO.setReqAddr(BizAreaUtil.getReqAddrFromLonLat(orderSourceStatDTO.getPublicLongitude(),
                orderSourceStatDTO.getPublicLatitude()));

        LOGGER.info("Build order source stat dto,result is ,orderSourceStatDTO:[{}]", JSON.toJSONString(orderSourceStatDTO));
        return orderSourceStatDTO;
    }


    /**
     * 租客订单请求参数封装
     *
     * @param orderNo               主订单号
     * @param renterOrderNo         租客子订单号
     * @param reqContext            下单请求参数
     * @param carRentTimeRangeResVO 提前延后信息
     * @return RenterOrderReqVO 租客订单请求参数
     */
    private RenterOrderReqVO buildRenterOrderReqVO(String orderNo, String renterOrderNo, OrderReqContext reqContext,
                                                   CarRentTimeRangeResVO carRentTimeRangeResVO) {

        RenterOrderReqVO renterOrderReqVO = new RenterOrderReqVO();
        renterOrderReqVO.setOrderNo(orderNo);
        renterOrderReqVO.setRenterOrderNo(renterOrderNo);

        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, RenterOrderReqVO.class, false);
        beanCopier.copy(reqContext.getOrderReqVO(), renterOrderReqVO, null);

        OrderReqVO orderReqVO = reqContext.getOrderReqVO();
        renterOrderReqVO.setEntryCode(orderReqVO.getSceneCode());
        renterOrderReqVO.setSource(orderReqVO.getSource());
        String driverIds = orderReqVO.getDriverIds();
        renterOrderReqVO.setDriverIds(ListUtil.parseString(driverIds, ","));
        renterOrderReqVO.setGetCarBeforeTime(null == carRentTimeRangeResVO || null == carRentTimeRangeResVO.getGetMinutes() ? 0 : carRentTimeRangeResVO.getGetMinutes());
        renterOrderReqVO.setReturnCarAfterTime(null == carRentTimeRangeResVO || null == carRentTimeRangeResVO.getReturnMinutes() ? 0 :
                carRentTimeRangeResVO.getReturnMinutes());

        RenterGoodsDetailDTO goodsDetail = reqContext.getRenterGoodsDetailDto();
        renterOrderReqVO.setGuidPrice(goodsDetail.getCarGuidePrice());
        renterOrderReqVO.setCarSurplusPrice(goodsDetail.getCarSurplusPrice());
        renterOrderReqVO.setInmsrp(goodsDetail.getCarInmsrp());
        renterOrderReqVO.setBrandId(goodsDetail.getBrand());
        renterOrderReqVO.setTypeId(goodsDetail.getType());
        renterOrderReqVO.setLicenseDay(goodsDetail.getLicenseDay());
        renterOrderReqVO.setLabelIds(goodsDetail.getLabelIds());
        renterOrderReqVO.setRenterGoodsPriceDetailDTOList(goodsDetail.getRenterGoodsPriceDetailDTOList());
        renterOrderReqVO.setPlateNum(goodsDetail.getCarPlateNum());


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
        ownerOrderReqDTO.setExpRentTime(reqContext.getOrderReqVO().getRentTime());
        ownerOrderReqDTO.setExpRevertTime(reqContext.getOrderReqVO().getRevertTime());
        ownerOrderReqDTO.setIsUseSpecialPrice(null == reqContext.getOrderReqVO().getUseSpecialPrice() ? 0 : Integer.valueOf(reqContext.getOrderReqVO().getUseSpecialPrice()));
        ownerOrderReqDTO.setSrvGetFlag(reqContext.getOrderReqVO().getSrvGetFlag());
        ownerOrderReqDTO.setSrvReturnFlag(reqContext.getOrderReqVO().getSrvReturnFlag());
        ownerOrderReqDTO.setCarNo(reqContext.getOrderReqVO().getCarNo());
        ownerOrderReqDTO.setCategory(Integer.valueOf(reqContext.getOrderReqVO().getOrderCategory()));

        ownerOrderReqDTO.setReplyFlag(reqContext.getOwnerGoodsDetailDto().getReplyFlag());
        ownerOrderReqDTO.setCarOwnerType(reqContext.getOwnerGoodsDetailDto().getCarOwnerType());


        LOGGER.info("Build owner order reqDTO,result is ,ownerOrderReqDTO:[{}]",
                JSON.toJSONString(ownerOrderReqDTO));

        return ownerOrderReqDTO;
    }

    /**
     * 提前延后时间计算请求参数封装
     *
     * @param orderReqVO 下单请求参数
     * @return CarRentTimeRangeReqVO 提前延后时间计算请求参数
     */
    private CarRentTimeRangeReqVO buildCarRentTimeRangeReqVO(OrderReqVO orderReqVO) {
        CarRentTimeRangeReqVO carRentTimeRangeReqVO = new CarRentTimeRangeReqVO();
        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, CarRentTimeRangeReqVO.class, false);
        beanCopier.copy(orderReqVO, carRentTimeRangeReqVO, null);

        LOGGER.info("Build CarRentTimeRangeReqVO,result is ,carRentTimeRangeReqVO:[{}]",
                JSON.toJSONString(carRentTimeRangeReqVO));
        return carRentTimeRangeReqVO;
    }

    /**
     * 车主券补贴信息封装(车主端)
     *
     * @param orderNo      主订单号
     * @param ownerOrderNo 车主订单号
     * @param memNo        车主会员注册号
     * @param ownerCoupon  车主券
     * @return OwnerOrderSubsidyDetailEntity 车主券补贴信息
     */
    private OwnerOrderSubsidyDetailEntity buildOwnerOrderSubsidyDetailEntity(String orderNo, String ownerOrderNo,
                                                                             String memNo,
                                                                             OrderCouponDTO ownerCoupon) {
        if (null == ownerCoupon) {
            return null;
        }

        OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity = new OwnerOrderSubsidyDetailEntity();
        ownerOrderSubsidyDetailEntity.setOrderNo(orderNo);
        ownerOrderSubsidyDetailEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderSubsidyDetailEntity.setMemNo(memNo);
        ownerOrderSubsidyDetailEntity.setSubsidyAmount(-ownerCoupon.getAmount());

        ownerOrderSubsidyDetailEntity.setSubsidyTypeCode(SubsidyTypeCodeEnum.RENT_AMT.getCode());
        ownerOrderSubsidyDetailEntity.setSubsidyTypeName(SubsidyTypeCodeEnum.RENT_AMT.getDesc());
        ownerOrderSubsidyDetailEntity.setSubsidySourceCode(SubsidySourceCodeEnum.OWNER.getCode());
        ownerOrderSubsidyDetailEntity.setSubsidySourceName(SubsidySourceCodeEnum.OWNER.getDesc());

        ownerOrderSubsidyDetailEntity.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
        ownerOrderSubsidyDetailEntity.setSubsidyTypeName(SubsidySourceCodeEnum.RENTER.getDesc());
        ownerOrderSubsidyDetailEntity.setSubsidyCostCode(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo());
        ownerOrderSubsidyDetailEntity.setSubsidyCostName(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getTxt());
        ownerOrderSubsidyDetailEntity.setSubsidyDesc("使用车主券抵扣租金");

        LOGGER.info("Build ownerOrderSubsidyDetailEntity,result is ,ownerOrderSubsidyDetailEntity:[{}]",
                JSON.toJSONString(ownerOrderSubsidyDetailEntity));
        return ownerOrderSubsidyDetailEntity;
    }

    /**
     * 车主租金明细(车主端，目前与租客相同)
     *
     * @param orderNo       主订单号
     * @param ownerOrderNo  车主订单号
     * @param memNo         车主会员注册号
     * @param rentAmtEntity 租客端租金信息
     * @return OwnerOrderPurchaseDetailEntity 租金费用明细
     */
    private OwnerOrderPurchaseDetailEntity buildOwnerOrderPurchaseDetailEntity(String orderNo, String ownerOrderNo,
                                                                               String memNo,
                                                                               RenterOrderCostDetailEntity rentAmtEntity) {

        if (null == rentAmtEntity) {
            return null;
        }
        OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity = new OwnerOrderPurchaseDetailEntity();

        BeanCopier beanCopier = BeanCopier.create(RenterOrderCostDetailEntity.class,
                OwnerOrderPurchaseDetailEntity.class, false);

        beanCopier.copy(rentAmtEntity, ownerOrderPurchaseDetailEntity, null);

        ownerOrderPurchaseDetailEntity.setOrderNo(orderNo);
        ownerOrderPurchaseDetailEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderPurchaseDetailEntity.setMemNo(memNo);

        LOGGER.info("Build ownerOrderPurchaseDetailEntity,result is ,ownerOrderPurchaseDetailEntity:[{}]",
                JSON.toJSONString(ownerOrderPurchaseDetailEntity));
        return ownerOrderPurchaseDetailEntity;
    }


    /**
     * 车主券绑定
     *
     * @param orderNo                主订单号
     * @param couponAndAutoCoinResVO 优惠券、凹凸币处理结果
     * @param reqContext             下单请求参数
     * @return OwnerCouponBindReqVO
     */
    private OwnerCouponBindReqVO buildOwnerCouponBindReqVO(String orderNo,
                                                           CouponAndAutoCoinResVO couponAndAutoCoinResVO,
                                                           OrderReqContext reqContext) {
        if (null == couponAndAutoCoinResVO.getIsUseOwnerCoupon() || !couponAndAutoCoinResVO.getIsUseOwnerCoupon()) {
            return null;
        }
        OwnerCouponBindReqVO ownerCouponBindReqVO = new OwnerCouponBindReqVO();
        ownerCouponBindReqVO.setCarNo(Integer.valueOf(reqContext.getOrderReqVO().getCarNo()));
        ownerCouponBindReqVO.setCouponNo(reqContext.getOrderReqVO().getCarOwnerCouponNo());
        ownerCouponBindReqVO.setRentAmt(couponAndAutoCoinResVO.getRentAmt());
        ownerCouponBindReqVO.setRenterFirstName(reqContext.getRenterMemberDto().getFirstName());
        ownerCouponBindReqVO.setRenterName(reqContext.getRenterMemberDto().getRealName());
        ownerCouponBindReqVO.setRenterSex(null == reqContext.getRenterMemberDto().getGender() ? "" :
                reqContext.getRenterMemberDto().getGender().toString());
        ownerCouponBindReqVO.setOrderNo(orderNo);
        return ownerCouponBindReqVO;
    }


    private AutoCoinDeductReqVO buildAutoCoinDeductReqVO(String orderNo, CouponAndAutoCoinResVO couponAndAutoCoinResVO, OrderReqContext reqContext) {
        AutoCoinDeductReqVO autoCoinDeductReqVO = new AutoCoinDeductReqVO();
        autoCoinDeductReqVO.setOrderNo(orderNo);
        autoCoinDeductReqVO.setMemNo(Integer.valueOf(reqContext.getOrderReqVO().getMemNo()));
        autoCoinDeductReqVO.setChargeAutoCoin(null == couponAndAutoCoinResVO.getChargeAutoCoin() ? 0 :
                -couponAndAutoCoinResVO.getChargeAutoCoin() * 100);
        autoCoinDeductReqVO.setOrderType("2");
        autoCoinDeductReqVO.setUseAutoCoin(reqContext.getOrderReqVO().getUseAutoCoin());
        autoCoinDeductReqVO.setRemark("租车消费");
        autoCoinDeductReqVO.setRemarkExtend("租车消费");
        return autoCoinDeductReqVO;
    }

    private SubmitOrderRiskCheckReqVO buildSubmitOrderRiskCheckReqVO(OrderReqVO orderReqVO, LocalDateTime reqTime) {

        SubmitOrderRiskCheckReqVO submitOrderRiskCheckReqVO = new SubmitOrderRiskCheckReqVO();
        BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, SubmitOrderRiskCheckReqVO.class, false);
        beanCopier.copy(orderReqVO, submitOrderRiskCheckReqVO, null);
        submitOrderRiskCheckReqVO.setReqTime(LocalDateTimeUtils.localDateTimeToDate(reqTime));
        return submitOrderRiskCheckReqVO;
    }
}
