package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.enums.OsTypeEnum;
import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.order.*;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.vo.req.CarRentTimeRangeReqVO;
import com.atzuche.order.coreapi.entity.vo.res.CarRentTimeRangeResVO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
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
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.atzuche.order.wallet.WalletProxyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private CarRentalTimeApiService carRentalTimeApiService;
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
                renterOrderCalCostService.getOrderCostAndDeailList(renterOrderCostReqDTO);

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
        //TODO:凹凸币
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




}
