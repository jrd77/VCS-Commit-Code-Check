package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRemarkListResponseVO {

    @AutoDocProperty(value = "备注id")
    private String remarkId;

    @AutoDocProperty(value = "备注编号")
    private String number;

    @AutoDocProperty(value = "备注类型 1:理赔备注,2:限制租客延时备注,3:违章备注,4:违章处理备注,5:交易备注,6:呼叫中心备注,7:跟进备注,8:运营清算备注,9:电销中心备注,10:商品运营备注,11:风控备注")
    private String remarkType;

    @AutoDocProperty(value = "操作人部门")
    private String departmentName;

    @AutoDocProperty(value = "操作人姓名")
    private String operatorName;

    @AutoDocProperty(value = "新增时间")
    private String createTime;

    @AutoDocProperty(value = "更新时间")
    private String updateTime;

    @AutoDocProperty(value = "备注内容")
    private String remarkContent;

    @AutoDocProperty(value = "是否限制租客延时")
    private String limitDelayedText;

    @AutoDocProperty(value = "跟进状态")
    private String followStatusText;

    @AutoDocProperty(value = "跟进失败原因")
    private String followFailReasonText;



}