package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.resp.car.CarOwnerInfoRespVO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class AdminCarOwnerInfoService {
    @Autowired
    private RemoteFeignService remoteFeignService;
    @Autowired
    private MemProxyService memProxyService;

    public CarOwnerInfoRespVO getCarOwnerInfo(String orderNo,String memNo){
        log.info("AdminCarOwnerInfoService.getCarOwnerInfo() --> order={},memNo={}",orderNo,memNo);
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

        OwnerMemberDTO ownerMemberDTO = remoteFeignService.remoteGetOwnerMember(orderNo, memNo);
        if(Objects.nonNull(ownerMemberDTO) && Objects.nonNull(ownerMemberDTO.getMemType())){
            respVO.setMemLevel(MemberFlagEnum.getRightByIndex(ownerMemberDTO.getMemType()).getRightName());
        }
        if(Objects.nonNull(ownerMemberDTO) && Objects.nonNull(ownerMemberDTO.getPhone())){
            respVO.setOwnerPhone(ownerMemberDTO.getPhone());
        }
        return respVO;
    }



}
