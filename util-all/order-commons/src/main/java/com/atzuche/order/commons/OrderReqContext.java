package com.atzuche.order.commons;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import lombok.Data;

/**
 * 下单订单请求上下午
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 7:32 下午
 **/
@Data
public class OrderReqContext {

    /**
     * 车主会员信息
     */
    private OwnerMemberDTO ownerMemberDto;
    /**
     * 租客会员信息
     */
    private RenterMemberDTO renterMemberDto;
    /**
     * 车主商品信息
     **/
    private OwnerGoodsDetailDTO ownerGoodsDetailDto;
    /**
     * 租客商品信息
     */
    private RenterGoodsDetailDTO renterGoodsDetailDto;
    /**
     * 提交订单请求信息
     */
    private NormalOrderReqVO normalOrderReqVO;

}
