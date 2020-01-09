package com.atzuche.order.admin.dto;

import com.atzuche.order.admin.common.PageBean;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRemarkLogListRequestDTO extends PageBean{

    public OrderRemarkLogListRequestDTO(long total,long pageNumber){
        super(pageNumber, total, 10);
    }
    @AutoDocProperty(value = "订单号")
    private String orderNo;

}
