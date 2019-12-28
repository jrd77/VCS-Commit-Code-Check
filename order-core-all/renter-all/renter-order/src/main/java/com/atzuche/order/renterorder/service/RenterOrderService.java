package com.atzuche.order.renterorder.service;

import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
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


    /**
     * 生成租客订单
     *
     * @param renterOrderReqVO 请求参数
     */
    public void generateRenterOrderInfo(RenterOrderReqVO renterOrderReqVO) {
        //1. 租客订单业务处理
        //1.1租车费用计算
        RenterOrderCostReqDTO renterOrderCostReqDTO = buildRenterOrderCostReqDTO(renterOrderReqVO);
        RenterOrderCostRespDTO renterOrderCostRespDTO =
                renterOrderCalCostService.getOrderCostAndDeailList(renterOrderCostReqDTO);


        //2.送取服务券



        //3.


    }


    /**
     * 租车费用计算相关参数封装
     *
     * @param renterOrderReqVO 生成租客订单请求参数
     * @return RenterOrderCostReqDTO
     */
    public RenterOrderCostReqDTO buildRenterOrderCostReqDTO(RenterOrderReqVO renterOrderReqVO) {
        //基础信息
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setStartTime(renterOrderReqVO.getRentTime());
        costBaseDTO.setStartTime(renterOrderReqVO.getRevertTime());
        costBaseDTO.setOrderNo(renterOrderReqVO.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterOrderReqVO.getRenterOrderNo());
        costBaseDTO.setMemNo(renterOrderReqVO.getMemNo());

        //租金计算相关信息
        RentAmtDTO rentAmtDTO = new RentAmtDTO();
        rentAmtDTO.setCostBaseDTO(costBaseDTO);
        rentAmtDTO.setRenterGoodsPriceDetailDTOList(renterOrderReqVO.getRenterGoodsPriceDetailDTOList());

        //保险计算相关信息
        InsurAmtDTO insurAmtDTO = new InsurAmtDTO();
        insurAmtDTO.setCostBaseDTO(costBaseDTO);
        insurAmtDTO.setCarLabelIds(renterOrderReqVO.getLabelIds());
        insurAmtDTO.setCertificationTime(renterOrderReqVO.getCertificationTime());
        insurAmtDTO.setGetCarBeforeTime(renterOrderReqVO.getGetCarBeforeTime());
        insurAmtDTO.setReturnCarAfterTime(renterOrderReqVO.getReturnCarAfterTime());
        insurAmtDTO.setInmsrp(renterOrderReqVO.getInmsrp());
        insurAmtDTO.setGuidPrice(renterOrderReqVO.getGuidPrice());

        //补充全险计算相关信息
        AbatementAmtDTO abatementAmtDTO = new AbatementAmtDTO();
        abatementAmtDTO.setCostBaseDTO(costBaseDTO);
        abatementAmtDTO.setCarLabelIds(renterOrderReqVO.getLabelIds());
        abatementAmtDTO.setCertificationTime(renterOrderReqVO.getCertificationTime());
        abatementAmtDTO.setGetCarBeforeTime(renterOrderReqVO.getGetCarBeforeTime());
        abatementAmtDTO.setReturnCarAfterTime(renterOrderReqVO.getReturnCarAfterTime());
        abatementAmtDTO.setInmsrp(renterOrderReqVO.getInmsrp());
        abatementAmtDTO.setGuidPrice(renterOrderReqVO.getGuidPrice());

        //附加驾驶人险计算相关信息
        ExtraDriverDTO extraDriverDTO = new ExtraDriverDTO();
        extraDriverDTO.setCostBaseDTO(costBaseDTO);
        extraDriverDTO.setDriverIds(renterOrderReqVO.getDriverIds());

        //取还车费用计算相关信息
        GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
        getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);
        getReturnCarCostReqDto.setCarLat(renterOrderReqVO.getCarLat());
        getReturnCarCostReqDto.setCarLon(renterOrderReqVO.getCarLon());
        getReturnCarCostReqDto.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        getReturnCarCostReqDto.setEntryCode(renterOrderReqVO.getEntryCode());
        getReturnCarCostReqDto.setSource(renterOrderReqVO.getSource());
        getReturnCarCostReqDto.setSrvGetLat(renterOrderReqVO.getSrvGetLat());
        getReturnCarCostReqDto.setSrvGetLon(renterOrderReqVO.getSrvGetLon());
        getReturnCarCostReqDto.setSrvReturnLon(renterOrderReqVO.getSrvReturnLon());
        getReturnCarCostReqDto.setSrvReturnLat(renterOrderReqVO.getSrvReturnLat());
        getReturnCarCostReqDto.setIsPackageOrder(false);

        //超运能溢价计算相关信息
        GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
        getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
        getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(renterOrderReqVO.getCityCode()));
        getReturnCarOverCostReqDto.setOrderType(1);

        //租车费用计算相关参数
        RenterOrderCostReqDTO renterOrderCostReqDTO = new RenterOrderCostReqDTO();
        renterOrderCostReqDTO.setCostBaseDTO(costBaseDTO);
        renterOrderCostReqDTO.setAbatementAmtDTO(abatementAmtDTO);
        renterOrderCostReqDTO.setRentAmtDTO(rentAmtDTO);
        renterOrderCostReqDTO.setInsurAmtDTO(insurAmtDTO);
        renterOrderCostReqDTO.setExtraDriverDTO(extraDriverDTO);
        renterOrderCostReqDTO.setMileageAmtDTO(null);
        renterOrderCostReqDTO.setGetReturnCarCostReqDto(getReturnCarCostReqDto);
        renterOrderCostReqDTO.setGetReturnCarOverCostReqDto(getReturnCarOverCostReqDto);
        return renterOrderCostReqDTO;
    }
}
