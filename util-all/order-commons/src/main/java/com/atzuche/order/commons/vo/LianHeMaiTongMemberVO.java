package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class LianHeMaiTongMemberVO {

    @AutoDocProperty("车主姓名")
    private String ownerName;
    @AutoDocProperty("车主会员号")
    private String ownerMemNo;
    @AutoDocProperty("租客姓名")
    private String renterName;
    @AutoDocProperty("租客会员号")
    private String renterMemNo;

    @AutoDocProperty("订单列表信息")
    List<LianHeMaiTongOrderVO> lianHeMaiTongOrderVOS;
}
