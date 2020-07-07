package com.atzuche.order.mem;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.commons.enums.MemRightEnum;
import com.atzuche.order.commons.enums.RightTypeEnum;
import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.member.detail.api.MemberDetailFeignService;
import com.autoyol.member.detail.enums.MemberSelectKeyEnum;
import com.autoyol.member.detail.vo.res.*;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 封装对远程会员详情服务的相关调用
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 2:18 下午
 **/
@Service
public class MemProxyService {
    private final static Logger log = LoggerFactory.getLogger(MemProxyService.class);

    @Autowired
    private MemberDetailFeignService memberDetailFeignService;

    public OrderRenterInfoDTO getRenterInfoByMemNo(String memNo){
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_AUTH_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ADDITION_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_STATISTICS_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_SECRET_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_RELIEF_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取租客会员信息,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "会员详情服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"MemberDetailFeignService.getMemberSelectInfo");
            String parameter = "memNo="+memNo+"&selectKey"+JSON.toJSONString(selectKey);
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            log.info("MemProxyService getRenterInfoByMemNo start param  [{}]", memNo);
            responseData = memberDetailFeignService.getMemberSelectInfo(Integer.parseInt(memNo), selectKey);
            log.info("MemProxyService getRenterInfoByMemNo end result [{}] [{}]", GsonUtils.toJson(responseData),memNo);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取租客会员信息失败,submitReqDto={},memNo={}",memNo,null,e);
            Cat.logError("Feign 获取租客会员信息失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }

        MemberTotalInfo memberTotalInfo = responseData.getData();
        log.info("memInfo is {}",JSON.toJSONString(memberTotalInfo));
        MemberAuthInfo memberAuthInfo = memberTotalInfo.getMemberAuthInfo();
        MemberCoreInfo memberCoreInfo = memberTotalInfo.getMemberCoreInfo();
        MemberBaseInfo memberBaseInfo = memberTotalInfo.getMemberBaseInfo();
        MemberAdditionInfo memberAdditionInfo = memberTotalInfo.getMemberAdditionInfo();
        MemberStatisticsInfo memberStatisticsInfo = memberTotalInfo.getMemberStatisticsInfo();
        MemberRoleInfo memberRoleInfo = memberTotalInfo.getMemberRoleInfo();
        MemberSecretInfo secretInfo = memberTotalInfo.getMemberSecretInfo();

        OrderRenterInfoDTO dto = new OrderRenterInfoDTO();
        dto.setMemNo(memNo);
        dto.setIdNo(secretInfo.getIdNo());
        dto.setAdditionalDrivers(memberAdditionInfo.getCommUseDriverList());
        dto.setBuyTimes(String.valueOf(memberStatisticsInfo.getSuccessOrderNum()));
        dto.setUpgrades("0");
        if(memberBaseInfo != null){
            dto.setGender(convertGender(memberBaseInfo.getGender()));
            dto.setCensusRegiste(memberBaseInfo.getCensusRegiste());
            dto.setCity(memberBaseInfo.getCity());
            dto.setProvince(memberBaseInfo.getProvince());
        }
        if(memberCoreInfo!=null){
            dto.setRealName(memberCoreInfo.getRealName());
            dto.setRenterPhone(String.valueOf(memberCoreInfo.getMobile()));
            dto.setRegTimeTxt(convertTime(memberCoreInfo.getRegTime()));
            dto.setRepeatTimeOrder(memberCoreInfo.getRepeatTimeOrder());
        }
        if(memberAuthInfo != null){
            dto.setEmail(memberAuthInfo.getEmail());
            dto.setDriLicRecordNo(memberAuthInfo.getDriLicRecordNo());
            dto.setDriveAge(convertAge(memberAuthInfo.getDriLicFirstTime()));
        }
        if(memberRoleInfo != null){
            dto.setInternalStaff(convertYesOrNo(memberRoleInfo.getInternalStaff()));
            dto.setCpicFlag(convertYesOrNo(memberRoleInfo.getCpicMemberFlag()));
        }
        return dto;
    }

    private String convertAge(String driLicFirstTime) {
        if(driLicFirstTime==null){
            return "0";
        }else{
            try{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(driLicFirstTime,formatter);
                long between1 = ChronoUnit.DAYS.between(date, LocalDate.now());
                return String.valueOf(between1/365);
            }catch (Exception e){
                return "0";
            }
        }
    }

    private String convertTime(Date regTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        return dateFormat.format(regTime);
    }

    private String convertYesOrNo(Integer internalStaff) {
        if(internalStaff!=null&&internalStaff==1){
            return "1";
        }
        return "0";
    }

    private String convertGender(Integer gender) {
        if(gender!=null&&gender==1){
            return "1";
        }
        return "2";
    }


    /**
     * 返回用户的会员号
     * @param mobile
     * @return
     */
    public Integer getMemNoByMoile(String mobile){
        ResponseData<Integer> responseData = null;
        log.info("Feign 开始获取车主会员信息,mobile={}",mobile);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "会员详情服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"MemberDetailFeignService.getMemNoByMobile");
            String parameter = "mobile="+mobile;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = memberDetailFeignService.getMemNoByMobile(Long.parseLong(mobile));
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();
        }
        catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 获取车主会员信息失败",e);
            log.error("Feign 获取车主会员信息失败,Response={},mobile={}",responseData,mobile,e);
            throw e;
        }finally {
            t.complete();
        }
    }



    //获取车主会员信息
    public OwnerMemberDTO getOwnerMemberInfo(String memNo)   {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ADDITION_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_STATISTICS_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_RELIEF_INFO.getKey(),MemberSelectKeyEnum.MEMBER_SECRET_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取车主会员信息,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "会员详情服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"MemberDetailFeignService.getMemberSelectInfo");
            String parameter = "memNo="+memNo+"&selectKey"+ JSON.toJSONString(selectKey);
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = memberDetailFeignService.getMemberSelectInfo(Integer.valueOf(memNo), selectKey);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }
        catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 获取车主会员信息失败",e);
            log.error("Feign 获取车主会员信息失败,Resonponse={},memNo={}",responseData,memNo,e);
            throw e;
        }finally {
            t.complete();
        }

        MemberTotalInfo memberTotalInfo = responseData.getData();
        MemberCoreInfo memberCoreInfo = memberTotalInfo.getMemberCoreInfo();
        MemberStatisticsInfo memberStatisticsInfo = memberTotalInfo.getMemberStatisticsInfo();
        OwnerMemberDTO ownerMemberDto = new OwnerMemberDTO();
        ownerMemberDto.setMemNo(memNo);
        ownerMemberDto.setPhone(memberCoreInfo.getMobile()==null ? "" : String.valueOf(memberCoreInfo.getMobile()));
        ownerMemberDto.setHeaderUrl(memberCoreInfo.getPortraitPath());
        ownerMemberDto.setRealName(memberCoreInfo.getRealName());
        ownerMemberDto.setNickName(memberCoreInfo.getNickName());
        ownerMemberDto.setOrderSuccessCount(memberStatisticsInfo.getSuccessOrderNum());
        ownerMemberDto.setHaveCar(null != memberTotalInfo.getMemberBaseInfo() ? memberTotalInfo.getMemberBaseInfo().getHaveCar() : 0);
        ownerMemberDto.setMemType(null != memberTotalInfo.getMemberRoleInfo() ? memberTotalInfo.getMemberRoleInfo().getMemberType() : null);
        ownerMemberDto.setIdNo(memberTotalInfo.getMemberSecretInfo().getIdNo());
        List<OwnerMemberRightDTO> rights = new ArrayList<>();
        MemberRoleInfo memberRoleInfo = memberTotalInfo.getMemberRoleInfo();

        if(memberRoleInfo != null){
            if(memberRoleInfo.getInternalStaff()!=null){
                OwnerMemberRightDTO internalStaff = new OwnerMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.STAFF.getRightCode());
                internalStaff.setRightName(MemRightEnum.STAFF.getRightName());
                internalStaff.setRightValue(String.valueOf(memberRoleInfo.getInternalStaff()));
                internalStaff.setRightType(RightTypeEnum.STAFF.getCode());
                internalStaff.setRightDesc("是否是内部员工");
                rights.add(internalStaff);
            }
            if(memberRoleInfo.getMemberFlag() != null){
                OwnerMemberRightDTO internalStaff = new OwnerMemberRightDTO();
                MemberFlagEnum memberFlagEnum = MemberFlagEnum.getRightByIndex(memberRoleInfo.getMemberFlag());
                internalStaff.setRightCode(memberFlagEnum.getRightCode());
                internalStaff.setRightName(memberFlagEnum.getRightName());
                internalStaff.setRightValue("1");
                internalStaff.setRightType(RightTypeEnum.MEMBER_FLAG.getCode());
                internalStaff.setRightDesc("会员标识");
                rights.add(internalStaff);
            }
            if(memberRoleInfo.getCpicMemberFlag() != null){
                OwnerMemberRightDTO internalStaff = new OwnerMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.CPIC_MEM.getRightCode());
                internalStaff.setRightName(MemRightEnum.CPIC_MEM.getRightName());
                internalStaff.setRightValue(String.valueOf(memberRoleInfo.getCpicMemberFlag()));
                internalStaff.setRightType(RightTypeEnum.CPIC.getCode());
                internalStaff.setRightDesc("是否太保会员");
                rights.add(internalStaff);
            }
        }
        MemberReliefInfo memberReliefInfo = memberTotalInfo.getMemberReliefInfo();
        if(memberReliefInfo != null){
            WxBindingTaskInfo wxBindingTaskInfo = memberReliefInfo.getWxBindingTaskInfo();
            if(wxBindingTaskInfo !=null){
                OwnerMemberRightDTO internalStaff = new OwnerMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.BIND_WECHAT.getRightCode());
                internalStaff.setRightName(MemRightEnum.BIND_WECHAT.getRightName());
                internalStaff.setRightValue(wxBindingTaskInfo.getReliefPercentage()==null?"0":String.valueOf(wxBindingTaskInfo.getReliefPercentage()));
                internalStaff.setRightDesc(wxBindingTaskInfo.getTitle());
                internalStaff.setRightType(RightTypeEnum.TASK.getCode());
                rights.add(internalStaff);
            }
            MemberLevelTaskInfo memberLevelTaskInfo = memberReliefInfo.getMemberLevelTaskInfo();
            if(memberLevelTaskInfo != null){
                OwnerMemberRightDTO internalStaff = new OwnerMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.MEMBER_LEVEL.getRightCode());
                internalStaff.setRightName(MemRightEnum.MEMBER_LEVEL.getRightName());
                internalStaff.setRightValue(memberLevelTaskInfo.getReliefPercentage()==null?"0":String.valueOf(memberLevelTaskInfo.getReliefPercentage()));
                internalStaff.setRightDesc(memberLevelTaskInfo.getTitle());
                internalStaff.setRightType(RightTypeEnum.TASK.getCode());
                rights.add(internalStaff);
            }
            InvitationTaskInfo invitationTaskInfo = memberReliefInfo.getInvitationTaskInfo();
            if(invitationTaskInfo != null){
                OwnerMemberRightDTO internalStaff = new OwnerMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.INVITE_FRIENDS.getRightCode());
                internalStaff.setRightName(MemRightEnum.INVITE_FRIENDS.getRightName());
                internalStaff.setRightValue(invitationTaskInfo.getReliefPercentage()==null?"0":String.valueOf(invitationTaskInfo.getReliefPercentage()));
                internalStaff.setRightDesc(invitationTaskInfo.getTitle());
                internalStaff.setRightType(RightTypeEnum.TASK.getCode());
                rights.add(internalStaff);
            }
            RentCarTaskInfo rentCarTaskInfo = memberReliefInfo.getRentCarTaskInfo();
            if(rentCarTaskInfo != null){
                OwnerMemberRightDTO internalStaff = new OwnerMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.SUCCESS_RENTCAR.getRightCode());
                internalStaff.setRightName(MemRightEnum.SUCCESS_RENTCAR.getRightName());
                internalStaff.setRightValue(rentCarTaskInfo.getReliefPercentage()==null?"0":String.valueOf(rentCarTaskInfo.getReliefPercentage()));
                internalStaff.setRightDesc(rentCarTaskInfo.getTitle());
                internalStaff.setRightType(RightTypeEnum.TASK.getCode());
                rights.add(internalStaff);
            }
        }
        ownerMemberDto.setOwnerMemberRightDTOList(rights);
        return ownerMemberDto;
    }

    //获取租客会员信息
    public RenterMemberDTO getRenterMemberInfo(String memNo)  {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_AUTH_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_BASE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ROLE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ADDITION_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_STATISTICS_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_RELIEF_INFO.getKey(),MemberSelectKeyEnum.MEMBER_SECRET_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取租客会员信息,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "会员详情服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"MemberDetailFeignService.getMemberSelectInfo");
            String parameter = "memNo="+memNo+"&selectKey"+JSON.toJSONString(selectKey);
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = memberDetailFeignService.getMemberSelectInfo(Integer.parseInt(memNo), selectKey);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            log.error("Feign 获取租客会员信息失败,ResponseData={},memNo={}",responseData,memNo,e);
            Cat.logError("Feign 获取租客会员信息失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }

        MemberTotalInfo memberTotalInfo = responseData.getData();
        MemberAuthInfo memberAuthInfo = memberTotalInfo.getMemberAuthInfo();
        MemberCoreInfo memberCoreInfo = memberTotalInfo.getMemberCoreInfo();
        MemberBaseInfo memberBaseInfo = memberTotalInfo.getMemberBaseInfo();
        MemberAdditionInfo memberAdditionInfo = memberTotalInfo.getMemberAdditionInfo();
        MemberStatisticsInfo memberStatisticsInfo = memberTotalInfo.getMemberStatisticsInfo();
        MemberRoleInfo memberRoleInfo = memberTotalInfo.getMemberRoleInfo();
        RenterMemberDTO renterMemberDto = new RenterMemberDTO();
        renterMemberDto.setDriverScore(memberBaseInfo.getTotalScore());
        renterMemberDto.setRepeatTimeOrder(memberCoreInfo.getRepeatTimeOrder());
        renterMemberDto.setMemNo(memNo);
        renterMemberDto.setPhone(memberCoreInfo.getMobile()==null ? "" : String.valueOf(memberCoreInfo.getMobile()));
        renterMemberDto.setHeaderUrl(memberCoreInfo.getPortraitPath());
        renterMemberDto.setRealName(memberCoreInfo.getRealName());
        renterMemberDto.setNickName(memberCoreInfo.getNickName());
        renterMemberDto.setCertificationTime(memberAuthInfo.getDriLicFirstTime()!=null?LocalDateTimeUtils.parseStringToLocalDate(memberAuthInfo.getDriLicFirstTime()):null);
        renterMemberDto.setRentFlag(memberCoreInfo.getRentFlag());
        renterMemberDto.setFirstName(memberBaseInfo.getFirstName());
        renterMemberDto.setGender(memberBaseInfo.getGender());
        renterMemberDto.setIdCardAuth(memberAuthInfo.getIdCardAuth());
        renterMemberDto.setIdNo(memberTotalInfo.getMemberSecretInfo().getIdNo());
        renterMemberDto.setDriLicAuth(memberAuthInfo.getDriLicAuth());
        renterMemberDto.setDriViceLicAuth(memberAuthInfo.getDriViceLicAuth());
        renterMemberDto.setOrderSuccessCount(memberStatisticsInfo.getSuccessOrderNum());
        List<CommUseDriverInfo> commUseDriverList = memberAdditionInfo.getCommUseDriverList();
        List<CommUseDriverInfoDTO> CommUseDriverList = new ArrayList<>();
        commUseDriverList.forEach(x->{
            CommUseDriverInfoDTO commUseDriverInfoDTO = new CommUseDriverInfoDTO();
            BeanUtils.copyProperties(x,commUseDriverInfoDTO);
            commUseDriverInfoDTO.setMobile(null == x.getMobile() ? null : x.getMobile().toString());
            CommUseDriverList.add(commUseDriverInfoDTO);
        });
        renterMemberDto.setCommUseDriverList(CommUseDriverList);
        renterMemberDto.setIsNew(memberRoleInfo.getIsNew());
        renterMemberDto.setRenterCheck(memberAuthInfo.getRenterCheck());
        renterMemberDto.setRegTime(memberCoreInfo.getRegTime()==null ? null: LocalDateTimeUtils.dateToLocalDateTime(memberCoreInfo.getRegTime()));
        renterMemberDto.setOuterSource(memberBaseInfo.getOuterSource());

        List<RenterMemberRightDTO> rights = new ArrayList<>();

        if(memberRoleInfo != null){
            if(memberRoleInfo.getInternalStaff()!=null){
                RenterMemberRightDTO internalStaff = new RenterMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.STAFF.getRightCode());
                internalStaff.setRightName(MemRightEnum.STAFF.getRightName());
                internalStaff.setRightType(RightTypeEnum.STAFF.getCode());
                internalStaff.setRightValue(String.valueOf(memberRoleInfo.getInternalStaff()));
                internalStaff.setRightDesc("是否是内部员工");
                rights.add(internalStaff);
            }
            if(memberRoleInfo.getMemberFlag() != null){
                MemberFlagEnum memberFlagEnum = MemberFlagEnum.getRightByIndex(memberRoleInfo.getMemberFlag());
                RenterMemberRightDTO internalStaff = new RenterMemberRightDTO();
                internalStaff.setRightCode(memberFlagEnum.getRightCode());
                internalStaff.setRightName(memberFlagEnum.getRightName());
                internalStaff.setRightType(RightTypeEnum.MEMBER_FLAG.getCode());
                internalStaff.setRightValue("1");
                internalStaff.setRightDesc("会员标识");
                rights.add(internalStaff);
            }
            if(memberRoleInfo.getCpicMemberFlag() != null){
                RenterMemberRightDTO internalStaff = new RenterMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.CPIC_MEM.getRightCode());
                internalStaff.setRightName(MemRightEnum.CPIC_MEM.getRightName());
                internalStaff.setRightValue(String.valueOf(memberRoleInfo.getCpicMemberFlag()));
                internalStaff.setRightType(RightTypeEnum.CPIC.getCode());
                internalStaff.setRightDesc("是否太保会员");
                rights.add(internalStaff);
            }
        }
        MemberReliefInfo memberReliefInfo = memberTotalInfo.getMemberReliefInfo();
        if(memberReliefInfo != null){
            WxBindingTaskInfo wxBindingTaskInfo = memberReliefInfo.getWxBindingTaskInfo();
            if(wxBindingTaskInfo !=null){
                RenterMemberRightDTO internalStaff = new RenterMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.BIND_WECHAT.getRightCode());
                internalStaff.setRightName(MemRightEnum.BIND_WECHAT.getRightName());
                internalStaff.setRightValue(wxBindingTaskInfo.getReliefPercentage()==null?"0":String.valueOf(wxBindingTaskInfo.getReliefPercentage()));
                internalStaff.setRightType(RightTypeEnum.TASK.getCode());
                internalStaff.setRightDesc(wxBindingTaskInfo.getTitle());
                rights.add(internalStaff);
            }
            MemberLevelTaskInfo memberLevelTaskInfo = memberReliefInfo.getMemberLevelTaskInfo();
            if(memberLevelTaskInfo != null){
                RenterMemberRightDTO internalStaff = new RenterMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.MEMBER_LEVEL.getRightCode());
                internalStaff.setRightName(MemRightEnum.MEMBER_LEVEL.getRightName());
                internalStaff.setRightValue(memberLevelTaskInfo.getReliefPercentage()==null?"0":String.valueOf(memberLevelTaskInfo.getReliefPercentage()));
                internalStaff.setRightType(RightTypeEnum.TASK.getCode());
                internalStaff.setRightDesc(memberLevelTaskInfo.getTitle());
                rights.add(internalStaff);
            }
            InvitationTaskInfo invitationTaskInfo = memberReliefInfo.getInvitationTaskInfo();
            if(invitationTaskInfo != null){
                RenterMemberRightDTO internalStaff = new RenterMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.INVITE_FRIENDS.getRightCode());
                internalStaff.setRightName(MemRightEnum.INVITE_FRIENDS.getRightName());
                internalStaff.setRightValue(invitationTaskInfo.getReliefPercentage()==null?"0":String.valueOf(invitationTaskInfo.getReliefPercentage()));
                internalStaff.setRightType(RightTypeEnum.TASK.getCode());
                internalStaff.setRightDesc(invitationTaskInfo.getTitle());
                rights.add(internalStaff);
            }
            RentCarTaskInfo rentCarTaskInfo = memberReliefInfo.getRentCarTaskInfo();
            if(rentCarTaskInfo != null){
                RenterMemberRightDTO internalStaff = new RenterMemberRightDTO();
                internalStaff.setRightCode(MemRightEnum.SUCCESS_RENTCAR.getRightCode());
                internalStaff.setRightName(MemRightEnum.SUCCESS_RENTCAR.getRightName());
                internalStaff.setRightValue(rentCarTaskInfo.getReliefPercentage()==null?"0":String.valueOf(rentCarTaskInfo.getReliefPercentage()));
                internalStaff.setRightType(RightTypeEnum.TASK.getCode());
                internalStaff.setRightDesc(rentCarTaskInfo.getTitle());
                rights.add(internalStaff);
            }
        }
        renterMemberDto.setRenterMemberRightDTOList(rights);
        log.info("ownerMemberDto.setOwnerMemberRightDTOList={}",JSON.toJSONString(rights));
        return renterMemberDto;
    }

    /**
     * @Author ZhangBin
     * @Date 2019/12/31 14:28
     * @Description: 根据会员号，获取常用驾驶人列表
     *
     **/
    public List<CommUseDriverInfoDTO> getCommUseDriverList(String memNo){
        List<String> selectKey = Arrays.asList(MemberSelectKeyEnum.MEMBER_ADDITION_INFO.getKey());
        ResponseData<MemberAdditionInfo> responseData = null;
        log.info("Feign 开始获取附加驾驶人信息,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "附加驾驶人信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"MemberDetailFeignService.getMemberSelectInfo");
            String parameter = "memNo="+memNo+"&selectKey"+JSON.toJSONString(selectKey);
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = memberDetailFeignService.getMemberAdditionInfo(Integer.parseInt(memNo));
            log.info("Feign 获取附加驾驶人信息 param={},response is {}",parameter,JSON.toJSONString(responseData));
            ResponseCheckUtil.checkResponse(responseData);
            MemberAdditionInfo memberAdditionInfo = responseData.getData();
            List<CommUseDriverInfo> commUseDriverList = memberAdditionInfo.getCommUseDriverList();
            List<CommUseDriverInfoDTO> CommUseDriverList = new ArrayList<>();
            commUseDriverList.forEach(x->{
                CommUseDriverInfoDTO commUseDriverInfoDTO = new CommUseDriverInfoDTO();
                BeanUtils.copyProperties(x,commUseDriverInfoDTO);
                CommUseDriverList.add(commUseDriverInfoDTO);
            });
            t.setStatus(Transaction.SUCCESS);
            return CommUseDriverList;
        }catch (Exception e){
            log.error("Feign 获取附加驾驶人信息失败,responseData={},memNo={}",memNo,responseData,e);
            Cat.logError("Feign 获取附加驾驶人信息失败",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }

    }


    /**
     * 获取简单的会员信息
     *
     * @param memNo 会员号
     * @return CashWithdrawalSimpleMemberDTO
     */
    public CashWithdrawalSimpleMemberDTO getSimpleMemberInfo(String memNo) {
        List<String> selectKey = Arrays.asList(
                MemberSelectKeyEnum.MEMBER_CORE_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_AUTH_INFO.getKey(),
                MemberSelectKeyEnum.MEMBER_ASSERTS_INFO.getKey());
        ResponseData<MemberTotalInfo> responseData = null;
        log.info("Feign 开始获取简单的会员信息,memNo={}", memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "会员详情服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "MemberDetailFeignService.getSimpleMemberInfo");
            String parameter = "memNo=" + memNo + "&selectKey" + JSON.toJSONString(selectKey);
            Cat.logEvent(CatConstants.FEIGN_PARAM, parameter);
            responseData = memberDetailFeignService.getMemberSelectInfo(Integer.parseInt(memNo), selectKey);
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("Feign 获取简单的会员信息失败,ResponseData={},memNo={}", responseData, memNo, e);
            Cat.logError("Feign 获取简单的会员信息失败", e);
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }

        MemberTotalInfo memberTotalInfo = responseData.getData();
        if (memberTotalInfo == null) {
            return null;
        }
        // 会员核心信息
        MemberCoreInfo memberCoreInfo = memberTotalInfo.getMemberCoreInfo();
        // 会员认证信息
        MemberAuthInfo memberAuthInfo = memberTotalInfo.getMemberAuthInfo();
        // 会员资产信息
        MemberAssetsInfo memberAssetsInfo = memberTotalInfo.getMemberAssetsInfo();
        CashWithdrawalSimpleMemberDTO simpleMem = new CashWithdrawalSimpleMemberDTO();
        simpleMem.setMemNo(memNo);
        if (memberCoreInfo != null) {
            simpleMem.setMobile(memberCoreInfo.getMobile() == null ? null : String.valueOf(memberCoreInfo.getMobile()));
            simpleMem.setRealName(memberCoreInfo.getRealName());
        }
        if (memberAuthInfo != null) {
            simpleMem.setIdCardAuth(memberAuthInfo.getIdCardAuth());
        }
        if (memberAssetsInfo != null) {
            simpleMem.setBalance(memberAssetsInfo.getBalance());
        }
        log.info("获取简单的会员信息. simpleMem:[{}]", JSON.toJSONString(simpleMem));
        return simpleMem;
    }


    /**
     * 获取会员假节日取消产生罚金次数
     *
     * @param memNo             会员号
     * @param holidayCircleList 租期内节假日列表
     * @return boolean true,累计超过X次补贴给平台 false,累计未超过X次补贴给车主/租客
     */
    public boolean countByHolidayCircleList(Integer memNo, List<Integer> holidayCircleList) {
        log.info("Feign 开始获取会员假节日取消产生罚金次数.param is, memNo:[{}], holidayCircleList:[{}]", memNo, holidayCircleList);
        if (CollectionUtils.isEmpty(holidayCircleList) || null == memNo) {
            return false;
        }
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "会员服务");
        ResponseData<Integer> responseData = null;
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "memberDetailFeignService.getHolidayDeductCount");
            String parameter = "memNo=" + memNo + "&holidayCircleList" + JSON.toJSONString(holidayCircleList);
            Cat.logEvent(CatConstants.FEIGN_PARAM, parameter);
            responseData = memberDetailFeignService.getHolidayDeductCount(memNo, holidayCircleList);
            log.info("获取会员假节日取消产生罚金次数.responseData:[{}]", JSON.toJSONString(responseData));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseData));
            t.setStatus(Transaction.SUCCESS);

            if (null != responseData) {
                Integer count = responseData.getData();
                return null != count && count >= 2;
            }
        } catch (Exception e) {
            log.error("Feign 获取会员假节日取消产生罚金次数失败,responseData={},memNo={}", memNo, responseData, e);
            Cat.logError("Feign 获取会员假节日取消产生罚金次数失败", e);
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }
        return false;

    }
    
    
    /**
     * 会员核心信息(不需要抛出异常，调该方法需要校验返回为空)
     * @param memNo
     * @return MemberCoreInfo
     */
    public MemberCoreInfo getMemberCoreInfoByMemNo(Integer memNo){
        ResponseData<MemberCoreInfo> responseData = null;
        log.info("Feign 获取开始会员核心信息,memNo={}",memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "会员详情服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"MemberDetailFeignService.getMemNoByMobile");
            String parameter = "memNo="+memNo;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = memberDetailFeignService.getMemberCoreInfo(memNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();
        }
        catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 获取会员核心信息失败",e);
            log.error("Feign 获取会员核心信息失败,Response={},memNo={}",responseData,memNo,e);
        }finally {
            t.complete();
        }
        return null;
    }
    
    
    /**
     * 返回用户的会员号(不需要抛出异常，调该方法需要校验返回为空)
     * @param mobile
     * @return Integer
     */
    public Integer getMemNoByMoileEx(Long mobile){
        ResponseData<Integer> responseData = null;
        log.info("Feign 开始根据手机号查询会员号,mobile={}",mobile);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "会员详情服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"MemberDetailFeignService.getMemNoByMobile");
            String parameter = "mobile="+mobile;
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            responseData = memberDetailFeignService.getMemNoByMobile(mobile);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            ResponseCheckUtil.checkResponse(responseData);
            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();
        }
        catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 根据手机号查询会员号失败",e);
            log.error("Feign 根据手机号查询会员号失败,Response={},mobile={}",responseData,mobile,e);
        }finally {
            t.complete();
        }
        return null;
    }


}
