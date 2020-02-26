package com.atzuche.order.rentercost.entity.dto;

import lombok.Data;

@Data
public class GetFbcFeeRequestDetailDTO {
    private String channelName;
    private String requestTime;
    private String getReturnType;
    private String cityId;
    private String getReturnTime;
    private String orderType;
    private String distance;
    private String renterLocation;
    private String sumJudgeFreeFee;

}
