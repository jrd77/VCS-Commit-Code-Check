package com.atzuche.order.coreapi.entity.request;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ModifyApplyHandleReq {

	@NotBlank(message="memNo不能为空")
	@AutoDocProperty(value="memNo,必填，",required=true)
	private String memNo;
	
	@NotBlank(message="处理修改申请编码不能为空")
	@AutoDocProperty(value="modifyApplicationId,必填，",required=true)
	private String modifyApplicationId;
	
	@NotBlank(message="修改订单申请flag不能为空")
    @Pattern(regexp="^[0-1]*$",message="修改订单申请flag必须为数字且为0或1")
	private String flag;
}
