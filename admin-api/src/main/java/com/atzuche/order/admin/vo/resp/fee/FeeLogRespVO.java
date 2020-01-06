package com.atzuche.order.admin.vo.resp.fee;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class FeeLogRespVO {
    @AutoDocProperty(value = "操作时间")
    private String dateStr;
    @AutoDocProperty(value = "操作人")
    private String operator;
    @AutoDocProperty(value = "费用项")
    private String feeTypeStr;

    @AutoDocProperty(value = "操作内容")
    private String remark;
}
