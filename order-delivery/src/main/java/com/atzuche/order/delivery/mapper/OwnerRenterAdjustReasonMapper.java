package com.atzuche.order.delivery.mapper;

import com.atzuche.order.delivery.entity.OwnerRenterAdjustReasonEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 车主和租客相互调价原因和备注表
 *
 * @author ZhangBin
 * @date 2020-07-29 13:34:29
 */
@Mapper
public interface OwnerRenterAdjustReasonMapper {

    OwnerRenterAdjustReasonEntity selectByPrimaryKey(Integer id);

    int insertSelective(OwnerRenterAdjustReasonEntity record);

    int updateByPrimaryKey(OwnerRenterAdjustReasonEntity record);

    int updateByPrimaryKeySelective(OwnerRenterAdjustReasonEntity record);

    OwnerRenterAdjustReasonEntity selectByChildNo(@Param("ownerOrderNo")String ownerOrderNo, @Param("renterOrderNo")String renterOrderNo);
}
