package com.atzuche.order.coreapi.service;

import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.OsTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.AdminGetDisCouponListReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.AdminGetDisCouponListResVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.RenterCostDetailVO;
import com.atzuche.order.commons.vo.res.RenterDeliveryFeeDetailVO;
import com.atzuche.order.commons.vo.res.coupon.DisCoupon;
import com.atzuche.order.commons.vo.res.coupon.OwnerDisCoupon;
import com.atzuche.order.commons.vo.res.order.*;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.coreapi.service.remote.CarRentalTimeApiProxyService;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.DeductContextDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
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
    private CarRentalTimeApiProxyService carRentalTimeApiService;
    @Autowired
    private OrderCommonConver orderCommonConver;
    @Autowired
    private MemProxyService memberService;
    @Autowired
    private CarProxyService goodsService;
    @Autowired
    private RenterCommodityService renterCommodityService;
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



    /**
     * 下单前费用计算
     *
     * @param orderReqVO 请求参数
     * @return NormalOrderCostCalculateResVO 返回信息
     */
    public NormalOrderCostCalculateResVO costCalculate(OrderReqVO orderReqVO) {
        //公共参数处理
        //1.请求参数处理
        OrderReqContext reqContext = new OrderReqContext();
        reqContext.setOrderReqVO(orderReqVO);
        //租客会员信息
        RenterMemberDTO renterMemberDTO =
                memberService.getRenterMemberInfo(String.valueOf(orderReqVO.getMemNo()));
        reqContext.setRenterMemberDto(renterMemberDTO);
        //租客商品明细
        RenterGoodsDetailDTO renterGoodsDetailDTO = goodsService.getRenterGoodsDetail(orderCommonConver.buildCarDetailReqVO(orderReqVO));
        reqContext.setRenterGoodsDetailDto(renterGoodsDetailDTO);
        //一天一价分组
        renterGoodsDetailDTO = renterCommodityService.setPriceAndGroup(renterGoodsDetailDTO);
        //车主商品明细
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = goodsService.getOwnerGoodsDetail(renterGoodsDetailDTO);
        reqContext.setOwnerGoodsDetailDto(ownerGoodsDetailDTO);
        //车主会员信息
        OwnerMemberDTO ownerMemberDTO = memberService.getOwnerMemberInfo(renterGoodsDetailDTO.getOwnerMemNo());
        reqContext.setOwnerMemberDto(ownerMemberDTO);
        //租车费用处理
        CarRentTimeRangeResVO carRentTimeRangeResVO =
                carRentalTimeApiService.getCarRentTimeRange(carRentalTimeApiService.buildCarRentTimeRangeReqVO(orderReqVO));

        RenterOrderReqVO renterOrderReqVO = orderCommonConver.buildRenterOrderReqVO(null, null, reqContext,
                carRentTimeRangeResVO);
        RenterOrderCostReqDTO renterOrderCostReqDTO =
                renterOrderService.buildRenterOrderCostReqDTO(renterOrderReqVO);
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
        res.setCostItemList(orderCommonConver.buildCostItemList(renterOrderCostRespDTO));
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
            Optional<RenterOrderCostDetailEntity> rentAmtOptional = renterOrderCostDetailEntities.stream().filter(cost -> cost.getCostCode().equals(RenterCashCodeEnum.RENT_AMT)).findFirst();
            holidayAverage = rentAmtOptional.get().getUnitPrice();
        }
        int srvGetCost = 0;
        int srvReturnCost = 0;
        //租客信息
        RenterMemberDTO renterMember = memberService.getRenterMemberInfo(reqVO.getMemNo());

        MemAvailCouponRequest request = new MemAvailCouponRequest();

        request.setMemNo(Integer.valueOf(reqVO.getMemNo()));
        request.setIsNew(request.getIsNew());
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
        resVO.setPlatformCouponList(buildDisCoupon(platformCoupons));
        resVO.setGetCarFeeDisCouponList(buildDisCoupon(getCarFeeCoupons));
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
            list.stream().forEach(c -> {
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
     * @return List<DisCoupon>
     */
    private List<DisCoupon>  buildDisCoupon(List<MemAvailCoupon> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            List<DisCoupon> coupons = new ArrayList<>();
            list.stream().forEach(c -> {
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
}
