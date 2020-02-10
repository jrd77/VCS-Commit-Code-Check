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
                    //添加操作人
                    record.setCreateOp(commUseDriverInfo.getConsoleOperatorName());
                    record.setUpdateOp(commUseDriverInfo.getConsoleOperatorName());
                    renterAdditionalDriverMapper.insertSelective(record);
                }
            }


        }


    }
    
    /**
     * 管理后台新增附加驾驶人，先逻辑删除，后新增。
     * @param orderNo
     * @param renterOrderNo
     * @param driverIds
     * @param commUseDriverList
     */
    public void insertBatchAdditionalDriverBeforeDel(String orderNo, String renterOrderNo, List<String> driverIds,List<CommUseDriverInfoDTO> commUseDriverList) {
		if (!CollectionUtils.isEmpty(driverIds) && !CollectionUtils.isEmpty(commUseDriverList)) {
			//先逻辑删除
			int i = renterAdditionalDriverMapper.delByRenterOrderNo(renterOrderNo);
			if(i > 0) {
				//后新增
				for (CommUseDriverInfoDTO commUseDriverInfo : commUseDriverList) {
					if (null != commUseDriverInfo.getId() && driverIds.contains(commUseDriverInfo.getId().toString())) {
						RenterAdditionalDriverEntity record = new RenterAdditionalDriverEntity();
						record.setOrderNo(orderNo);
						record.setRenterOrderNo(renterOrderNo);
						record.setDriverId(String.valueOf(commUseDriverInfo.getId()));
						record.setRealName(commUseDriverInfo.getRealName());
						record.setPhone(String.valueOf(commUseDriverInfo.getMobile()));
						//添加操作人
						record.setCreateOp(commUseDriverInfo.getConsoleOperatorName());
						record.setUpdateOp(commUseDriverInfo.getConsoleOperatorName());
						renterAdditionalDriverMapper.insertSelective(record);
					}
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
    /**
     * 获取租客已添加的附加驾驶人
     * @param renterOrderNo 租客子单号
     * @return
     */
    public List<RenterAdditionalDriverEntity> listDriversByRenterOrderNo(String renterOrderNo) {
        return renterAdditionalDriverMapper.selectByRenterOrderNo(renterOrderNo);
    }


}
