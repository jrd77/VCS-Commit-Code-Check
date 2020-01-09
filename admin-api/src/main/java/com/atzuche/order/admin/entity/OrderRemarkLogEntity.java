package com.atzuche.order.admin.entity;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRemarkLogEntity {

    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "备注id")
    private String remarkId;

    @AutoDocProperty(value = "备注编号")
    private String number;

    @AutoDocProperty(value = "备注类型 1:理赔备注,2:限制租客延时备注,3:违章备注,4:违章处理备注,5:交易备注,6:呼叫中心备注,7:跟进备注,8:运营清算备注,9:电销中心备注,10:商品运营备注,11:风控备注")
    private String remarkType;

    @AutoDocProperty(value = "操作人部门id")
    private String departmentId;

    @AutoDocProperty(value = "备注内容")
    private String remarkContent;

    @AutoDocProperty(value = "创建人")
    private String createOp;

    @AutoDocProperty(value = "操作人")
    private String updateOp;

    @AutoDocProperty(value = "是否删除，1:是，0:否")
    private String isDelete;

    @AutoDocProperty(value = "创建时间")
    private String createTime;

    @AutoDocProperty(value = "修改时间")
    private String updateTime;

    @AutoDocProperty(value = "是否限制租客延时")
    private String limitDelayed;

    @AutoDocProperty(value = "跟进状态")
    private String followStatus;

    @AutoDocProperty(value = "跟进失败原因")
    private String followFailReason;

    @AutoDocProperty(value = "操作类型 1:新增,2更新,3删除")
    private String operateType;

    @AutoDocProperty(value = "备注历史内容")
    private String remarkHistory;



}
