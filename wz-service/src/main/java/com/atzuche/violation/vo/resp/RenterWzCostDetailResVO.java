package com.atzuche.violation.vo.resp;

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
public class RenterWzCostDetailResVO {

    @AutoDocProperty("订单号")
    private String orderNo;

    @AutoDocProperty("费用类型")
    private Integer costType;

    @AutoDocProperty("费用编码")
    private String costCode;

    @AutoDocProperty("费用描述")
    private String costDesc;

    @AutoDocProperty("费用")
    private String amount;

    @AutoDocProperty("备注")
    private String remark;

    @AutoDocProperty("备注名称")
    private String remarkName;

}
