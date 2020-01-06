package com.atzuche.order.admin.vo.req.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * RenterWzCostReqVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class RenterWzCostReqVO {

    @AutoDocProperty("订单号")
    private String orderNo;


    @AutoDocProperty("费用详情")
    private List<RenterWzCostDetailReqVO> costDetails;

}
