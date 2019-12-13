package com.atzuche.order.coreapi.entity.request;

import java.util.List;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;

@Data
public class ModifyOrderReq {
	@NotBlank(message="订单编号不能为空")
	@Pattern(regexp="^\\d*$",message="订单编号必须为数字")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	@NotBlank(message="memNo不能为空")
	@AutoDocProperty(value="memNo,必填，",required=true)
	private String memNo;
	
	@AutoDocProperty(value="补充全险是否开启，0：否，1：是")
	private String abatementFlag;
	
	@AutoDocProperty(value="取车时间,必填，",required=true)
	@NotBlank(message="rentTime不能为空")
	@Pattern(regexp="^\\d*$",message="rentTime必须为数字")
	private String rentTime;
	
	@NotBlank(message="revertTime不能为空")
	@Pattern(regexp="^\\d*$",message="revertTime必须为数字")
	@AutoDocProperty(value="还车时间,必填，",required=true)
	private String revertTime;
	
	@AutoDocProperty(value="取车地址")
	@NotBlank(message ="取车地址不能为空")
	private String getCarAddress;
	
	@AutoDocProperty(value="取车地址纬度")
	@NotBlank(message ="取车地址纬度不能为空")
    private String getCarLat;
	
	@AutoDocProperty(value="取车地址经度")
	@NotBlank(message ="取车地址经度不能为空")
    private String getCarLon;
	
	@AutoDocProperty(value="还车地址")
	@NotBlank(message ="还车地址不能为空")
	private String revertCarAddress;
	
	@AutoDocProperty(value="还车地址纬度")
	@NotBlank(message ="还车地址纬度不能为空")
    private String revertCarLat;
	
	@AutoDocProperty(value="还车地址经度")
	@NotBlank(message ="还车地址经度不能为空")
    private String revertCarLon;
	
	@AutoDocProperty(value="【增加附加驾驶员】附加驾驶员ID列表")
	private List<String> driverIds;
}
