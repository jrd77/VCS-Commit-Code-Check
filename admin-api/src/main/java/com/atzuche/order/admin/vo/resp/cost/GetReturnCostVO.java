/**
 * 
 */
package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@Data
@ToString
public class GetReturnCostVO {
	@AutoDocProperty("还车费用")
    private String returnCost;
    @AutoDocProperty("预期应收取车费用")
    private String getShouldCost;
    @AutoDocProperty("预期应收还车费用")
    private String returnShouldCost;
    @AutoDocProperty("取车费用")
    private String getCost;
    
    @AutoDocProperty("取车时间是否超出运能:1-超出运能，0-未超出")
    private String isGetOverTransport;
    @AutoDocProperty("取车时间超出运能附加费用")
    private String getOverTransportFee;
    @AutoDocProperty("取车时间超出运能附加费用(夜间)")
    private String nightGetOverTransportFee;
    
    @AutoDocProperty("还车时间是否超出运能:1-超出运能，0-未超出")
    private String isReturnOverTransport;
    @AutoDocProperty("还车时间超出运能附加费用")
    private String returnOverTransportFee;
    @AutoDocProperty("还车时间超出运能附加费用(夜间)")
    private String nightReturnOverTransportFee;
    
}
