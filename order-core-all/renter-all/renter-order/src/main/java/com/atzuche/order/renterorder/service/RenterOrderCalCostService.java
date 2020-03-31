package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coin.service.AccountRenterCostCoinService;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.res.order.*;
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
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.dto.DeductContextDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidResultVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerDiscountCouponVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.autoyol.auto.coin.service.vo.res.AutoCoinResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.coupon.api.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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

    @Resource
    private AccountRenterCostCoinService accountRenterCostCoinService;

    /**
     * 计算租客订单的基础费用项和费用明细列表，不包括各项减免
     *
     **/
    public RenterOrderCostRespDTO calcBasicRenterOrderCostAndDeailList(RenterOrderCostReqDTO renterOrderCostReqDTO) {

        LOGGER.info("租客费用-费用聚合开始renterOrderCostReqDTO=[{}]", JSON.toJSONString(renterOrderCostReqDTO));
        CostBaseDTO costBaseDTO = renterOrderCostReqDTO.getCostBaseDTO();
        RenterOrderCostRespDTO renterOrderCostRespDTO = new RenterOrderCostRespDTO();
        List<RenterOrderCostDetailEntity> detailList = new ArrayList<>();
        List<RenterOrderSubsidyDetailDTO> subsidyList = new ArrayList<>();
        List<RenterOrderSubsidyDetailDTO> subsidyOutList = renterOrderCostReqDTO.getSubsidyOutList();
        Map<String, List<RenterOrderSubsidyDetailDTO>> subsidyOutGroup = Optional
                .ofNullable(subsidyOutList)
                .orElseGet(ArrayList::new)
                .stream()
                .collect(Collectors.groupingBy(RenterOrderSubsidyDetailDTO::getSubsidyCostCode));
        LOGGER.info("租客费用-修改订单产生的补贴分组结果subsidyOutGroup=[{}]", JSON.toJSONString(subsidyOutGroup));

        //获取租金
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostCombineService.listRentAmtEntity(renterOrderCostReqDTO.getRentAmtDTO());
        List<RenterOrderSubsidyDetailDTO> rentAmtSubSidy = subsidyOutGroup.get(RenterCashCodeEnum.RENT_AMT.getCashNo());
        int rentAmtSubsidyAmt = rentAmtSubSidy == null ? 0 : rentAmtSubSidy.get(0).getSubsidyAmount();
        int rentAmt = renterOrderCostDetailEntities.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        rentAmt = rentAmt + rentAmtSubsidyAmt;
        detailList.addAll(renterOrderCostDetailEntities);
        if (rentAmtSubSidy != null) {
            subsidyList.addAll(rentAmtSubSidy);
        }
        renterOrderCostRespDTO.setRentAmount(rentAmt);
        LOGGER.info("租客费用-获取租金-rentAmt=[{}]", rentAmt);

        //获取平台保障费
        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(renterOrderCostReqDTO.getInsurAmtDTO());
        List<RenterOrderSubsidyDetailDTO> insurAmtSubSidy = subsidyOutGroup.get(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo());
        int insurAmtSubSidyAmt = insurAmtSubSidy == null ? 0 : insurAmtSubSidy.stream().collect(Collectors.summingInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount));
        int insurAmt = insurAmtEntity.getTotalAmount();
        insurAmt = insurAmt + insurAmtSubSidyAmt;
        renterOrderCostRespDTO.setBasicEnsureAmount(insurAmt);
        detailList.add(insurAmtEntity);
        if (insurAmtSubSidy != null) {
            subsidyList.addAll(insurAmtSubSidy);
        }
        LOGGER.info("租客费用-获取平台保障费-insurAmt=[{}]", insurAmt);

        //获取全面保障费
        List<RenterOrderCostDetailEntity> comprehensiveEnsureList = renterOrderCostCombineService.listAbatementAmtEntity(renterOrderCostReqDTO.getAbatementAmtDTO());
        List<RenterOrderSubsidyDetailDTO> comprehensiveEnsureSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo());
        int comprehensiveEnsureSubsidyAmount = comprehensiveEnsureSubsidy == null ? 0 : comprehensiveEnsureSubsidy.stream().collect(Collectors.summingInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount));
        int comprehensiveEnsureAmount = comprehensiveEnsureList.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        comprehensiveEnsureAmount = comprehensiveEnsureAmount + comprehensiveEnsureSubsidyAmount;
        renterOrderCostRespDTO.setComprehensiveEnsureAmount(comprehensiveEnsureAmount);
        detailList.addAll(comprehensiveEnsureList);
        if (comprehensiveEnsureSubsidy != null) {
            subsidyList.addAll(comprehensiveEnsureSubsidy);
        }
        LOGGER.info("租客费用-获取全面保障费-comprehensiveEnsureAmount=[{}]", comprehensiveEnsureAmount);

        //获取附加驾驶人保险金额
        RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(renterOrderCostReqDTO.getExtraDriverDTO());
        List<RenterOrderSubsidyDetailDTO> totalAmountSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo());
        int totalAmountSubsidyAmount = totalAmountSubsidy == null ? 0 : totalAmountSubsidy.get(0).getSubsidyAmount();
        int extraDriverAmount = extraDriverInsureAmtEntity.getTotalAmount();
        extraDriverAmount = extraDriverAmount + totalAmountSubsidyAmount;
        renterOrderCostRespDTO.setAdditionalDrivingEnsureAmount(extraDriverAmount);
        if (extraDriverAmount != 0) {
            detailList.add(extraDriverInsureAmtEntity);
        }
        if (totalAmountSubsidy != null) {
            subsidyList.addAll(totalAmountSubsidy);
        }
        LOGGER.info("租客费用-获取附加驾驶人保险金额extraDriverAmount=[{}]", extraDriverAmount);

        //获取平台手续费
        RenterOrderCostDetailEntity serviceChargeFeeEntity = renterOrderCostCombineService.getServiceChargeFeeEntity(costBaseDTO);
        List<RenterOrderSubsidyDetailDTO> serviceSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.FEE.getCashNo());
        int serviceSubsidyAmount = serviceSubsidy == null ? 0 : serviceSubsidy.get(0).getSubsidyAmount();
        int serviceAmount = serviceChargeFeeEntity.getTotalAmount();
        serviceAmount = serviceAmount + serviceSubsidyAmount;
        renterOrderCostRespDTO.setCommissionAmount(serviceAmount);
        detailList.add(serviceChargeFeeEntity);
        if (serviceSubsidy != null) {
            subsidyList.addAll(serviceSubsidy);
        }
        LOGGER.info("租客费用-获取平台手续费serviceAmount=[{}]", serviceAmount);

        //获取取还车费用
        GetReturnCarCostReqDto getReturnCarCostReqDto = renterOrderCostReqDTO.getGetReturnCarCostReqDto();
        getReturnCarCostReqDto.setSumJudgeFreeFee(Math.abs(rentAmt + insurAmt + serviceAmount + comprehensiveEnsureAmount));

        GetReturnCostDTO returnCarCost = renterOrderCostCombineService.getReturnCarCost(getReturnCarCostReqDto);
        List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = returnCarCost.getRenterOrderSubsidyDetailDTOList();
        List<RenterOrderSubsidyDetailDTO> getSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.SRV_GET_COST.getCashNo());
        List<RenterOrderSubsidyDetailDTO> returnSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo());
        int getSubsidyAmt = getSubsidy == null ? 0 : getSubsidy.get(0).getSubsidyAmount();
        int returnSubsidyAmt = returnSubsidy == null ? 0 : returnSubsidy.get(0).getSubsidyAmount();
        int getReturnAmt = returnCarCost.getRenterOrderCostDetailEntityList().stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        getReturnAmt = getReturnAmt + getSubsidyAmt + returnSubsidyAmt;
        detailList.addAll(returnCarCost.getRenterOrderCostDetailEntityList());
        subsidyList.addAll(renterOrderSubsidyDetailDTOList);
        if (getSubsidy != null) {
            subsidyList.addAll(getSubsidy);
        }
        if (returnSubsidy != null) {
            subsidyList.addAll(returnSubsidy);
        }
        GetReturnResponseVO getReturnResponseVO = returnCarCost.getGetReturnResponseVO();
        renterOrderCostRespDTO.setGetRealAmt(getReturnResponseVO.getGetFee());
        renterOrderCostRespDTO.setReturnRealAmt(getReturnResponseVO.getReturnFee());
        LOGGER.info("租客费用-获取取还车费用getReturnAmt=[{}]", getReturnAmt);

        //获取取还车超运能费用
        GetReturnOverCostDTO getReturnOverCost = renterOrderCostCombineService.getGetReturnOverCost(renterOrderCostReqDTO.getGetReturnCarOverCostReqDto());
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = getReturnOverCost.getRenterOrderCostDetailEntityList();
        List<RenterOrderSubsidyDetailDTO> getOverSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo());
        List<RenterOrderSubsidyDetailDTO> returnOverSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo());
        int getOverSubsidyAmt = getOverSubsidy == null ? 0 : getOverSubsidy.get(0).getSubsidyAmount();
        int returnOverSubsidyAmt = returnOverSubsidy == null ? 0 : returnOverSubsidy.get(0).getSubsidyAmount();
        Integer getOverAmt = renterOrderCostDetailEntityList.stream()
                .filter(x -> RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        Integer returnOverAmt = renterOrderCostDetailEntityList.stream()
                .filter(x -> RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        int getReturnOverCostAmount = getOverAmt + returnOverAmt;
        getReturnOverCostAmount = getReturnOverCostAmount + getOverSubsidyAmt + returnOverSubsidyAmt;
        detailList.addAll(renterOrderCostDetailEntityList);
        renterOrderCostRespDTO.setGetOverAmt(getOverAmt);
        renterOrderCostRespDTO.setReturnOverAmt(returnOverAmt);
        if (getOverSubsidy != null) {
            subsidyList.addAll(getOverSubsidy);
        }
        if (returnOverSubsidy != null) {
            subsidyList.addAll(returnOverSubsidy);
        }
        LOGGER.info("租客费用-获取取还车超运能费用getReturnOverCostAmount=[{}]", getReturnOverCostAmount);

        //租车费用 = 租金+平台保障费+全面保障费+取还车费用+取还车超运能费用+附加驾驶员费用+手续费；
        int rentCarAmount = rentAmt + insurAmt + comprehensiveEnsureAmount + getReturnAmt + getReturnOverCostAmount + extraDriverAmount + serviceAmount;
        LOGGER.info("租客费用-租车费用rentCarAmount=[{}]", rentCarAmount);

        renterOrderCostRespDTO.setRentCarAmount(rentCarAmount);
        renterOrderCostRespDTO.setRentAmount(rentAmt);
        renterOrderCostRespDTO.setRenterOrderCostDetailDTOList(detailList);
        renterOrderCostRespDTO.setRenterOrderSubsidyDetailDTOList(subsidyList);
        LOGGER.info("获取费用项和费用明细列表 renterOrderCostRespDTO:[{}]", JSON.toJSONString(renterOrderCostRespDTO));
        return renterOrderCostRespDTO;
    }

    /**
     * 保存费用及其费用明细
     *
     * @author ZhangBin
     * @date 2019/12/28 17:37
     **/
    public void saveOrderCostAndDeailList(RenterOrderCostRespDTO renterOrderCostRespDTO) {
        LOGGER.info("下单-租客端-保存费用及其明细renterOrderCostRespDTO=[{}]", JSON.toJSONString(renterOrderCostRespDTO));
        List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList();
        //数据转化
        List<RenterOrderSubsidyDetailEntity> subsidyListEntity = renterOrderSubsidyDetailDTOList.stream().map(x -> {
            RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity = new RenterOrderSubsidyDetailEntity();
            BeanUtils.copyProperties(x, renterOrderSubsidyDetailEntity);
            renterOrderSubsidyDetailEntity.setOrderNo(renterOrderCostRespDTO.getOrderNo());
            renterOrderSubsidyDetailEntity.setRenterOrderNo(renterOrderCostRespDTO.getRenterOrderNo());
            return renterOrderSubsidyDetailEntity;
        }).collect(Collectors.toList());
        //保存费用明细
        LOGGER.info("下单-租客端-保存费用明细参数param=[{}]", JSON.toJSONString(renterOrderCostRespDTO.getRenterOrderCostDetailDTOList()));
        Integer costDetailResult = renterOrderCostDetailService.saveRenterOrderCostDetailBatch(renterOrderCostRespDTO.getRenterOrderCostDetailDTOList());
        LOGGER.info("下单-租客端-保存费用明细结果costDetailResult=[{}],参数param=[{}]", costDetailResult, JSON.toJSONString(renterOrderCostRespDTO.getRenterOrderCostDetailDTOList()));
        //保存补贴明细
        LOGGER.info("下单-租客端-保存补贴明细参数param=[{}]", JSON.toJSONString(subsidyListEntity));
        Integer subsidyResult = renterOrderSubsidyDetailService.saveRenterOrderSubsidyDetailBatch(subsidyListEntity);
        LOGGER.info("下单-租客端-保存补贴明细结果subsidyResult=[{}]，参数param=[{}]", subsidyResult, JSON.toJSONString(subsidyListEntity));

        //保存费用统计信息
        RenterOrderCostEntity renterOrderCostEntity = new RenterOrderCostEntity();
        BeanUtils.copyProperties(renterOrderCostRespDTO, renterOrderCostEntity);
        renterOrderCostEntity.setOrderNo(renterOrderCostRespDTO.getOrderNo());
        renterOrderCostEntity.setRenterOrderNo(renterOrderCostRespDTO.getRenterOrderNo());
        renterOrderCostEntity.setRentCarAmount(renterOrderCostRespDTO.getRentAmount());
        LOGGER.info("下单-租客端-保存费用参数renterOrderCostEntity=[{}]", JSON.toJSONString(renterOrderCostEntity));
        Integer costResult = renterOrderCostService.saveRenterOrderCost(renterOrderCostEntity);
        LOGGER.info("下单-租客端-保存费用结果costResult=[{}]，参数renterOrderCostEntity=[{}]", costResult, JSON.toJSONString(renterOrderCostEntity));

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
                ownerCoupon.setCouponName(coupon.getCouponName() == null ? "车主券" : coupon.getCouponName());
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
        MemAvailCoupon response =
                platformCouponService.checkAndUseCouponAailable(memAvailCouponRequestVO.getOrderNo(),
                        memAvailCouponRequestVO.getDisCouponId(), request);
        if (null != response) {
            OrderCouponDTO getCarFeeDiscoupon = new OrderCouponDTO();
            getCarFeeDiscoupon.setCouponId(memAvailCouponRequestVO.getDisCouponId());
            getCarFeeDiscoupon.setCouponName(response.getDisName());
            getCarFeeDiscoupon.setCouponDesc(response.getDescription());
            getCarFeeDiscoupon.setAmount(null == response.getRealCouponOffset() ? 0 : response.getRealCouponOffset());
            getCarFeeDiscoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
            //绑定后变更为已使用
            getCarFeeDiscoupon.setStatus(getCarFeeDiscoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
            return getCarFeeDiscoupon;

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
        MemAvailCoupon response =
                platformCouponService.checkAndUseCouponAailable(memAvailCouponRequestVO.getOrderNo(),
                        memAvailCouponRequestVO.getDisCouponId(), request);
        if (null != response) {
            OrderCouponDTO getCarFeeDiscoupon = new OrderCouponDTO();
            getCarFeeDiscoupon.setCouponId(memAvailCouponRequestVO.getDisCouponId());
            getCarFeeDiscoupon.setCouponName(response.getDisName());
            getCarFeeDiscoupon.setCouponDesc(response.getDescription());
            getCarFeeDiscoupon.setAmount(null == response.getRealCouponOffset() ? 0 : response.getRealCouponOffset());
            getCarFeeDiscoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
            //绑定后变更为已使用
            getCarFeeDiscoupon.setStatus(getCarFeeDiscoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
            return getCarFeeDiscoupon;
        }
        return null;
    }


    /**
     * 计算车主券抵扣信息(修改订单用)
     *
     * @param ownerCouponGetAndValidReqVO 请求参数
     * @param ooupon 券信息
     * @return OrderCouponDTO 订单优惠券信息
     */
    public OrderCouponDTO calOwnerCouponDeductInfo(OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO, OrderCouponEntity ooupon) {

        LOGGER.info("获取车主优惠券抵扣信息. param is,ownerCouponGetAndValidReqDTO:[{}]",
                JSON.toJSONString(ownerCouponGetAndValidReqVO));
        if (null == ownerCouponGetAndValidReqVO || StringUtils.isBlank(ownerCouponGetAndValidReqVO.getCouponNo())) {
            return null;
        }
        OwnerCouponGetAndValidResultVO result =
                ownerDiscountCouponService.getAndValidCoupon(ownerCouponGetAndValidReqVO.getOrderNo(),
                        ownerCouponGetAndValidReqVO.getRentAmt(), ownerCouponGetAndValidReqVO.getCouponNo(),
                        ownerCouponGetAndValidReqVO.getCarNo(), ownerCouponGetAndValidReqVO.getMark());
        if (ooupon == null) {
        	if (null != result) {
                if (StringUtils.equals(ErrorCode.SUCCESS.getCode(), result.getResCode()) && null != result.getData()) {
                    OwnerDiscountCouponVO coupon = result.getData().getCouponDTO();
                    if (coupon != null) {
                    	OrderCouponDTO ownerCoupon = new OrderCouponDTO();
                    	ownerCoupon.setCouponId(coupon.getCouponNo());
                        ownerCoupon.setCouponName(coupon.getCouponName()==null?"车主券":coupon.getCouponName());
                        ownerCoupon.setCouponDesc(coupon.getCouponText());
                        ownerCoupon.setAmount(null == coupon.getDiscount() ? 0 : coupon.getDiscount());
                        ownerCoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode());
                        //绑定后变更为已使用
                        ownerCoupon.setStatus(ownerCoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
                        return ownerCoupon;
                    }
                }
            }
        } else {
        	OrderCouponDTO ownerCoupon = new OrderCouponDTO();
            ownerCoupon.setCouponId(ooupon.getCouponId());
            ownerCoupon.setCouponName(ooupon.getCouponName());
            ownerCoupon.setCouponDesc(ooupon.getCouponDesc());
            ownerCoupon.setAmount(0);
            ownerCoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode());
            ownerCoupon.setStatus(OrderConstant.NO);
            if (null != result) {
                if (StringUtils.equals(ErrorCode.SUCCESS.getCode(), result.getResCode()) && null != result.getData()) {
                    OwnerDiscountCouponVO coupon = result.getData().getCouponDTO();
                    if (coupon != null) {
                    	ownerCoupon.setCouponId(coupon.getCouponNo());
                        ownerCoupon.setCouponName(coupon.getCouponName()==null?"车主券":coupon.getCouponName());
                        ownerCoupon.setCouponDesc(coupon.getCouponText());
                        ownerCoupon.setAmount(null == coupon.getDiscount() ? 0 : coupon.getDiscount());
                    }
                    ownerCoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode());
                    //绑定后变更为已使用
                    ownerCoupon.setStatus(ownerCoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
                }
            }
            return ownerCoupon;
        }
        return null;
    }
    
    
    /**
     * 计算取送服务券抵扣信息
     *
     * @param request 请求参数
     * @param coupon  优惠券信息
     * @return OrderCouponDTO
     */
    public OrderCouponDTO checkGetCarFreeCouponAvailable(CouponSettleRequest request, OrderCouponEntity coupon) {

        LOGGER.info("获取优惠券抵扣信息(送取服务券). param is,request:[{}]",
                JSON.toJSONString(request));

        if (null == request || coupon == null) {
            return null;
        }
        CouponSettleResponse response = platformCouponService.checkGetCarFreeCouponAvailable(request);
        OrderCouponDTO getCarFeeDiscoupon = new OrderCouponDTO();
        getCarFeeDiscoupon.setCouponId(coupon.getCouponId());
        getCarFeeDiscoupon.setCouponName(coupon.getCouponName());
        getCarFeeDiscoupon.setCouponDesc(coupon.getCouponDesc());
        getCarFeeDiscoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
        getCarFeeDiscoupon.setAmount(0);
        getCarFeeDiscoupon.setStatus(OrderConstant.NO);
        if (null != response) {
            getCarFeeDiscoupon.setAmount(response.getDisAmt());
            //绑定后变更为已使用
            getCarFeeDiscoupon.setStatus(getCarFeeDiscoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
        }
        return getCarFeeDiscoupon;
    }


    /**
     * 计算平台优惠券抵扣信息
     *
     * @param request 请求参数
     * @param coupon  优惠券信息
     * @return OrderCouponDTO
     */
    public OrderCouponDTO checkCouponAvailable(CouponSettleRequest request, OrderCouponEntity coupon) {

        LOGGER.info("获取优惠券抵扣信息(平台优惠券). param is,request:[{}]",
                JSON.toJSONString(request));
        if (null == request || coupon == null) {
            return null;
        }
        CouponSettleResponse response = platformCouponService.checkCouponAvailable(request);
        OrderCouponDTO getCarFeeDiscoupon = new OrderCouponDTO();
        getCarFeeDiscoupon.setCouponId(coupon.getCouponId());
        getCarFeeDiscoupon.setCouponName(coupon.getCouponName());
        getCarFeeDiscoupon.setCouponDesc(coupon.getCouponDesc());
        getCarFeeDiscoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
        getCarFeeDiscoupon.setAmount(0);
        getCarFeeDiscoupon.setStatus(OrderConstant.NO);
        if (null != response) {
            getCarFeeDiscoupon.setAmount(response.getDisAmt());
            //绑定后变更为已使用
            getCarFeeDiscoupon.setStatus(getCarFeeDiscoupon.getAmount() > 0 ? OrderConstant.YES : OrderConstant.NO);
        }
        return getCarFeeDiscoupon;
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


    /**
     * 下单前费用计--获取车主券抵扣信息
     *
     * @param deductContext    抵扣信息公共参数
     * @param ownerCouponReqVO 车主券拉取请求参数
     * @return CarOwnerCouponReductionVO
     */
    public CarOwnerCouponReductionVO getCarOwnerCouponReductionVo(DeductContextDTO deductContext,
                                                                  OwnerCouponReqVO ownerCouponReqVO) {
        CarOwnerCouponReductionVO carOwnerCouponReductionVo = new CarOwnerCouponReductionVO();
        //掉车主券服务获取可用的券列表
        List<OwnerDiscountCouponVO> ownerDiscountCouponList =
                ownerDiscountCouponService.getCouponList(deductContext.getSurplusRentAmt()
                        , ownerCouponReqVO.getMemNo(), ownerCouponReqVO.getCarNo(), ownerCouponReqVO.getCouponNo());
        if (!CollectionUtils.isEmpty(ownerDiscountCouponList)) {
            if (StringUtils.isNotBlank(ownerCouponReqVO.getCouponNo())) {
                // 当前使用的放在第一个
                OwnerDiscountCouponVO curObj = ownerDiscountCouponList.get(0);
                carOwnerCouponReductionVo.setDiscouponDeductibleAmt(null == curObj || null == curObj.getDiscount() ?
                        0 : curObj.getDiscount());
                //更新剩余租金信息
                deductContext.setSurplusRentAmt(deductContext.getSurplusRentAmt() - carOwnerCouponReductionVo.getDiscouponDeductibleAmt());
            }

            List<CarOwnerCouponDetailVO> carOwnerCouponDetailVoList = new ArrayList<>();
            ownerDiscountCouponList.forEach(ownerCoupon -> {
                CarOwnerCouponDetailVO carOwnerCouponDetail = new CarOwnerCouponDetailVO();
                try {
                    BeanUtils.copyProperties(ownerCoupon, carOwnerCouponDetail);
                    carOwnerCouponDetailVoList.add(carOwnerCouponDetail);
                } catch (Exception e) {
                    LOGGER.error("下单前费用计算，获取车主券抵扣信息复制属性出错：", e);
                }
            });
            if (!carOwnerCouponDetailVoList.isEmpty()) {
                carOwnerCouponReductionVo.setAvailableCouponList(carOwnerCouponDetailVoList);
            }
        } else {
            return null;
        }
        return carOwnerCouponReductionVo;
    }


    /**
     * 下单前费用计算--优惠券抵扣信息
     *
     * @param deductContext           抵扣信息公共参数
     * @param memAvailCouponRequestVO 优惠券列表请求参数
     * @param disCouponIds            选中优惠券ID
     * @param getCarFreeCouponId      选中取送服务券ID
     * @return CouponReductionVO
     */
    public CouponReductionVO getCouponReductionVO(DeductContextDTO deductContext,
                                                  MemAvailCouponRequestVO memAvailCouponRequestVO, String disCouponIds
            , String getCarFreeCouponId) {

        LOGGER.info("获取优惠券抵扣信息(送取服务券+平台优惠券). param is,deductContext:[{}],memAvailCouponRequestVO:[{}]," +
                        "disCouponIds:[{}],getCarFreeCouponId:[{}]", JSON.toJSONString(deductContext),
                JSON.toJSONString(memAvailCouponRequestVO),
                disCouponIds, getCarFreeCouponId);

        MemAvailCouponRequest request = new MemAvailCouponRequest();
        BeanCopier beanCopier = BeanCopier.create(MemAvailCouponRequestVO.class, MemAvailCouponRequest.class, false);
        beanCopier.copy(memAvailCouponRequestVO, request, null);
        request.setAveragePrice(memAvailCouponRequestVO.getHolidayAverage());
        request.setLabelIds(memAvailCouponRequestVO.getLabelIds());
        request.setRentAmt(deductContext.getSurplusRentAmt());
        MemAvailCouponResponse memAvailCouponResponse = platformCouponService.findAvailMemCouponsV2(request);


        if (null != memAvailCouponResponse) {
            differentiateCouponsByPlatformType(memAvailCouponResponse, deductContext.getOsVal());
            List<MemAvailCoupon> availCoupons = memAvailCouponResponse.getAvailCoupons();
            if (!CollectionUtils.isEmpty(availCoupons)) {
                MemAvailCoupon discoupon = null;
                MemAvailCoupon getCarFeeDiscoupon = null;
                Map<String, MemAvailCoupon> availCouponMap = availCoupons.stream()
                        .collect(Collectors.toMap(MemAvailCoupon::getId, memAvailCoupon -> memAvailCoupon));

                if (StringUtils.isNotBlank(disCouponIds)) {
                    discoupon = availCouponMap.get(disCouponIds);
                }

                if (StringUtils.isNotBlank(getCarFreeCouponId)) {
                    getCarFeeDiscoupon = availCouponMap.get(getCarFreeCouponId);
                }

                int discouponDeductibleAmt = 0;
                if (null != discoupon) {
                    discouponDeductibleAmt = discoupon.getRealCouponOffset();
                    int surplusRentAmt = deductContext.getSurplusRentAmt() - discouponDeductibleAmt;
                    deductContext.setSurplusRentAmt(surplusRentAmt > 0 ? surplusRentAmt : 0);
                }
                if (null != getCarFeeDiscoupon) {
                    discouponDeductibleAmt = discouponDeductibleAmt + getCarFeeDiscoupon.getRealCouponOffset();
                }

                CouponReductionVO couponReductionVO = new CouponReductionVO();
                couponReductionVO.setDiscouponDeductibleAmt(discouponDeductibleAmt);
                couponReductionVO.setDiscoupons(newCouponToOdlCouponV2(availCoupons));

                LOGGER.info("获取优惠券抵扣信息(送取服务券+平台优惠券). result is,couponReductionVO:[{}]", JSON.toJSONString(couponReductionVO));
                return couponReductionVO;
            }
        }


        return null;
    }

    /**
     * 下单前费用计算--凹凸币抵扣信息
     *
     * @param deductContext 抵扣信息公共参数
     * @param memNo         租客会员号
     * @param useAutoCoin   使用凹凸币标识
     * @return AutoCoinReductionVO
     */
    public AutoCoinReductionVO getAutoCoinReductionVO(DeductContextDTO deductContext, String memNo,
                                                       Integer useAutoCoin) {

        LOGGER.info("下单前费用计算--凹凸币抵扣信息. param is,deductContext:[{}],memNo:[{}],useAutoCoin:[{}]",
                JSON.toJSONString(deductContext), memNo, useAutoCoin);
        int pointValue = accountRenterCostCoinService.getUserCoinTotalAmt(memNo);
        AutoCoinResponseVO crmCustPoint = new AutoCoinResponseVO();
        crmCustPoint.setMemNo(Integer.valueOf(memNo));
        crmCustPoint.setPointValue(pointValue);
        RenterOrderSubsidyDetailDTO autoCoinSubsidyInfo = renterOrderSubsidyDetailService.calAutoCoinSubsidyInfo(crmCustPoint,
                deductContext.getOriginalRentAmt(), deductContext.getSurplusRentAmt(), useAutoCoin);

        AutoCoinReductionVO autoCoinReductionVO = new AutoCoinReductionVO();
        autoCoinReductionVO.setAutoCoinIsShow(String.valueOf(OrderConstant.NO));
        autoCoinReductionVO.setAvailableAutoCoinAmt(pointValue);
        autoCoinReductionVO.setAutoCoinDeductibleAmt(null == autoCoinSubsidyInfo || null ==
                autoCoinSubsidyInfo.getSubsidyAmount() ? 0 : autoCoinSubsidyInfo.getSubsidyAmount());

        LOGGER.info("下单前费用计算--凹凸币抵扣信息. result is,autoCoinReductionVO:[{}]", JSON.toJSONString(autoCoinReductionVO));
        return autoCoinReductionVO;
    }


    /**
     * 根据AUT-4074增加根据券的平台类型来区分券是否可用
     *
     * @param response 会员优惠券信息列表
     * @param osVal    OsTypeEnum.osVal
     */
    private void differentiateCouponsByPlatformType(MemAvailCouponResponse response, String osVal) {
        if (StringUtils.isNotBlank(osVal)) {
            List<MemAvailCoupon> availCoupons = response.getAvailCoupons();
            List<MemAvailCoupon> unAvailCoupons = response.getUnavailCoupons();
            CopyOnWriteArrayList<MemAvailCoupon> copyOnWriteArrayList = new CopyOnWriteArrayList(availCoupons);
            for (MemAvailCoupon memAvailCoupon : copyOnWriteArrayList) {
                if (memAvailCoupon != null && StringUtils.isNotBlank(memAvailCoupon.getPlatformType())) {
                    String platformTypes = memAvailCoupon.getPlatformType();
                    String[] types = platformTypes.split(",");
                    if (!Arrays.asList(types).contains(osVal)) {
                        if (copyOnWriteArrayList.remove(memAvailCoupon)) {
                            unAvailCoupons.add(memAvailCoupon);
                        }
                    }
                }
            }
            response.setAvailCoupons(copyOnWriteArrayList);
            response.setUnavailCoupons(unAvailCoupons);
        }
    }

    /**
     * 优惠券列表转换
     *
     * @param availCoupons 可用优惠券列表
     * @return List<DisCouponMemInfoVO>
     */
    private List<DisCouponMemInfoVO> newCouponToOdlCouponV2(List<MemAvailCoupon> availCoupons) {
        List<DisCouponMemInfoVO> availableDisCoupons = new ArrayList<>();
        for (MemAvailCoupon memCoupon : availCoupons) {
            if (memCoupon == null) {
                continue;
            }
            DisCouponMemInfoVO disCouponMemInfoVO = new DisCouponMemInfoVO();
            BeanUtils.copyProperties(memCoupon,disCouponMemInfoVO);
            Integer couponType = memCoupon.getCouponType();
            if (couponType == null) {
                disCouponMemInfoVO.setPlatformCouponType("-1");
            } else {
                disCouponMemInfoVO.setPlatformCouponType(String.valueOf(couponType));
            }
            // v512增加是否全减字段，用于区分取还车类型优惠券展示样式
            Integer costFree = memCoupon.getCostFree();
            if (costFree == null) {
                disCouponMemInfoVO.setIsCostFree("0");
            } else {
                disCouponMemInfoVO.setIsCostFree(String.valueOf(costFree));
            }

            disCouponMemInfoVO.setCondAmt(null);
            disCouponMemInfoVO.setCreateTime(null);
            disCouponMemInfoVO.setDisAmt(null);
            disCouponMemInfoVO.setStartDate(null);
            disCouponMemInfoVO.setEndDate(null);
            disCouponMemInfoVO.setCouponType("0");
            availableDisCoupons.add(disCouponMemInfoVO);
        }
        return availableDisCoupons;
    }






}
