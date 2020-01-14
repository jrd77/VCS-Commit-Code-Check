package com.atzuche.order.admin.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerRentDetailDTO {
    @AutoDocProperty("请求时间")
    private String reqTime;
    @AutoDocProperty("开始时间")
    private String rentTime;
    @AutoDocProperty("结束时间")
    private String revertTime;

}
