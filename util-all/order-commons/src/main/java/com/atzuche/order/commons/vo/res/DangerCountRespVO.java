package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class DangerCountRespVO {
    @AutoDocProperty("出险次数")
    private Integer countClaim;
    @AutoDocProperty("更新时间")
    private String updateTime;
}
