package com.atzuche.order.rentercost.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.mapper.RenterOrderCostDetailMapper;


/**
 * 租客费用明细表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:30:57
 */
@Service
public class RenterOrderCostDetailService{
    @Autowired
    private RenterOrderCostDetailMapper renterOrderCostDetailMapper;

	/**
	 * 返回子订单的租车费用明细
	 * @param orderNo
	 * @param renterOrderNo
	 * @return
	 */
    public List<RenterOrderCostDetailEntity> getRenterOrderCostDetailList(String orderNo,String renterOrderNo){
    	return renterOrderCostDetailMapper.listRenterOrderCostDetail(orderNo,renterOrderNo);
	}

	/**
	 * 返回子订单的租车费用总额
	 * @param orderNo
	 * @param renterOrderNo
	 * @return
	 */
	public int getTotalOrderCostAmt(String orderNo,String renterOrderNo){
		List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList = getRenterOrderCostDetailList(orderNo,renterOrderNo);
		int total =0;
		for(RenterOrderCostDetailEntity entity:renterOrderCostDetailEntityList){
            if(entity.getTotalAmount()!=null){
            	total = total + entity.getTotalAmount();
			}
		}
		return total;
	}



    
    /**
     * 保存费用明细
     * @param renterOrderCostDetailEntity 费用明细
     * @return Integer
     */
    public Integer saveRenterOrderCostDetail(RenterOrderCostDetailEntity renterOrderCostDetailEntity) {
    	return renterOrderCostDetailMapper.saveRenterOrderCostDetail(renterOrderCostDetailEntity);
    }


    /**
     * 批量保存费用明细
     * @param costList 费用明细列表
     * @return Integer
     */
    public Integer saveRenterOrderCostDetailBatch(List<RenterOrderCostDetailEntity> costList) {
    	return renterOrderCostDetailMapper.saveRenterOrderCostDetailBatch(costList);
    }
    
    /**
     * 获取费用明细列表
     * @param orderNo 主订单号
     * @param renterOrderNo 租客订单号
     * @return List<RenterOrderCostDetailEntity>
     */
    public List<RenterOrderCostDetailEntity> listRenterOrderCostDetail(String orderNo, String renterOrderNo) {
    	List<RenterOrderCostDetailEntity> list = renterOrderCostDetailMapper.listRenterOrderCostDetail(orderNo, renterOrderNo);
    	return list;
    }
    
    
    /**
     * 获取费用明细列表(相同费用编码合并后的)
     * @param orderNo 主订单号
     * @param renterOrderNo 租客订单号
     * @return List<RenterOrderCostDetailEntity>
     */
    public List<RenterOrderCostDetailEntity> listRenterOrderCostDetailMerge(String orderNo, String renterOrderNo) {
    	List<RenterOrderCostDetailEntity> list = listRenterOrderCostDetail(orderNo, renterOrderNo);
    	if (list != null && !list.isEmpty()) {
    		List<RenterOrderCostDetailEntity> afterList = new ArrayList<RenterOrderCostDetailEntity>();
    		// 相同费用编码合并成一条
    		// 按费用编码分组
    		Map<String, List<RenterOrderCostDetailEntity>> costMap = list.stream().collect(Collectors.groupingBy(RenterOrderCostDetailEntity::getCostCode));
    		for(Map.Entry<String, List<RenterOrderCostDetailEntity>> it : costMap.entrySet()){
    			List<RenterOrderCostDetailEntity> costList = it.getValue();
    			Integer totalAmount = 0;
    			if (costList != null && !costList.isEmpty()) {
    				totalAmount += costList.stream().mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
    				RenterOrderCostDetailEntity curObj = costList.get(0);
    				curObj.setTotalAmount(totalAmount);
    				curObj.setUnitPrice(null);
    				curObj.setCount(null);
    				curObj.setStartTime(null);
    				curObj.setEndTime(null);
    				afterList.add(curObj);
    			}
    		}
    		return afterList;
    	}
    	return list;
    }

	public int saveOrUpdateRenterOrderCostDetail(RenterOrderCostDetailEntity extraDriverInsureAmtEntity) {
		List<RenterOrderCostDetailEntity> list = listRenterOrderCostDetail(extraDriverInsureAmtEntity.getOrderNo(), extraDriverInsureAmtEntity.getRenterOrderNo());
		boolean isExists = false;
		for (RenterOrderCostDetailEntity renterOrderCostDetailEntity : list) {
			if(renterOrderCostDetailEntity.getCostCode().equals(RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo())) {
				//根据ID修改
				extraDriverInsureAmtEntity.setId(renterOrderCostDetailEntity.getId());
				//不修改
				extraDriverInsureAmtEntity.setCreateOp(null);
				renterOrderCostDetailMapper.updateByPrimaryKeySelective(extraDriverInsureAmtEntity);
				isExists = true;
			}
		}
		if(!isExists) {
			renterOrderCostDetailMapper.saveRenterOrderCostDetail(extraDriverInsureAmtEntity);
		}
		return 1;
	}
	
	
	
}
