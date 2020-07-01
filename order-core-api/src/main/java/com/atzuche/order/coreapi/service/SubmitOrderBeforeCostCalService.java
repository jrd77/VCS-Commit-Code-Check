package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.SysConfigSDK;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.SectionDeliveryUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.SectionParamDTO;
import com.atzuche.order.commons.enums.OsTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.AccurateGetReturnSrvVO;
import com.atzuche.order.commons.vo.req.AdminGetDisCouponListReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.AdminGetDisCouponListResVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.RenterCostDetailVO;
import com.atzuche.order.commons.vo.res.RenterDeliveryFeeDetailVO;
import com.atzuche.order.commons.vo.res.SectionDeliveryVO;
import com.atzuche.order.commons.vo.res.coupon.DisCoupon;
import com.atzuche.order.commons.vo.res.coupon.OwnerDisCoupon;
import com.atzuche.order.commons.vo.res.order.*;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submit.filter.cost.LongOrderCostFilterChain;
import com.atzuche.order.coreapi.submit.filter.cost.LongSubmitOrderBeforeCostFilterChain;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryMode;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.DeductContextDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.service.InsurAbamentDiscountService;
import com.atzuche.order.renterorder.service.RenterOrderCalCostService;
import com.atzuche.order.renterorder.service.RenterOrderCostHandleService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.RenterOrderCarDepositResVO;
import com.atzuche.order.renterorder.vo.RenterOrderIllegalResVO;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerDiscountCouponVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.coupon.api.MemAvailCoupon;
import com.autoyol.coupon.api.MemAvailCouponRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 下单前费用计算
 *
 * @author pengcheng.fu
 * @date 2020/1/11 16:11
 */
