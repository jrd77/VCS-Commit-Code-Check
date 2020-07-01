package com.atzuche.order.photo.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
public class OrderPhotoDTO implements Serializable {
	@AutoDocProperty(value = "图片id")
	private String id;
	@AutoDocProperty(value = "图片路径")
	private String path;
	@AutoDocProperty(value = "用户上传类型,1:租客，2:车主,3:平台")
	private String userType;
	@AutoDocProperty(value = "用户上传类型描述")
	private String userTypeText;
	@AutoDocProperty(value = "订单号")
	private String orderNo;
	@AutoDocProperty(value = "创建时间")
	private String createTime;
	@AutoDocProperty(value = "操作人")
	private String operator;

	

}
