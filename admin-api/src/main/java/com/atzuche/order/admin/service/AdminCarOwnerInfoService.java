package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.vo.resp.car.CarOwnerInfoRespVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryRespDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import com.atzuche.order.open.service.FeignMemberService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Slf4j
@Service
public class AdminCarOwnerInfoService {
    @Autowired
    FeignMemberService feignMemberService;
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

        OwnerMemberDTO ownerMemberDTO = remoteGetOwnerMember(orderNo, memNo);
        if(Objects.nonNull(ownerMemberDTO) && Objects.nonNull(ownerMemberDTO.getMemType())){
            respVO.setMemLevel(MemberFlagEnum.getRightByIndex(ownerMemberDTO.getMemType()).getRightName());
        }
        if(Objects.nonNull(ownerMemberDTO) && Objects.nonNull(ownerMemberDTO.getPhone())){
            respVO.setOwnerPhone(ownerMemberDTO.getPhone());
        }
        return respVO;
    }

    private OwnerMemberDTO remoteGetOwnerMember(String orderNo,String memNo){
        ResponseData<OwnerMemberDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取历史订单列表");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignMemberService.queryOwnerMemberByOrderNoAndOwnerNo");
            log.info("Feign 开始获取车主会员信息,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseObject =  feignMemberService.queryOwnerMemberByOrderNoAndOwnerNo(orderNo, memNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取车主会员信息异常,responseObject={},orderNo={},memNo={}", JSON.toJSONString(responseObject),orderNo,memNo,e);
            Cat.logError("Feign 获取车主会员信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

}
