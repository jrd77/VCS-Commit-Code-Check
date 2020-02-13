package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 12:43 下午
 **/
public class OrderSupplementDetailVO {
    /**
     * 费用类型：1-补付费用，2-订单欠款
     */
    @AutoDocProperty(value = "费用类型：1-补付费用，2-订单欠款")
    private Integer cashType;
    /**
     * 费用类型文案
     */
    @AutoDocProperty(value = "费用类型文案")
    private String cashTypeTxt;
    /**
     * 费用编码
     */
    @AutoDocProperty(value = "费用编码")
    private String cashNo;
    /**
     * 补付名称
     */
    @AutoDocProperty(value = "补付名称")
    private String title;
    /**
     * 单项补付金额
     */
    @AutoDocProperty(value = "单项补付金额")
    private Integer amt;
    /**
     * 请款码
     */
    @AutoDocProperty(value = "请款码")
    private String requestPayCode;
    /**
     * 操作状态:0,待提交 1,已生效 2,已失效 3,已撤回
     */
    @AutoDocProperty(value = "操作状态:0,待提交 1,已生效 2,已失效 3,已撤回")
    private Integer opStatus;
    /**
     * 补付类型:1,系统创建 2,手动创建
     */
    @AutoDocProperty(value = "补付类型:1,系统创建 2,手动创建")
    private Integer supplementType;
    /**
     * 操作类型:1,修改订单 2,车管家录入 3,租车押金结算 4,违章押金结算 5,手动添加
     */
    @AutoDocProperty(value = "操作类型:1,修改订单 2,车管家录入 3,租车押金结算 4,违章押金结算 5,手动添加")
    private Integer opType;
    /**
     * 备注
     */
    @AutoDocProperty(value = "备注")
    private String remark;
    /**
     * 支付状态:0.无需支付 1.未支付 2.已取消 3.已支付 4.支付中，5.支付失败 10.租车押金结算抵扣  20.违章押金结算抵扣
     */
    @AutoDocProperty(value = "支付状态:0.无需支付 1.未支付 2.已取消 3.已支付 4.支付中，5.支付失败 10.租车押金结算抵扣  20.违章押金结算抵扣")
    private Integer payFlag;
}
