package com.atzuche.order.photo.vo.req;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author 胡春林
 * 仁云交接车照片
 */
@Data
@ToString
public class DeliveryCarConditionPhotoVO {

    private Integer id;
    private String path;
    private Integer userType;
    private Integer serialNumber;
    private Long orderNo;
    private Long photoType;
    private Date createTime;
    private Date updateTime;
}
