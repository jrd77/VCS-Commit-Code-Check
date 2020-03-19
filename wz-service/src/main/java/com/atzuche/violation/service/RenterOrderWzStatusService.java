package com.atzuche.violation.service;


import com.atzuche.order.commons.DateUtils;
import com.atzuche.violation.common.AdminUserUtil;
import com.atzuche.violation.entity.RenterOrderWzStatusEntity;
import com.atzuche.violation.enums.WzStatusEnums;
import com.atzuche.violation.mapper.RenterOrderWzStatusMapper;
import com.atzuche.violation.vo.req.ViolationCompleteRequestVO;
import com.atzuche.violation.vo.req.ViolationHandleAlterationRequestVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * RenterOrderWzStatusService
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class RenterOrderWzStatusService {

    @Resource
    private RenterOrderWzStatusMapper renterOrderWzStatusMapper;

    public void updateTransIllegalQuery(Integer illegalQuery, String orderNo, String carNum) {
        renterOrderWzStatusMapper.updateTransIllegalQuery(illegalQuery,orderNo,carNum);
    }

    public void updateTransIllegalStatus(Integer status, String orderNo, String carNum) {
        renterOrderWzStatusMapper.updateTransIllegalStatus(status,orderNo,carNum, WzStatusEnums.getStatusDesc(status));
    }

    public void updateIllegalHandle(Integer managementMode, String orderNo, String carNum) {
        renterOrderWzStatusMapper.updateIllegalHandle(managementMode,orderNo,carNum);
    }

    public RenterOrderWzStatusEntity selectByOrderNo(String orderNo, String carNum) {
        return renterOrderWzStatusMapper.selectByOrderNo(orderNo,carNum);
    }

    public void updateStatusByOrderNoAndCarNum(String orderNo, Integer status, String carNum) {
        renterOrderWzStatusMapper.updateStatusByOrderNoAndCarNum(orderNo,status,carNum,WzStatusEnums.getStatusDesc(status));
    }

    public Integer getTransWzDisposeStatusByOrderNo(String orderNo, String plateNum) {
        return renterOrderWzStatusMapper.getTransWzDisposeStatusByOrderNo(orderNo,plateNum);
    }

    /**
     * 修改违章信息
     * @param violationAlterationRequestVO
     */
    public void updateOrderWzStatus(ViolationHandleAlterationRequestVO violationAlterationRequestVO) {
        RenterOrderWzStatusEntity renterOrderWzStatusEntity = new RenterOrderWzStatusEntity();
        Integer managementMode = Integer.parseInt(violationAlterationRequestVO.getManagementMode());
        renterOrderWzStatusEntity.setManagementMode(managementMode);
        renterOrderWzStatusEntity.setWzHandleCompleteTime(DateUtils.parseDate(violationAlterationRequestVO.getWzHandleCompleteTime(), DateUtils.DATE_DEFAUTE));
        renterOrderWzStatusEntity.setWzRenterLastTime(DateUtils.parseDate(violationAlterationRequestVO.getWzRenterLastTime(), DateUtils.DATE_DEFAUTE));
        renterOrderWzStatusEntity.setWzPlatformLastTime(DateUtils.parseDate(violationAlterationRequestVO.getWzPlatformLastTime(), DateUtils.DATE_DEFAUTE));
        renterOrderWzStatusEntity.setWzRemarks( violationAlterationRequestVO.getWzRemarks());

        if(managementMode == 1){
            renterOrderWzStatusEntity.setStatus(25);
            renterOrderWzStatusEntity.setStatusDesc(WzStatusEnums.getStatusDesc(25));
        } else if (managementMode == 2) {
            renterOrderWzStatusEntity.setStatus(26);
            renterOrderWzStatusEntity.setStatusDesc(WzStatusEnums.getStatusDesc(26));
        } else if (managementMode == 3) {
            renterOrderWzStatusEntity.setStatus(40);
            renterOrderWzStatusEntity.setStatusDesc(WzStatusEnums.getStatusDesc(40));
        } else if (managementMode == 4) {
            renterOrderWzStatusEntity.setStatus(46);
            renterOrderWzStatusEntity.setStatusDesc(WzStatusEnums.getStatusDesc(46));
        }

        renterOrderWzStatusMapper.updateOrderWzStatus(renterOrderWzStatusEntity);
    }
    /**
     * 修改违章信息
     * @param violationCompleteRequestVO
     */
    public void updateOrderWzStatus(ViolationCompleteRequestVO violationCompleteRequestVO) {
        Integer managementMode = Integer.parseInt(violationCompleteRequestVO.getManagementMode());
        RenterOrderWzStatusEntity renterOrderWzStatusEntity = new RenterOrderWzStatusEntity();
        renterOrderWzStatusEntity.setOrderNo(violationCompleteRequestVO.getOrderNo());
        Integer wzDisposeStatus=0;
        if(managementMode== 1){
            //租客
            wzDisposeStatus = 50;//已处理-租客处理
        }else if(managementMode==2){
            //平台
            wzDisposeStatus = 52;//已处理-平台处理
        }else if(managementMode==3){
            //车主
            wzDisposeStatus = 51;//已处理-车主处理
        }else if(managementMode == 4){
            // 无数据
            wzDisposeStatus = 45;//已处理-无数据
        }
        renterOrderWzStatusEntity.setStatus(wzDisposeStatus);
        //更新违章完成时间
        renterOrderWzStatusEntity.setWzHandleCompleteTime(new Date());
        renterOrderWzStatusMapper.updateOrderWzStatus(renterOrderWzStatusEntity);
    }


    /**
     * 修改违章状态
     * @param orderNo
     * @param carNumber
     * @param wzDisposeStatus
     */
    public void updateTransWzDisposeStatus(String orderNo, String carNumber, int wzDisposeStatus) {
        RenterOrderWzStatusEntity renterOrderWzStatusEntity = new RenterOrderWzStatusEntity();
        renterOrderWzStatusEntity.setOrderNo(orderNo);
        renterOrderWzStatusEntity.setCarPlateNum(carNumber);
        renterOrderWzStatusEntity.setStatus(wzDisposeStatus);
        renterOrderWzStatusEntity.setStatusDesc(WzStatusEnums.getStatusDesc(wzDisposeStatus));
        renterOrderWzStatusEntity.setUpdateOp(AdminUserUtil.getAdminUser().getAuthName());
        renterOrderWzStatusMapper.updateTransWzDisposeStatus(renterOrderWzStatusEntity);
    }



    public List<RenterOrderWzStatusEntity> queryInfosByOrderNo(String orderNo) {
        return renterOrderWzStatusMapper.queryInfosByOrderNo(orderNo);
    }

    public void createInfo(String orderNo,String carNum,String operator,String renterNo,String ownerNo,String carNo){
        renterOrderWzStatusMapper.deleteInfoByOrderNo(orderNo,operator);
        RenterOrderWzStatusEntity dto = new RenterOrderWzStatusEntity();
        dto.setOrderNo(orderNo);
        dto.setCarPlateNum(carNum);
        dto.setStatus(5);
        dto.setCreateOp(operator);
        dto.setCreateTime(new Date());
        dto.setRenterNo(renterNo);
        dto.setOwnerNo(ownerNo);
        dto.setCarNo(carNo);
        dto.setStatusDesc(WzStatusEnums.getStatusDesc(dto.getStatus()));
        renterOrderWzStatusMapper.saveRenterOrderWzStatus(dto);
    }

    public List<RenterOrderWzStatusEntity> queryIllegalOrderListByMemNo(String memNo) {
        return renterOrderWzStatusMapper.queryIllegalOrderListByMemNo(memNo);
    }

    public RenterOrderWzStatusEntity getOrderInfoByOrderNo(String orderNo) {
        return renterOrderWzStatusMapper.getOrderInfoByOrderNo(orderNo);
    }

}
