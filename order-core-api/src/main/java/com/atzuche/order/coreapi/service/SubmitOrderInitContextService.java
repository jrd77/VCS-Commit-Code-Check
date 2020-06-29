package com.atzuche.order.coreapi.service;

import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.service.remote.CarRentalTimeApiProxyService;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 订单请求参数context 初始化处理类
 *
 * @author pengcheng.fu
 * @date 2020/4/13 10:22
 */

@Service
public class SubmitOrderInitContextService {

    @Autowired
    private MemProxyService memberService;
    @Autowired
    private CarProxyService goodsService;
    @Autowired
    private RenterCommodityService renterCommodityService;
    @Autowired
    private CarRentalTimeApiProxyService carRentalTimeApiService;
    @Autowired
    private OrderCommonConver orderCommonConver;


    /**
     * 构建请求参数上下文
     *
     * @param orderReqVO 下单请求参数
     * @return OrderReqContext
     */
    public OrderReqContext convertOrderReqContext(OrderReqVO orderReqVO) {
        //1.请求参数处理
        OrderReqContext reqContext = new OrderReqContext();
        orderReqVO.setReqTime(LocalDateTime.now());
        reqContext.setOrderReqVO(orderReqVO);
        //租客会员信息
        RenterMemberDTO renterMemberDTO =
                memberService.getRenterMemberInfo(orderReqVO.getMemNo());
        reqContext.setRenterMemberDto(renterMemberDTO);
        //租客商品明细
        RenterGoodsDetailDTO renterGoodsDetailDTO =
                goodsService.getRenterGoodsDetail(orderCommonConver.buildCarDetailReqVO(orderReqVO));
        reqContext.setRenterGoodsDetailDto(renterGoodsDetailDTO);

        //一天一价分组
        renterGoodsDetailDTO = renterCommodityService.setPriceAndGroup(renterGoodsDetailDTO);

        //车主商品明细
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = goodsService.getOwnerGoodsDetail(renterGoodsDetailDTO);
        reqContext.setOwnerGoodsDetailDto(ownerGoodsDetailDTO);

        //车主会员信息
        OwnerMemberDTO ownerMemberDTO = memberService.getOwnerMemberInfo(renterGoodsDetailDTO.getOwnerMemNo());
        reqContext.setOwnerMemberDto(ownerMemberDTO);

        //提前延后时间计算
        CarRentTimeRangeDTO carRentTimeRangeResVO =
                carRentalTimeApiService.getCarRentTimeRange(carRentalTimeApiService.buildCarRentTimeRangeReqVO(orderReqVO));
        reqContext.setCarRentTimeRangeDTO(carRentTimeRangeResVO);
        return reqContext;
    }

}
