package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.DepositAmtDTO;
import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostCarDepositAmtReqDTO;
import com.atzuche.order.coreapi.entity.dto.cost.res.OrderCarDepositAmtResDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentermem.entity.dto.MemRightCarDepositAmtReqDTO;
import com.atzuche.order.rentermem.entity.dto.MemRightCarDepositAmtRespDTO;
import com.atzuche.order.rentermem.service.RenterMemberRightService;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.vo.RenterOrderCarDepositResVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.platformcost.model.CarDepositAmtVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 计算订单车辆押金
 *
 * @author pengcheng.fu
 * @date 2020/3/31 11:00
 */
@Service
@Slf4j
public class OrderCarDepositAmtFilter implements OrderCostFilter {

    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired
    private RenterMemberRightService renterMemberRightService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        OrderCostCarDepositAmtReqDTO orderCostCarDepositAmtReqDTO = context.getReqContext().getCostCarDepositAmtReqDTO();
        log.info("订单费用计算-->车辆押金.param is,baseReqDTO:[{}],orderCostCarDepositAmtReqDTO:[{}]",
                JSON.toJSONString(baseReqDTO),
                JSON.toJSONString(orderCostCarDepositAmtReqDTO));

        if (Objects.isNull(baseReqDTO) && Objects.isNull(orderCostCarDepositAmtReqDTO)) {
            throw new OrderCostFilterException(ErrorCode.PARAMETER_ERROR.getCode(), "计算车辆押金参数为空!");
        }

        DepositAmtDTO depositAmtDTO = new DepositAmtDTO();
        depositAmtDTO.setSurplusPrice(Objects.isNull(orderCostCarDepositAmtReqDTO.getSurplusPrice()) ?
                orderCostCarDepositAmtReqDTO.getGuidPrice() :
                orderCostCarDepositAmtReqDTO.getSurplusPrice());
        depositAmtDTO.setCityCode(orderCostCarDepositAmtReqDTO.getCityCode());
        depositAmtDTO.setBrand(orderCostCarDepositAmtReqDTO.getBrand());
        depositAmtDTO.setType(orderCostCarDepositAmtReqDTO.getType());
        depositAmtDTO.setLicenseDay(orderCostCarDepositAmtReqDTO.getLicenseDay());
        CarDepositAmtVO carDepositAmt = renterOrderCostCombineService.getCarDepositAmtVO(depositAmtDTO);
        log.info("订单费用计算-->车辆押金.carDepositAmt:[{}]", JSON.toJSONString(carDepositAmt));

        MemRightCarDepositAmtReqDTO memRightCarDepositAmtReqDTO = new MemRightCarDepositAmtReqDTO();
        memRightCarDepositAmtReqDTO.setGuidPrice(orderCostCarDepositAmtReqDTO.getGuidPrice());
        memRightCarDepositAmtReqDTO.setOriginalDepositAmt(null == carDepositAmt.getCarDepositAmt() ? 0 :
                Math.abs(carDepositAmt.getCarDepositAmt()));
        memRightCarDepositAmtReqDTO.setRenterMemberRightDTOList(orderCostCarDepositAmtReqDTO.getRenterMemberRightDTOList());
        memRightCarDepositAmtReqDTO.setOrderCategory(baseReqDTO.getOrderCategory());
        log.info("订单费用计算-->车辆押金.memRightCarDepositAmtReqDTO:[{}]",
                JSON.toJSONString(memRightCarDepositAmtReqDTO));
        MemRightCarDepositAmtRespDTO memRightCarDepositAmtRespDTO =
                renterMemberRightService.carDepositAmt(memRightCarDepositAmtReqDTO);
        log.info("订单费用计算-->车辆押金.memRightCarDepositAmtRespDTO:[{}]",
                JSON.toJSONString(memRightCarDepositAmtRespDTO));

        //车辆押金账户信息
        RenterOrderCarDepositResVO renterOrderCarDepositResVO = new RenterOrderCarDepositResVO();
        renterOrderCarDepositResVO.setMemNo(baseReqDTO.getMemNo());
        renterOrderCarDepositResVO.setOrderNo(baseReqDTO.getOrderNo());
        renterOrderCarDepositResVO.setReductionAmt(null == memRightCarDepositAmtRespDTO.getReductionDepositAmt() ?
                OrderConstant.ZERO : Math.abs(memRightCarDepositAmtRespDTO.getReductionDepositAmt()));
        renterOrderCarDepositResVO.setYingfuDepositAmt(-(memRightCarDepositAmtRespDTO.getOriginalDepositAmt() - renterOrderCarDepositResVO.getReductionAmt()));
        renterOrderCarDepositResVO.setFreeDepositType(FreeDepositTypeEnum.getFreeDepositTypeEnumByCode(orderCostCarDepositAmtReqDTO.getFreeDoubleTypeId()));
        renterOrderCarDepositResVO.setReductionRate(Double.valueOf(memRightCarDepositAmtRespDTO.getReductionRate() * 100).intValue());

        //车辆押金明细
        RenterDepositDetailEntity record = new RenterDepositDetailEntity();
        record.setOrderNo(baseReqDTO.getOrderNo());
        record.setSuggestTotal(carDepositAmt.getSuggestTotal());
        record.setCarSpecialCoefficient(carDepositAmt.getCarSpecialCoefficient());
        record.setNewCarCoefficient(carDepositAmt.getNewCarCoefficient());
        record.setOriginalDepositAmt(carDepositAmt.getCarDepositAmt());
        record.setReductionDepositAmt(memRightCarDepositAmtRespDTO.getReductionDepositAmt());
        record.setReductionRate(memRightCarDepositAmtRespDTO.getReductionRate());

        OrderCarDepositAmtResDTO orderCarDepositAmtResDTO = new OrderCarDepositAmtResDTO();
        orderCarDepositAmtResDTO.setCarDepositAmt(renterOrderCarDepositResVO.getYingfuDepositAmt());
        orderCarDepositAmtResDTO.setCarDeposit(renterOrderCarDepositResVO);
        orderCarDepositAmtResDTO.setDepositDetailEntity(record);

        log.info("订单费用计算-->车辆押金.result is, orderCarDepositAmtResDTO:[{}]", JSON.toJSONString(orderCarDepositAmtResDTO));
        context.getResContext().setOrderCarDepositAmtResDTO(orderCarDepositAmtResDTO);
    }
}
