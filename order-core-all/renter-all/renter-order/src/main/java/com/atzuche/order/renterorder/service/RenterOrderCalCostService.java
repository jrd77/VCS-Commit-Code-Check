package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.GetReturnCostDTO;
import com.atzuche.order.rentercost.entity.dto.GetReturnOverCostDTO;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.entity.vo.GetReturnResponseVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidResultVO;
import com.atzuche.order.renterorder.vo.owner.OwnerDiscountCouponVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.coupon.api.MemAvailCoupon;
import com.autoyol.coupon.api.MemAvailCouponRequest;
import com.autoyol.coupon.api.MemAvailCouponResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 租客订单相关费用计算处理类
 *
 * @author ZhangBin
 * @date 2019/12/25 15:17
 */
@Service
public class RenterOrderCalCostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenterOrderCalCostService.class);

    @Resource
    private RenterOrderMapper renterOrderMapper;

    @Resource
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Resource
    private OwnerDiscountCouponService ownerDiscountCouponService;

    @Resource
    private PlatformCouponService platformCouponService;

    @Resource
    private RenterOrderCostDetailService renterOrderCostDetailService;

    @Resource
    private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

    @Resource
    private RenterOrderCostService renterOrderCostService;



    /**
     * 获取费用项和费用明细列表 + 落库
     *
     * @author ZhangBin
     * @date 2019/12/24 15:21
     **/
    public RenterOrderCostRespDTO getOrderCostAndDeailList(RenterOrderCostReqDTO renterOrderCostReqDTO) {
        CostBaseDTO costBaseDTO = renterOrderCostReqDTO.getCostBaseDTO();
        RenterOrderCostRespDTO renterOrderCostRespDTO = new RenterOrderCostRespDTO();
        List<RenterOrderCostDetailEntity> detailList = new ArrayList<>();
        List<RenterOrderSubsidyDetailDTO> subsidyList = new ArrayList<>();

        //获取租金
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostCombineService.listRentAmtEntity(renterOrderCostReqDTO.getRentAmtDTO());
        int rentAmt = renterOrderCostDetailEntities.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        detailList.addAll(renterOrderCostDetailEntities);
        renterOrderCostRespDTO.setRentAmount(rentAmt);

        //获取平台保障费
        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(renterOrderCostReqDTO.getInsurAmtDTO());
        int insurAmt = insurAmtEntity.getTotalAmount();
        renterOrderCostRespDTO.setBasicEnsureAmount(insurAmt);
        detailList.add(insurAmtEntity);

        //获取全面保障费

        List<RenterOrderCostDetailEntity> comprehensiveEnsureList = renterOrderCostCombineService.listAbatementAmtEntity(renterOrderCostReqDTO.getAbatementAmtDTO());
        int comprehensiveEnsureAmount = comprehensiveEnsureList.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        renterOrderCostRespDTO.setComprehensiveEnsureAmount(comprehensiveEnsureAmount);
        detailList.addAll(comprehensiveEnsureList);

        //获取附加驾驶人费用
        RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(renterOrderCostReqDTO.getExtraDriverDTO());
        int totalAmount = extraDriverInsureAmtEntity.getTotalAmount();
        renterOrderCostRespDTO.setAdditionalDrivingEnsureAmount(totalAmount);
        detailList.add(extraDriverInsureAmtEntity);

        //获取平台手续费
        RenterOrderCostDetailEntity serviceChargeFeeEntity = renterOrderCostCombineService.getServiceChargeFeeEntity(costBaseDTO);
        int serviceAmount = serviceChargeFeeEntity.getTotalAmount();
        renterOrderCostRespDTO.setCommissionAmount(serviceAmount);
        detailList.add(serviceChargeFeeEntity);

        //获取取还车费用
        GetReturnCarCostReqDto getReturnCarCostReqDto = renterOrderCostReqDTO.getGetReturnCarCostReqDto();
        getReturnCarCostReqDto.setSumJudgeFreeFee(Math.abs(rentAmt + insurAmt + serviceAmount + comprehensiveEnsureAmount));
        GetReturnCostDTO returnCarCost = renterOrderCostCombineService.getReturnCarCost(getReturnCarCostReqDto);
        int getReturnAmt = returnCarCost.getRenterOrderCostDetailEntityList().stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        detailList.addAll(returnCarCost.getRenterOrderCostDetailEntityList());
        List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = returnCarCost.getRenterOrderSubsidyDetailDTOList();
        subsidyList.addAll(renterOrderSubsidyDetailDTOList);
        GetReturnResponseVO getReturnResponseVO = returnCarCost.getGetReturnResponseVO();
        renterOrderCostRespDTO.setGetRealAmt(getReturnResponseVO.getGetFee());
        renterOrderCostRespDTO.setReturnRealAmt(getReturnResponseVO.getReturnFee());

        //获取取还车超运能费用
        GetReturnOverCostDTO getReturnOverCost = renterOrderCostCombineService.getGetReturnOverCost(renterOrderCostReqDTO.getGetReturnCarOverCostReqDto());
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = getReturnOverCost.getRenterOrderCostDetailEntityList();
        Integer getOverAmt = renterOrderCostDetailEntityList.stream()
                .filter(x -> RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        Integer returnOverAmt = renterOrderCostDetailEntityList.stream()
                .filter(x -> RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        int getReturnOverCostAmount = getOverAmt + returnOverAmt;
        detailList.addAll(renterOrderCostDetailEntityList);
        renterOrderCostRespDTO.setGetOverAmt(getOverAmt);
        renterOrderCostRespDTO.setReturnOverAmt(returnOverAmt);

        //租车费用 = 租金+平台保障费+全面保障费+取还车费用+取还车超运能费用+附加驾驶员费用+手续费；
        int rentCarAmount = rentAmt + insurAmt + comprehensiveEnsureAmount + getReturnAmt + getReturnOverCostAmount + totalAmount + serviceAmount;

        renterOrderCostRespDTO.setRentCarAmount(rentCarAmount);
        renterOrderCostRespDTO.setRenterOrderCostDetailDTOList(detailList);
        LOGGER.info("获取费用项和费用明细列表 renterOrderCostRespDTO:[{}]", JSON.toJSONString(renterOrderCostRespDTO));

        //数据转化
        List<RenterOrderSubsidyDetailEntity> subsidyListEntity = subsidyList.stream().map(x -> {
            RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity = new RenterOrderSubsidyDetailEntity();
            BeanUtils.copyProperties(x, renterOrderSubsidyDetailEntity);
            renterOrderSubsidyDetailEntity.setOrderNo(costBaseDTO.getOrderNo());
            renterOrderSubsidyDetailEntity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
            return renterOrderSubsidyDetailEntity;
        }).collect(Collectors.toList());


        //保存费用明细
        renterOrderCostDetailService.saveRenterOrderCostDetailBatch(detailList);
        //保存补贴明细
        renterOrderSubsidyDetailService.saveRenterOrderSubsidyDetailBatch(subsidyListEntity);
        //保存费用统计信息
        RenterOrderCostEntity renterOrderCostEntity = new RenterOrderCostEntity();
        BeanUtils.copyProperties(renterOrderCostRespDTO,renterOrderCostEntity);
        renterOrderCostEntity.setOrderNo(costBaseDTO.getOrderNo());
        renterOrderCostEntity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        renterOrderCostService.saveRenterOrderCost(renterOrderCostEntity);
        return renterOrderCostRespDTO;
    }


    /**
     * 计算车主券抵扣信息
     *
     * @param ownerCouponGetAndValidReqVO 请求参数
     * @return OrderCouponDTO 订单优惠券信息
     */
    public OrderCouponDTO calOwnerCouponDeductInfo(OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO) {

        LOGGER.info("获取车主优惠券抵扣信息. param is,ownerCouponGetAndValidReqDTO:[{}]",
                JSON.toJSONString(ownerCouponGetAndValidReqVO));
        if (null == ownerCouponGetAndValidReqVO || StringUtils.isBlank(ownerCouponGetAndValidReqVO.getCouponNo())) {
            return null;
        }

        OwnerCouponGetAndValidResultVO result =
                ownerDiscountCouponService.getAndValidCoupon(ownerCouponGetAndValidReqVO.getOrderNo(),
                        ownerCouponGetAndValidReqVO.getRentAmt(), ownerCouponGetAndValidReqVO.getCouponNo(),
                        ownerCouponGetAndValidReqVO.getCarNo(), ownerCouponGetAndValidReqVO.getMark());

        if (null != result) {
            if (StringUtils.equals(ErrorCode.SUCCESS.getCode(), result.getResCode()) && null != result.getData()) {
                OwnerDiscountCouponVO coupon = result.getData().getCouponDTO();
                OrderCouponDTO ownerCoupon = new OrderCouponDTO();
                ownerCoupon.setCouponId(coupon.getCouponNo());
                ownerCoupon.setCouponName(coupon.getCouponName());
                ownerCoupon.setCouponDesc(coupon.getCouponText());
                ownerCoupon.setAmount(null == coupon.getDiscount() ? 0 : coupon.getDiscount());
                ownerCoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode());
                //绑定后变更为已使用
                ownerCoupon.setStatus(ownerCoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
                return ownerCoupon;
            }
        }
        return null;
    }


    /**
     * 计算取送服务券抵扣信息
     *
     * @param memAvailCouponRequestVO 请求参数
     * @return OrderCouponDTO 订单优惠券信息
     */
    public OrderCouponDTO calGetAndReturnSrvCouponDeductInfo(MemAvailCouponRequestVO memAvailCouponRequestVO) {

        LOGGER.info("获取优惠券抵扣信息(送取服务券). param is,memAvailCouponRequestVO:[{}]",
                JSON.toJSONString(memAvailCouponRequestVO));

        if (null == memAvailCouponRequestVO || StringUtils.isBlank(memAvailCouponRequestVO.getDisCouponId())) {
            return null;
        }
        MemAvailCouponRequest request = buildMemAvailCouponRequest(memAvailCouponRequestVO);
        MemAvailCouponResponse response = platformCouponService.findAvailMemGetAndReturnSrvCoupons(request);
        if (null != response && !CollectionUtils.isEmpty(response.getAvailCoupons())) {
            Map<String, MemAvailCoupon> availCouponMap = response.getAvailCoupons().stream()
                    .collect(Collectors.toMap(MemAvailCoupon::getId, memAvailCoupon -> memAvailCoupon));
            MemAvailCoupon memAvailCoupon = availCouponMap.get(memAvailCouponRequestVO.getDisCouponId());

            if (null != memAvailCoupon) {
                OrderCouponDTO getCarFeeDiscoupon = new OrderCouponDTO();
                getCarFeeDiscoupon.setCouponId(memAvailCouponRequestVO.getDisCouponId());
                getCarFeeDiscoupon.setCouponName(memAvailCoupon.getDisName());
                getCarFeeDiscoupon.setCouponDesc(memAvailCoupon.getDescription());
                getCarFeeDiscoupon.setAmount(null == memAvailCoupon.getRealCouponOffset() ? 0 : memAvailCoupon.getRealCouponOffset());
                getCarFeeDiscoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
                //绑定后变更为已使用
                getCarFeeDiscoupon.setStatus(getCarFeeDiscoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
                return getCarFeeDiscoupon;
            }

        }

        return null;
    }


    /**
     * 计算平台优惠券抵扣信息
     *
     * @param memAvailCouponRequestVO 请求参数
     * @return OrderCouponDTO 订单优惠券信息
     */
    public OrderCouponDTO calPlatformCouponDeductInfo(MemAvailCouponRequestVO memAvailCouponRequestVO) {

        LOGGER.info("获取优惠券抵扣信息(平台优惠券). param is,memAvailCouponRequestVO:[{}]",
                JSON.toJSONString(memAvailCouponRequestVO));

        if (null == memAvailCouponRequestVO || StringUtils.isBlank(memAvailCouponRequestVO.getDisCouponId())) {
            return null;
        }
        MemAvailCouponRequest request = buildMemAvailCouponRequest(memAvailCouponRequestVO);
        MemAvailCouponResponse response = platformCouponService.findAvailMemCoupons(request);
        if (null != response && !CollectionUtils.isEmpty(response.getAvailCoupons())) {
            Map<String, MemAvailCoupon> availCouponMap = response.getAvailCoupons().stream()
                    .collect(Collectors.toMap(MemAvailCoupon::getId, memAvailCoupon -> memAvailCoupon));
            MemAvailCoupon memAvailCoupon = availCouponMap.get(memAvailCouponRequestVO.getDisCouponId());

            if (null != memAvailCoupon) {
                OrderCouponDTO getCarFeeDiscoupon = new OrderCouponDTO();
                getCarFeeDiscoupon.setCouponId(memAvailCouponRequestVO.getDisCouponId());
                getCarFeeDiscoupon.setCouponName(memAvailCoupon.getDisName());
                getCarFeeDiscoupon.setCouponDesc(memAvailCoupon.getDescription());
                getCarFeeDiscoupon.setAmount(null == memAvailCoupon.getRealCouponOffset() ? 0 : memAvailCoupon.getRealCouponOffset());
                getCarFeeDiscoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
                //绑定后变更为已使用
                getCarFeeDiscoupon.setStatus(getCarFeeDiscoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
                return getCarFeeDiscoupon;
            }

        }

        return null;
    }


    /**
     * 优惠券服务请求参数处理
     *
     * @param memAvailCouponRequestVO 请求参数
     * @return MemAvailCouponRequest 优惠券服务请求参数
     */
    private MemAvailCouponRequest buildMemAvailCouponRequest(MemAvailCouponRequestVO memAvailCouponRequestVO) {
        MemAvailCouponRequest request = new MemAvailCouponRequest();

        BeanCopier beanCopier = BeanCopier.create(MemAvailCouponRequestVO.class, MemAvailCouponRequest.class, false);
        beanCopier.copy(memAvailCouponRequestVO, request, null);

        request.setAveragePrice(memAvailCouponRequestVO.getHolidayAverage());
        request.setLabelIds(memAvailCouponRequestVO.getLabelIds());

        return request;
    }
}
