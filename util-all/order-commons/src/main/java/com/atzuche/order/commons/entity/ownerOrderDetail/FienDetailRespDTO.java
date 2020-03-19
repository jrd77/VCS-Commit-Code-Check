package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class FienDetailRespDTO {
    @AutoDocProperty("操作时间")
    private String createTimeStr;
    @AutoDocProperty("费用")
    private Integer amt;
    @AutoDocProperty("操作人")
    private String operater;

}
