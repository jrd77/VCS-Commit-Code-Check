package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by qincai.lin on 2020/1/2.
 */
@Data
@ToString
public class OrderRemarkOverviewListResponseVO {
    @AutoDocProperty(value = "备注总览")
    private List<OrderRemarkOverviewResponseVO> remarkOverviewList;


}
