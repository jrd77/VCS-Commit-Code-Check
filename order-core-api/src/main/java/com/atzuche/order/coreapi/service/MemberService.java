package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerMemberDto;
import com.atzuche.order.commons.entity.dto.OwnerMemberRightDto;
import com.atzuche.order.commons.entity.dto.RenterMemberDto;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDto;
import com.atzuche.order.commons.enums.OwnerMemRightEnum;
import com.atzuche.order.commons.enums.RenterMemRightEnum;
import com.atzuche.order.coreapi.entity.request.NormalOrderReqVO;
import com.atzuche.order.coreapi.enums.SubmitOrderErrorEnum;
import com.atzuche.order.coreapi.submitOrder.exception.OwnerberByFeignException;
import com.atzuche.order.coreapi.submitOrder.exception.RenterMemberByFeignException;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.member.detail.api.MemberDetailFeignService;
import com.autoyol.member.detail.enums.MemberSelectKeyEnum;
import com.autoyol.member.detail.vo.res.MemberAuthInfo;
import com.autoyol.member.detail.vo.res.MemberCoreInfo;
import com.autoyol.member.detail.vo.res.MemberRoleInfo;
import com.autoyol.member.detail.vo.res.MemberTotalInfo;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 2:18 下午
 **/
@Service
public class MemberService {
    private final static Logger log = LoggerFactory.getLogger(MemberService.class);
    
    @Autowired
    private MemberDetailFeignService memberDetailFeignService;

