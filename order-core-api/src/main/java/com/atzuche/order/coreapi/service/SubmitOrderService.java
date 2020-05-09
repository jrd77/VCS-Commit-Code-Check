package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CarRentTimeRangeDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.vo.req.AutoCoinDeductReqVO;
import com.atzuche.order.coreapi.entity.vo.req.OwnerCouponBindReqVO;
import com.atzuche.order.coreapi.filter.StockFilter;
import com.atzuche.order.coreapi.service.remote.StockProxyService;
import com.atzuche.order.coreapi.service.remote.UniqueOrderNoProxyService;
import com.atzuche.order.coreapi.submit.filter.cost.LongOrderCostFilterChain;
import com.atzuche.order.coreapi.utils.BizAreaUtil;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
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
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.service.OrderTransferRecordService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.CouponAndAutoCoinResVO;
import com.atzuche.order.renterorder.vo.RenterOrderCarDepositResVO;
import com.atzuche.order.renterorder.vo.RenterOrderIllegalResVO;
import com.atzuche.order.renterorder.vo.RenterOrderResVO;
import com.atzuche.order.renterwz.service.RenterOrderWzStatusService;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.dto.OrderInfoDTO;
import com.autoyol.car.api.model.enums.OrderOperationTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 订单业务处理类
 *
 * @author pengcheng.fu
 * @date 2020/01/02 14:37
 */
