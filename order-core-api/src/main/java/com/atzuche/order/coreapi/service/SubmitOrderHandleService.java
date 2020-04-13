package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.CommonUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.utils.BizAreaUtil;
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
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.OrderTransferRecordEntity;
import com.atzuche.order.renterorder.service.OrderTransferRecordService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterwz.service.RenterOrderWzStatusService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * 提交订单数据落库操作
 * <p><font color=red>主订单、租客订单、车主订单、押金(违章押金、车辆押金)、违章信息初始化、还车记录初始化等</font></p>
 *
 * @author pengcheng.fu
 * @date 2020/4/9 18:10
 */

@Service
public class SubmitOrderHandleService {

    private static Logger logger = LoggerFactory.getLogger(SubmitOrderHandleService.class);

    @Autowired
    private ParentOrderService parentOrderService;
    @Autowired
    private OrderFlowService orderFlowService;

    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private RenterMemberService renterMemberService;

    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private OwnerMemberService ownerMemberService;

    @Autowired
    private RenterOrderWzStatusService renterOrderWzStatusService;
    @Autowired
    private OrderTransferRecordService orderTransferRecordService;
    @Autowired
    private CashierService cashierService;
    @Autowired
    private OrderCommonConver orderCommonConver;


    /**
     * 下单数据落库操作
     *
     * @param context     请求参数
     * @param costContext 订单费用信息
     */
    @Transactional(rollbackFor = Exception.class)
    public int save(OrderReqContext context, OrderCostContext costContext) {
        OrderCostBaseReqDTO baseReqDTO = costContext.getReqContext().getBaseReqDTO();
        // 租客订单处理
        renterOrderService.createRenterOrder(orderCommonConver.buildCreateRenterOrderDataReqDTO(context, costContext));
        // 租客商品处理
        renterGoodsService.save(baseReqDTO.getOrderNo(), baseReqDTO.getRenterOrderNo(), context.getRenterGoodsDetailDto());
        // 租客会员处理
        renterMemberService.save(baseReqDTO.getOrderNo(), baseReqDTO.getRenterOrderNo(), context.getRenterMemberDto());
        // 车辆押金处理
        CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO = new CreateOrderRenterDepositReqVO();
        BeanUtils.copyProperties(costContext.getResContext().getOrderCarDepositAmtResDTO().getCarDeposit(), createOrderRenterDepositReqVO);
        cashierService.insertRenterDeposit(createOrderRenterDepositReqVO);
        // 违章押金处理
        CreateOrderRenterWZDepositReqVO renterOrderIllegalDepositReq = new CreateOrderRenterWZDepositReqVO();
        BeanUtils.copyProperties(costContext.getResContext().getOrderIllegalDepositAmtResDTO().getIllegalDeposit(),
                renterOrderIllegalDepositReq);
        cashierService.insertRenterWZDeposit(renterOrderIllegalDepositReq);
        // 车主订单处理
        ownerOrderService.generateRenterOrderInfo(buildOwnerOrderReqDTO(context, costContext));
        // 车主商品处理
        ownerGoodsService.save(baseReqDTO.getOrderNo(), baseReqDTO.getRenterOrderNo(), context.getOwnerGoodsDetailDto());
        // 车主会员处理
        ownerMemberService.save(baseReqDTO.getOrderNo(), baseReqDTO.getRenterOrderNo(), context.getOwnerMemberDto());
        // 违章信息初始化
        String operator = StringUtils.isNotBlank(context.getOrderReqVO().getOperator()) ?
                context.getRenterMemberDto().getRealName() : context.getOrderReqVO().getOperator();
        renterOrderWzStatusService.createInfo(
                baseReqDTO.getOrderNo(),
                context.getRenterGoodsDetailDto().getCarPlateNum(),
                operator,
                context.getRenterMemberDto().getMemNo(),
                context.getOwnerMemberDto().getMemNo(),
                context.getOrderReqVO().getCarNo());
        // 主订单处理(订单:order、状态:order_status、统计:order_source_stat等)
        boolean replyFlag = null != context.getRenterGoodsDetailDto().getReplyFlag() &&
                context.getRenterGoodsDetailDto().getReplyFlag() == OrderConstant.YES;
        ParentOrderDTO parentOrderDTO = buildParentOrderDTO(
                baseReqDTO.getOrderNo(),
                context.getRiskAuditId(),
                replyFlag,
                context.getOrderReqVO());
        parentOrderService.saveParentOrderInfo(parentOrderDTO);
        // 订单流程处理(orderFlow)
        orderFlowService.inserOrderStatusChangeProcessInfo(baseReqDTO.getOrderNo(), OrderStatusEnum.from(parentOrderDTO.getOrderStatusDTO().getStatus()));
        // 换车记录初始化(orderTransferRecordService.saveOrderTransferRecord)
        orderTransferRecordService.saveOrderTransferRecord(convertToOrderTransferRecordEntity(context, baseReqDTO.getOrderNo()));
        return parentOrderDTO.getOrderStatusDTO().getStatus();
    }

