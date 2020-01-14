package com.atzuche.order.photo.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class OrderPhotoDTO {
	private Long id;
	private int photoType;
	private String path;
	private int userType;
	private Long ordeNo;
	private Date createTime;
	private Date updateTime;
	private String operator;

	

}
