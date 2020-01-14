package com.atzuche.order.photo.entity;

import lombok.Data;
import lombok.ToString;

/**
 * Created by tao.sun on 2018/2/1.
 */
@Data
@ToString
public class PhotoPathDTO {
    private String userType; //用户类型: 1-租客，2-车主，3-平台
    private String img; //图片地址

}
