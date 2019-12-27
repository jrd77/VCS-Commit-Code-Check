package com.atzuche.order.rentercost.service;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.CrmCustPointDTO;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.mapper.RenterOrderSubsidyDetailMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 租客补贴明细表
 */
@Service
public class RenterOrderSubsidyDetailService {
    @Autowired
    private RenterOrderSubsidyDetailMapper renterOrderSubsidyDetailMapper;

    @Resource
    private AutoCoinCostCalService autoCoinCostCalService;


    /**
     * 获取补贴明细列表
     *
     * @param orderNo       主订单号
     * @param renterOrderNo 租客订单号
     * @return List<RenterOrderSubsidyDetailEntity>
     */
    public List<RenterOrderSubsidyDetailEntity> listRenterOrderSubsidyDetail(String orderNo, String renterOrderNo) {
        return renterOrderSubsidyDetailMapper.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
    }

    /**
     * 保存租客补贴信息
     *
     * @param renterOrderSubsidyDetailEntity 补贴明细
     * @return Integer
     */
    public Integer saveRenterOrderSubsidyDetail(RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity) {
        return renterOrderSubsidyDetailMapper.saveRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
    }

    /**
     * 批量保存租客补贴信息
     *
     * @param entityList 补贴明细列表
     * @return Integer
     */
    public Integer saveRenterOrderSubsidyDetailBatch(List<RenterOrderSubsidyDetailEntity> entityList) {
        return renterOrderSubsidyDetailMapper.saveRenterOrderSubsidyDetailBatch(entityList);
    }


    /**
     * 计算限时红包补贴信息
     *
     * @param memNo          租客会员注册号
     * @param surplusRentAmt 抵扣后剩余租金
     * @param reductiAmt     限时红包面额
     * @return RenterOrderSubsidyDetailDTO 抵扣限时红包信息
     */
    public RenterOrderSubsidyDetailDTO calLimitRedSubsidyInfo(int memNo, int surplusRentAmt, Integer reductiAmt) {

        if (null == reductiAmt || reductiAmt == 0) {
            return null;
        }
        //真实抵扣金额
        int realLimitReductiAmt = reductiAmt > surplusRentAmt ? surplusRentAmt : reductiAmt;
        RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();
        renterOrderSubsidyDetailDTO.setMemNo(String.valueOf(memNo));
        renterOrderSubsidyDetailDTO.setSubsidyAmount(realLimitReductiAmt);
        renterOrderSubsidyDetailDTO.setSubsidyTypeCode(SubsidyTypeCodeEnum.RENT_AMT.getCode());
        renterOrderSubsidyDetailDTO.setSubsidTypeName(SubsidyTypeCodeEnum.RENT_AMT.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
        renterOrderSubsidyDetailDTO.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());

        renterOrderSubsidyDetailDTO.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
        renterOrderSubsidyDetailDTO.setSubsidTypeName(SubsidySourceCodeEnum.RENTER.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidyCostCode(RenterCashCodeEnum.REAL_LIMIT_REDUCTI.getCashNo());
        renterOrderSubsidyDetailDTO.setSubsidyCostName(RenterCashCodeEnum.REAL_LIMIT_REDUCTI.getTxt());
        renterOrderSubsidyDetailDTO.setSubsidyDesc("使用限时红包抵扣租金");
        return renterOrderSubsidyDetailDTO;
    }


    /**
     * 计算会员凹凸币抵扣信息
     *
     * @param crmCustPoint    租客凹凸信息
     * @param originalRentAmt 原始租金
     * @param surplusRentAmt  抵扣后剩余租金
     * @param useAutoCoin     使用凹凸币标识
     * @return RenterOrderSubsidyDetailDTO 抵扣凹凸币信息
     */
    public RenterOrderSubsidyDetailDTO calAutoCoinSubsidyInfo(CrmCustPointDTO crmCustPoint,
                                                              int originalRentAmt,
                                                              int surplusRentAmt,
                                                              Integer useAutoCoin) {
        if (null == useAutoCoin || useAutoCoin == 0) {
            return null;
        }
        return autoCoinCostCalService.calAutoCoinDeductInfo(originalRentAmt, surplusRentAmt, crmCustPoint);
    }


    public RenterOrderSubsidyDetailDTO calOwnerCouponSubsidyInfo(OrderCouponDTO ownerCoupon) {



        return null;
    }


    public RenterOrderSubsidyDetailDTO calGetCarFeeCouponSubsidyInfo(OrderCouponDTO getCarFeeCoupon) {



        return null;
    }


    public RenterOrderSubsidyDetailDTO calPlatformCouponSubsidyInfo(OrderCouponDTO platformCoupon) {



        return null;
    }

}
