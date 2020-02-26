/**
 * 
 */
package com.atzuche.order.admin.vo.resp.order.cost.detail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@ToString
@Data
public class RenterPriceAdjustmentResVO {
	@ApiModelProperty(value="租客给车主的调价",required=true)
//    @NotBlank(message="renterOrderNo不能为空")
    private String renterToOwnerAdjustAmt;
    
    @ApiModelProperty(value="车主给租客的调价",required=true)
//    @NotBlank(message="renterOrderNo不能为空")
    private String ownerToRenterAdjustAmt;
}
