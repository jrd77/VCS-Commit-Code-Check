package com.atzuche.order.coreapi.entity.request;

import lombok.Data;

/**
 * IllegalAppealReqVO
 *
 * @author shisong
 * @date 2020/1/15
 */
@Data
public class IllegalAppealReqVO {

    private String orderNo;
    private String illegalNum;
    private String appealContent;
    private String token;

}
