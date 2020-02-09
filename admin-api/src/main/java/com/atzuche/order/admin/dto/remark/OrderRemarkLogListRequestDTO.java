package com.atzuche.order.admin.dto.remark;

import com.atzuche.order.admin.common.PageBean;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=true)
public class OrderRemarkLogListRequestDTO extends PageBean{

    public OrderRemarkLogListRequestDTO(long pageNumber,long total){
        super(pageNumber, total, PageBean.PAGE_SIZE);
    }
    @AutoDocProperty(value = "订单号")
    private String orderNo;

}