    /**
     * 对象转换
     *
     * @param reqContext 请求参数
     * @param orderNo    订单号
     * @return OrderTransferRecordEntity
     */
    public OrderTransferRecordEntity convertToOrderTransferRecordEntity(OrderReqContext reqContext, String orderNo) {
        if (reqContext == null) {
            return null;
        }
        RenterGoodsDetailDTO renterGoodsDetailDto = reqContext.getRenterGoodsDetailDto();
        if (renterGoodsDetailDto == null) {
            return null;
        }
        OrderTransferRecordEntity orderTransferRecordEntity = new OrderTransferRecordEntity();
        orderTransferRecordEntity.setCarNo(renterGoodsDetailDto.getCarNo() == null ? null : String.valueOf(renterGoodsDetailDto.getCarNo()));
        orderTransferRecordEntity.setCarPlateNum(renterGoodsDetailDto.getCarPlateNum());
        orderTransferRecordEntity.setOperator(OrderConstant.SYSTEM_OPERATOR);
        RenterMemberDTO renterMemberDto = reqContext.getRenterMemberDto();
        if (renterMemberDto != null) {
            orderTransferRecordEntity.setMemNo(renterMemberDto.getMemNo());
        }
        orderTransferRecordEntity.setOrderNo(orderNo);
        orderTransferRecordEntity.setSource(OrderConstant.THREE);
        return orderTransferRecordEntity;
    }


