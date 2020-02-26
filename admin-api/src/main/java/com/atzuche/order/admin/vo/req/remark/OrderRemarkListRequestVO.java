package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class OrderRemarkListRequestVO {

    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "备注类型 1:理赔备注,2:限制租客延时备注,3:违章备注,4:违章处理备注,5:交易备注,6:呼叫中心备注,7:跟进备注,8:运营清算备注,9:电销中心备注,10:商品运营备注,11:风控备注")
    private String remarkType;

    @AutoDocProperty(value = "操作人部门id")
    private String departmentId;

    @AutoDocProperty(value = "操作人姓名")
    private String operatorName;

    @AutoDocProperty(value = "备注开始时间")
    private String startTime;

    @AutoDocProperty(value = "备注结束时间")
    private String endTime;




}
