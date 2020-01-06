package com.atzuche.order.admin.vo.req.fee;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

/***
 * 费用操作日志
 */
@Data
public class FeeLogReqVO {
    @AutoDocProperty(value = "开始时间")
    private String startTimeStr;
    @AutoDocProperty(value = "结束时间")
    private String endTimeStr;
    @AutoDocProperty(value = "费用项")
    private String feeType;
    @AutoDocProperty(value = "操作人")
    private String operator;
    @AutoDocProperty(value = "操作内容")
    private String remark;
}
