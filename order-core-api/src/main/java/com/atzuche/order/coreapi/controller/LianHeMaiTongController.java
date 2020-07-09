package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.vo.LianHeMaiTongMemberReqVO;
import com.atzuche.order.commons.vo.LianHeMaiTongMemberVO;
import com.atzuche.order.commons.vo.LianHeMaiTongOrderVO;
import com.atzuche.order.coreapi.service.LianHeMaiTongService;
import com.autoyol.commons.utils.Page;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/lianHeMaiTong")
@AutoDocVersion(version = "联合麦通接口文档")
public class LianHeMaiTongController {
    @Autowired
    private LianHeMaiTongService lianHeMaiTongService;

    @AutoDocMethod(description = "联合麦通接口文档会员信息查询", value = "联合麦通接口文档会员信息查询", response = LianHeMaiTongMemberVO.class)
    @RequestMapping("/getMemberInfo")
    public ResponseData<LianHeMaiTongMemberVO> getMemberInfo(LianHeMaiTongMemberReqVO lianHeMaiTongMemberReqVO){
        LianHeMaiTongMemberVO lianHeMaiTongMemberVO = lianHeMaiTongService.getMemberInfo(lianHeMaiTongMemberReqVO);
        return ResponseData.success(lianHeMaiTongMemberVO);
    }
}
