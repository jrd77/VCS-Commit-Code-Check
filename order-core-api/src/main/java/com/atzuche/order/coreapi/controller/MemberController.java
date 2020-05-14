package com.atzuche.order.coreapi.controller;


import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/*
 * @Author ZhangBin
 * @Date 2020/4/28 11:11
 * @Description: 会员相关的类
 *
 **/
@RestController
@Slf4j
public class MemberController {
    @Autowired
    private OwnerMemberService ownerMemberService;
    @Autowired
    private RenterMemberService renterMemberService;
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 11:21
     * @Description: 根据订单号和车主会员号查询车主信息
     *
     **/
    @GetMapping("/member/owner/queryOwnerMemberByOrderNoAndOwnerNo")
    public ResponseData<OwnerMemberDTO> queryOwnerMemberByOrderNoAndOwnerNo(@RequestParam("orderNo") String orderNo, @RequestParam("ownerMemberNo")String ownerMemberNo){
        OwnerMemberEntity ownerMemberEntity = ownerMemberService.queryOwnerMemberEntityByOrderNoAndOwnerNo(orderNo, ownerMemberNo);
        OwnerMemberDTO ownerMemberDTO = null;
        if(ownerMemberEntity != null){
            ownerMemberDTO = new OwnerMemberDTO();
            BeanUtils.copyProperties(ownerMemberEntity,ownerMemberDTO);
        }
        return ResponseData.success(ownerMemberDTO);
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 11:21
     * @Description: 根据车主子订单号查询车主会员和车主权益信息
     *
     **/
    @GetMapping("/member/owner/queryOwnerMemberByOwnerOrderNo")
    public ResponseData<com.atzuche.order.commons.entity.dto.OwnerMemberDTO > queryOwnerMemberByOwnerOrderNo(@RequestParam("ownerOrderNo") String ownerOrderNo, @RequestParam("isNeedRight")boolean isNeedRight){
        com.atzuche.order.commons.entity.dto.OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderNo, isNeedRight);
        return ResponseData.success(ownerMemberDTO);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 11:21
     * @Description: 根据车主子订单号查询车主会员和车主权益信息
     *
     **/
    @GetMapping("/member/renter/queryRenterMemberByOwnerOrderNo")
    public ResponseData<com.atzuche.order.commons.entity.dto.RenterMemberDTO > queryRenterMemberByOwnerOrderNo(@RequestParam("renterOrderNo") String renterOrderNo, @RequestParam("isNeedRight")boolean isNeedRight){
        RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderNo, isNeedRight);
        return ResponseData.success(renterMemberDTO);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 14:43
     * @Description: 根据订单号获取会员号
     *
     **/
    @GetMapping("/member/renter/getRenterMemberByOrderNo")
    public ResponseData<String> getRenterMemberByOrderNo(@RequestParam("orderNo")String orderNo){
        String renterMember = renterMemberService.getRenterNoByOrderNo(orderNo);
        return ResponseData.success(renterMember);
    }
}
