package com.atzuche.order.rentercost.service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.mapper.RenterOrderSubsidyDetailMapper;
import com.autoyol.auto.coin.service.vo.res.AutoCoinResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


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
    
    
    public int saveOrUpdateRenterOrderSubsidyDetail(RenterOrderSubsidyDetailEntity record) {
    	List<RenterOrderSubsidyDetailEntity> list = listRenterOrderSubsidyDetail(record.getOrderNo(), record.getRenterOrderNo()); 
    	boolean isExists = false;
    	for (RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity : list) {
			//存在
    		if(renterOrderSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidySourceCode()) && renterOrderSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidyTargetCode()) && renterOrderSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())) {
    			record.setId(renterOrderSubsidyDetailEntity.getId());
    			renterOrderSubsidyDetailMapper.updateByPrimaryKeySelective(record);
    			isExists = true;
			}
		}
    	
    	if(!isExists) {
    		//增加记录
    		renterOrderSubsidyDetailMapper.saveRenterOrderSubsidyDetail(record);
    	}
    	
    	
    	return 1;
    }
    
    /**
     * 数据转化
     * @param costBaseDTO 基础信息
     * @param fineAmt 罚金金额
     * @param code 罚金补贴方编码枚举
     * @param source 罚金来源编码枚举
     * @param type 罚金类型枚举
     * @return ConsoleRenterOrderFineDeatailEntity
     */
    public RenterOrderSubsidyDetailEntity buildData(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidySourceCodeEnum target, SubsidySourceCodeEnum source, SubsidyTypeCodeEnum type,RenterCashCodeEnum cash) {
        if (subsidyAmount == null || subsidyAmount == 0) {
            return null;
        }
        
        RenterOrderSubsidyDetailEntity entity = new RenterOrderSubsidyDetailEntity();
        // 补贴金额
        entity.setSubsidyAmount(subsidyAmount);
        
        entity.setSubsidySourceCode(source.getCode());
        entity.setSubsidySourceName(source.getDesc());
        
        entity.setSubsidyTargetCode(target.getCode());
        entity.setSubsidyTargetName(target.getDesc());
        
        entity.setSubsidyTypeCode(type.getCode());
        entity.setSubsidyTypeName(type.getDesc());
        
        entity.setSubsidyCostCode(cash.getCashNo());
        entity.setSubsidyCostName(cash.getTxt());
        
        entity.setMemNo(costBaseDTO.getMemNo());
        entity.setOrderNo(costBaseDTO.getOrderNo());
        ///必传
        entity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        return entity;
    }
    
    

    /**
     * 批量保存租客补贴信息
     *
     * @param entityList 补贴明细列表
     * @return Integer
     */
    public Integer saveRenterOrderSubsidyDetailBatch(List<RenterOrderSubsidyDetailEntity> entityList) {
        if(entityList == null || entityList.size()<=0){
            return 0;
        }
        return renterOrderSubsidyDetailMapper.saveRenterOrderSubsidyDetailBatch(entityList);
    }


    /**
     * 限时红包补贴信息
     *
     * @param memNo          租客会员注册号
     * @param surplusRentAmt 抵扣后剩余租金
     * @param reductiAmt     限时红包面额
     * @return RenterOrderSubsidyDetailDTO 限时红包补贴信息
     */
    public RenterOrderSubsidyDetailDTO calLimitRedSubsidyInfo(Integer memNo,
                                                              int surplusRentAmt,
                                                              Integer reductiAmt) {

        if (null == reductiAmt || reductiAmt == 0) {
            return null;
        }
        //真实抵扣金额
        int realLimitReductiAmt = reductiAmt > surplusRentAmt ? surplusRentAmt : reductiAmt;
        RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();
        renterOrderSubsidyDetailDTO.setMemNo(String.valueOf(memNo));
        renterOrderSubsidyDetailDTO.setSubsidyAmount(realLimitReductiAmt);
        renterOrderSubsidyDetailDTO.setSubsidyTypeCode(SubsidyTypeCodeEnum.RENT_AMT.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTypeName(SubsidyTypeCodeEnum.RENT_AMT.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
        renterOrderSubsidyDetailDTO.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());

        renterOrderSubsidyDetailDTO.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidyCostCode(RenterCashCodeEnum.REAL_LIMIT_REDUCTI.getCashNo());
        renterOrderSubsidyDetailDTO.setSubsidyCostName(RenterCashCodeEnum.REAL_LIMIT_REDUCTI.getTxt());
        renterOrderSubsidyDetailDTO.setSubsidyDesc("使用限时红包抵扣租金");
        return renterOrderSubsidyDetailDTO;
    }


    /**
     * 凹凸币补贴信息
     *
     * @param crmCustPoint    租客凹凸信息
     * @param originalRentAmt 原始租金
     * @param surplusRentAmt  抵扣后剩余租金
     * @param useAutoCoin     使用凹凸币标识
     * @return RenterOrderSubsidyDetailDTO 补贴凹凸币信息
     */
    public RenterOrderSubsidyDetailDTO calAutoCoinSubsidyInfo(AutoCoinResponseVO crmCustPoint,
                                                              int originalRentAmt,
                                                              int surplusRentAmt,
                                                              Integer useAutoCoin) {
        if (null == useAutoCoin || useAutoCoin == 0) {
            return null;
        }
        return autoCoinCostCalService.calAutoCoinDeductInfo(originalRentAmt, surplusRentAmt, crmCustPoint);
    }


    /**
     * 租客端车主券补贴明细
     *
     * @param memNo       租客会员注册号
     * @param ownerCoupon 车主券抵扣信息
     * @return RenterOrderSubsidyDetailDTO 补贴明细
     */
    public RenterOrderSubsidyDetailDTO calOwnerCouponSubsidyInfo(Integer memNo, OrderCouponDTO ownerCoupon) {
        if (null == ownerCoupon) {
            return null;
        }
        RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();
        renterOrderSubsidyDetailDTO.setMemNo(String.valueOf(memNo));
        renterOrderSubsidyDetailDTO.setSubsidyAmount(ownerCoupon.getAmount());
        renterOrderSubsidyDetailDTO.setSubsidyTypeCode(SubsidyTypeCodeEnum.RENT_AMT.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTypeName(SubsidyTypeCodeEnum.RENT_AMT.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidySourceCode(SubsidySourceCodeEnum.OWNER.getCode());
        renterOrderSubsidyDetailDTO.setSubsidySourceName(SubsidySourceCodeEnum.OWNER.getDesc());

        renterOrderSubsidyDetailDTO.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidyCostCode(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo());
        renterOrderSubsidyDetailDTO.setSubsidyCostName(RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getTxt());
        renterOrderSubsidyDetailDTO.setSubsidyDesc("使用车主券抵扣租金");
        return renterOrderSubsidyDetailDTO;
    }


    /**
     * 租客端送取服务券补贴明细
     *
     * @param memNo           租客会员注册号
     * @param getCarFeeCoupon 送取服务券抵扣信息
     * @return RenterOrderSubsidyDetailDTO 补贴明细
     */
    public RenterOrderSubsidyDetailDTO calGetCarFeeCouponSubsidyInfo(Integer memNo, OrderCouponDTO getCarFeeCoupon) {
        if (null == getCarFeeCoupon) {
            return null;
        }
        RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();
        renterOrderSubsidyDetailDTO.setMemNo(String.valueOf(memNo));
        renterOrderSubsidyDetailDTO.setSubsidyAmount(getCarFeeCoupon.getAmount());
        renterOrderSubsidyDetailDTO.setSubsidyTypeCode(SubsidyTypeCodeEnum.GET_RETURN_CAR.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTypeName(SubsidyTypeCodeEnum.GET_RETURN_CAR.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
        renterOrderSubsidyDetailDTO.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());

        renterOrderSubsidyDetailDTO.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidyCostCode(RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getCashNo());
        renterOrderSubsidyDetailDTO.setSubsidyCostName(RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET.getTxt());
        renterOrderSubsidyDetailDTO.setSubsidyDesc("使用送取服务券抵扣取还车费用(包含对应的运能溢价金额)");
        return renterOrderSubsidyDetailDTO;
    }

    /**
     * 租客端平台优惠券补贴明细
     *
     * @param memNo          租客会员注册号
     * @param platformCoupon 平台优惠券抵扣信息
     * @return RenterOrderSubsidyDetailDTO 补贴明细
     */
    public RenterOrderSubsidyDetailDTO calPlatformCouponSubsidyInfo(Integer memNo, OrderCouponDTO platformCoupon) {
        if (null == platformCoupon) {
            return null;
        }
        RenterOrderSubsidyDetailDTO renterOrderSubsidyDetailDTO = new RenterOrderSubsidyDetailDTO();
        renterOrderSubsidyDetailDTO.setMemNo(String.valueOf(memNo));
        renterOrderSubsidyDetailDTO.setSubsidyAmount(platformCoupon.getAmount());
        renterOrderSubsidyDetailDTO.setSubsidyTypeCode(SubsidyTypeCodeEnum.RENT_AMT.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTypeName(SubsidyTypeCodeEnum.RENT_AMT.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidySourceCode(SubsidySourceCodeEnum.PLATFORM.getCode());
        renterOrderSubsidyDetailDTO.setSubsidySourceName(SubsidySourceCodeEnum.PLATFORM.getDesc());

        renterOrderSubsidyDetailDTO.setSubsidyTargetCode(SubsidySourceCodeEnum.RENTER.getCode());
        renterOrderSubsidyDetailDTO.setSubsidyTargetName(SubsidySourceCodeEnum.RENTER.getDesc());
        renterOrderSubsidyDetailDTO.setSubsidyCostCode(RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo());
        renterOrderSubsidyDetailDTO.setSubsidyCostName(RenterCashCodeEnum.REAL_COUPON_OFFSET.getTxt());
        renterOrderSubsidyDetailDTO.setSubsidyDesc("使用优惠券抵扣租车费用(目前只抵扣租金)");
        return renterOrderSubsidyDetailDTO;
    }

}
