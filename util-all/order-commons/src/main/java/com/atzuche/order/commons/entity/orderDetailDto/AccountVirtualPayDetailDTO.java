package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * account_virtual_pay_detail
 * @author 
 */
@Data
@ToString
public class AccountVirtualPayDetailDTO implements Serializable {


    /**
     * 账号编码
     */
    private String accountNo;

    /**
     * 账号名称
     */
    private String accountName;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private Integer amt;

    /**
     * 操作类型:01-支付,04退款
     */
    private String payType;

    /**
     * 支付类型 11-租金，2-押金，3-违章押金
     */
    private String payCashType;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 操作时间
     */
    private LocalDateTime createTime;


}