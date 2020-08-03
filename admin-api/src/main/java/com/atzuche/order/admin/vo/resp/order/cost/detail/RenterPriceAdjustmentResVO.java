/**
 * 
 */
package com.atzuche.order.admin.vo.resp.order.cost.detail;

import com.autoyol.doc.annotation.AutoDocProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

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

    @AutoDocProperty("调价原因类型")
    private Integer adjustReasonType;

    @AutoDocProperty("当调价原因类型为【其他】时的备注内容")
    private String remarkContent;
}
