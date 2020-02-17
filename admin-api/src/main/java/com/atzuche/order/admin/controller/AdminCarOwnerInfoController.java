package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.resp.car.CarOwnerInfoRespVO;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RequestMapping("/console/carOwner")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看车主信息接口文档")
public class AdminCarOwnerInfoController {
    private final static Logger logger = LoggerFactory.getLogger(AdminCarOwnerInfoController.class);
    

    @Autowired
    private MemProxyService memProxyService;

    @Autowired
    private CarProxyService carProxyService;
    @Autowired OwnerMemberService ownerMemberService;


    @AutoDocMethod(description = "查看车主信息接口信息", value = "查看车主信息接口信息", response = CarOwnerInfoRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<?> getCarOwnerInfo(@RequestParam("orderNo")String orderNo,@RequestParam("memNo")String memNo) {
        logger.info("reqVo is order={},memNo={}",orderNo,memNo);
        OrderRenterInfoDTO orderRenterInfoDTO =  memProxyService.getRenterInfoByMemNo(memNo);
        CarOwnerInfoRespVO respVO = new CarOwnerInfoRespVO();
        respVO.setRealName(orderRenterInfoDTO.getRealName());
        respVO.setCity(orderRenterInfoDTO.getCity());
        respVO.setDriLicRecordNo(orderRenterInfoDTO.getDriLicRecordNo());
        respVO.setEmail(orderRenterInfoDTO.getEmail());
        respVO.setGender(orderRenterInfoDTO.getGender());
        respVO.setIdNo(orderRenterInfoDTO.getIdNo());
        respVO.setOwnerNo(orderRenterInfoDTO.getMemNo());
        respVO.setProvince(orderRenterInfoDTO.getProvince());

        //车主等级
        OwnerMemberEntity ownerMemberEntity = ownerMemberService.queryOwnerMemberEntityByOrderNoAndOwnerNo(orderNo,memNo);
        if(Objects.nonNull(ownerMemberEntity) && Objects.nonNull(ownerMemberEntity.getMemType())){
            respVO.setMemLevel(MemberFlagEnum.getRightByIndex(ownerMemberEntity.getMemType()).getRightName());
        }
        if(Objects.nonNull(ownerMemberEntity) && Objects.nonNull(ownerMemberEntity.getPhone())){
            respVO.setOwnerPhone(ownerMemberEntity.getPhone());
        }
        return ResponseData.success(respVO);
    }

}
