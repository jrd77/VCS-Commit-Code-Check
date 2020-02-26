package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by qincai.lin on 2019/12/30.
 */
@Data
@ToString
public class ModificationOrderRequestVO implements Serializable {
	@NotBlank(message="订单编号不能为空")
	@AutoDocProperty(value="订单编号,必填，",required=true)
	private String orderNo;
	
//	@NotBlank(message="memNo不能为空")
//	@AutoDocProperty(value="memNo,必填，",required=true)
//	private String memNo;
	
//	@NotBlank(message="租客子订单编号不能为空")
//	@AutoDocProperty(value="租客子订单编号,必填，",required=true)
//	private String renterOrderNo;

}
