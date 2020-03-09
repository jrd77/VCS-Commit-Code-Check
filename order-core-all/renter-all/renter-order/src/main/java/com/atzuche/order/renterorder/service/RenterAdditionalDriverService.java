package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoDTO;
import com.atzuche.order.renterorder.entity.RenterAdditionalDriverEntity;
import com.atzuche.order.renterorder.mapper.RenterAdditionalDriverMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 租客端附加驾驶人信息表
 *
 * @author ZhangBin
 * @date 2019-12-28 15:51:58
 */
@Service
public class RenterAdditionalDriverService {
    private static Logger logger = LoggerFactory.getLogger(RenterAdditionalDriverService.class);

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
        logger.info("Batch insert additional driver.param is,orderNo:[{}],renterOrderNo:[{}],driverIds:[{}],commUseDriverList:[{}]",
                orderNo, renterOrderNo, JSON.toJSON(driverIds), JSON.toJSON(commUseDriverList));

        if (!CollectionUtils.isEmpty(driverIds) && !CollectionUtils.isEmpty(commUseDriverList)) {
            for (CommUseDriverInfoDTO commUseDriverInfo : commUseDriverList) {
                if (null != commUseDriverInfo.getId() && driverIds.contains(commUseDriverInfo.getId().toString())) {
                    RenterAdditionalDriverEntity record = new RenterAdditionalDriverEntity();
                    record.setOrderNo(orderNo);
                    record.setRenterOrderNo(renterOrderNo);
                    record.setDriverId(String.valueOf(commUseDriverInfo.getId()));
                    record.setRealName(commUseDriverInfo.getRealName());
                    record.setPhone(String.valueOf(commUseDriverInfo.getMobile()));
                    record.setIdCard(commUseDriverInfo.getIdCard());
                    record.setDriLicAllowCar(commUseDriverInfo.getDriLicAllowCar());
                    record.setValidityStartDate(commUseDriverInfo.getValidityStartDate());
                    record.setValidityEndDate(commUseDriverInfo.getValidityEndDate());

                    //添加操作人
                    record.setCreateOp(commUseDriverInfo.getConsoleOperatorName());
                    record.setUpdateOp(commUseDriverInfo.getConsoleOperatorName());
                    renterAdditionalDriverMapper.insertSelective(record);
                }
            }


        }


    }
    
    
    /**
     * 批量保存附加驾驶人信息（修改订单）
     *
     * @param orderNo           主订单号
     * @param renterOrderNo     租客订单号
     * @param initRentOrderNo	上笔生效的租客子单号
     * @param driverIds         用户选择附加驾驶人信息
     * @param commUseDriverList 租客可用附加驾驶人列表
     */
    public void insertBatchAdditionalDriver(String orderNo, String renterOrderNo, String initRentOrderNo, List<String> driverIds,
                                            List<CommUseDriverInfoDTO> commUseDriverList) {
        logger.info("Batch insert additional driver.param is,orderNo:[{}],renterOrderNo:[{}],driverIds:[{}],commUseDriverList:[{}]",
                orderNo, renterOrderNo, JSON.toJSON(driverIds), JSON.toJSON(commUseDriverList));
        if (driverIds == null || driverIds.isEmpty()) {
        	return;
        }
        List<String> historyDriverList = new ArrayList<String>();
        for (String driverId:driverIds) {
        	boolean flag = true;
    		if (commUseDriverList != null && !commUseDriverList.isEmpty()) {
    			for (CommUseDriverInfoDTO commUseDriverInfo : commUseDriverList) {
                    if (null != commUseDriverInfo.getId() && driverId.equals(commUseDriverInfo.getId().toString())) {
                    	flag = false;
                        RenterAdditionalDriverEntity record = new RenterAdditionalDriverEntity();
                        record.setOrderNo(orderNo);
                        record.setRenterOrderNo(renterOrderNo);
                        record.setDriverId(String.valueOf(commUseDriverInfo.getId()));
                        record.setRealName(commUseDriverInfo.getRealName());
                        record.setPhone(String.valueOf(commUseDriverInfo.getMobile()));
                        record.setIdCard(commUseDriverInfo.getIdCard());
                        record.setDriLicAllowCar(commUseDriverInfo.getDriLicAllowCar());
                        record.setValidityStartDate(commUseDriverInfo.getValidityStartDate());
                        record.setValidityEndDate(commUseDriverInfo.getValidityEndDate());

                        //添加操作人
                        record.setCreateOp(commUseDriverInfo.getConsoleOperatorName());
                        record.setUpdateOp(commUseDriverInfo.getConsoleOperatorName());
                        renterAdditionalDriverMapper.insertSelective(record);
                        break;
                    }
                }
    		}
    		if (flag) {
    			historyDriverList.add(driverId);
    		}
    	}
        if (historyDriverList.isEmpty()) {
        	return;
        }
        // 获取已经购买的附加驾驶人信息
        List<RenterAdditionalDriverEntity> initAddDriverList = listDriversByRenterOrderNo(initRentOrderNo);
        if (initAddDriverList == null || initAddDriverList.isEmpty()) {
        	return;
        }
        for (RenterAdditionalDriverEntity driverEntity:initAddDriverList) {
        	if (driverEntity.getDriverId() != null && historyDriverList.contains(driverEntity.getDriverId())) {
        		driverEntity.setRenterOrderNo(renterOrderNo);
        		driverEntity.setId(null);
        		driverEntity.setCreateTime(null);
        		driverEntity.setUpdateTime(null);
        		renterAdditionalDriverMapper.insertSelective(driverEntity);
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
			renterAdditionalDriverMapper.delByRenterOrderNo(renterOrderNo);
			//如果是同一个人删除又添加，则i=0
//			if(i > 0) {
				//后新增
				for (CommUseDriverInfoDTO commUseDriverInfo : commUseDriverList) {
					if (null != commUseDriverInfo.getId() && driverIds.contains(commUseDriverInfo.getId().toString())) {
						RenterAdditionalDriverEntity record = new RenterAdditionalDriverEntity();
						record.setOrderNo(orderNo);
						record.setRenterOrderNo(renterOrderNo);
						record.setDriverId(String.valueOf(commUseDriverInfo.getId()));
						record.setRealName(commUseDriverInfo.getRealName());
						record.setPhone(String.valueOf(commUseDriverInfo.getMobile()));
						///
						record.setIdCard(commUseDriverInfo.getIdCard());
	                    record.setDriLicAllowCar(commUseDriverInfo.getDriLicAllowCar());
	                    record.setValidityStartDate(commUseDriverInfo.getValidityStartDate());
	                    record.setValidityEndDate(commUseDriverInfo.getValidityEndDate());
						//添加操作人
						record.setCreateOp(commUseDriverInfo.getConsoleOperatorName());
						record.setUpdateOp(commUseDriverInfo.getConsoleOperatorName());
						renterAdditionalDriverMapper.insertSelective(record);
					}
				}
//			}
		}else {
			//仅仅删除 20200211
			delByRenterOrderNo(renterOrderNo);
		}

		
    }
    
    public int delByRenterOrderNo(String renterOrderNo) {
    	return renterAdditionalDriverMapper.delByRenterOrderNo(renterOrderNo);
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
