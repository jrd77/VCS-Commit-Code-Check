package com.atzuche.order.renterorder.service;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RenterOrderCalCostService {
    @Autowired
    private RenterOrderMapper renterOrderMapper;
    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;

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

    /*
     * @Author ZhangBin
     * @Date 2019/12/24 15:21
     * @Description: 获取费用项和费用明细列表+落库
     *
     **/
    public RenterOrderCostRespDTO getRenterOrderCostAndDeailList(RenterOrderCostReqDTO renterOrderCostReqDTO){
        RenterOrderCostRespDTO renterOrderCostRespDTO = new RenterOrderCostRespDTO();
        List<RenterOrderCostDetailEntity> detailList = new ArrayList<>();

        //获取租金
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostCombineService.listRentAmtEntity(renterOrderCostReqDTO.getRentAmtDTO());
        int rentAmt = renterOrderCostDetailEntities.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        detailList.addAll(renterOrderCostDetailEntities);

        //获取平台保障费
        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(renterOrderCostReqDTO.getInsurAmtDTO());
        Integer insurAmt = insurAmtEntity.getTotalAmount();
        renterOrderCostRespDTO.setBasicEnsureAmount(insurAmt);
        detailList.add(insurAmtEntity);

        //获取全面保障费
        List<RenterOrderCostDetailEntity> comprehensiveEnsureList = renterOrderCostCombineService.listAbatementAmtEntity(renterOrderCostReqDTO.getAbatementAmtDTO());
        Integer comprehensiveEnsureAmount = comprehensiveEnsureList.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        renterOrderCostRespDTO.setComprehensiveEnsureAmount(comprehensiveEnsureAmount);
        detailList.addAll(comprehensiveEnsureList);


        //获取附加驾驶人费用
        RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(renterOrderCostReqDTO.getExtraDriverDTO());
        Integer totalAmount = extraDriverInsureAmtEntity.getTotalAmount();
        renterOrderCostRespDTO.setAdditionalDrivingEnsureAmount(totalAmount);
        detailList.add(extraDriverInsureAmtEntity);

        //获取取还车费用 TODO

        //获取取还车超运能费用 TODO

        //获取平台手续费
        RenterOrderCostDetailEntity serviceChargeFeeEntity = renterOrderCostCombineService.getServiceChargeFeeEntity(renterOrderCostReqDTO.getCostBaseDTO());
        Integer serviceAmount = serviceChargeFeeEntity.getTotalAmount();
        renterOrderCostRespDTO.setCommissionAmount(serviceAmount);
        detailList.add(serviceChargeFeeEntity);

        //租车费用 = 租金+平台保障费+全面保障费+取还车费用+取还车超云能费用+附加驾驶员费用+手续费；
        int rentCarAmount = rentAmt + insurAmt + comprehensiveEnsureAmount + 0 + 0 + totalAmount + serviceAmount;

        renterOrderCostRespDTO.setRentCarAmount(rentCarAmount);
        renterOrderCostRespDTO.setRenterOrderCostDetailDTOList(detailList);
        return renterOrderCostRespDTO;
    }
}
