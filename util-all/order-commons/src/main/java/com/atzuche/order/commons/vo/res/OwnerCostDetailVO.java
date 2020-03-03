package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.vo.OwnerFineVO;
import com.atzuche.order.commons.vo.OwnerSubsidyDetailVO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OwnerCostDetailVO {
    @AutoDocProperty(value = "主订单号")
    private String orderNo;
    @AutoDocProperty(value = "车主子单号")
    private String ownerOrderNo;
    @AutoDocProperty(value = "车主会员号")
    private String ownerMemNo;

    @AutoDocProperty(value = "租金总额")
    private Integer rentAmt;

    @AutoDocProperty(value = "补贴及其详情")
    private OwnerSubsidyDetailVO subsidyDetail;

    @AutoDocProperty(value = "罚金及其详情")
    private OwnerFineVO fineDetail;

    @AutoDocProperty(value = "增值费用")
    private OwnerIncrementDetailVO incrementDetail;

}
