package com.atzuche.order.commons.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LianHeMaiTongMemberReqVO {
    private String phone;
    private String memNo;
    private String platNum;
    private Integer pageNum = 1;
    private Integer PageSize = 15;
}