@Service
public class SubmitOrderBeforeCostCalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderBeforeCostCalService.class);

    @Autowired
    private RenterOrderCalCostService renterOrderCalCostService;
    @Autowired
    private OrderCommonConver orderCommonConver;
    @Autowired
    private MemProxyService memberService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterOrderCostHandleService renterOrderCostHandleService;
    @Autowired
    private WalletProxyService walletProxyService;
    @Autowired
    private RenterCostFacadeService renterCostFacadeService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired
    private CouponAndCoinHandleService couponAndCoinHandleService;
    @Autowired
    private InsurAbamentDiscountService insurAbamentDiscountService;
    @Autowired
    private SubmitOrderInitContextService submitOrderInitContextService;
    @Autowired
    private LongSubmitOrderBeforeCostFilterChain longSubmitOrderBeforeCostFilterChain;
    @Autowired
    private CityConfigSDK cityConfigSDK;
    @Autowired
    private SysConfigSDK sysConfigSDK;



    /**
     * 下单前费用计算
     *
     * @param orderReqVO 请求参数
     * @return NormalOrderCostCalculateResVO 返回信息
     */
    public NormalOrderCostCalculateResVO costCalculate(OrderReqVO orderReqVO) {
        //请求参数处理
        OrderReqContext reqContext = submitOrderInitContextService.convertOrderReqContext(orderReqVO);
        RenterOrderReqVO renterOrderReqVO = orderCommonConver.buildRenterOrderReqVO(null, null, reqContext);
        RenterOrderCostReqDTO renterOrderCostReqDTO =
                renterOrderService.buildRenterOrderCostReqDTO(renterOrderReqVO);
        // 获取平台保障费和补充保障服务费折扣补贴
 		List<RenterOrderSubsidyDetailDTO> insurDiscountSubsidyList = insurAbamentDiscountService.getInsureDiscountSubsidy(renterOrderCostReqDTO, null);
 		renterOrderCostReqDTO.setSubsidyOutList(insurDiscountSubsidyList);
 		RenterOrderCostRespDTO renterOrderCostRespDTO =
                renterOrderCalCostService.calcBasicRenterOrderCostAndDeailList(renterOrderCostReqDTO);

        //抵扣公共信息抽取
        DeductContextDTO deductContext = orderCommonConver.initDeductContext(renterOrderCostRespDTO);
        deductContext.setOsVal(StringUtils.isBlank(orderReqVO.getOS()) ?
                OsTypeEnum.OTHER.getOsVal() : OsTypeEnum.from(orderReqVO.getOS()).getOsVal());
        //车主券抵扣
        CarOwnerCouponReductionVO carOwnerCouponReductionVO =
                renterOrderCalCostService.getCarOwnerCouponReductionVo(deductContext,
                new OwnerCouponReqVO(Integer.valueOf(orderReqVO.getMemNo()),Integer.valueOf(orderReqVO.getCarNo()),
                        orderReqVO.getCarOwnerCouponNo()));
        //TODO:限时红包


        //优惠券抵扣
        MemAvailCouponRequestVO memAvailCouponRequestVO =
                renterOrderService.buildMemAvailCouponRequestVO(renterOrderCostRespDTO, renterOrderReqVO);
        CouponReductionVO couponReductionVO = renterOrderCalCostService.getCouponReductionVO(deductContext,
                memAvailCouponRequestVO,
                renterOrderReqVO.getDisCouponIds(),renterOrderReqVO.getGetCarFreeCouponId());
        //凹凸币
        AutoCoinReductionVO autoCoinReductionVO = renterOrderCalCostService.getAutoCoinReductionVO(deductContext,
                orderReqVO.getMemNo(),
                orderReqVO.getUseAutoCoin());

        //钱包抵扣信息
        int wallet = walletProxyService.getWalletByMemNo(renterOrderReqVO.getMemNo());

        //车辆押金处理
        RenterOrderCarDepositResVO renterOrderCarDepositResVO =
                renterOrderCostHandleService.handleCarDepositAmtNotSave(renterOrderReqVO);
        //违章押金处理
        RenterOrderIllegalResVO renterOrderIllegalResVO =
                renterOrderCostHandleService.handleIllegalDepositAmt(renterOrderCostReqDTO.getCostBaseDTO(),renterOrderReqVO);


        //返回信息处理
        NormalOrderCostCalculateResVO res = new NormalOrderCostCalculateResVO();
        res.setCostItemList(orderCommonConver.buildCostItemList(renterOrderCostRespDTO, renterOrderReqVO));
        res.setTotalCost(orderCommonConver.buildTotalCostVO(res.getCostItemList()));
        res.setDeposit(orderCommonConver.buildDepositAmtVO(renterOrderCarDepositResVO));
        res.setIllegalDeposit(orderCommonConver.buildIllegalDepositVO(renterOrderIllegalResVO));

        ReductionVO reduction = new ReductionVO();
        reduction.setCarOwnerCouponReduction(carOwnerCouponReductionVO);
        reduction.setCouponReduction(couponReductionVO);
        reduction.setAutoCoinReduction(autoCoinReductionVO);
        reduction.setWalletReduction(new WalletReductionVO(wallet));
        res.setReduction(reduction);
        return res;

    }

    /**
     * 长租订单-下单前费用计算
     *
     * @param orderReqVO 请求参数
     * @return NormalOrderCostCalculateResVO
     */
    public NormalOrderCostCalculateResVO costCalculateForLong(OrderReqVO orderReqVO) {
        //请求参数初始化
        OrderReqContext context = submitOrderInitContextService.convertOrderReqContext(orderReqVO);
        //费用计算参数初始化
        OrderCostContext orderCostContext = orderCommonConver.initOrderCostContext(null, null, null, context);
        //计算订单费用
        longSubmitOrderBeforeCostFilterChain.calculate(orderCostContext);
        //费用项列表
        List<CostItemVO> costItemVOList = orderCommonConver.buildCostItemList(orderCostContext);
        //费用总计
        TotalCostVO totalCostVO = orderCommonConver.buildTotalCostVO(costItemVOList);
        //车辆押金信息
        DepositAmtVO carDeposit =
                orderCommonConver.buildDepositAmtVO(orderCostContext.getResContext().getOrderCarDepositAmtResDTO().getCarDeposit());
        //违章押金信息
        IllegalDepositVO illegalDepositVO =
                orderCommonConver.buildIllegalDepositVO(orderCostContext.getResContext().getOrderIllegalDepositAmtResDTO().getIllegalDeposit());

        //抵扣公共信息抽取
        DeductContextDTO deductContext = orderCommonConver.initDeductContext(orderCostContext);
        deductContext.setOsVal(StringUtils.isBlank(orderReqVO.getOS()) ?
                OsTypeEnum.OTHER.getOsVal() : OsTypeEnum.from(orderReqVO.getOS()).getOsVal());

        //车主券抵扣
        CarOwnerCouponReductionVO carOwnerCouponReductionVO = new CarOwnerCouponReductionVO(OrderConstant.ZERO);
        //限时红包
        //优惠券抵扣
        MemAvailCouponRequestVO memAvailCouponRequestVO =
                orderCommonConver.buildMemAvailCouponRequestVO(context, orderCostContext);
        CouponReductionVO couponReductionVO = renterOrderCalCostService.getCouponReductionVO(deductContext,
                memAvailCouponRequestVO, orderReqVO.getDisCouponIds());
        //凹凸币
        AutoCoinReductionVO autoCoinReductionVO = renterOrderCalCostService.getAutoCoinReductionVO(deductContext,
                orderReqVO.getMemNo(),
                orderReqVO.getUseAutoCoin());
        //钱包抵扣信息
        WalletReductionVO walletReductionVO =
                new WalletReductionVO(walletProxyService.getWalletByMemNo(orderReqVO.getMemNo()));

        NormalOrderCostCalculateResVO res = new NormalOrderCostCalculateResVO();
        res.setTotalCost(totalCostVO);
        res.setCostItemList(costItemVOList);
        res.setDeposit(carDeposit);
        res.setIllegalDeposit(illegalDepositVO);

        ReductionVO reduction = new ReductionVO();
        reduction.setCarOwnerCouponReduction(carOwnerCouponReductionVO);
        reduction.setCouponReduction(couponReductionVO);
        reduction.setAutoCoinReduction(autoCoinReductionVO);
        reduction.setWalletReduction(walletReductionVO);
        res.setReduction(reduction);
        return res;
    }


    /**
     * 获取订单内租客优惠抵扣信息
     * <p>可用平台券列表</p>
     * <p>可用送取服务券列表</p>
     * <p>可用车主券列表</p>
     * <p>可用钱包余额</p>
     *
     * @param reqVO 请求参数
     */
    public AdminGetDisCouponListResVO getDisCouponListByOrderNo(AdminGetDisCouponListReqVO reqVO){
        OrderEntity orderEntity = orderService.getOrderEntity(reqVO.getOrderNo());
        if(null == orderEntity){
            throw new OrderNotFoundException(reqVO.getOrderNo());
        }
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderEntity.getOrderNo());
        if(null == renterOrderEntity){
            throw new OrderNotFoundException(reqVO.getOrderNo());
        }
        String renterOrderNo = renterOrderEntity.getRenterOrderNo();
        RenterCostDetailVO renterCostDetail = renterCostFacadeService.getRenterCostFullDetail(reqVO.getOrderNo(), renterOrderNo, reqVO.getMemNo());

        int holidayAverage = 0;
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostDetailService.getRenterOrderCostDetailList(reqVO.getOrderNo(), renterOrderNo);
        if(CollectionUtils.isNotEmpty(renterOrderCostDetailEntities)) {
            Optional<RenterOrderCostDetailEntity> rentAmtOptional = renterOrderCostDetailEntities.stream().filter(cost -> cost.getCostCode().equals(RenterCashCodeEnum.RENT_AMT.getCashNo())).findFirst();
            holidayAverage = rentAmtOptional.isPresent() ? rentAmtOptional.get().getUnitPrice() : OrderConstant.ZERO;
        }
        int srvGetCost = 0;
        int srvReturnCost = 0;
        //租客信息
        RenterMemberDTO renterMember = memberService.getRenterMemberInfo(reqVO.getMemNo());

        MemAvailCouponRequest request = new MemAvailCouponRequest();

        request.setMemNo(Integer.valueOf(reqVO.getMemNo()));
        request.setIsNew((renterMember.getIsNew() != null && renterMember.getIsNew() == 1) ? true:false);
        request.setCityCode(Integer.valueOf(orderEntity.getCityCode()));
        request.setLabelIds(null);
        request.setRentTime(DateUtils.formateLong(renterOrderEntity.getExpRentTime(),DateUtils.DATE_DEFAUTE));
        request.setRevertTime(DateUtils.formateLong(renterOrderEntity.getExpRevertTime(),DateUtils.DATE_DEFAUTE));
        request.setRentAmt(renterCostDetail.getRentAmt());
        request.setAveragePrice(holidayAverage);
        request.setInsureTotalPrices(renterCostDetail.getInsuranceAmt());
        request.setAbatement(renterCostDetail.getAbatementInsuranceAmt());
        RenterDeliveryFeeDetailVO renterDeliveryFeeDetailVO = renterCostDetail.getDeliveryFeeDetail();
        if(null != renterDeliveryFeeDetailVO) {
            srvGetCost = renterDeliveryFeeDetailVO.getSrvGetCostAmt() + renterDeliveryFeeDetailVO.getGetBlockedRaiseAmt();
            srvReturnCost = renterDeliveryFeeDetailVO.getSrvReturnCostAmt() + renterDeliveryFeeDetailVO.getReturnBlockedRaiseAmt();
        }
        request.setSrvGetCost(srvGetCost);
        request.setSrvReturnCost(srvReturnCost);
        request.setCarNo(Integer.valueOf(renterOrderEntity.getGoodsCode()));

        //获取平台券
        List<MemAvailCoupon>  platformCoupons = couponAndCoinHandleService.getMemPlatformCoupon(request);
        request.setOriginalRentAmt(renterCostDetail.getRentAmt());
        request.setCounterFee(renterCostDetail.getFee());
        //获取送取服务券
        List<MemAvailCoupon>  getCarFeeCoupons = couponAndCoinHandleService.getMemGetCarFeeCoupon(request);
        //获取车主券
        List<OwnerDiscountCouponVO> ownerCoupons = couponAndCoinHandleService.getMemOwnerCoupon(request.getRentAmt(),request.getMemNo(),request.getCarNo());
        //获取钱包余额
        int balance = walletProxyService.getWalletByMemNo(reqVO.getMemNo());

        AdminGetDisCouponListResVO resVO = new AdminGetDisCouponListResVO();
        resVO.setWalletBal(String.valueOf(balance));
        resVO.setOwnerDisCouponList(buildOwnerDisCoupon(ownerCoupons));
        resVO.setPlatformCouponList(buildDisCoupon(platformCoupons, OrderConstant.ONE));
        resVO.setGetCarFeeDisCouponList(buildDisCoupon(getCarFeeCoupons, OrderConstant.TWO));
        return resVO;

    }


    /**
     * 车主券信息转换
     *
     * @param list 车主券列表
     * @return List<OwnerDisCoupon>
     */
    private List<OwnerDisCoupon> buildOwnerDisCoupon(List<OwnerDiscountCouponVO> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            List<OwnerDisCoupon> coupons = new ArrayList<>();
            list.forEach(c -> {
                OwnerDisCoupon ownerDisCoupon = new OwnerDisCoupon();
                BeanUtils.copyProperties(c, ownerDisCoupon);
                coupons.add(ownerDisCoupon);
            });

            return coupons;
        }

        return null;
    }

    /**
     * 优惠券信息转换
     *
     * @param list 优惠券列表
     * @param opType 操作类型:1 优惠券 2 送取服务券
     * @return List<DisCoupon>
     */
    private List<DisCoupon>  buildDisCoupon(List<MemAvailCoupon> list, int opType) {
        if(CollectionUtils.isNotEmpty(list)) {
            List<DisCoupon> coupons = new ArrayList<>();
            if(2 == opType) {
                list =  list.stream().filter(c -> c.getCouponType() == 8).collect(Collectors.toList());
            }
            list.forEach(c -> {
                DisCoupon disCoupon = new DisCoupon();
                disCoupon.setDisCouponId(c.getId());
                disCoupon.setDisName(c.getDisName());
                disCoupon.setCondAmt(String.valueOf(c.getCondAmt()));
                disCoupon.setDisAmt(String.valueOf(c.getRealCouponOffset()));
                disCoupon.setStartDate(c.getStartDate());
                disCoupon.setEndDate(c.getEndDate());
                disCoupon.setRealCouponOffset(String.valueOf(c.getRealCouponOffset()));
                coupons.add(disCoupon);
            });

            return coupons;
        }

        return null;
    }
    
    
    /**
     * 获取区间配送信息
     * @param orderReqVO
     * @return SectionDeliveryVO
     */
    public SectionDeliveryVO getSectionDelivery(OrderReqVO orderReqVO) {
    	if (orderReqVO == null) {
    		return null;
    	}
    	// 是否使用取车服务:0.否 1.是
    	Integer srvGetFlag = orderReqVO.getSrvGetFlag() == null ? 0:orderReqVO.getSrvGetFlag();
    	// 是否使用还车服务:0.否 1.是
    	Integer srvReturnFlag = orderReqVO.getSrvReturnFlag() == null ? 0:orderReqVO.getSrvReturnFlag();
    	if (srvGetFlag.intValue() == 0 && srvReturnFlag.intValue() == 0) {
    		// 未使用取还车服务不需要计算
    		return null;
    	}
    	SectionDeliveryVO sectionDeliveryVO = new SectionDeliveryVO();
    	// 配送模式：0-区间配送，1-精准配送
        Integer distributionMode = orderReqVO.getDistributionMode() == null ? 0:orderReqVO.getDistributionMode();
        sectionDeliveryVO.setDistributionMode(distributionMode);
        AccurateGetReturnSrvVO accurateGetReturnSrvVO = getAccurateGetReturnSrvVO();
        if (accurateGetReturnSrvVO != null) {
        	sectionDeliveryVO.setAccurateGetSrvUnit(accurateGetReturnSrvVO.getAccurateGetSrvAmt());
        	sectionDeliveryVO.setAccurateReturnSrvUnit(accurateGetReturnSrvVO.getAccurateReturnSrvAmt());
        }
    	if (distributionMode.intValue() == 1) {
        	// 精准配送
        	return sectionDeliveryVO;
        }
    	LOGGER.info("config-从城市配置中获取区间配置,cityCode=[{}]", orderReqVO.getCityCode());
        CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(),Integer.valueOf(orderReqVO.getCityCode()));
        LOGGER.info("config-从城市配置中获取区间配置,configByCityCode=[{}]", JSON.toJSONString(configByCityCode));
        RenterOrderDeliveryMode mode = new RenterOrderDeliveryMode();
        BeanUtils.copyProperties(configByCityCode, mode);
        SectionParamDTO sectionParam = getSectionParamDTO(orderReqVO.getRentTime(), orderReqVO.getRevertTime(), mode);
    	sectionDeliveryVO = SectionDeliveryUtils.getRenterSectionDeliveryVO(sectionParam, DateUtils.DATE_DEFAUTE1);
    	if (sectionDeliveryVO == null) {
    		return null;
    	}
    	sectionDeliveryVO.setDistributionMode(distributionMode);
    	if (accurateGetReturnSrvVO != null) {
        	sectionDeliveryVO.setAccurateGetSrvUnit(accurateGetReturnSrvVO.getAccurateGetSrvAmt());
        	sectionDeliveryVO.setAccurateReturnSrvUnit(accurateGetReturnSrvVO.getAccurateReturnSrvAmt());
        }
    	if (srvGetFlag.intValue() == 0) {
    		sectionDeliveryVO.setRentTimeEnd(null);
    		sectionDeliveryVO.setRentTimeStart(null);
    	}
    	if (srvReturnFlag.intValue() == 0) {
    		sectionDeliveryVO.setRevertTimeEnd(null);
    		sectionDeliveryVO.setRevertTimeStart(null);
    	}
        return sectionDeliveryVO;
    }
    
    /**
     * 数据转换
     * @param rentTime
     * @param revertTime
     * @param mode
     * @return SectionParamDTO
     */
    public SectionParamDTO getSectionParamDTO(LocalDateTime rentTime, LocalDateTime revertTime, RenterOrderDeliveryMode mode) {
    	SectionParamDTO sectionParamDTO = new SectionParamDTO();
    	sectionParamDTO.setRentAfterMinutes(mode.getRentAfterMinutes());
    	sectionParamDTO.setRentBeforeMinutes(mode.getRentBeforeMinutes());
    	sectionParamDTO.setRentTime(rentTime);
    	sectionParamDTO.setRevertAfterMinutes(mode.getRevertAfterMinutes());
    	sectionParamDTO.setRevertBeforeMinutes(mode.getRevertBeforeMinutes());
    	sectionParamDTO.setRevertTime(revertTime);
    	return sectionParamDTO;
    }
    
    /**
     * 获取准时达服务单价
     * @return AccurateGetReturnSrvVO
     */
    public AccurateGetReturnSrvVO getAccurateGetReturnSrvVO() {
    	AccurateGetReturnSrvVO accurateGetReturnSrvVO = new AccurateGetReturnSrvVO();
    	List<SysConfigEntity> sysConfigSDKConfig = sysConfigSDK.getConfig(new DefaultConfigContext());
        List<SysConfigEntity> sysConfigEntityList = Optional.ofNullable(sysConfigSDKConfig)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> GlobalConstant.GET_RETURN_ACCURATE_SRV.equals(x.getAppType()))
                .collect(Collectors.toList());
        SysConfigEntity sysGetAccurateAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.ACCURATE_GET_SRV_UNIT.equals(x.getItemKey())).findFirst().get();
        LOGGER.info("config-从配置中获取精准取车服务费单价sysGetAccurateAmt=[{}]",JSON.toJSONString(sysGetAccurateAmt));
        Integer getAccurateCost = (sysGetAccurateAmt==null||sysGetAccurateAmt.getItemValue()==null) ? 30 : Integer.valueOf(sysGetAccurateAmt.getItemValue());
        SysConfigEntity sysReturnAccurateAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.ACCURATE_RETURN_SRV_UNIT.equals(x.getItemKey())).findFirst().get();
        LOGGER.info("config-从配置中获取精准还车服务费单价sysReturnAccurateAmt=[{}]",JSON.toJSONString(sysReturnAccurateAmt));
        Integer returnAccurateCost = (sysReturnAccurateAmt==null||sysReturnAccurateAmt.getItemValue()==null) ? 30 : Integer.valueOf(sysReturnAccurateAmt.getItemValue());
        accurateGetReturnSrvVO.setAccurateGetSrvAmt(getAccurateCost);
        accurateGetReturnSrvVO.setAccurateReturnSrvAmt(returnAccurateCost);
        return accurateGetReturnSrvVO;
    }
}
