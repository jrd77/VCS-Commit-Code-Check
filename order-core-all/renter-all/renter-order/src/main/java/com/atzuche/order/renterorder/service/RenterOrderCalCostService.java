package com.atzuche.order.renterorder.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.dto.coupon.OrderCouponDTO;
import com.atzuche.order.renterorder.dto.coupon.OwnerCouponGetAndValidReqDTO;
import com.atzuche.order.renterorder.dto.coupon.owner.OwnerCouponGetAndValidResultDTO;
import com.atzuche.order.renterorder.dto.coupon.owner.OwnerDiscountCouponDTO;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租客订单相关费用计算处理类
 *
 * @author ZhangBin
 * @date 2019/12/25 15:17
 */
@Service
public class RenterOrderCalCostService {

    @Resource
    private RenterOrderCostCombineService renterOrderCostCombineService;

    @Resource
    private OwnerDiscountCouponService ownerDiscountCouponService;


    /**
     * 获取费用项和费用明细列表+落库
     *
     * @author ZhangBin
     * @date 2019/12/24 15:21
     **/
    public RenterOrderCostRespDTO getRenterOrderCostAndDeailList(RenterOrderCostReqDTO renterOrderCostReqDTO) {
        RenterOrderCostRespDTO renterOrderCostRespDTO = new RenterOrderCostRespDTO();
        List<RenterOrderCostDetailEntity> detailList = new ArrayList<>();

        //获取租金
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostCombineService.listRentAmtEntity(renterOrderCostReqDTO.getRentAmtDTO());
        int rentAmt = renterOrderCostDetailEntities.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        detailList.addAll(renterOrderCostDetailEntities);

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

        //获取取还车费用 TODO

        //获取取还车超运能费用 TODO

        //获取平台手续费
        RenterOrderCostDetailEntity serviceChargeFeeEntity = renterOrderCostCombineService.getServiceChargeFeeEntity(renterOrderCostReqDTO.getCostBaseDTO());
        int serviceAmount = serviceChargeFeeEntity.getTotalAmount();
        renterOrderCostRespDTO.setCommissionAmount(serviceAmount);
        detailList.add(serviceChargeFeeEntity);

        //租车费用 = 租金+平台保障费+全面保障费+取还车费用+取还车超云能费用+附加驾驶员费用+手续费；
        int rentCarAmount = rentAmt + insurAmt + comprehensiveEnsureAmount + totalAmount + serviceAmount;

        renterOrderCostRespDTO.setRentCarAmount(rentCarAmount);
        renterOrderCostRespDTO.setRenterOrderCostDetailDTOList(detailList);
        return renterOrderCostRespDTO;
    }


    /**
     * 计算车主券抵扣信息
     *
     * @param ownerCouponGetAndValidReqDTO 请求参数
     * @return OrderCouponDTO 订单优惠券信息
     */
    public OrderCouponDTO calOwnerCouponDeductInfo(OwnerCouponGetAndValidReqDTO ownerCouponGetAndValidReqDTO) {
        OwnerCouponGetAndValidResultDTO result =
                ownerDiscountCouponService.getAndValidCoupon(ownerCouponGetAndValidReqDTO.getOrderNo(),
                        ownerCouponGetAndValidReqDTO.getRentAmt(), ownerCouponGetAndValidReqDTO.getCouponNo(),
                        ownerCouponGetAndValidReqDTO.getCarNo(), ownerCouponGetAndValidReqDTO.getMark());

        if (null != result) {
            if (StringUtils.equals(ErrorCode.SUCCESS.getCode(), result.getResCode()) && null != result.getData()) {
                OwnerDiscountCouponDTO coupon = result.getData().getCouponDTO();
                OrderCouponDTO ownerCoupon = new OrderCouponDTO();
                ownerCoupon.setCouponId(coupon.getCouponNo());
                ownerCoupon.setCouponName(coupon.getCouponName());
                ownerCoupon.setCouponDesc(coupon.getCouponText());
                ownerCoupon.setAmount(null == coupon.getDiscount() ? 0 : coupon.getDiscount());
                ownerCoupon.setCouponType(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode());
                //绑定后变更为已使用
                ownerCoupon.setStatus(OrderConstant.NO);
                return ownerCoupon;
            }
        }
        return null;
    }





}
