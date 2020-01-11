package com.atzuche.order.coreapi.entity.vo.req;

import lombok.Data;

/**
 * 凹凸币扣除请求参数
 *
 * @author pengcheng.fu
 * @date 2020/1/1 16:55
 */

@Data
public class AutoCoinDeductReqVO {

    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 租客订单号
     */
    private String renterOrderNo;

    /**
     * 使用凹凸币标识
     */
    private Integer useAutoCoin;

    /**
     * 租客会员注册号
     */
    private String memNo;


    /**
     * 扣减/充值凹凸币
     */
    private Integer chargeAutoCoin;

    /**
     * 备注(增加/代扣原因)
     */
    private String remark;
    /**
     * 类型,约定1-50为表示订单号，51-100表示车辆注册号,如有其他后面添加。1为车主结算,2租车消费,3车主订单分享,4:租客评价，5：车主评价,6:会员等级权益赠送凹凸币，7:管理后台操作用户凹凸币，10:结算返还,11:取消订单返回，12：恢复订单抵扣，13：邀请有礼会员注册送凹凸币，14：，15：修改订单车主拒绝退回补扣的凹凸币，16：修改订单补扣凹凸币，17：修改订单车主同意退回多余的凹凸币，51车辆检测"
     */
    private String orderType;

    /**
     * 备注信息(增加/代扣凹凸币原因选择'其他'时,此地段为必填项)
     */
    private String remarkExtend;





}
