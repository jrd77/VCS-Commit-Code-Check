package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class FienDetailRespListDTO {
    @AutoDocProperty("罚金列表明细")
    private List<FienDetailRespDTO> fienDetailRespDTOS;
}