@Service
public class SubmitOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderService.class);

    public static final Integer AUTO_REPLY_FLAG = 1;

    @Autowired
    private MemProxyService memberService;
    @Autowired
    private CarProxyService goodsService;
    @Resource
    private UniqueOrderNoProxyService uniqueOrderNoService;
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
    @Autowired
    private OrderCommonConver orderCommonConver;
    @Autowired
    private StockProxyService stockService;
    @Autowired
    private RenterOrderWzStatusService renterOrderWzStatusService;
    @Autowired
    private OrderTransferRecordService orderTransferRecordService;
    @Autowired
    private LongOrderCostFilterChain longOrderCostFilterChain;
    @Autowired
    private SubmitOrderHandleService submitOrderHandleService;



    /**
     * 提交订单
     *
     * @param context 下单请求信息
     * @return OrderResVO 下单返回结果
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResVO submitOrder(OrderReqContext context) {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        //1.生成主订单号
        String orderNo = uniqueOrderNoService.genOrderNo();

        //提前延后时间计算
        CarRentTimeRangeDTO carRentTimeRangeResVO = context.getCarRentTimeRangeDTO();
        //4.创建租客子订单
        //4.1.生成租客子订单号
        String renterOrderNo = uniqueOrderNoService.genRenterOrderNo(orderNo);
        //4.2.调用租客订单模块处理租客订单相关业务
        RenterOrderResVO renterOrderResVO =
                renterOrderService.generateRenterOrderInfo(orderCommonConver.buildRenterOrderReqVO(orderNo, renterOrderNo, context));
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
        RenterGoodsDetailDTO renterGoodsDetailDTO = context.getRenterGoodsDetailDto();
        renterGoodsDetailDTO.setOrderNo(orderNo);
        renterGoodsDetailDTO.setRenterOrderNo(renterOrderNo);
        renterGoodsService.save(renterGoodsDetailDTO);

        //4.5.租客信息处理
        //4.6.租客权益信息处理
        RenterMemberDTO renterMemberDTO = context.getRenterMemberDto();
        renterMemberDTO.setOrderNo(orderNo);
        renterMemberDTO.setRenterOrderNo(renterOrderNo);
        renterMemberDTO.setOrderCategory(orderReqVO.getOrderCategory());
        renterMemberService.save(renterMemberDTO);


        //5.创建车主子订单
        //5.1.生成车主子订单号
        String ownerOrderNo = uniqueOrderNoService.genOwnerOrderNo(orderNo);
        //5.2.调用车主订单模块处理车主订单相关业务
        OwnerOrderReqDTO ownerOrderReqDTO = buildOwnerOrderReqDTO(orderNo, ownerOrderNo, context);
        ownerOrderReqDTO.setRenterOrderNo(renterOrderNo);
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetails = new ArrayList<>();
        OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity =
                submitOrderHandleService.buildOwnerOrderSubsidyDetailEntity(orderNo,
                ownerOrderNo,
                renterGoodsDetailDTO.getOwnerMemNo(), renterOrderResVO.getOwnerCoupon());
        if(Objects.nonNull(ownerOrderSubsidyDetailEntity)) {
            ownerOrderSubsidyDetails.add(ownerOrderSubsidyDetailEntity);
        }
        ownerOrderReqDTO.setOwnerOrderSubsidyDetails(ownerOrderSubsidyDetails);

        ownerOrderReqDTO.setOwnerOrderPurchaseDetailEntity(submitOrderHandleService.buildOwnerOrderPurchaseDetailEntity(orderNo, ownerOrderNo,
                renterGoodsDetailDTO.getOwnerMemNo(), renterOrderResVO.getRentAmtEntity()));

        ownerOrderReqDTO.setShowRentTime(null == carRentTimeRangeResVO ? null : carRentTimeRangeResVO.getAdvanceStartDate());
        ownerOrderReqDTO.setShowRevertTime(null == carRentTimeRangeResVO ? null : carRentTimeRangeResVO.getDelayEndDate());
        ownerOrderReqDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());
        ownerOrderReqDTO.setGpsSerialNumber(renterGoodsDetailDTO.getGpsSerialNumber());
        ownerOrderService.generateRenterOrderInfo(ownerOrderReqDTO);
        //5.3.接收车主订单返回信息

        //5.4.车主商品
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = context.getOwnerGoodsDetailDto();
        ownerGoodsDetailDTO.setOrderNo(orderNo);
        ownerGoodsDetailDTO.setOwnerOrderNo(ownerOrderNo);
        ownerGoodsDetailDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());
        ownerGoodsService.save(ownerGoodsDetailDTO);
        //5.5.车主会员
        OwnerMemberDTO ownerMemberDTO = context.getOwnerMemberDto();
        ownerMemberDTO.setOrderNo(orderNo);
        ownerMemberDTO.setOwnerOrderNo(ownerOrderNo);
        ownerMemberDTO.setMemNo(renterGoodsDetailDTO.getOwnerMemNo());

        ownerMemberService.save(ownerMemberDTO);

        //配送订单处理..............
        deliveryCarService.addFlowOrderInfo(context);
        //违章状态
        String operator = orderReqVO.getOperator() == null || orderReqVO.getOperator().trim().length() <= 0 ? renterMemberDTO.getRealName() : orderReqVO.getOperator();
        //租客会员号
        String renterNo = orderReqVO.getMemNo();
        //车主会员号
        String ownerNo = ownerGoodsDetailDTO.getMemNo();
        renterOrderWzStatusService.createInfo(orderNo, ownerGoodsDetailDTO.getCarPlateNum(), operator, renterNo, ownerNo, String.valueOf(ownerGoodsDetailDTO.getCarNo()));
        //6.主订单相关信息处理
        ParentOrderDTO parentOrderDTO = new ParentOrderDTO();
        //6.1主订单信息处理
        parentOrderDTO.setOrderDTO(submitOrderHandleService.buildOrderDTO(orderNo, context.getRiskAuditId(), orderReqVO));

        //6.2主订单扩展信息(统计信息)处理
        parentOrderDTO.setOrderSourceStatDTO(submitOrderHandleService.buildOrderSourceStatDTO(orderNo, orderReqVO));

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
                buildOwnerCouponBindReqVO(orderNo, renterOrderResVO.getCouponAndAutoCoinResVO(), context);

        boolean bindOwnerCouponResult = couponAndCoinHandleService.bindOwnerCoupon(ownerCouponBindReqVO);
        LOGGER.info("Bind owner coupon result is:[{}]", bindOwnerCouponResult);


        AutoCoinDeductReqVO autoCoinDeductReqVO = buildAutoCoinDeductReqVO(orderNo,
                renterOrderNo, renterOrderResVO.getCouponAndAutoCoinResVO().getChargeAutoCoin(), context);
        boolean deductionAotuCoinResult = couponAndCoinHandleService.deductionAotuCoin(autoCoinDeductReqVO);
        LOGGER.info("Deduct autoCoin result is:[{}]", deductionAotuCoinResult);

        // 增加一条下单的换车记录
        orderTransferRecordService.saveOrderTransferRecord(submitOrderHandleService.convertToOrderTransferRecordEntity(context, orderNo));

        //是自动应答的车辆才能锁库存，其他类型车辆要车主同意时才能锁库存。
        if (AUTO_REPLY_FLAG.equals(context.getRenterGoodsDetailDto().getReplyFlag())) {
            OrderInfoDTO orderInfoDTO = initOrderInfoDTO(context.getOrderReqVO());
            orderInfoDTO.setOrderNo(orderNo);
            stockService.cutCarStock(orderInfoDTO);
        }
        //end 组装接口返回
        OrderResVO orderResVO = new OrderResVO();
        orderResVO.setOrderNo(orderNo);
        orderResVO.setStatus(String.valueOf(orderStatusDTO.getStatus()));
        orderResVO.setReplyFlag(context.getRenterGoodsDetailDto().getReplyFlag());
        return orderResVO;
    }


    /**
     * 长租订单提交
     *
     * @param context 下单请求信息
     * @return OrderResVO 下单返回结果
     */

    public OrderResVO submitLongOrder(OrderReqContext context) {
        OrderReqVO orderReqVO = context.getOrderReqVO();
        //生成主订单号
        String orderNo = uniqueOrderNoService.genOrderNo();
        //生成租客订单号
        String renterOrderNo = uniqueOrderNoService.genRenterOrderNo(orderNo);
        //生成车主子订单号
        String ownerOrderNo = uniqueOrderNoService.genOwnerOrderNo(orderNo);
        //初始化费用计算参数
        OrderCostContext orderCostContext = orderCommonConver.initOrderCostContext(orderNo, renterOrderNo, ownerOrderNo, context);
        //计算订单费用
        longOrderCostFilterChain.calculate(orderCostContext);
        // 数据落库(主订单、租客订单、车主订单、押金(违章押金、车辆押金)、违章信息初始化、还车记录初始化等)
        int status = submitOrderHandleService.save(context, orderCostContext);
        // 配送订单处理
        deliveryCarService.addFlowOrderInfo(context);
        // 扣减车辆库存
        cutStockHandle(orderNo, context.getRenterGoodsDetailDto().getReplyFlag(), orderReqVO);
        return new OrderResVO(orderNo, String.valueOf(status), context.getRenterGoodsDetailDto().getReplyFlag());
    }


    /**
     * 车辆库存扣减处理
     * <p>是自动应答的车辆才能锁库存，其他类型车辆要车主同意时才能锁库存。</p>
     *
     * @param orderNo    订单号
     * @param replyFlag  自动应答
     * @param orderReqVO 请求参数
     */
    private void cutStockHandle(String orderNo, Integer replyFlag, OrderReqVO orderReqVO) {
        if (AUTO_REPLY_FLAG.equals(replyFlag)) {
            OrderInfoDTO orderInfoDTO = initOrderInfoDTO(orderReqVO);
            orderInfoDTO.setOrderNo(orderNo);
            stockService.cutCarStock(orderInfoDTO);
        }
    }

    /**
     * 库存扣减参数封装
     *
     * @param orderReqVO 请求参数
     * @return OrderInfoDTO 库存扣减参数
     */
    private OrderInfoDTO initOrderInfoDTO(OrderReqVO orderReqVO) {
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setOrderNo(null);
        orderInfoDTO.setCityCode(Integer.valueOf(orderReqVO.getCityCode()));
        orderInfoDTO.setCarNo(Integer.valueOf(orderReqVO.getCarNo()));
        orderInfoDTO.setOldCarNo(null);
        orderInfoDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(orderReqVO.getRentTime()));
        orderInfoDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(orderReqVO.getRevertTime()));
        orderInfoDTO.setOperationType(OrderOperationTypeEnum.ZCXD.getType());

        LocationDTO getCarAddress = new LocationDTO();
        getCarAddress.setFlag(OrderConstant.NO);
        if (orderReqVO.getSrvGetFlag() == OrderConstant.YES) {
            getCarAddress.setFlag(OrderConstant.YES);
            getCarAddress.setLat(orderReqVO.getSrvGetLat() == null ? OrderConstant.D_ZERO : Double.valueOf(orderReqVO.getSrvGetLat()));
            getCarAddress.setLon(orderReqVO.getSrvGetLon() == null ? OrderConstant.D_ZERO : Double.valueOf(orderReqVO.getSrvGetLon()));
            getCarAddress.setCarAddress(orderReqVO.getSrvGetAddr());
        }
        LocationDTO returnCarAddress = new LocationDTO();
        returnCarAddress.setFlag(OrderConstant.NO);
        if (orderReqVO.getSrvReturnFlag() == OrderConstant.YES) {
            returnCarAddress.setFlag(OrderConstant.YES);
            returnCarAddress.setLat(orderReqVO.getSrvReturnLat() == null ? OrderConstant.D_ZERO :
                    Double.valueOf(orderReqVO.getSrvReturnLat()));
            returnCarAddress.setLon(orderReqVO.getSrvReturnLon() == null ? OrderConstant.D_ZERO : Double.valueOf(orderReqVO.getSrvReturnLon()));
            returnCarAddress.setCarAddress(orderReqVO.getSrvReturnAddr());
        }
        orderInfoDTO.setGetCarAddress(getCarAddress);
        orderInfoDTO.setReturnCarAddress(returnCarAddress);
        orderInfoDTO.setLongRent(StockFilter.isLongRent(orderReqVO.getOrderCategory()));
        return orderInfoDTO;
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
    private OrderDTO buildOrderDTO(String orderNo, String riskAuditId, OrderReqVO orderReqVO, LocalDateTime reqTime) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setMemNoRenter(orderReqVO.getMemNo());
        orderDTO.setCategory(Integer.valueOf(orderReqVO.getOrderCategory()));
        orderDTO.setCityCode(orderReqVO.getCityCode());
        orderDTO.setCityName(orderReqVO.getCityName());
        orderDTO.setEntryCode(orderReqVO.getSceneCode());
        orderDTO.setSource(orderReqVO.getSource());
        orderDTO.setExpRentTime(orderReqVO.getRentTime());
        orderDTO.setExpRevertTime(orderReqVO.getRevertTime());
        //绑卡，芝麻都属于免押方式。
        orderDTO.setIsFreeDeposit(StringUtils.isBlank(orderReqVO.getFreeDoubleTypeId())
                || Integer.parseInt(orderReqVO.getFreeDoubleTypeId()) == FreeDepositTypeEnum.CONSUME.getCode() ?
                0 : 1);
        orderDTO.setIsOutCity(orderReqVO.getIsLeaveCity());
        orderDTO.setRentCity(orderReqVO.getRentCity());
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
        orderSourceStatDTO.setSrcPort(orderReqVO.getSrcPort()==null?"":String.valueOf(orderReqVO.getSrcPort()));
        orderSourceStatDTO.setPublicLongitude(orderReqVO.getPublicLongitude());
        orderSourceStatDTO.setPublicLatitude(orderReqVO.getPublicLatitude());
        orderSourceStatDTO.setReqAddr(BizAreaUtil.getReqAddrFromLonLat(orderSourceStatDTO.getPublicLongitude(),
                orderSourceStatDTO.getPublicLatitude()));
        orderSourceStatDTO.setDevice(orderReqVO.getDeviceName());
        orderSourceStatDTO.setUseAutoCoin(orderReqVO.getUseAutoCoin());
        orderSourceStatDTO.setSpecialConsole(orderReqVO.getSpecialConsole()==null?0:Integer.valueOf(orderReqVO.getSpecialConsole()));
        orderSourceStatDTO.setReqSource(null == orderReqVO.getReqSource() ? null : orderReqVO.getReqSource().toString());
        LOGGER.info("Build order source stat dto,result is ,orderSourceStatDTO:[{}]", JSON.toJSONString(orderSourceStatDTO));
        return orderSourceStatDTO;
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
        ownerOrderReqDTO.setServiceRate(reqContext.getOwnerGoodsDetailDto().getServiceRate());
        ownerOrderReqDTO.setServiceProxyRate(reqContext.getOwnerGoodsDetailDto().getServiceProxyRate());

        LOGGER.info("Build owner order reqDTO,result is ,ownerOrderReqDTO:[{}]",
                JSON.toJSONString(ownerOrderReqDTO));

        return ownerOrderReqDTO;
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
        ownerCouponBindReqVO.setRentAmt(Math.abs(couponAndAutoCoinResVO.getRentAmt()));
        ownerCouponBindReqVO.setRenterFirstName(reqContext.getRenterMemberDto().getFirstName());
        ownerCouponBindReqVO.setRenterName(reqContext.getRenterMemberDto().getRealName());
        ownerCouponBindReqVO.setRenterSex(null == reqContext.getRenterMemberDto().getGender() ? "" :
                reqContext.getRenterMemberDto().getGender().toString());
        ownerCouponBindReqVO.setOrderNo(orderNo);
        LOGGER.info("Build OwnerCouponBindReqVO result is:[{}]", ownerCouponBindReqVO);
        return ownerCouponBindReqVO;
    }


    private AutoCoinDeductReqVO buildAutoCoinDeductReqVO(String orderNo, String renterOrderNo, Integer chargeAutoCoin,
                                                         OrderReqContext reqContext) {
        AutoCoinDeductReqVO autoCoinDeductReqVO = new AutoCoinDeductReqVO();
        autoCoinDeductReqVO.setOrderNo(orderNo);
        autoCoinDeductReqVO.setRenterOrderNo(renterOrderNo);
        autoCoinDeductReqVO.setMemNo(reqContext.getOrderReqVO().getMemNo());
        autoCoinDeductReqVO.setChargeAutoCoin(null == chargeAutoCoin ? 0 : chargeAutoCoin);
        autoCoinDeductReqVO.setOrderType("2");
        autoCoinDeductReqVO.setUseAutoCoin(reqContext.getOrderReqVO().getUseAutoCoin());
        autoCoinDeductReqVO.setRemark("租车消费");
        autoCoinDeductReqVO.setRemarkExtend("租车消费");
        LOGGER.info("Build AutoCoinDeductReqVO result is:[{}]", autoCoinDeductReqVO);
        return autoCoinDeductReqVO;
    }


}
