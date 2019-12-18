package com.atzuche.order.accountrenterwzdepost.vo.res;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 个人总欠款信息
 * @author haibao.yan
 */
@Data
public class AccountRenterWZDepositResVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 应收违章押金
     */
    private Integer yingshouDeposit;
    /**
     * 实收违章押金
     */
    private Integer shishouDeposit;
    /**
     * 是否预授权
     */
    private Integer isAuthorize;
    /**
     * 是否免押
     */
    private Integer isFreeDeposit;
    /**
     * 应退押金
     */
    private Integer shouldReturnDeposit;
    /**
     * 实退押金
     */
    private Integer realReturnDeposit;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 创建人
     */
    private String createOp;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    /**
     * 更新人
     */
    private String updateOp;





}
