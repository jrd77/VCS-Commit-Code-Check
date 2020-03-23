package com.atzuche.order.delivery.vo.handover;

import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 租客交接车备注信息
 */
@Data
@ToString
public class HandoverCarRemarkDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String renterOrderNo;
    /**
     * 交车类型 1-车主向租客交车、2-租客向车主交车、3-车管家向租客交车、4-租客向车管家交车
     */
    private Integer type;
    /**
     * 备注内容
     */
    private String remark;
    /**
     * 备注人手机号
     */
    private String phone;
    /**
     * 备注人会员号
     */
    private String memNo;
    /**
     * 备注人姓名
     */
    private String realName;

    /**
     * 对应流程系统步骤，第一步传1，以此类推
     */
    private String proId;
}
