package com.atzuche.order.renterorder.service;

import com.atzuche.order.commons.entity.dto.CommUseDriverInfoDTO;
import com.atzuche.order.renterorder.entity.RenterAdditionalDriverEntity;
import com.atzuche.order.renterorder.mapper.RenterAdditionalDriverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 租客端附加驾驶人信息表
 *
 * @author ZhangBin
 * @date 2019-12-28 15:51:58
 */
@Service
public class RenterAdditionalDriverService {
    @Autowired
    private RenterAdditionalDriverMapper renterAdditionalDriverMapper;


    /**
     * 批量保存附加驾驶人信息
     *
     * @param orderNo           主订单号
     * @param renterOrderNo     租客订单号
     * @param driverIds         用户选择附加驾驶人信息
     * @param commUseDriverList 租客可用附加驾驶人列表
     */
    public void insertBatchAdditionalDriver(String orderNo, String renterOrderNo, List<String> driverIds,
                                            List<CommUseDriverInfoDTO> commUseDriverList) {
        if (!CollectionUtils.isEmpty(driverIds) && !CollectionUtils.isEmpty(commUseDriverList)) {
            for (CommUseDriverInfoDTO commUseDriverInfo : commUseDriverList) {
                if (null != commUseDriverInfo.getId() && driverIds.contains(commUseDriverInfo.getId().toString())) {
                    RenterAdditionalDriverEntity record = new RenterAdditionalDriverEntity();
                    record.setOrderNo(orderNo);
                    record.setRenterOrderNo(renterOrderNo);
                    record.setDriverId(String.valueOf(commUseDriverInfo.getId()));
                    record.setRealName(commUseDriverInfo.getRealName());
                    record.setPhone(String.valueOf(commUseDriverInfo.getMobile()));
                    renterAdditionalDriverMapper.insertSelective(record);
                }
            }


        }


    }


    /**
     * 获取租客已添加的附加驾驶人
     * @param renterOrderNo 租客子单号
     * @return List<String>
     */
    public List<String> listDriverIdByRenterOrderNo(String renterOrderNo) {
    	return renterAdditionalDriverMapper.listDriverIdByRenterOrderNo(renterOrderNo);
    }
}
