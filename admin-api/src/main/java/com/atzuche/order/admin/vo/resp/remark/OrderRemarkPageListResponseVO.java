package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OrderRemarkPageListResponseVO {

    @AutoDocProperty(value = "订单备注列表")
    private List<OrderRemarkListResponseVO> orderRemarkList;



}
