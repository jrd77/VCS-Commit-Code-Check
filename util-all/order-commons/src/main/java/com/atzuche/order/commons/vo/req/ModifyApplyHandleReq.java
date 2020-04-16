package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class ModifyApplyHandleReq {

//	@NotBlank(message="车主memNo不能为空")
	@AutoDocProperty(value="车主memNo,必填，",required=true)
	private String memNo;
	
	@NotBlank(message="处理修改申请编码不能为空")
	@AutoDocProperty(value="modifyApplicationId,必填，",required=true)
	private String modifyApplicationId;
	
	@NotBlank(message="修改订单申请flag不能为空，1-同意，0-拒绝")
	private String flag;
}