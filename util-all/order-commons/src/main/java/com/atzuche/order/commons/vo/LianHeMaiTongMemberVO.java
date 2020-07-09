package com.atzuche.order.commons.vo;

import com.autoyol.commons.utils.Page;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class LianHeMaiTongMemberVO {

    @AutoDocProperty("姓名")
    private String name;
    @AutoDocProperty("会员号")
    private String memNo;
    @AutoDocProperty("手机号")
    private String phone;
    @AutoDocProperty("短租订单详情地址")
    private String ordinaryDetailUrl = "/system/transaction/order/ordinary/detail/orderInfo/{ORDER_NO}";
    @AutoDocProperty("长租订单详情地址")
    private String longDetailUrl = "/system/transaction/order/longrent/detail/orderInfo/{ORDER_NO}";

    @AutoDocProperty("订单列表信息")
    Page<LianHeMaiTongOrderVO> page;
}
