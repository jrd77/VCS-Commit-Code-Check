package com.atzuche.order.admin.vo.res.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * RenterWzCostDetail
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class RenterWzCostDetailVO {

    @AutoDocProperty("订单号")
    private String orderNo;

    @AutoDocProperty("费用编码")
    private String costCode;

    @AutoDocProperty("费用描述")
    private String costDesc;

    @AutoDocProperty("费用")
    private String amount;

    @AutoDocProperty("备注")
    private String remark;

}
