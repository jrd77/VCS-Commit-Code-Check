package com.atzuche.order.wallet.server.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * trans_supplement_detail
 * @author 
 */
@Data
public class TransSupplementDetailEntity implements Serializable {
    /**
     * 自增长序列号
     */
    private Integer id;

    /**
     * 订单号
     */
    private Long orderNo;

    /**
     * 会员号
     */
    private Integer memNo;

    /**
     * 补付名称
     */
    private String title;

    /**
     * 单项补付金额
     */
    private Integer amt;

    /**
     * 一笔支付对多个订单的总额去支付。
     */
    private Integer tspId;

    /**
     * 补付记录:trans_supplement_flow.id
     */
    private Integer flowId;

    /**
     * 仁云收款ID
     */
    private String orderId;

    /**
     * 操作状态:0,待提交 1,已生效 2,已失效 3,已撤回
     */
    private Byte opStatus;

    /**
     * 补付类型:1,系统创建 2,手动创建
     */
    private Byte supplementType;

    /**
     * 操作类型:1,修改订单 2,车管家录入 3,租车押金结算 4,违章押金结算 5,手动添加 6,欠款抵扣
     */
    private Byte opType;

    /**
     * 请款码，流程系统对接接口传递费用列表
     */
    private String requestPayCode;

    /**
     * 费用类型:1,租车押金 2,违章押金 3,订单欠款
     */
    private String cashType;

    /**
     * 费用编码，关联trans_renter_cash_code_supplement.cash_no
     */
    private Integer cashNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 支付状态:0.无需支付 1.未支付 2.已取消 3.已支付 4.支付中，5.支付失败 10.租车押金结算抵扣  20.违章押金结算抵扣
     */
    private Integer payFlag;

    /**
     * 任云通知标识位,0未通知，1成功，2失败
     */
    private Byte isNotice;

    /**
     * 违章押金结算是否结算,0未结算，1已结算
     */
    private Byte settleDeposit;

    /**
     * 租车押金结算(含取消判责)是否结算,0未结算，1已结算
     */
    private Byte settleRent;

    /**
     * 无需支付是否使用,0未使用，1已使用
     */
    private Byte isUsed;

    /**
     * 操作状态(取消订单之前的状态):0,待提交 1,已生效 2,已失效 3,已撤回
     */
    private Byte oldOpStatus;

    /**
     * 支付状态(取消订单之前的状态):0.无需支付 1.未支付 2.已取消 3.已支付 4.支付中，5.支付失败 10.租车押金结算抵扣  20.违章押金结算抵扣
     */
    private Byte oldPayFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateOp;

    /**
     * 0-正常，1-已逻辑删除
     */
    private Byte isDelete;

    /**
     * 父级ID
     */
    private Integer pid;

    /**
     * 父级ID-back
     */
    private Integer pidBack;

    private static final long serialVersionUID = 1L;
}