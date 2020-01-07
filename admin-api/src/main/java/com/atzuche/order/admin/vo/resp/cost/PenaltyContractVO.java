package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
/**
 * @author 胡春林
 * 违约罚金
 */
@Data
@ToString
public class PenaltyContractVO {
    @AutoDocProperty("租客提前还车罚金")
    private String renterBeforeReturnCarAmt;
    @AutoDocProperty("租客延迟还车罚金")
    private String renterDelayReturnCarAmt;
    @AutoDocProperty("租客违约罚金")
    private String renterPenaltyAmt;
    @AutoDocProperty("租客取还车违约金")
    private String rentergetReturnCarPanalAmt;
    @ApiModelProperty(value="子订单号",required=true)
    private String renterOrderNo;
}
