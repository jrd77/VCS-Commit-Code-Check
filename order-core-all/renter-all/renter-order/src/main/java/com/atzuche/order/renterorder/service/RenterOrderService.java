package com.atzuche.order.renterorder.service;

import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 租客订单子表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Service
public class RenterOrderService {

    @Resource
    private RenterOrderMapper renterOrderMapper;

    @Resource
    private RenterOrderCalCostService renterOrderCalCostService;


    public List<RenterOrderEntity> listAgreeRenterOrderByOrderNo(String orderNo) {
        return renterOrderMapper.listAgreeRenterOrderByOrderNo(orderNo);
    }
    
    /**
     * 获取有效的租客子单
     * @param orderNo 主订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByOrderNoAndIsEffective(String orderNo) {
    	return renterOrderMapper.getRenterOrderByOrderNoAndIsEffective(orderNo);
    }
    
    
    /**
     * 获取租客子单根据租客子单号
     * @param renterOrderNo 租客子订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByRenterOrderNo(String renterOrderNo) {
    	return renterOrderMapper.getRenterOrderByRenterOrderNo(renterOrderNo);
    }
    
    /**
     * 修改租客子订单是否有效状态
     * @param id
     * @param effectiveFlag
     * @return Integer
     */
    public Integer updateRenterOrderEffective(Integer id, Integer effectiveFlag) {
    	return renterOrderMapper.updateRenterOrderEffective(id, effectiveFlag);
    }
    
    /**
     * 保存租客子订单
     * @param renterOrderEntity
     * @return Integer
     */
    public Integer saveRenterOrder(RenterOrderEntity renterOrderEntity) {
    	return renterOrderMapper.insertSelective(renterOrderEntity);
    }
    
    /**
     * 获取待支付的租客子订单
     * @param orderNo 主订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByOrderNoAndWaitPay(String orderNo) {
    	return renterOrderMapper.getRenterOrderByOrderNoAndWaitPay(orderNo);
    }






    public void generateRenterOrderInfo(String orderNo,String renterOrderNo) {
        //1.租客订单处理
        //1.1租车费用计算
        RenterOrderCostReqDTO renterOrderCostReqDTO = new RenterOrderCostReqDTO();

        //1.2车主券抵扣

        //1.3限时红包抵扣

        //1.4


    }



    private RenterOrderCostReqDTO buildRenterOrderCostReqDTO() {
        RenterOrderCostReqDTO renterOrderCostReqDTO = new RenterOrderCostReqDTO();

        CostBaseDTO costBaseDTO = new CostBaseDTO();


        RentAmtDTO rentAmtDTO = new RentAmtDTO();


        InsurAmtDTO insurAmtDTO = new InsurAmtDTO();


        AbatementAmtDTO abatementAmtDTO = new AbatementAmtDTO();

        ExtraDriverDTO extraDriverDTO = new ExtraDriverDTO();


        return null;
    }
}
