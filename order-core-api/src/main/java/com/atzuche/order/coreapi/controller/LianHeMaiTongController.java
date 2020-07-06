package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.vo.LianHeMaiTongMemberVO;
import com.atzuche.order.coreapi.service.LianHeMaiTongService;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/lianHeMaiTong")
@AutoDocVersion(version = "联合麦通接口文档")
public class LianHeMaiTongController {
    @Autowired
    private LianHeMaiTongService lianHeMaiTongService;

    @AutoDocMethod(description = "会员信息查询", value = "会员信息查询", response = LianHeMaiTongMemberVO.class)
    @RequestMapping("/getMemberInfo")
    public ResponseData<LianHeMaiTongMemberVO> getMemberInfo(String phone,String memNo,String platNum){
        LianHeMaiTongMemberVO lianHeMaiTongMemberVO = lianHeMaiTongService.getMemberInfo(phone, memNo, platNum);
        return ResponseData.success(lianHeMaiTongMemberVO);
    }
}
