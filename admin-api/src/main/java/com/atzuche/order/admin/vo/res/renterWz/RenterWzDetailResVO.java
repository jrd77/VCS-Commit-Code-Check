package com.atzuche.order.admin.vo.res.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * RenterWzDetailResVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class RenterWzDetailResVO {

    @AutoDocProperty("订单号")
    private String orderNo;

    @AutoDocProperty(value="违章押金信息")
    private RenterWzInfoVO info;

    @AutoDocProperty(value="违章押金暂扣处理")
    private RenterWzWithholdVO withhold;

    @AutoDocProperty(value="费用详情")
    private List<RenterWzCostDetailVO> costDetails;

}
