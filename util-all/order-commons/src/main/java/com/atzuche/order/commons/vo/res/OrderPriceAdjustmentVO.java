package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OrderPriceAdjustmentVO implements java.io.Serializable {

    /**
     * 主订单ID
     */
    @AutoDocProperty(value = "主订单ID")
    private String orderNo;
    /**
     * 车主Code
     */
    @AutoDocProperty(value = "车主Code")
    private String ownerMemberCode;
    /**
     * 租客code
     */
    @AutoDocProperty(value = "租客code")
    private String renterMemberCode;
    /**
     * 费用
     */
    @AutoDocProperty(value = "费用")
    private Integer amt;
    /**
     * 1是车主给租客的补贴；2是租客给车主的补贴
     */
    @AutoDocProperty(value = "1是车主给租客的补贴；2是租客给车主的补贴")
    private Integer type;
    /**
     * 补贴说明
     */
    @AutoDocProperty(value = "补贴说明")
    private String remark;
}
