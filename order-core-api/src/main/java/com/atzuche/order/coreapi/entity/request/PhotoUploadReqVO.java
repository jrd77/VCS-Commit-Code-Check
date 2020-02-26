package com.atzuche.order.coreapi.entity.request;

import lombok.Data;

/**
 * PhotoUploadReqVO
 *
 * @author shisong
 * @date 2019/12/31
 */
@Data
public class PhotoUploadReqVO {

    private String photoContent;

    private String picKey;

    private String serialNumber;

    private String userType;

    private String orderNo;

    private Integer memNo;

}
