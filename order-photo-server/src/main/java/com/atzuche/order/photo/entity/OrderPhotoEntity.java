package com.atzuche.order.photo.entity;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class OrderPhotoEntity {
	private String id;
	private String photoType;
	private String path;
	private String userType;
	private String orderNo;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
	private String operator;
	private Integer serialNumber;


    public void setPhotoOperator(Integer photoUserType) {
        if (photoUserType.intValue() == 3) {
            setOperator("平台");
        } else {
            setOperator(photoUserType.intValue() == 1 ? "租客" : "车主");
        }
    }

	

}
