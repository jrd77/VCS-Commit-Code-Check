package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.vo.req.OwnerUpdateSeeVO;
import com.atzuche.order.commons.vo.req.RenterAndOwnerSeeOrderVO;
import com.atzuche.order.coreapi.service.OrderBusinessService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
public class OrderBusinessController {
    @Autowired
    private OrderBusinessService orderBusinessService;
    @Autowired
    private OrderService orderService;
    /*
     * @Author ZhangBin
     * @Date 2020/3/11 18:23
     * @Description: 跟心是否查看标记
     *
     **/
    @PostMapping("/orderBusiness/renterAndOwnerSeeOrder")
    public ResponseData<?> renterAndOwnerSeeOrder(@RequestBody @Valid RenterAndOwnerSeeOrderVO renterAndOwnerSeeOrderVO, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);
        OrderEntity orderEntity = orderService.getOrderEntity(renterAndOwnerSeeOrderVO.getOrderNo());
        if(orderEntity == null){
            log.error("订单号不存在orderNo={}",renterAndOwnerSeeOrderVO.getOrderNo());
            return ResponseData.error();
        }
        orderBusinessService.renterAndOwnerSeeOrder(renterAndOwnerSeeOrderVO);
        return ResponseData.success();
    }
    /*
     * @Author ZhangBin
     * @Date 2020/3/11 18:22
     * @Description: 根据车主会员号更新是否关闭小红点
     *
     **/
    @PostMapping("orderBusiness/ownerUpdateSee")
    public ResponseData<?>  ownerUpdateSee(@RequestBody @Valid OwnerUpdateSeeVO ownerUpdateSeeVO, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);
        orderBusinessService.ownerUpdateSee(ownerUpdateSeeVO);
        return ResponseData.success();
    }
    /*
     * @Author ZhangBin
     * @Date 2020/3/11 18:22
     * @Description: 根据订单号查询车主会员信息
     *
     **/
    @GetMapping("/orderBusiness/queryOwnerMemDetail")
    public ResponseData<OwnerMemberDTO> queryOwnerMemDetail(@RequestParam(name = "orderNo",required = true) String orderNo){
        OwnerMemberDTO ownerMemberDTO = orderBusinessService.queryOwnerMemDetail(orderNo);
        return ResponseData.success(ownerMemberDTO);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/3/11 18:22
     * @Description: 根据订单号查询车租客会员信息
     *
     **/
    @GetMapping("/orderBusiness/queryRenterMemDetail")
    public ResponseData<RenterMemberDTO> queryRenterMemDetail(@RequestParam(name = "orderNo",required = true) String orderNo){
        RenterMemberDTO renterMemberDTO = orderBusinessService.queryRenterMemDetail(orderNo);
        return ResponseData.success(renterMemberDTO);
    }
}