    //获取车主会员信息
    public OwnerMemberDto getOwnerMemberInfo(String memNo) throws RenterMemberByFeignException {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取车主会员信息,memNo={}",memNo);
        Transaction t = Cat.newTransaction("REMOTE-METHOD", "GET-REMOTE-MEM-INFO");
        try{
            responseData = memberDetailFeignService.getMemberSelectInfo(Integer.valueOf(memNo), selectKey);
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                log.error("Feign 获取车主会员信息失败,orderContextDto={},memNo={}",memNo, JSON.toJSONString(responseData));
                OwnerberByFeignException ownerberByFeignException = new OwnerberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_OWNER_MEMBER_FAIL.getCode(), SubmitOrderErrorEnum.FEIGN_GET_OWNER_MEMBER_FAIL.getText());
                Cat.logError("Feign 获取车主会员信息失败",ownerberByFeignException);
                t.setStatus(ownerberByFeignException);
                throw ownerberByFeignException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 获取车主会员信息失败",e);
            log.error("Feign 获取车主会员信息失败,orderContextDto={},memNo={}",memNo,e);
            throw new OwnerberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_OWNER_MEMBER_ERROR.getCode(),SubmitOrderErrorEnum.FEIGN_GET_OWNER_MEMBER_ERROR.getText());
        }finally {
            t.complete();
        }


        MemberTotalInfo memberTotalInfo = responseData.getData();
        MemberCoreInfo memberCoreInfo = memberTotalInfo.getMemberCoreInfo();
        OwnerMemberDto ownerMemberDto = new OwnerMemberDto();
        ownerMemberDto.setMemNo(memNo);
        ownerMemberDto.setPhone(memberCoreInfo.getPhone());
        ownerMemberDto.setHeaderUrl(memberCoreInfo.getPortraitPath());
        ownerMemberDto.setRealName(memberCoreInfo.getRealName());
        ownerMemberDto.setNickName(memberCoreInfo.getNickName());
        List<OwnerMemberRightDto> rights = new ArrayList<>();
        MemberRoleInfo memberRoleInfo = memberTotalInfo.getMemberRoleInfo();
        if(memberRoleInfo != null){
            if(memberRoleInfo.getInternalStaff()!=null){
                OwnerMemberRightDto internalStaff = new OwnerMemberRightDto();
                internalStaff.setRightCode(OwnerMemRightEnum.STAFF.getRightCode());
                internalStaff.setRightName(OwnerMemRightEnum.STAFF.getRightName());
                //internalStaff.setRightValue(String.valueOf(memberRoleInfo.getInternalStaff()));
                internalStaff.setRightDesc("是否是内部员工");
                rights.add(internalStaff);
            }
            if(memberRoleInfo.getMemberFlag() != null){
                OwnerMemberRightDto internalStaff = new OwnerMemberRightDto();
                internalStaff.setRightCode(OwnerMemRightEnum.VIP.getRightCode());
                internalStaff.setRightName(OwnerMemRightEnum.VIP.getRightName());
                //internalStaff.setRightValue(String.valueOf(memberRoleInfo.getMemberFlag()));
                internalStaff.setRightDesc("会员标识");
                rights.add(internalStaff);
            }
            if(memberRoleInfo.getCpicMemberFlag() != null){
                OwnerMemberRightDto internalStaff = new OwnerMemberRightDto();
                internalStaff.setRightCode(OwnerMemRightEnum.CPIC_MEM.getRightCode());
                internalStaff.setRightName(OwnerMemRightEnum.CPIC_MEM.getRightName());
                //internalStaff.setRightValue(String.valueOf(memberRoleInfo.getCpicMemberFlag()));
                internalStaff.setRightDesc("是否太保会员");
                rights.add(internalStaff);
            }
        }
        ownerMemberDto.setOwnerMemberRightDtoList(rights);
        return ownerMemberDto;
    }

    //获取租客会员信息
    public RenterMemberDto getRenterMemberInfo(String memNo) throws RenterMemberByFeignException {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_AUTH_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取租客会员信息,memNo={}",memNo);
        Transaction t = Cat.newTransaction("REMOTE-METHOD", "GET-REMOTE-MEM-INFO");
        try{
            responseData = memberDetailFeignService.getMemberSelectInfo(Integer.parseInt(memNo), selectKey);
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode()) || responseData.getData() == null){
                log.error("Feign 获取租客会员信息失败,memNo={},responseData={}",memNo,JSON.toJSONString(responseData));
                RenterMemberByFeignException renterMemberByFeignException = new RenterMemberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_RENTER_MEMBER_FAIL.getCode(), SubmitOrderErrorEnum.FEIGN_GET_RENTER_MEMBER_FAIL.getText());
                Cat.logError("Feign 获取租客会员信息失败",renterMemberByFeignException);
                throw renterMemberByFeignException;
            }
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取租客会员信息失败,submitReqDto={},memNo={}",memNo,null,e);
            RenterMemberByFeignException renterMemberByFeignException = new RenterMemberByFeignException(SubmitOrderErrorEnum.FEIGN_GET_RENTER_MEMBER_ERROR.getCode(), SubmitOrderErrorEnum.FEIGN_GET_RENTER_MEMBER_ERROR.getText());
            Cat.logError("Feign 获取租客会员信息失败",renterMemberByFeignException);
            t.setStatus(e);
            throw renterMemberByFeignException;
        }finally {
            t.complete();
        }

        MemberTotalInfo memberTotalInfo = responseData.getData();
        MemberAuthInfo memberAuthInfo = memberTotalInfo.getMemberAuthInfo();
        MemberCoreInfo memberCoreInfo = memberTotalInfo.getMemberCoreInfo();
        RenterMemberDto renterMemberDto = new RenterMemberDto();
        renterMemberDto.setMemNo(memNo);
        renterMemberDto.setPhone(memberCoreInfo.getPhone());
        renterMemberDto.setHeaderUrl(memberCoreInfo.getPortraitPath());
        renterMemberDto.setRealName(memberCoreInfo.getRealName());
        renterMemberDto.setNickName(memberCoreInfo.getNickName());
        renterMemberDto.setCertificationTime(LocalDateTimeUtils.parseStringToLocalDate(memberAuthInfo.getDriLicFirstTime()));
        //renterMemberDto.setOrderSuccessCount();
        List<RenterMemberRightDto> rights = new ArrayList<>();
        MemberRoleInfo memberRoleInfo = memberTotalInfo.getMemberRoleInfo();
        if(memberRoleInfo != null){
            if(memberRoleInfo.getInternalStaff()!=null){
                RenterMemberRightDto internalStaff = new RenterMemberRightDto();
                internalStaff.setRightCode(RenterMemRightEnum.STAFF.getRightCode());
                internalStaff.setRightName(RenterMemRightEnum.STAFF.getRightName());
                internalStaff.setRightDesc("是否是内部员工");
                rights.add(internalStaff);
            }
            if(memberRoleInfo.getMemberFlag() != null){
                RenterMemberRightDto internalStaff = new RenterMemberRightDto();
                internalStaff.setRightCode(RenterMemRightEnum.VIP.getRightCode());
                internalStaff.setRightName(RenterMemRightEnum.VIP.getRightName());
                internalStaff.setRightDesc("会员标识");
                rights.add(internalStaff);
            }
            if(memberRoleInfo.getCpicMemberFlag() != null){
                RenterMemberRightDto internalStaff = new RenterMemberRightDto();
                internalStaff.setRightCode(RenterMemRightEnum.CPIC_MEM.getRightCode());
                internalStaff.setRightName(RenterMemRightEnum.CPIC_MEM.getRightName());
                internalStaff.setRightDesc("是否太保会员");
                rights.add(internalStaff);
            }
        }
        renterMemberDto.setRenterMemberRightDtoList(rights);
        return renterMemberDto;
    }
}
