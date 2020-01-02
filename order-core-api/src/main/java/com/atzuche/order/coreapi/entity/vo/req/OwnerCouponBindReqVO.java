package com.atzuche.order.coreapi.entity.vo.req;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 绑定车主券请求参数
 *
 * @author pengcheng.fu
 * @date 2020/1/1 15:59
 */

@Data
public class OwnerCouponBindReqVO implements Serializable {

    private static final long serialVersionUID = 7665607750083670744L;

    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 原始租金
     */
    private Integer rentAmt;

    /**
     * 券码
     */
    private String couponNo;

    /**
     * 车辆注册号
     */
    private Integer carNo;

    /**
     * 租客姓名
     */
    private String renterName;

    /**
     * 租客姓氏
     */
    private String renterFirstName;

    /**
     * 租客性别
     */
    private String renterSex;

}
