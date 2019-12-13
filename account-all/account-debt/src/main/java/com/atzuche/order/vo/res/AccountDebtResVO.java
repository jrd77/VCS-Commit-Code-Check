package com.atzuche.order.vo.res;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 个人总欠款信息
 * @author haibao.yan
 */
@Data
public class AccountDebtResVO {

    /**
     * 会员号
     */
    private Integer memNo;
    /**
     * 总欠款
     */
    private Integer debtAmt;
    /**
     * 更新版本号
     */
    private Integer version;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;



}
