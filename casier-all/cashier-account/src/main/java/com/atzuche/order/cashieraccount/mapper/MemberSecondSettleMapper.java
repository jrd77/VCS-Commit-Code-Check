/**
 * 
 */
package com.atzuche.order.cashieraccount.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.cashieraccount.entity.MemberSecondSettleEntity;

/**
 * @author jing.huang
 *
 */
@Mapper
public interface MemberSecondSettleMapper {
    Integer getMemberSecondSettleEntityNumber(@Param("orderNo") String orderNo,@Param("settleType")Integer settleType);
    int insertSelective(MemberSecondSettleEntity record);
    
}
