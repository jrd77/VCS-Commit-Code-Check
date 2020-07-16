package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterInsureCoefficientReason;

public interface RenterInsureCoefficientReasonMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RenterInsureCoefficientReason record);

    int insertSelective(RenterInsureCoefficientReason record);

    RenterInsureCoefficientReason selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RenterInsureCoefficientReason record);

    int updateByPrimaryKey(RenterInsureCoefficientReason record);
}