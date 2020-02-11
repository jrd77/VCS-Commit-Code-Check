package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class OwnerRentDetailDTO {
    @AutoDocProperty("请求时间")
    private String reqTimeStr;
    @AutoDocProperty("开始时间")
    private String rentTimeStr;
    @AutoDocProperty("结束时间")
    private String revertTimeStr;
    @AutoDocProperty("日均价")
    private Integer dayAverageAmt;
    @AutoDocProperty("一天一价列表")
    List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOS;
}
