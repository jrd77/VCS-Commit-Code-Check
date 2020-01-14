package com.atzuche.order.photo.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class OrderPhotoDTO {
	@AutoDocProperty(value = "图片id")
	private String id;
	@AutoDocProperty(value = "图片路径")
	private String path;
	@AutoDocProperty(value = "用户上传类型")
	private String userTypeText;
	@AutoDocProperty(value = "订单号")
	private String orderNo;
	@AutoDocProperty(value = "创建时间")
	private String createTime;
	@AutoDocProperty(value = "操作人")
	private String operator;

	

}
