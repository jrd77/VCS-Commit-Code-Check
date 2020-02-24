package com.atzuche.order.commons.vo.res.account.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单车主 真实收益
 * @author haibao.yan
 */
@Data
public class AccountOwnerSettleCostDetailResVO {


    /**
     * 主订单号
     */
    @AutoDocProperty("主订单号")
    private String orderNo;
    /**
     * 子订单号
     */
    @AutoDocProperty("子订单号")
    private String ownerOrderNo;
    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private String memNo;
    /**
     * 金额
     */
    @AutoDocProperty("金额")
    private Integer amt;
    /**
     * 费用编码
     */
    @AutoDocProperty("费用编码")
    private String sourceCode;
    /**
     * 费用来源描述
     */
    @AutoDocProperty("费用来源描述")
    private String sourceDetail;
    /**
     * 费用唯一凭证
     */
    @AutoDocProperty("费用唯一凭证")
    private String uniqueNo;
    /**
     * 费用类型
     */
    @AutoDocProperty("费用类型")
    private Integer costType;
    /**
     * 创建时间
     */
    @AutoDocProperty("创建时间")
    private LocalDateTime createTime;

}
