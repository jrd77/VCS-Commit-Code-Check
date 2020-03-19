package com.atzuche.violation.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.entity.WzCostLogEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.renterwz.service.WzCostLogService;
import com.atzuche.violation.common.AdminUserUtil;
import com.atzuche.violation.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.violation.entity.AccountRenterWzDepositEntity;
import com.atzuche.violation.entity.RenterOrderWzStatusEntity;
import com.atzuche.violation.enums.WzCostEnums;
import com.atzuche.violation.vo.req.*;
import com.atzuche.violation.vo.resp.RenterWzCostDetailResVO;
import com.atzuche.violation.vo.resp.ViolationAlterationLogListResponseVO;
import com.atzuche.violation.vo.resp.ViolationAlterationLogResponseVO;
import com.atzuche.violation.vo.resp.ViolationHandleInformationResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ViolationManageService {
    private static final Logger logger = LoggerFactory.getLogger(ViolationManageService.class);
    private static final int RENTER_HANDLE_WZ_DISPOSE_STATUS = 10;
    private static final int HAS_ILLEGAL_NO=3;



    private static final List<String> COST_CODE_LIST = Arrays.asList("11240040","11240041","11240042","11240043","11240044","11240045");


    @Autowired
    RenterOrderWzCostDetailService renterOrderWzCostDetailService;
    @Autowired
    RenterOrderWzStatusService renterOrderWzStatusService;
    @Autowired
    AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired
    RenterOrderWzDetailService renterOrderWzDetailService;



    @Autowired
    private RenterOrderWzSettleFlagService renterOrderWzSettleFlagService;

    @Autowired
    private AccountRenterWzDepositDetailNoTService accountRenterWzDepositDetailNoTService;

    @Autowired
    RenterWzService renterWzService;

    @Autowired
    WzCostLogService wzCostLogService;


    /**
     * 无违章操作处理
     */
    public void manualHandle(ViolationHandleRequestVO violationHandleRequestVO){
        RenterOrderWzCostDetailEntity renterOrderWzCostDetailEntity = renterOrderWzCostDetailService.queryInfoByOrderAndCode(violationHandleRequestVO.getOrderNo(),WzCostEnums.INSURANCE_CLAIM.getCode());
        //租客自行处理
        if(!ObjectUtils.isEmpty(renterOrderWzCostDetailEntity)&&renterOrderWzCostDetailEntity.getAmount()>0){
            renterOrderWzStatusService.updateTransWzDisposeStatus(violationHandleRequestVO.getOrderNo(),violationHandleRequestVO.getPlateNum(),RENTER_HANDLE_WZ_DISPOSE_STATUS);
        } else {
            renterOrderWzSettleFlagService.updateIsIllegal(violationHandleRequestVO.getOrderNo(),violationHandleRequestVO.getPlateNum(),HAS_ILLEGAL_NO, AdminUserUtil.getAdminUser().getAuthName());
        }
    }


    /**
     * 获取违章处理信息
     */
    public ViolationHandleInformationResponseVO getViolationHandleInformation(ViolationInformationRequestVO violationInformationRequestVO){
        String orderNo = violationInformationRequestVO.getOrderNo();
        String plateNum = violationInformationRequestVO.getPlateNum();
        ViolationHandleInformationResponseVO violationHandleInformationResponseVO = new ViolationHandleInformationResponseVO();
        AccountRenterWzDepositEntity entity = accountRenterWzDepositNoTService.getAccountRenterWZDepositByOrder(orderNo);
        List<RenterWzCostDetailResVO> costDetails = getRenterWzCostDetailRes(orderNo);
        int zanKouAmount = this.getZanKouAmount(costDetails);
        violationHandleInformationResponseVO.setWzDepositAmt(entity.getShishouDeposit().toString());
        violationHandleInformationResponseVO.setShouldReturnDepositAmt(String.valueOf(entity.getShishouDeposit()-zanKouAmount));
        if (!CollectionUtils.isEmpty(costDetails)) {
            for (RenterWzCostDetailResVO renterWzCostDetailResVO : costDetails) {
                //保险理赔
                if(renterWzCostDetailResVO.getCostCode().equals(WzCostEnums.INSURANCE_CLAIM.getCode())){
                    violationHandleInformationResponseVO.setWzInsuranceRemark(renterWzCostDetailResVO.getRemark());
                    violationHandleInformationResponseVO.setWzInsuranceAmt(renterWzCostDetailResVO.getAmount());
                }else if(renterWzCostDetailResVO.getCostCode().equals(WzCostEnums.WZ_OTHER_FINE.getCode())){
                    violationHandleInformationResponseVO.setOtherDeductionRemark(renterWzCostDetailResVO.getRemark());
                    violationHandleInformationResponseVO.setOtherDeductionAmt(renterWzCostDetailResVO.getAmount());
                }else if(renterWzCostDetailResVO.getCostCode().equals(WzCostEnums.WZ_STOP_COST.getCode())){
                    violationHandleInformationResponseVO.setWzOffStreamCostRemark(renterWzCostDetailResVO.getRemark());
                    violationHandleInformationResponseVO.setWzOffStreamCostAmt(renterWzCostDetailResVO.getAmount());
                }else if(renterWzCostDetailResVO.getCostCode().equals(WzCostEnums.WZ_DYS_FINE.getCode())){
                    violationHandleInformationResponseVO.setWzDysFineRemark(renterWzCostDetailResVO.getRemark());
                    violationHandleInformationResponseVO.setWzDysFineAmt(renterWzCostDetailResVO.getAmount());
                }else if(renterWzCostDetailResVO.getCostCode().equals(WzCostEnums.WZ_SERVICE_COST.getCode())){
                    violationHandleInformationResponseVO.setWzServiceCostRemark(renterWzCostDetailResVO.getRemark());
                    violationHandleInformationResponseVO.setWzServiceCostAmt(renterWzCostDetailResVO.getAmount());
                }else if(renterWzCostDetailResVO.getCostCode().equals(WzCostEnums.WZ_FINE.getCode())){
                    violationHandleInformationResponseVO.setWzFineRemark(renterWzCostDetailResVO.getRemark());
                    violationHandleInformationResponseVO.setWzFineAmt(renterWzCostDetailResVO.getAmount());
                }
            }
        }

        RenterOrderWzStatusEntity renterOrderWzStatusEntity = renterOrderWzStatusService.selectByOrderNo(orderNo, plateNum);
        AccountRenterWzDepositDetailEntity accountRenterWzDepositDetailEntity = accountRenterWzDepositDetailNoTService.findByOrderNoAndCode(orderNo);

        if(!ObjectUtils.isEmpty(accountRenterWzDepositDetailEntity)){
            //违章押金退款时间
            violationHandleInformationResponseVO.setViolationRefundTime(accountRenterWzDepositDetailEntity.getCreateTime().toString());
        }
        //租客最晚处理时间
        violationHandleInformationResponseVO.setWzRenterLastTime(renterOrderWzStatusEntity.getWzRenterLastTime().toString());
        //平台最晚处理时间
        violationHandleInformationResponseVO.setWzPlatformLastTime(renterOrderWzStatusEntity.getWzPlatformLastTime().toString());
        //办理完成时间
        violationHandleInformationResponseVO.setWzHandleCompleteTime(renterOrderWzStatusEntity.getWzHandleCompleteTime().toString());

        return violationHandleInformationResponseVO;
    }


    /**
     * 更新违章处理信息
     * @param violationAlterationRequestVO
     */
    public void updateViolationHandle(ViolationHandleAlterationRequestVO violationAlterationRequestVO){
        List<RenterWzCostDetailReqVO > costDetails = new ArrayList<>();
        String orderNo = violationAlterationRequestVO.getOrderNo();
        //其他扣款
        RenterWzCostDetailReqVO wzOtherFine = new RenterWzCostDetailReqVO();
        wzOtherFine.setAmount(violationAlterationRequestVO.getOtherDeductionAmt());
        wzOtherFine.setCostCode(WzCostEnums.WZ_OTHER_FINE.getCode());
        wzOtherFine.setCostDesc(WzCostEnums.WZ_OTHER_FINE.getDesc());
        wzOtherFine.setCostType(WzCostEnums.WZ_OTHER_FINE.getType());
        wzOtherFine.setOrderNo(orderNo);
        wzOtherFine.setRemark(violationAlterationRequestVO.getOtherDeductionRemark());
        //协助违章处理
        RenterWzCostDetailReqVO wzFine = new RenterWzCostDetailReqVO();
        wzFine.setAmount(violationAlterationRequestVO.getWzFineAmt());
        wzFine.setCostCode(WzCostEnums.WZ_FINE.getCode());
        wzFine.setCostDesc(WzCostEnums.WZ_FINE.getDesc());
        wzFine.setCostType(WzCostEnums.WZ_FINE.getType());
        wzFine.setOrderNo(orderNo);
        wzFine.setRemark(violationAlterationRequestVO.getWzFineRemark());

        //凹凸代步服务费
        RenterWzCostDetailReqVO wzServiceCost = new RenterWzCostDetailReqVO();
        wzServiceCost.setAmount(violationAlterationRequestVO.getWzServiceCostAmt());
        wzServiceCost.setCostCode(WzCostEnums.WZ_SERVICE_COST.getCode());
        wzServiceCost.setCostDesc(WzCostEnums.WZ_SERVICE_COST.getDesc());
        wzServiceCost.setCostType(WzCostEnums.WZ_SERVICE_COST.getType());
        wzServiceCost.setOrderNo(orderNo);
        wzServiceCost.setRemark(violationAlterationRequestVO.getWzServiceCostRemark());

        //不良用车处罚金
        RenterWzCostDetailReqVO wzDysFine = new RenterWzCostDetailReqVO();
        wzDysFine.setAmount(violationAlterationRequestVO.getWzDysFineAmt());
        wzDysFine.setCostCode(WzCostEnums.WZ_DYS_FINE.getCode());
        wzDysFine.setCostDesc(WzCostEnums.WZ_DYS_FINE.getDesc());
        wzDysFine.setCostType(WzCostEnums.WZ_DYS_FINE.getType());
        wzDysFine.setOrderNo(orderNo);
        wzDysFine.setRemark(violationAlterationRequestVO.getWzDysFineRemark());

        //停运费
        RenterWzCostDetailReqVO wzStopCost = new RenterWzCostDetailReqVO();
        wzStopCost.setAmount(violationAlterationRequestVO.getWzOffStreamCostAmt());
        wzStopCost.setCostCode(WzCostEnums.WZ_STOP_COST.getCode());
        wzStopCost.setCostDesc(WzCostEnums.WZ_STOP_COST.getDesc());
        wzStopCost.setCostType(WzCostEnums.WZ_STOP_COST.getType());
        wzStopCost.setOrderNo(orderNo);
        wzStopCost.setRemark(violationAlterationRequestVO.getWzOffStreamCostRemark());

        costDetails.add(wzOtherFine);
        costDetails.add(wzFine);
        costDetails.add(wzServiceCost);
        costDetails.add(wzDysFine);
        costDetails.add(wzStopCost);

        renterWzService.updateWzCost(orderNo, costDetails);
        renterOrderWzStatusService.updateOrderWzStatus(violationAlterationRequestVO);

    }

    /**
     * 获取违章修改日志列表
     * @param violationInformationRequestVO
     * @return
     */
    public ViolationAlterationLogListResponseVO selectAlterationLogList(ViolationInformationRequestVO violationInformationRequestVO){
        ViolationAlterationLogListResponseVO violationAlterationLogListResponseVO = new ViolationAlterationLogListResponseVO();
        List<ViolationAlterationLogResponseVO> logList = new ArrayList<>();
        List<WzCostLogEntity> wzCostLogEntities = wzCostLogService.queryWzCostLogsByOrderNo(violationInformationRequestVO.getOrderNo());
        if(!CollectionUtils.isEmpty(wzCostLogEntities)) {
            wzCostLogEntities.forEach(wzCostLog -> {
                ViolationAlterationLogResponseVO violationAlterationLogResponseVO = new ViolationAlterationLogResponseVO();
                violationAlterationLogResponseVO.setContent(wzCostLog.getContent());
                violationAlterationLogResponseVO.setCreateTime(DateUtils.formate(wzCostLog.getCreateTime(),DateUtils.DATE_DEFAUTE1 ));
                violationAlterationLogResponseVO.setOperationType("订单扣款处理");
                violationAlterationLogResponseVO.setOperator(wzCostLog.getOperator());
                logList.add(violationAlterationLogResponseVO);
            });
        }
        violationAlterationLogListResponseVO.setAlterationLogList(logList);
        return violationAlterationLogListResponseVO;
    }


    /**
     * 确认违章办理完成
     * @param violationCompleteRequestVO
     */
    public void updateConfirmComplete(ViolationCompleteRequestVO violationCompleteRequestVO){
        renterOrderWzStatusService.updateOrderWzStatus(violationCompleteRequestVO);
        renterOrderWzDetailService.updateIllegalStatus(violationCompleteRequestVO.getOrderNo());
        //记录日志
    }




    private int getZanKouAmount(List<RenterWzCostDetailResVO> costDetails) {
        if(CollectionUtils.isEmpty(costDetails)){
            return 0;
        }
        return costDetails
                .stream()
                .filter(Objects::nonNull)
                .filter(dto -> StringUtils.isNotBlank(dto.getAmount()))
                .map(RenterWzCostDetailResVO::getAmount)
                .mapToInt(Integer::parseInt)
                .sum();
    }


    private List<RenterWzCostDetailResVO> getRenterWzCostDetailRes(String orderNo) {
        List<RenterOrderWzCostDetailEntity> results = new ArrayList<>();
        for (String costCode : COST_CODE_LIST) {
            RenterOrderWzCostDetailEntity dto = renterOrderWzCostDetailService.queryInfoWithSumAmountByOrderAndCode(orderNo,costCode);
            if(dto == null){
                dto = new RenterOrderWzCostDetailEntity();
                dto.setAmount(0);
                dto.setCostCode(costCode);
                dto.setCostDesc(WzCostEnums.getDesc(costCode));
                dto.setOrderNo(orderNo);
            }
            results.add(dto);
        }
        List<RenterWzCostDetailResVO> costDetails = new ArrayList<>();
        RenterWzCostDetailResVO dto;
        for (RenterOrderWzCostDetailEntity costDetail : results) {
            dto = new RenterWzCostDetailResVO();
            BeanUtils.copyProperties(costDetail,dto);
            dto.setAmount(String.valueOf(costDetail.getAmount()));
            dto.setCostType(WzCostEnums.getType(costDetail.getCostCode()));
            dto.setRemarkName(WzCostEnums.getRemark(costDetail.getCostCode()));
            costDetails.add(dto);
        }
        return costDetails;
    }


}
