package com.atzuche.order.commons.entity.dto;

import com.atzuche.order.commons.entity.request.SubmitOrderReqVO;
import lombok.Data;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 15:15
 * @Description: 下单操作全局的参数传递封装
 * 
 **/
@Data
public class OrderContextDTO {
    private OwnerMemberDTO ownerMemberDto;//车主会员信息
    private RenterMemberDTO renterMemberDto;//租客会员信息
    private OwnerGoodsDetailDTO ownerGoodsDetailDto;//车主商品信息
    private RenterGoodsDetailDTO renterGoodsDetailDto;//租客商品信息
    private SubmitOrderReqVO submitOrderReqVO;//订单信息
    private String orderNo;//主订单号
}
