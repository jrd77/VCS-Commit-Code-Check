package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class RenterOrderWzDetailLogList{
    private List<RenterOrderWzDetailLogDTO> renterOrderWzDetailLogDTOS;
    @AutoDocProperty(value="总记录数")
    private Long count;
    @AutoDocProperty(value="总页数")
    private Integer totalPage;
}
