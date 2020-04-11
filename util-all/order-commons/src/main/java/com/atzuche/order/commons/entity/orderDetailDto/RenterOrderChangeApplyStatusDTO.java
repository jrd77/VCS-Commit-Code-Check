package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class RenterOrderChangeApplyStatusDTO {
    /**
     * 是否修改起租时间 0-否，1-是
     */
    @AutoDocProperty("是否修改起租时间 0-否，1-是")
    private Integer rentTimeFlag;
    /**
     * 申请类型 1-租期修改，2-取还车地址修改，3-租期地址修改）
     */
    @AutoDocProperty("申请类型 1-租期修改，2-取还车地址修改，3-租期地址修改）")
    private Integer applyType;
    /**
     * 审核状态:0-未处理，1-已同意，2-主动拒绝,3-自动拒绝
     */
    @AutoDocProperty("审核状态:0-未处理，1-已同意，2-主动拒绝,3-自动拒绝")
    private Integer auditStatus;

    @AutoDocProperty("创建时间 yyyy-Mm-dd HH:mm:ss")
    private String createTimeStr;

    @AutoDocProperty("修改申请的子订单号")
    private String renterOrderNo;
}
