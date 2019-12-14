package com.atzuche.order.coreapi.entity.dto;

import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.autoyol.member.detail.vo.res.MemberTotalInfo;
import lombok.Data;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:15
 * @Description: 下单操作全局的参数传递封装
 * 
 **/
@Data
public class OrderContextDto {
    private MemberTotalInfo ownerMemberInfo;//车主会员信息
    private MemberTotalInfo renterMemberInfo;//租客会员信息

    private SubmitOrderReq submitOrderReq;//订单信息

}
