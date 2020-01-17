/**
 * 
 */
package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class CommUseDriverInfoSimpleDTO {
	@AutoDocProperty(value = "主键id")
    private Integer id;

    @AutoDocProperty(value = "真实姓名")
    private String realName;

    @AutoDocProperty(value = "手机号")
    private String mobile;  //long修改为字符串类型
}
