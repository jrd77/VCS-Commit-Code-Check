package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.RenterInsureCoefficient;

public interface RenterInsureCoefficientMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RenterInsureCoefficient record);

    int insertSelective(RenterInsureCoefficient record);

    RenterInsureCoefficient selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RenterInsureCoefficient record);

    int updateByPrimaryKey(RenterInsureCoefficient record);
}