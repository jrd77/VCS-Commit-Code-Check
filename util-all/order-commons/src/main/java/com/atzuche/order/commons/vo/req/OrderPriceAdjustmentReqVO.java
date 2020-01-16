package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OrderPriceAdjustmentReqVO implements java.io.Serializable {

    /**
     * 主订单ID
     */
    @AutoDocProperty(value = "主订单ID")
    private String orderNo;
    /**
     * 车主Code
     */
    @AutoDocProperty(value = "车主Code")
    private java.lang.String ownerMemberCode;
    /**
     * 租客code
     */
    @AutoDocProperty(value = "租客code")
    private java.lang.String renterMemberCode;
    /**
     * 费用
     */
    @AutoDocProperty(value = "费用")
    private Integer amt;
    /**
     * 1是车主给租客的调价；2是租客给车主的调价
     */
    @AutoDocProperty(value = "1是车主给租客的调价；2是租客给车主的调价")
    private Integer type;
    /**
     * 补贴说明
     */
    @AutoDocProperty(value = "补贴说明")
    private String remark;

    /**
     * 创建人员
     */
    @AutoDocProperty(value = "操作人员")
    private String operation;


    public static enum OrderPriceAdjustmentReqVOType {
        OWNER_TO_RENTER(1, "车主给租客的调价"),
        RENTER_TO_OWNER(2, "租客给车主的调价"),
        ;

        OrderPriceAdjustmentReqVOType(Integer code, String label) {
            this.code = code;
            this.label = label;
        }

        private Integer code;
        private String label;

        public Integer getCode() {
            return code;
        }
    }
}
