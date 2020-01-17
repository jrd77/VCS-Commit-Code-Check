package com.atzuche.order.coreapi.entity.request;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyOrderAppReq {
	
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
	@NotBlank(message="租客memNo不能为空")
	@AutoDocProperty(value="租客memNo,必填，",required=true)
	private String memNo;
	
	@AutoDocProperty(value="补充全险是否开启，0：否，1：是")
	private Integer abatementFlag;
	
	@AutoDocProperty(value="取车时间,格式 yyyyMMddHHmmss")
	private String rentTime;
	
	@AutoDocProperty(value="还车时间,格式 yyyyMMddHHmmss")
	private String revertTime;
	
	@AutoDocProperty(value="取车地址")
	private String getCarAddress;
	
	@AutoDocProperty(value="取车地址纬度")
    private String getCarLat;
	
	@AutoDocProperty(value="取车地址经度")
    private String getCarLon;
	
	@AutoDocProperty(value="还车地址")
	private String revertCarAddress;
	
	@AutoDocProperty(value="还车地址纬度")
    private String revertCarLat;
	
	@AutoDocProperty(value="还车地址经度")
    private String revertCarLon;
	
	@AutoDocProperty(value="【增加附加驾驶员】附加驾驶员ID列表")
	private List<String> driverIds;
}
