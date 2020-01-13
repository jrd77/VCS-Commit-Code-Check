/**
 * 
 */
package com.atzuche.order.admin.vo.req.cost;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@Data
@ToString
public class GetReturnRequestVO {
//	@ApiModelProperty(value="车辆号",required=true)
//    @NotBlank(message="carNo不能为空")
//    private String carNo;
	
	@ApiModelProperty(value="起租时间yyyy-MM-dd HH:mm:ss",required=true)
    @NotBlank(message="起租时间不能为空")
    private String rentTime;
	@ApiModelProperty(value="还车时间yyyy-MM-dd HH:mm:ss",required=true)
    @NotBlank(message="还车时间不能为空")
    private String revertTime;
	
	@ApiModelProperty(value="取车维度")
    private String getCarLan;
	@ApiModelProperty(value="取车经度")
    private String getCarLon;
	@ApiModelProperty(value="还车维度")
    private String returnCarLan;
	@ApiModelProperty(value="还车经度")
    private String returnCarLon;
	
	@ApiModelProperty(value="计算标识:1取车费用，2还车费用")
	@NotBlank(message="计算标识:1取车费用，2还车费用不能为空")
    private String flag;
	
	
	@ApiModelProperty(value="场景编码",required=true)
    @NotBlank(message="场景编码不能为空")
    private String sceneCode;
	@ApiModelProperty(value="订单来源",required=true)
    @NotBlank(message="订单来源不能为空")
    private String source;
	@ApiModelProperty(value="城市编码",required=true)
    @NotBlank(message="城市编码不能为空")
    private String cityCode;
	
	/**
     * 订单类型:1,短租订单 2,平台套餐订单
     */
	@ApiModelProperty(value="订单类型:1,短租订单 2,平台套餐订单",required=true)
    @NotBlank(message="订单类型不能为空")
	private String orderType;
    
}