    /**
     * 组装主订单基本信息
     *
     * @param orderNo     主订单号
     * @param riskAuditId 风控审核结果ID
     * @param orderReqVO  下单请求参数
     * @return OrderDTO 主订单基本信息
     */
    public OrderDTO buildOrderDTO(String orderNo, String riskAuditId, OrderReqVO orderReqVO) {
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
                OrderConstant.NO : OrderConstant.YES);
        orderDTO.setIsOutCity(orderReqVO.getIsLeaveCity());
        orderDTO.setRentCity(orderReqVO.getRentCity());
        orderDTO.setReqTime(orderReqVO.getReqTime());
        orderDTO.setIsUseAirPortService(orderReqVO.getUseAirportService());
        orderDTO.setFlightId(orderReqVO.getFlightNo());
        orderDTO.setRiskAuditId(riskAuditId);
        orderDTO.setLimitAmt(StringUtils.isBlank(orderReqVO.getReductiAmt()) ? OrderConstant.ZERO :
                Integer.valueOf(orderReqVO.getReductiAmt()));
        orderDTO.setBasePath(CommonUtils.createTransBasePath(orderNo));
        orderDTO.setOrderNo(orderNo);
        orderDTO.setMemNoRenter(orderReqVO.getMemNo());
        logger.info("Build order dto,result is ,orderDTO:[{}]", JSON.toJSONString(orderDTO));
        return orderDTO;
    }


    /**
     * 组装主订单来源统计信息
     *
     * @param orderNo    主订单号
     * @param orderReqVO 下单请求参数
     * @return OrderSourceStatDTO 主订单来源统计信息
     */
    public OrderSourceStatDTO buildOrderSourceStatDTO(String orderNo, OrderReqVO orderReqVO) {
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
        orderSourceStatDTO.setSrcPort(orderReqVO.getSrcPort() == null ? "" : String.valueOf(orderReqVO.getSrcPort()));
        orderSourceStatDTO.setPublicLongitude(orderReqVO.getPublicLongitude());
        orderSourceStatDTO.setPublicLatitude(orderReqVO.getPublicLatitude());
        orderSourceStatDTO.setReqAddr(BizAreaUtil.getReqAddrFromLonLat(orderSourceStatDTO.getPublicLongitude(),
                orderSourceStatDTO.getPublicLatitude()));
        orderSourceStatDTO.setDevice(orderReqVO.getDeviceName());
        orderSourceStatDTO.setUseAutoCoin(orderReqVO.getUseAutoCoin());
        orderSourceStatDTO.setSpecialConsole(orderReqVO.getSpecialConsole() == null ? OrderConstant.ZERO :
                Integer.valueOf(orderReqVO.getSpecialConsole()));
        orderSourceStatDTO.setReqSource(null == orderReqVO.getReqSource() ? null : orderReqVO.getReqSource().toString());
        orderSourceStatDTO.setLongRentCouponCode(orderReqVO.getLongOwnerCouponNo());
        logger.info("Build order source stat dto,result is ,orderSourceStatDTO:[{}]", JSON.toJSONString(orderSourceStatDTO));
        return orderSourceStatDTO;
    }


    /**
     * build ParentOrderDTO
     *
     * @param orderNo     订单号
     * @param riskAuditId 风控审核ID
     * @param replyFlag   是否自动应答
     * @param orderReqVO  下单请求参数
     * @return ParentOrderDTO
     */
    public ParentOrderDTO buildParentOrderDTO(String orderNo, String riskAuditId, boolean replyFlag,
                                              OrderReqVO orderReqVO) {
        ParentOrderDTO parentOrderDTO = new ParentOrderDTO();
        OrderDTO orderDTO = buildOrderDTO(orderNo, riskAuditId, orderReqVO);
        OrderSourceStatDTO orderSourceStatDTO = buildOrderSourceStatDTO(orderNo, orderReqVO);

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        if (replyFlag) {
            orderStatusDTO.setStatus(OrderStatusEnum.TO_CONFIRM.getStatus());
        } else {
            orderStatusDTO.setStatus(OrderStatusEnum.TO_PAY.getStatus());
        }

        parentOrderDTO.setOrderDTO(orderDTO);
        parentOrderDTO.setOrderStatusDTO(orderStatusDTO);
        parentOrderDTO.setOrderSourceStatDTO(orderSourceStatDTO);
        return parentOrderDTO;

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
    public OwnerOrderSubsidyDetailEntity buildOwnerOrderSubsidyDetailEntity(String orderNo, String ownerOrderNo,
                                                                            String memNo, OrderCouponDTO ownerCoupon) {
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
        ownerOrderSubsidyDetailEntity.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
        ownerOrderSubsidyDetailEntity.setSubsidyCostCode(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo());
        ownerOrderSubsidyDetailEntity.setSubsidyCostName(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getTxt());
        ownerOrderSubsidyDetailEntity.setSubsidyDesc("使用车主券抵扣租金");

        logger.info("Build ownerOrderSubsidyDetailEntity,result is ,ownerOrderSubsidyDetailEntity:[{}]",
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
    public OwnerOrderPurchaseDetailEntity buildOwnerOrderPurchaseDetailEntity(String orderNo, String ownerOrderNo,
                                                                              String memNo,
                                                                              RenterOrderCostDetailEntity rentAmtEntity) {

        if (null == rentAmtEntity) {
            return null;
        }
        OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity = new OwnerOrderPurchaseDetailEntity();
        BeanUtils.copyProperties(rentAmtEntity, ownerOrderPurchaseDetailEntity);
        ownerOrderPurchaseDetailEntity.setTotalAmount(Math.abs(rentAmtEntity.getTotalAmount()));
        ownerOrderPurchaseDetailEntity.setCostCode(OwnerCashCodeEnum.RENT_AMT.getCashNo());
        ownerOrderPurchaseDetailEntity.setCostCodeDesc(OwnerCashCodeEnum.RENT_AMT.getTxt());
        ownerOrderPurchaseDetailEntity.setOrderNo(orderNo);
        ownerOrderPurchaseDetailEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderPurchaseDetailEntity.setMemNo(memNo);

        logger.info("Build ownerOrderPurchaseDetailEntity,result is ,ownerOrderPurchaseDetailEntity:[{}]",
                JSON.toJSONString(ownerOrderPurchaseDetailEntity));
        return ownerOrderPurchaseDetailEntity;
    }

    /**
     * 车主订单请求参数封装
     *
     * @param reqContext  下单请求参数
     * @param costContext 订单费用详情
     * @return OwnerOrderReqDTO 车主订单请求参数
     */
    private OwnerOrderReqDTO buildOwnerOrderReqDTO(OrderReqContext reqContext, OrderCostContext costContext) {

        OwnerOrderReqDTO ownerOrderReqDTO = new OwnerOrderReqDTO();
        ownerOrderReqDTO.setOrderNo(costContext.getReqContext().getBaseReqDTO().getOrderNo());
        ownerOrderReqDTO.setOwnerOrderNo(costContext.getReqContext().getBaseReqDTO().getOwnerOrderNo());
        ownerOrderReqDTO.setMemNo(reqContext.getRenterGoodsDetailDto().getOwnerMemNo());
        ownerOrderReqDTO.setExpRentTime(reqContext.getOrderReqVO().getRentTime());
        ownerOrderReqDTO.setExpRevertTime(reqContext.getOrderReqVO().getRevertTime());
        ownerOrderReqDTO.setIsUseSpecialPrice(null == reqContext.getOrderReqVO().getUseSpecialPrice() ?
                OrderConstant.NO :
                Integer.valueOf(reqContext.getOrderReqVO().getUseSpecialPrice()));
        ownerOrderReqDTO.setSrvGetFlag(reqContext.getOrderReqVO().getSrvGetFlag());
        ownerOrderReqDTO.setSrvReturnFlag(reqContext.getOrderReqVO().getSrvReturnFlag());
        ownerOrderReqDTO.setCarNo(reqContext.getOrderReqVO().getCarNo());
        ownerOrderReqDTO.setCategory(Integer.valueOf(reqContext.getOrderReqVO().getOrderCategory()));

        ownerOrderReqDTO.setReplyFlag(reqContext.getOwnerGoodsDetailDto().getReplyFlag());
        ownerOrderReqDTO.setCarOwnerType(reqContext.getOwnerGoodsDetailDto().getCarOwnerType());
        ownerOrderReqDTO.setServiceRate(reqContext.getOwnerGoodsDetailDto().getServiceRate());
        ownerOrderReqDTO.setServiceProxyRate(reqContext.getOwnerGoodsDetailDto().getServiceProxyRate());


        Optional<OrderCouponDTO> ownerCoupon =
                costContext.getCostDetailContext().getCoupons().stream().filter(coupon -> Objects.nonNull(coupon.getCouponType()) && coupon.getCouponType() == CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode()).findFirst();
        ownerOrderReqDTO.setOwnerOrderSubsidyDetailEntity(buildOwnerOrderSubsidyDetailEntity(ownerOrderReqDTO.getOrderNo(),
                ownerOrderReqDTO.getOwnerOrderNo(), ownerOrderReqDTO.getMemNo(), ownerCoupon.orElse(null)));

        Optional<RenterOrderCostDetailEntity> rentAmtEntity =
                costContext.getCostDetailContext().getCostDetails().stream().filter(costDetail -> StringUtils.equals(costDetail.getCostCode(),
                        RenterCashCodeEnum.RENT_AMT.getCashNo())).findFirst();
        ownerOrderReqDTO.setOwnerOrderPurchaseDetailEntity(buildOwnerOrderPurchaseDetailEntity(ownerOrderReqDTO.getOrderNo(), ownerOrderReqDTO.getOwnerOrderNo(),
                ownerOrderReqDTO.getMemNo(), rentAmtEntity.orElse(null)));

        ownerOrderReqDTO.setShowRentTime(null == reqContext.getCarRentTimeRangeDTO() ?
                reqContext.getOrderReqVO().getRentTime() :
                reqContext.getCarRentTimeRangeDTO().getAdvanceStartDate());

        ownerOrderReqDTO.setShowRevertTime(null == reqContext.getCarRentTimeRangeDTO() ? reqContext.getOrderReqVO().getRevertTime()
                : reqContext.getCarRentTimeRangeDTO().getDelayEndDate());

        ownerOrderReqDTO.setGpsSerialNumber(reqContext.getRenterGoodsDetailDto().getGpsSerialNumber());
        logger.info("Build owner order reqDTO,result is ,ownerOrderReqDTO:[{}]",
                JSON.toJSONString(ownerOrderReqDTO));
        return ownerOrderReqDTO;
    }


}
