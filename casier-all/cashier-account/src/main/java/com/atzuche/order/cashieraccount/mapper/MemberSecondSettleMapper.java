package com.atzuche.order.cashieraccount.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.cashieraccount.entity.MemberSecondSettleEntity;

/**
 * @author jing.huang
 */
@Mapper
public interface MemberSecondSettleMapper {

    /**
     * 查询记录数
     *
     * @param orderNo    订单号
     * @param settleType 结算类型
     * @return Integer 记录数
     */
    Integer getMemberSecondSettleEntityNumber(@Param("orderNo") String orderNo, @Param("settleType") Integer settleType);

    /**
     * 新增
     *
     * @param record 数据
     * @return 成功条数
     */
    int insertSelective(MemberSecondSettleEntity record);

    /**
     * 查询指定条件记录数
     *
     * @param memNo        会员号
     * @param orderNo      订单号
     * @param settleType   结算类型：1租车押金，2违章押金
     * @param isSecondFlow 是否分流：0否1是
     * @return Integer 记录数
     */
    Integer selectByCondition(@Param("memNo") Integer memNo, @Param("orderNo") String orderNo,
                              @Param("settleType") Integer settleType, @Param("isSecondFlow") Integer isSecondFlow);


}
