/**
 * 
 */
package com.atzuche.order.commons.vo.req;

import org.hibernate.validator.constraints.NotBlank;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * @author jing.huang
 *
 */
public class SupplementReqVO {
	@AutoDocProperty(value = "会员号")
    @NotBlank(message = "会员号不能为空")
    private String memNo;

	public String getMemNo() {
		return memNo;
	}
	
	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}
	
}
