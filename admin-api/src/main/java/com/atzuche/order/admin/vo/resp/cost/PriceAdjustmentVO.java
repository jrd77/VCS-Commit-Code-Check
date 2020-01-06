package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 租客车主互相调价
 */
@Data
@ToString
public class PriceAdjustmentVO {
    @AutoDocProperty("租客给车主调价")
    private String renterToOwnerPrice;
    @AutoDocProperty("车主给租客调价")
    private String ownerToRenterPrice;
    @ApiModelProperty(value="子订单号",required=true)
    private String renterOrderNo;
}
