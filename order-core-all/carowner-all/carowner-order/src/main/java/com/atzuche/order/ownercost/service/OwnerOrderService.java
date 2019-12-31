package com.atzuche.order.ownercost.service;


import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderCostReqDTO;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderReqDTO;
import com.atzuche.order.ownercost.mapper.OwnerOrderMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerOrderService {
    @Autowired
    private OwnerOrderMapper ownerOrderMapper;
    @Autowired
    private OwnerOrderCalCostService ownerOrderCalCostService;

    /*
     * @Author ZhangBin
     * @Date 2019/12/25 10:08
     * @Description: 查询有效的子订单
     *
     **/
    public OwnerOrderEntity getOwnerOrderByOrderNoAndIsEffective(String orderNo){
        return ownerOrderMapper.getOwnerOrderByOrderNoAndIsEffective(orderNo);
    }
    
    /**
     * 根据id把上笔车主子单置为无效
     * @param id
     * @return Integer
     */
    public Integer updateOwnerOrderInvalidById(Integer id) {
    	return ownerOrderMapper.updateOwnerOrderInvalidById(id);
    }
    
    /**
     * 保存车主子订单
     * @param ownerOrderEntity
     * @return Integer
     */
    public Integer saveOwnerOrder(OwnerOrderEntity ownerOrderEntity) {
    	return ownerOrderMapper.insertSelective(ownerOrderEntity);
    }


    /*
     * @Author ZhangBin
     * @Date 2019/12/31 14:45
     * @Description: 生成车主订单
     * 
     **/
    public void generateRenterOrderInfo(OwnerOrderReqDTO ownerOrderReqDTO){
        //1、生成车主子订单
        OwnerOrderEntity ownerOrderEntity = new OwnerOrderEntity();
        BeanUtils.copyProperties(ownerOrderReqDTO,ownerOrderEntity);
        ownerOrderEntity.setGoodsCode(ownerOrderReqDTO.getCarNo());
        ownerOrderEntity.setGoodsType(String.valueOf(ownerOrderReqDTO.getCategory()));
        ownerOrderMapper.insert(ownerOrderEntity);
        //2、生成费用信息

        OwnerOrderCostReqDTO ownerOrderCostReqDTO = new OwnerOrderCostReqDTO();

        ownerOrderCostReqDTO.setCarOwnerType(ownerOrderReqDTO.getCarOwnerType());
        ownerOrderCostReqDTO.setSrvGetFlag(ownerOrderReqDTO.getSrvGetFlag());
        ownerOrderCostReqDTO.setSrvReturnFlag(ownerOrderReqDTO.getSrvReturnFlag());
        ownerOrderCostReqDTO.setOwnerOrderPurchaseDetailEntity(ownerOrderReqDTO.getOwnerOrderPurchaseDetailEntity());
        ownerOrderCostReqDTO.setOwnerOrderSubsidyDetailEntity(ownerOrderReqDTO.getOwnerOrderSubsidyDetailEntity());

        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(ownerOrderReqDTO.getOrderNo());
        costBaseDTO.setOwnerOrderNo(ownerOrderReqDTO.getOwnerOrderNo());
        costBaseDTO.setMemNo(ownerOrderReqDTO.getMemNo());
        ownerOrderCostReqDTO.setCostBaseDTO(costBaseDTO);

        ownerOrderCalCostService.getOrderCostAndDeailList(ownerOrderCostReqDTO);

    }

}
