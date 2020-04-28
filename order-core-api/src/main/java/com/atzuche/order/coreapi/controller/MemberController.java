package com.atzuche.order.coreapi.controller;


import com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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


    /*
     * @Author ZhangBin
     * @Date 2020/4/28 11:21
     * @Description: 根据订单号和车主会员号查询车主信息
     *
     **/
    @GetMapping("/member/owner/queryOwnerMemberByOrderNoAndOwnerNo")
    public ResponseData<OwnerMemberDTO> queryOwnerMemberByOrderNoAndOwnerNo(@Param("orderNo") String orderNo, @Param("ownerMemberNo")String ownerMemberNo){
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
    public ResponseData<com.atzuche.order.commons.entity.dto.OwnerMemberDTO > queryOwnerMemberByOwnerOrderNo(@Param("ownerOrderNo") String ownerOrderNo, @Param("isNeedRight")boolean isNeedRight){
        com.atzuche.order.commons.entity.dto.OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderNo, isNeedRight);
        return ResponseData.success(ownerMemberDTO);
    }
}
