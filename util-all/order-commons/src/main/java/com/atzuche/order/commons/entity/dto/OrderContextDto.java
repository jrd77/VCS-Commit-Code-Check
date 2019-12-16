package com.atzuche.order.commons.entity.dto;

import com.atzuche.order.commons.entity.request.SubmitOrderReq;
import lombok.Data;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:15
 * @Description: 下单操作全局的参数传递封装
 * 
 **/
@Data
public class OrderContextDto {
//    private OwnerMemberDto ownerMemberDto;//车主会员信息
////    private RenterMemberDto renterMemberDto;//租客会员信息
    private CarDetailDto carDetailDto;//车辆信息
    private SubmitOrderReq submitOrderReq;//订单信息
    private long orderNo;//主订单号
}
