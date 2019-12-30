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


    /*
     * @Author ZhangBin
     * @Date 2019/12/28 19:19
     * @Description: 计算费用明细并保存费用
     * 
     **/
    public void  getAndSAveOrderCostAndDeailList(RenterOrderCostReqDTO renterOrderCostReqDTO){
        RenterOrderCostRespDTO renterOrderCostRespDTO = this.getOrderCostAndDeailList(renterOrderCostReqDTO);
        this.saveOrderCostAndDeailList(renterOrderCostRespDTO);
    }


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
        List<RenterOrderSubsidyDetailDTO> subsidyOutList = renterOrderCostReqDTO.getSubsidyOutList();
        if(subsidyOutList == null){
            subsidyOutList = new ArrayList<>();
        }
        Map<String, List<RenterOrderSubsidyDetailDTO>> subsidyOutGroup = subsidyOutList.stream().collect(Collectors.groupingBy(RenterOrderSubsidyDetailDTO::getSubsidyCostCode));


        //获取租金
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostCombineService.listRentAmtEntity(renterOrderCostReqDTO.getRentAmtDTO());
        List<RenterOrderSubsidyDetailDTO> rentAmtSubSidy = subsidyOutGroup.get(RenterCashCodeEnum.RENT_AMT.getCashNo());
        int rentAmtSubsidyAmt = rentAmtSubSidy == null ? 0 : rentAmtSubSidy.get(0).getSubsidyAmount();
        int rentAmt = renterOrderCostDetailEntities.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        rentAmt = rentAmt + rentAmtSubsidyAmt;
        detailList.addAll(renterOrderCostDetailEntities);
        subsidyList.addAll(rentAmtSubSidy);
        renterOrderCostRespDTO.setRentAmount(rentAmt);

        
        //获取平台保障费
        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(renterOrderCostReqDTO.getInsurAmtDTO());
        List<RenterOrderSubsidyDetailDTO> insurAmtSubSidy = subsidyOutGroup.get(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo());
        int insurAmtSubSidyAmt = insurAmtSubSidy == null ? 0:insurAmtSubSidy.get(0).getSubsidyAmount();
        int insurAmt = insurAmtEntity.getTotalAmount();
        insurAmt = insurAmt + insurAmtSubSidyAmt;
        renterOrderCostRespDTO.setBasicEnsureAmount(insurAmt);
        detailList.add(insurAmtEntity);
        subsidyList.addAll(insurAmtSubSidy);

        //获取全面保障费
        List<RenterOrderCostDetailEntity> comprehensiveEnsureList = renterOrderCostCombineService.listAbatementAmtEntity(renterOrderCostReqDTO.getAbatementAmtDTO());
        List<RenterOrderSubsidyDetailDTO> comprehensiveEnsureSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo());
        int comprehensiveEnsureSubsidyAmount = comprehensiveEnsureSubsidy == null ? 0:comprehensiveEnsureSubsidy.get(0).getSubsidyAmount();
        int comprehensiveEnsureAmount = comprehensiveEnsureList.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        comprehensiveEnsureAmount = comprehensiveEnsureAmount + comprehensiveEnsureSubsidyAmount;
        renterOrderCostRespDTO.setComprehensiveEnsureAmount(comprehensiveEnsureAmount);
        detailList.addAll(comprehensiveEnsureList);
        subsidyList.addAll(comprehensiveEnsureSubsidy);

        //获取附加驾驶人保险金额
        RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(renterOrderCostReqDTO.getExtraDriverDTO());
        List<RenterOrderSubsidyDetailDTO> totalAmountSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo());
        int totalAmountSubsidyAmount = totalAmountSubsidy == null ? 0:totalAmountSubsidy.get(0).getSubsidyAmount();
        int totalAmount = extraDriverInsureAmtEntity.getTotalAmount();
        totalAmount = totalAmount + totalAmountSubsidyAmount;
        renterOrderCostRespDTO.setAdditionalDrivingEnsureAmount(totalAmount);
        detailList.add(extraDriverInsureAmtEntity);
        subsidyList.addAll(totalAmountSubsidy);

        //获取平台手续费
        RenterOrderCostDetailEntity serviceChargeFeeEntity = renterOrderCostCombineService.getServiceChargeFeeEntity(costBaseDTO);
        List<RenterOrderSubsidyDetailDTO> serviceSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.FEE.getCashNo());
        int serviceSubsidyAmount = serviceSubsidy == null ? 0 : serviceSubsidy.get(0).getSubsidyAmount();
        int serviceAmount = serviceChargeFeeEntity.getTotalAmount();
        serviceAmount = serviceAmount + serviceSubsidyAmount;
        renterOrderCostRespDTO.setCommissionAmount(serviceAmount);
        detailList.add(serviceChargeFeeEntity);
        subsidyList.addAll(serviceSubsidy);

        //获取取还车费用
        GetReturnCarCostReqDto getReturnCarCostReqDto = renterOrderCostReqDTO.getGetReturnCarCostReqDto();
        getReturnCarCostReqDto.setSumJudgeFreeFee(Math.abs(rentAmt + insurAmt + serviceAmount + comprehensiveEnsureAmount));
        GetReturnCostDTO returnCarCost = renterOrderCostCombineService.getReturnCarCost(getReturnCarCostReqDto);
        List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = returnCarCost.getRenterOrderSubsidyDetailDTOList();
        List<RenterOrderSubsidyDetailDTO> getSubsidy= subsidyOutGroup.get(RenterCashCodeEnum.SRV_GET_COST.getCashNo());
        List<RenterOrderSubsidyDetailDTO> returnSubsidy = subsidyOutGroup.get(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo());
        int getSubsidyAmt = getSubsidy == null ? 0 : getSubsidy.get(0).getSubsidyAmount();
        int returnSubsidyAmt = returnSubsidy == null ? 0: returnSubsidy.get(0).getSubsidyAmount();
        int getReturnAmt = returnCarCost.getRenterOrderCostDetailEntityList().stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        getReturnAmt = getReturnAmt + getSubsidyAmt + returnSubsidyAmt;
        detailList.addAll(returnCarCost.getRenterOrderCostDetailEntityList());
        subsidyList.addAll(renterOrderSubsidyDetailDTOList);
        subsidyList.addAll(getSubsidy);
        subsidyList.addAll(returnSubsidy);
        GetReturnResponseVO getReturnResponseVO = returnCarCost.getGetReturnResponseVO();
        renterOrderCostRespDTO.setGetRealAmt(getReturnResponseVO.getGetFee());
        renterOrderCostRespDTO.setReturnRealAmt(getReturnResponseVO.getReturnFee());

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
        subsidyList.addAll(getOverSubsidy);
        subsidyList.addAll(returnOverSubsidy);

        //租车费用 = 租金+平台保障费+全面保障费+取还车费用+取还车超运能费用+附加驾驶员费用+手续费；
        int rentCarAmount = rentAmt + insurAmt + comprehensiveEnsureAmount + getReturnAmt + getReturnOverCostAmount + totalAmount + serviceAmount;

        renterOrderCostRespDTO.setRentCarAmount(rentCarAmount);
        renterOrderCostRespDTO.setRenterOrderCostDetailDTOList(detailList);
        renterOrderCostRespDTO.setRenterOrderSubsidyDetailDTOList(subsidyList);
        LOGGER.info("获取费用项和费用明细列表 renterOrderCostRespDTO:[{}]", JSON.toJSONString(renterOrderCostRespDTO));
        return renterOrderCostRespDTO;
    }
    /*
     * @Author ZhangBin
     * @Date 2019/12/28 17:37
     * @Description: 保存费用及其费用明细
     * 
     **/
    private void saveOrderCostAndDeailList(RenterOrderCostRespDTO renterOrderCostRespDTO){
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
        renterOrderCostDetailService.saveRenterOrderCostDetailBatch(renterOrderCostRespDTO.getRenterOrderCostDetailDTOList());
        //保存补贴明细
        renterOrderSubsidyDetailService.saveRenterOrderSubsidyDetailBatch(subsidyListEntity);
        //保存费用统计信息
        RenterOrderCostEntity renterOrderCostEntity = new RenterOrderCostEntity();
        BeanUtils.copyProperties(renterOrderCostRespDTO,renterOrderCostEntity);
        renterOrderCostEntity.setOrderNo(renterOrderCostRespDTO.getOrderNo());
        renterOrderCostEntity.setRenterOrderNo(renterOrderCostRespDTO.getRenterOrderNo());
        renterOrderCostService.saveRenterOrderCost(renterOrderCostEntity);
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
