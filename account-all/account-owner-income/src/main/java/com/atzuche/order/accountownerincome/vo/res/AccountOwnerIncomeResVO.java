package com.atzuche.order.accountownerincome.vo.res;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车主收益总信息
 * @author haibao.yan
 */
@Data
public class AccountOwnerIncomeResVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 收益总金额
     */
    private Integer incomeAmt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
